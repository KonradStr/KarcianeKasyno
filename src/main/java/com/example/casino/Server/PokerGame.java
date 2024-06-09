package com.example.casino.Server;

import com.example.casino.Packets.GamePacket;
import com.example.casino.Packets.Packet;
import com.example.casino.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PokerGame implements Callable<ArrayList<ClientHandler>> {
    String id;

    int playersReady;

    List<ClientHandler> players;
    List<Player> playersData;
    ArrayList<Karta> tableCards;
    private Talia deck = new Talia(false);
    final Lock lock = new ReentrantLock();
    final Condition next = lock.newCondition();
    Integer currentBid;
    private ArrayList<ClientHandler> Winners() {
        ClientHandler highestHand = players.get(0);
        for (ClientHandler ch:
             players) {
            if (Objects.requireNonNull(highestHand.getPlayer().pokerHand.compareWith(ch.getPlayer().pokerHand))
                    == PokerHand.Result.LOSS) {
                highestHand = ch;
            }
        }
        ArrayList<ClientHandler> ret = new ArrayList<>();
        for (ClientHandler ch:
             players) {
            if(highestHand.getPlayer().pokerHand.compareWith(ch.getPlayer().pokerHand) == PokerHand.Result.TIE)
                ret.add(ch);
        }
        return ret;
    }

    boolean nextPlayer = false;

    Talia talia;
    int moneyPool;



    public PokerGame(String id) {
        currentBid = 0;
        moneyPool = 0;
        playersReady = 0;
        players = new ArrayList<>();
        playersData = new ArrayList<>();
        this.id = id;
        this.talia = new Talia(false);
    }

    public PokerGame(String id, ClientHandler ch) {
        currentBid = 0;
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
    public ArrayList<ClientHandler> call() throws Exception {
        this.tableCards = new ArrayList<>();
        Thread.sleep(1000);
        talia.SzuflujTalie();
        for (int i = 0; i < 5; i++) {
            this.tableCards.add(talia.KartaZTalii());
        }
        for (Player p : playersData){
            p.passedAway = false;
            p.setMoney(1000);
            p.pokerHand.addHand(tableCards);
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
        Thread.sleep(1000);
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
        Thread.sleep(2000);
        playersData.get(1).setMoney(playersData.get(1).getMoney() - 10);
        moneyPool += 10;
        players.get(1).sendPacket(new GamePacket("big blind", GamePacket.Status.BIG_BLIND, moneyPool));
        for (ClientHandler ch : players){
            if (!ch.equals(players.get(1))){
                ch.sendPacket(new GamePacket("someone put small blind", GamePacket.Status.BIG_BLIND, players.get(1).getPlayer()));
            }
        }
        currentBid = 10;
        for (int i = 2; i < playersData.size(); i++){
            players.get(i).sendPacket(new GamePacket("your move", GamePacket.Status.MOVE, GamePacket.MOVE_TYPE.CALL, currentBid));
            lock.lock();
            while (!nextPlayer) {
                next.await();
            }
            lock.unlock();
            nextPlayer = false;
        }
        for (int i = 1; i <= 3; i++) {
            broadcast(new GamePacket("środkowe karta:", GamePacket.Status.TABLE_CARDS, i, tableCards.get(i-1)));
            Thread.sleep(1500);
        }
        for (int i = 0; i < 3; i++){
            System.out.println("runda :" + i+1);
            for (ClientHandler ch : players){
                System.out.println(ch.getPlayer().getPlayerData());

                ch.sendPacket(new GamePacket("your move", GamePacket.Status.MOVE, GamePacket.MOVE_TYPE.CALL));
                for (ClientHandler otherCh : players) {
                    if (!otherCh.equals(ch)) {
                        otherCh.sendPacket(new GamePacket("other player move", GamePacket.Status.MOVE, ch.getPlayer()));
                    }
                }
                lock.lock();
                while (!nextPlayer) {
                    next.await();
                }
                lock.unlock();
                nextPlayer = false;
            }
            System.out.println("dalsze działanie");
            if (i == 2) break;
            broadcast(new GamePacket("środkowe karta:", GamePacket.Status.TABLE_CARDS, i + 4, tableCards.get(i+3)));
            Thread.sleep(1500);
        }
        /*
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


        return Winners();
    }


    public void unlockLock(){
        this.nextPlayer = true;
        lock.lock();
        this.next.signal();
        lock.unlock();
    }
}
