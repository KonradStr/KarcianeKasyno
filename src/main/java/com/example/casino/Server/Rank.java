package com.example.casino.Server;

public enum Rank {
    Two, Three, Four, Five, Six, Seven, Eight, Nine, Teen, Jack, Queen, King, Ace, Joker;
    public static Rank getRank(int i) {
        return values()[i];
    }
}
