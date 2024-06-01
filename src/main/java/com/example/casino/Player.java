package com.example.casino;

import java.io.Serializable;

public class Player implements Serializable {
    private String username;
    private Integer id;

    private boolean isReady;

    public Player(Integer id, String username, boolean isReady) {
        this.id = id;
        this.username = username;
        this.isReady = isReady;
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
}
