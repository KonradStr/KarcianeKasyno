package com.example.casino.Server;

import com.example.casino.Packets.GamePacket;
import com.example.casino.Packets.Packet;
import com.example.casino.Packets.PacketType;
import com.example.casino.Player;

import java.util.ArrayList;
import java.util.Collections;
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
    Integer moneyPool;

    public void handlerRaise(ClientHandler ch, Integer r) {
        if (ch.getPlayer().curBid < currentBid) {
            ch.getPlayer().money -= (currentBid - ch.getPlayer().curBid);
            ch.getPlayer().curBid = currentBid;
        }
        currentBid += r;
        ch.getPlayer().curBid = currentBid;
        ch.getPlayer().money -= r;
        updateMoneyPool();
        broadcast(new Packet(PacketType.GAME, ch.toString() + " raised " + r));
    }

    public void handlerCall(ClientHandler ch) {
        if (ch.getPlayer().curBid < currentBid) {
            ch.getPlayer().money -= currentBid - ch.getPlayer().curBid;
            ch.getPlayer().curBid = currentBid;
        }
        updateMoneyPool();
        broadcast(new Packet(PacketType.GAME, ch.toString() + " called"));
    }

    public void handlerFold(ClientHandler ch) {
        ch.getPlayer().pass();
        broadcast(new Packet(PacketType.GAME, ch.toString() + " folded"));
    }

    public void updateMoneyPool() {
        moneyPool = 0;
        for (Player p :
                playersData) {
            moneyPool += p.curBid;
        }
    }

    public boolean canProceed() {
        boolean ret = true;
        for (Player p :
                playersData) {
            if (p.curBid < currentBid && !p.passedAway && p.money > 0) {
                ret = false;
                break;
            }
        }
        return ret;
    }

    private ArrayList<ClientHandler> Showdown() {
        ClientHandler highestHand = players.get(0);
        for (ClientHandler ch :
                players) {
            if (!ch.getPlayer().passedAway &&
                    highestHand.getPlayer().pokerHand.compareWith(ch.getPlayer().pokerHand)
                            == PokerHand.Result.LOSS) {
                highestHand = ch;
            }
        }
        ArrayList<ClientHandler> ret = new ArrayList<>();
        for (ClientHandler ch :
                players) {
            if (!ch.getPlayer().passedAway &&
                    highestHand.getPlayer().pokerHand.compareWith(ch.getPlayer().pokerHand)
                            == PokerHand.Result.TIE)
                ret.add(ch);

        }
        return ret;
    }


    boolean nextPlayer = false;

    Talia talia;


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

    public void broadcast(Packet packet) {
        for (ClientHandler ch : players) {
            System.out.println("ch" + ch.toString());
            ch.sendPacket(packet);
        }
    }


    private void deal() throws InterruptedException {
        this.tableCards = new ArrayList<>();
        talia = new Talia(false);
        talia.SzuflujTalie();

        List<Player> otherPlayers = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            otherPlayers.clear();
            ClientHandler ch = players.get(i);
            for (int j = i + 1; j < players.size(); j++) {
                otherPlayers.add(playersData.get(j));
            }
            for (int k = 0; k < i; k++) {
                otherPlayers.add(playersData.get(k));
            }

            System.out.println("Dla:" + ch.getPlayer().getPlayerData());
            for (Player p : otherPlayers) {
                System.out.println(p.getPlayerData());
            }

            System.out.println("Liczba graczy");
            ch.sendPacket(new GamePacket("Game Starting",
                    GamePacket.Status.START, ch.getPlayer(), otherPlayers));
        }

        for (ClientHandler ch : players) {
            ch.getPlayer().passedAway = false;
            ch.getPlayer().clearHand();
            ch.getPlayer().setMoney(1000);
        }

        for (ClientHandler ch : players) {
            Karta card = talia.KartaZTalii();
            ch.sendPacket(new GamePacket("FirstHandCard",
                    GamePacket.Status.FIRST_HAND_CARD, card));
            ch.getPlayer().setCard1(card);
        }

        for (ClientHandler ch : players) {
            Karta card = talia.KartaZTalii();
            ch.sendPacket(new GamePacket("SecondHandCard",
                    GamePacket.Status.SECOND_HAND_CARD, card));
            ch.getPlayer().setCard2(card);
        }

        for (int i = 0; i < 5; i++) {
            this.tableCards.add(talia.KartaZTalii());
        }

        for (ClientHandler ch : players) {
            ch.getPlayer().pokerHand.addHand(tableCards);
        }
    }

    @Override
    public ArrayList<ClientHandler> call() throws Exception { //rozdanie

        deal();

        Thread.sleep(1000);
        handlerRaise(players.get(0), 5);
        players.get(0).sendPacket(new GamePacket("small blind",
                GamePacket.Status.SMALL_BLIND, moneyPool));
        for (ClientHandler ch : players) {
            if (!ch.equals(players.get(0))) {
                ch.sendPacket(new GamePacket("someone put small blind",
                        GamePacket.Status.SMALL_BLIND, players.get(0).getPlayer()));
            }
        }
        Thread.sleep(2000);
        handlerRaise(players.get(1), 10);
        players.get(1).sendPacket(new GamePacket("big blind",
                GamePacket.Status.BIG_BLIND, moneyPool));
        for (ClientHandler ch : players) {
            if (!ch.equals(players.get(1))) {
                ch.sendPacket(new GamePacket("someone put small blind",
                        GamePacket.Status.BIG_BLIND, players.get(1).getPlayer()));
            }
        }
        for (int i = 2; i < playersData.size(); i++) {
            players.get(i).sendPacket(new GamePacket("your move",
                    GamePacket.Status.MOVE,
                    GamePacket.MOVE_TYPE.CALL, currentBid));
            lock.lock();
            while (!nextPlayer) {
                next.await();
            }
            lock.unlock();
            nextPlayer = false;
        }
        turn();
        for (int i = 1; i <= 3; i++) {
            broadcast(new GamePacket("środkowe karta:",
                    GamePacket.Status.TABLE_CARDS, i, tableCards.get(i - 1)));
            Thread.sleep(1500);
        }
        turn();
        int temp = players.size();
        for (ClientHandler ch : players) {
            if (ch.getPlayer().passedAway) temp--;
        }
        if (temp <= 1) return Showdown();
        broadcast(new GamePacket("środkowe karta:",
                GamePacket.Status.TABLE_CARDS, 4, tableCards.get(3)));
        Thread.sleep(1500);
        turn();
        temp = players.size();
        for (ClientHandler ch : players) {
            if (ch.getPlayer().passedAway) temp--;
        }
        if (temp <= 1) return Showdown();
        broadcast(new GamePacket("środkowe karta:",
                GamePacket.Status.TABLE_CARDS, 5, tableCards.get(4)));
        Thread.sleep(1500);
        turn();
//
//        for (int i = 0; i < 3; i++){
//            System.out.println("runda :" + i+1);
//            for (ClientHandler ch : players){
//                System.out.println(ch.getPlayer().getPlayerData());
//
//                ch.sendPacket(new GamePacket("your move", GamePacket.Status.MOVE));
//                for (ClientHandler otherCh : players) {
//                    if (!otherCh.equals(ch)) {
//                        otherCh.sendPacket(new GamePacket("other player move", GamePacket.Status.MOVE, ch.getPlayer()));
//                    }
//                }
//                lock.lock();
//                while (!nextPlayer) {
//                    next.await();
//                }
//                lock.unlock();
//                nextPlayer = false;
//            }
//            System.out.println("dalsze działanie");
//            if (i == 2) break;
//            broadcast(new GamePacket("środkowe karta:", GamePacket.Status.TABLE_CARDS, i + 4, tableCards.get(i+3)));
//            Thread.sleep(1500);
//        }
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
        players.addLast(players.removeFirst());
        return Showdown();
    }

    private void turn() throws InterruptedException {
        while (!canProceed()) {
            for (int i = 0; i < playersData.size(); i++) {
                players.get(i).sendPacket(new GamePacket("your move", GamePacket.Status.MOVE, GamePacket.MOVE_TYPE.CALL, currentBid));
                lock.lock();
                while (!nextPlayer) {
                    next.await();
                }
                lock.unlock();
                nextPlayer = false;
            }
        }
    }


    public void unlockLock() {
        this.nextPlayer = true;
        lock.lock();
        this.next.signal();
        lock.unlock();
    }
}
