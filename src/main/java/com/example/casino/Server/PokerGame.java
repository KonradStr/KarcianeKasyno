package com.example.casino.Server;

import com.example.casino.Packets.Packet;
import com.example.casino.Player;

import java.util.ArrayList;
import java.util.List;

public class PokerGame {
    String id;

    List<ClientHandler> players;
    List<Player> playersData;

    public PokerGame(String id) {
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        this.id = id;
    }

    public PokerGame(String id, ClientHandler ch) {
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        players.add(ch);
        playersData.add(ch.getPlayer());
        this.id = id;
    }

    public void broadcast(Packet packet){
        for (ClientHandler ch : players){
            System.out.println("ch" +ch.toString());
            ch.sendPacket(packet);
        }
    }
}
