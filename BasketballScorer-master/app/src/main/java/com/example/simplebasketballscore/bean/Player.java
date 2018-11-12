package com.example.simplebasketballscore.bean;

public class Player {
    private int num;
    private String name;
    private int score = 0;
    private int isTeamA;

    public Player() {
    }

    public Player(int num, String name, int score, int isTeamA) {
        this.num = num;
        this.name = name;
        this.score = score;
        this.isTeamA = isTeamA;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getIsTeamA() {
        return isTeamA;
    }

    public void setIsTeamA(int isTeamA) {
        this.isTeamA = isTeamA;
    }
}
