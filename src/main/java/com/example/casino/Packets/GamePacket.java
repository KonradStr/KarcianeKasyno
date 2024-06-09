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
    public enum MOVE_TYPE{
        CALL,
        CHECK
    }
    private MOVE_TYPE move_type;
    private Status status;
    private Player player;
    private int moneyPool;
    private Integer cardindex;
    private Integer currentBid;

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

    public GamePacket(String data, Status status, MOVE_TYPE move_type) {
        super(PacketType.GAME, data);
        this.status = status;
        this.move_type = move_type;
    }
    public GamePacket(String data, Status status, MOVE_TYPE move_type, Integer currentBid) {
        super(PacketType.GAME, data);
        this.status = status;
        this.move_type = move_type;
        this.currentBid = currentBid;
    }
    public GamePacket(String data, Status status, Player player) {
        super(PacketType.GAME, data);
        this.status = status;
        this.player = player;
    }

    public GamePacket(String data, Status status, Integer cardIndex, Karta card) {
        super(PacketType.GAME, data);
        this.status = status;
        this.card = card;
        this.cardindex = cardIndex;
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

    public Integer getCardindex() {
        return cardindex;
    }

    public MOVE_TYPE getMove_type() {
        return move_type;
    }

    public Integer getCurrentBid() {
        return currentBid;
    }
}
