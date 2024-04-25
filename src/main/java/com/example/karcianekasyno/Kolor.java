package com.example.karcianekasyno;

public enum Kolor {
    Spades, Clubs, Hearts, Diamonds, Joker;
    public static Kolor getKolor(int i) {
        return values()[i];
    }
}
