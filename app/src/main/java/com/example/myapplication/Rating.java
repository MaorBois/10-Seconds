package com.example.myapplication;

public class Rating
{
    private int arr_index;
    private int player_rate;

    public Rating(int arr_index, int player_rate)
    {
        this.arr_index = arr_index;
        this.player_rate = player_rate;
    }

    public Rating() {
    }

    public int getArr_index() {
        return arr_index;
    }

    public void setArr_index(int arr_index) {
        this.arr_index = arr_index;
    }

    public int getPlayer_rate() {
        return player_rate;
    }

    public void setPlayer_rate(int player_rate) {
        this.player_rate = player_rate;
    }
}
