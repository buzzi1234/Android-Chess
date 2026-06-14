package com.example.chessgame;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class loginPage extends AppCompatActivity implements View.OnClickListener {

    //fields
    ImageButton backbtn;
    EditText emailtv, passwordtv;
    ImageButton loginbtn;

    FirebaseAuth auth;

    //constructor
    @SuppressLint({"MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //find buttons and textviews
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(this);

        emailtv = findViewById(R.id.emailtv);
        passwordtv = findViewById(R.id.passwordtv);
        loginbtn = findViewById(R.id.loginbtn);

        auth = FirebaseAuth.getInstance();

        loginbtn.setOnClickListener(this);


    }

    /*
    The func see which button pressed and move to the right activity
     */
    @Override
    public void onClick(View v) {

        if(backbtn == v)
        {
            //letting the lobby know where does the client come from
            Intent intent = new Intent(loginPage.this,log_lobby.class);
            intent.putExtra("FROM_SCREEN", "LOGIN");
            startActivity(intent);
            finish();
        }
        if(loginbtn == v)
        {
            //try to login
            String txt_logemail = emailtv.getText().toString();
            String txt_logPw = passwordtv.getText().toString();
            loginClient(txt_logemail, txt_logPw);
        }

    }

    /*
    The func check if the email and password are already register in the auth table
    if yes he will move to the lobby activity and let the lobby know the client came from
    the loginPage
    input: txtLogemail -> the email the client gave
           txtLogPw    -> the password the client gave
    output: none
     */
    private void loginClient(String txtLogemail, String txtLogPw) {
        auth.signInWithEmailAndPassword(txtLogemail, txtLogPw).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String uid = auth.getUid();
                if (uid == null) return;

                GlobalStat globalStat = (GlobalStat) getApplicationContext();

                if (globalStat.userStats == null) {
                    globalStat.userStats = new UserStatistics();
                }

                // Bind the essential local variables first
                globalStat.userStats.Uid = uid;

                // Set a fallback username from the email prefix in case Firestore is empty
                globalStat.userStats.username = txtLogemail.substring(0, txtLogemail.indexOf('@'));

                DatabaseManager.loadUserProfile(loginPage.this, uid, new DatabaseManager.UserProfileCallback() {
                    @Override
                    public void onProfileLoaded(boolean success) {
                        // This block executes dynamically after your network request drops
                        Toast.makeText(loginPage.this, "Login successful", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(loginPage.this, lobby.class);
                        intent.putExtra("FROM_SCREEN", "LOGIN");
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }

}


