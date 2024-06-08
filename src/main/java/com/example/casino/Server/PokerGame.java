package com.example.casino.Server;

import com.example.casino.Packets.GamePacket;
import com.example.casino.Packets.Packet;
import com.example.casino.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PokerGame implements Callable<Player> {
    String id;

    int playersReady;

    List<ClientHandler> players;
    List<Player> playersData;
    final Lock lock = new ReentrantLock();
    final Condition next = lock.newCondition();
    Object monitorObj = new Object();


    public PokerGame(String id) {
        playersReady = 0;
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        this.id = id;
    }

    public PokerGame(String id, ClientHandler ch) {
        playersReady = 0;
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

    @Override
    public Player call() throws Exception {
        Thread.sleep(1000);
        broadcast(new GamePacket("Game Starting", GamePacket.Status.START));
        broadcast(new GamePacket("Karty na rece", GamePacket.Status.HAND_CARDS));
        broadcast(new GamePacket("środkowe karty:", GamePacket.Status.TABLE_CARDS));

        for (ClientHandler ch : players) {
            System.out.println(ch.getPlayer().getPlayerData());
            synchronized (monitorObj) {
                ch.sendPacket(new GamePacket("twój ruch", GamePacket.Status.MOVE));
                System.out.println("wysłano");
                monitorObj.wait();
            }
            System.out.println("next iteration");
        }

        return playersData.get(0);
    }



    public Object getMonitorObj() {
        return monitorObj;
    }
}
