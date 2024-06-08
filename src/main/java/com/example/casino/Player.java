package com.example.casino;

import com.example.casino.Server.Karta;

import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private Integer id;

    private boolean isReady;

    private Karta card1;
    private Karta card2;
    private Integer money;

    public Player(Integer id, String username, boolean isReady) {
        this.id = id;
        this.username = username;
        this.isReady = isReady;
        this.money = 1000;
    }

    public String getPlayerData(){
        return username;
    }

    public Integer getPlayerID(){
        return id;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setCard1(Karta card1) {
        this.card1 = card1;
    }

    public void setCard2(Karta card2) {
        this.card2 = card2;
    }

    public Karta getCard1() {
        return card1;
    }

    public Karta getCard2() {
        return card2;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }
}
