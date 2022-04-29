package com.example.myapplication;

public class Player
{

    private int Score;
    private String Name;
    private String drawingUrl;
    private boolean drawingIsReady = false;


    public Player()
    {}

    public Player(int score, String name)
    {
        Score = score;
        Name = name;
    }

    public int getScore()
    {
        return Score;
    }

    public void setScore(Integer score)
    {
        Score = score;
    }

    public String getName()
    {
        return Name;
    }

    public void setName(String name)
    {
        Name = name;
    }

    public String getDrawingUrl()
    {
        return drawingUrl;
    }

    public void setDrawingUrl(String drawingUrl)
    {
        this.drawingUrl = drawingUrl;
    }

    public boolean isDrawingIsReady()
    {
        return drawingIsReady;
    }

    public void setDrawingIsReady(boolean drawingIsReady)
    {
        this.drawingIsReady = drawingIsReady;
    }
}
