package com.example.myapplication;

import java.util.ArrayList;
import java.util.Random;

public class GameRoom
{
    public int roomNum = 0;
    public int roundNum = 0;
    public ArrayList<Player> arr_players = new ArrayList<>();
    public String roomCode;
    public String voterName;
    public String timeLimit = "10";
    public String roundLimit = "10";
    public String roomSubject = "no subject";
    public ArrayList<Rating> game_arr_round_rates = new ArrayList<>();
    public boolean finish, gameStarted = false;


    public GameRoom()
    {}

    public GameRoom(int roomNum, String gameCreatorName)
    {
        this.roomCode = random();
        this.roomNum = roomNum;
        Player p = new Player(0, gameCreatorName);
        this.arr_players.add(p);
        Rating rating = new Rating(0, 0);
        game_arr_round_rates.add(rating);
    }

    private static String random()
    {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 6; i++)
        {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }
}