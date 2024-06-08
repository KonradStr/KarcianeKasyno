package com.example.casino.Packets;

import com.example.casino.Player;
import com.example.casino.Server.Karta;

import java.util.List;

public class GamePacket extends Packet{



    public enum Status{
        START,
        FIRST_HAND_CARD,
        SECOND_HAND_CARD,
        TABLE_CARDS,
        MOVE,
        SMALL_BLIND,
        BIG_BLIND
    }
    private Status status;
    private Player player;
    private int moneyPool;

    private Karta card;
    private List<Player> players;
    public GamePacket(String data) {
        super(PacketType.GAME, data);
    }

    public GamePacket(String data, Status status) {
        super(PacketType.GAME, data);
        this.status = status;
    }

    public GamePacket(String data, Status status, Player player,List<Player> players) {
        super(PacketType.GAME, data);
        this.status = status;
        this.players = players;
        this.player = player;
    }

    public GamePacket(String data, Status status, Player player) {
        super(PacketType.GAME, data);
        this.status = status;
        this.player = player;
    }

    public GamePacket(String data, Status status, Karta card) {
        super(PacketType.GAME, data);
        this.status = status;
        this.card = card;
    }

    public Status getStatus() {
        return status;
    }

    public Karta getCard() {
        return card;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public GamePacket(String data, Status status, int moneyPool) {
        super(PacketType.GAME, data);
        this.status = status;
        this.moneyPool = moneyPool;
    }

    public Player getPlayer() {
        return player;
    }
}
