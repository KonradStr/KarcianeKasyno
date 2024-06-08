package com.example.casino.Server;

import com.example.casino.Client;
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

    Talia talia;
    int moneyPool;


    public PokerGame(String id) {
        moneyPool = 0;
        playersReady = 0;
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        this.id = id;
        this.talia = new Talia(false);
    }

    public PokerGame(String id, ClientHandler ch) {
        playersReady = 0;
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        players.add(ch);
        playersData.add(ch.getPlayer());
        this.id = id;
        this.talia = new Talia(false);
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
        talia.SzuflujTalie();
        for (Player p : playersData){
            p.setMoney(1000);
        }
        List<Player> otherPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++){
            otherPlayers.clear();
            ClientHandler ch = players.get(i);
            for (int j = i+1; j < players.size(); j++){
                otherPlayers.add(playersData.get(j));
            }
            for (int k = 0; k < i; k++){
                otherPlayers.add(playersData.get(k));
            }

            System.out.println("Dla:" + ch.getPlayer().getPlayerData());
            for (Player p : otherPlayers){
                System.out.println(p.getPlayerData());
            }

            System.out.println("Liczba graczy");
            ch.sendPacket(new GamePacket("Game Starting", GamePacket.Status.START, ch.getPlayer(),otherPlayers));
        }
        for (ClientHandler ch : players){
            Karta card = talia.KartaZTalii();
            ch.sendPacket(new GamePacket("FirstHandCard", GamePacket.Status.FIRST_HAND_CARD, card));
            ch.getPlayer().setCard1(card);
        }
        Thread.sleep(1000);
        for (ClientHandler ch : players){
            Karta card = talia.KartaZTalii();
            ch.sendPacket(new GamePacket("SecondHandCard", GamePacket.Status.SECOND_HAND_CARD, card));
            ch.getPlayer().setCard2(card);
        }
        Thread.sleep(1000);
        moneyPool += 5;
        playersData.get(0).setMoney(playersData.get(0).getMoney() - 5);
        players.get(0).sendPacket(new GamePacket("small blind", GamePacket.Status.SMALL_BLIND, moneyPool));
        for (ClientHandler ch : players){
            if (!ch.equals(players.get(0))){
                ch.sendPacket(new GamePacket("someone put small blind", GamePacket.Status.SMALL_BLIND, players.get(0).getPlayer()));
            }
        }
        Thread.sleep(4000);
        playersData.get(1).setMoney(playersData.get(1).getMoney() - 10);
        moneyPool += 10;
        players.get(1).sendPacket(new GamePacket("big blind", GamePacket.Status.BIG_BLIND, moneyPool));
        for (ClientHandler ch : players){
            if (!ch.equals(players.get(1))){
                ch.sendPacket(new GamePacket("someone put small blind", GamePacket.Status.BIG_BLIND, players.get(1).getPlayer()));
            }
        }
        for (int i = 2; i < playersData.size(); i++){
            synchronized (monitorObj) {
                players.get(i).sendPacket(new GamePacket("your move", GamePacket.Status.MOVE));
                monitorObj.wait();
            }
        }
        /*
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
         */


        return playersData.get(0);
    }



    public Object getMonitorObj() {
        return monitorObj;
    }
}
