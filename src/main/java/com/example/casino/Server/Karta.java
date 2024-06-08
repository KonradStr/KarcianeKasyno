package com.example.casino.Server;

public class Karta {
    public Kolor kolor;
    public Rank rank;
    //public Image skin;
    public Karta(int kolor, int rank) {
        this.kolor = Kolor.getKolor(kolor);
        this.rank = Rank.getRank(rank);
        //this.skin = new Image("src/main/resources/cards/"+rank+kolor+".png");
    }
}
