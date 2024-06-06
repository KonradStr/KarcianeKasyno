package com.example.karcianekasyno;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RemikPlayer {
    private String name;
    private ArrayList<RemikCard> cardsOnHand = new ArrayList<>();

    public RemikPlayer(String name){
        this.name = name;
    }

    public void addCard(RemikCard newCard){
        cardsOnHand.add(newCard);
    }

    public ArrayList<RemikCard> getCardsOnHand() {
        return cardsOnHand;
    }

    public void sortCardsBySortValue() {
        Collections.sort(cardsOnHand, Comparator.comparingInt(RemikCard::getSortValue));
    }

    public String getName() {
        return name;
    }

}
