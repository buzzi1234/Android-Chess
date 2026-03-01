package com.example.chessgame;

public abstract class Piece {
    //fields
    protected Point m_startIndex;
    protected Point m_endIndex;
    protected char m_color;
    protected char m_type;

    //constructor reset fields
    Piece() {
        this.m_startIndex = new Point();
        this.m_endIndex = new Point();
        setColor(Constants.EMPTY_SLOT);
        setType(Constants.EMPTY_SLOT);
    }

    //destructor
    public void destroy()
    {
        this.m_startIndex.destroy();
        this.m_endIndex.destroy();
        this.m_startIndex = null;
        this.m_endIndex = null;
        this.m_color = '\0';
        this.m_type = '\0';

    }
    //abstract function for every piece
    public abstract boolean move(char color, Piece[][] board);
    public abstract boolean eat(char color, Piece[][] board);
    public abstract boolean inTheWay(char color, Piece[][] board);

    //get functions
    public Point getStartIndex()
    {
        return this.m_startIndex;
    }
    public Point getEndIndex()
    {
        return this.m_endIndex;
    }
    public char getColor()
    {
        return this.m_color;
    }
    public char getType()
    {
        return this.m_type;
    }

    //set functions
    public void setStartIndex(Point startIndex)
    {
        this.m_startIndex = startIndex;
    }
    public void setEndIndex(Point endIndex)
    {
        this.m_endIndex = endIndex;
    }

    public void setColor(char color)
    {
        this.m_color = color;
    }
    public void setType(char type)
    {
        this.m_type = type;
    }


}