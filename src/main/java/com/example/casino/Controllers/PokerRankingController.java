package com.example.casino.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PokerRankingController {
    private HashMap<Integer,Integer> pokerRankingMap = new HashMap<>();

    @FXML
    ListView top10poker;

    public void initTop10List(){
        top10poker.getItems().clear();
        ArrayList<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(pokerRankingMap.entrySet());
        entryList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (Map.Entry<Integer, Integer> entry : entryList) {
            String entryString = "Player ID: " + entry.getKey() + ", punkty: " + entry.getValue();
            top10poker.getItems().add(entryString);
        }

        //top10poker.getItems().addAll(this.pokerRankingMap.keySet().);
    }

    public void setPokerRankingMap(HashMap<Integer, Integer> pokerRankingMap) {
        this.pokerRankingMap = pokerRankingMap;
    }
}
