package com.example.karcianekasyno;

public class Karta {
    public Kolor kolor;
    public Rank rank;
    public Karta(int kolor, int rank) {
        this.kolor = Kolor.getKolor(kolor);
        this.rank = Rank.getRank(rank);
    }
}
