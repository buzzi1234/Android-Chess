package com.example.chessgame;

public class Point {

    //fields
    private int m_row;
    private int m_col;

    //constructor with row and col
    public Point(int row, int col)
    {
        this.m_row = row;
        this.m_col = col;
    }

    //constructor without row and col
    public Point()
    {
        this.m_row = 0;
        this.m_col = 0;
    }

    //destructor
    public void destroy()
    {
        this.m_row = 0;
        this.m_col = 0;
    }

    //The func return the row of the point
    public int getRow()
    {
        return this.m_row;
    }

    //The func return the col of the point
    public int getCol()
    {
        return this.m_col;
    }


    //the func get a new row and replace with the old row
    public void setRow(int row)
    {
        this.m_row = row;
    }
    //the func get a new col and replace with the old col
    public void setCol(int col)
    {
        this.m_col = col;
    }

}
