package com.example.chessgame;

import android.content.Context;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseManager {

    public interface DataStatusCallback {
        void onDataLoaded(UserStatistics stats);
    }

    public interface UserProfileCallback {
        void onProfileLoaded(boolean success);
    }
    public interface DeleteAccountCallback {
        void onAccountDeleted(boolean success, String errorMessage);
    }
    /*
    The func delete the user account from the firestore thats inculde the stats table
    input: context -> which class he came from
    output: none ( delete the account )
     */
    public static void deleteUserAccount(Context context, final DeleteAccountCallback callback) {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            if (callback != null) callback.onAccountDeleted(false, "No user is currently signed in.");
            return;
        }

        String uid = user.getUid();

        //wipe out their row/document from the Firestore collection completely
        com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("Statistics")
                .document(uid)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    android.util.Log.d("DB_MANAGER", "Firestore statistics document deleted successfully.");

                    // delete the identity credentials from Auth
                    user.delete()
                            .addOnSuccessListener(aVoidAuth -> {
                                android.util.Log.d("DB_MANAGER", "User Auth credentials purged successfully.");

                                // Clear local memory space cache representation
                                GlobalStat globalStat = (GlobalStat) context.getApplicationContext();
                                globalStat.userStats = new UserStatistics();

                                if (callback != null) callback.onAccountDeleted(true, null);
                            })
                            .addOnFailureListener(e -> {
                                android.util.Log.e("DB_MANAGER", "Auth account purge failed: " + e.getMessage());
                                // This happens if the user logged in a long time ago and needs to re-authenticate
                                if (callback != null) callback.onAccountDeleted(false, "Authentication timeout. Please log out and back in to delete your account.");
                            });
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("DB_MANAGER", "Failed to clear Firestore statistics: " + e.getMessage());
                    if (callback != null) callback.onAccountDeleted(false, e.getMessage());
                });
    }


    /*
    The func load the username from the stats table by using the uid of the client.
    input: uid -> the user id that the firestore create for each client
    output: none ( update the username in the globalstat )
     */
    public static void loadUserProfile(Context context, String uid, final UserProfileCallback callback) {
        if (uid == null || uid.trim().isEmpty()) {
            if (callback != null) callback.onProfileLoaded(false);
            return;
        }

        GlobalStat globalStat = (GlobalStat) context.getApplicationContext();

        FirebaseFirestore.getInstance()
                .collection("Statistics")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Extract just the username field explicitly
                        String cloudUsername = documentSnapshot.getString("username");
                        if (cloudUsername != null && !cloudUsername.trim().isEmpty()) {
                            globalStat.userStats.username = cloudUsername;
                        }
                    }
                    // Signal the UI thread that processing has finished safely
                    if (callback != null) callback.onProfileLoaded(true);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("DB_MANAGER", "Failed to load user profile: " + e.getMessage());
                    // Fire fallback signal anyway so login screen flow isn't left hanging frozen
                    if (callback != null) callback.onProfileLoaded(false);
                });
    }

    /*
    The func load all the stats from the stats table by using the uid of the client.
    input: uid -> the user id that the firestore create for each client
    output: none ( update the username in the globalstat )
     */
    public static void loadStatsFromFirebase(Context context, final DataStatusCallback callback) {
        GlobalStat globalStat = (GlobalStat) context.getApplicationContext();

        if (globalStat.userStats == null || globalStat.userStats.Uid == null
                || globalStat.userStats.Uid.trim().isEmpty()) {
            android.util.Log.e("FIREBASE_READ", "Cannot load stats: UID is null.");
            return;
        }

        String uid = globalStat.userStats.Uid;

        FirebaseFirestore.getInstance()
                .collection("Statistics")
                .document(uid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        UserStatistics stats = snapshot.toObject(UserStatistics.class);
                        if (stats != null) {
                            // Keep the UID and username that we already have in memory
                            stats.Uid      = globalStat.userStats.Uid;
                            stats.username = globalStat.userStats.username;

                            // Write everything back into global memory
                            globalStat.userStats = stats;
                            globalStat.statsLoadedFromFirebase = true;
                        }
                    } else {
                        // New user — no document yet, that's fine
                        globalStat.statsLoadedFromFirebase = true;
                    }

                    if (callback != null) callback.onDataLoaded(globalStat.userStats);
                })
                .addOnFailureListener(e -> {
                    android.util.Log.e("FIREBASE_READ", "Read failed: " + e.getMessage());
                    // Still fire callback so UI doesn't stay stuck on "..."
                    if (callback != null) callback.onDataLoaded(globalStat.userStats);
                });
    }

    /*
    The func saves the stats from the globalstat in the stats table in the fireStore
    input: uid -> the user id that the firestore create for each client
    output: none ( update the stats in firestore )
     */
    public static void saveStatsToFirebase(Context context) {
        GlobalStat globalStat = (GlobalStat) context.getApplicationContext();

        if (globalStat.userStats == null || globalStat.userStats.Uid == null
                || globalStat.userStats.Uid.trim().isEmpty()) {
            android.util.Log.e("FIREBASE_WRITE", "Cannot save stats: UID is null.");
            return;
        }

        String uid = globalStat.userStats.Uid;

        FirebaseFirestore.getInstance()
                .collection("Statistics")
                .document(uid)
                .set(globalStat.userStats)
                .addOnSuccessListener(aVoid ->
                        android.util.Log.d("FIREBASE_WRITE", "Stats saved successfully for UID: " + uid))
                .addOnFailureListener(e ->
                        android.util.Log.e("FIREBASE_WRITE", "Save failed: " + e.getMessage()));
    }

    /*
    The func update just the username in the stats table
    input: uid -> the user id that the firestore create for each client
    output: none ( update the username in firestore )
     */
    public static void updateUsername(String uid, String newUsername) {
        if (uid == null || uid.trim().isEmpty()) return;

        FirebaseFirestore.getInstance()
                .collection("Statistics")
                .document(uid)
                .update("username", newUsername)
                .addOnSuccessListener(aVoid -> android.util.Log.d("DB_MANAGER", "Username synced"))
                .addOnFailureListener(e -> android.util.Log.e("DB_MANAGER", "Sync failed: " + e.getMessage()));
    }
}