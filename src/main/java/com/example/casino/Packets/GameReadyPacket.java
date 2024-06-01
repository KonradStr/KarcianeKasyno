package com.example.casino.Packets;

import com.example.casino.Player;

public class GameReadyPacket extends Packet{
    private String UUID;
    private Player player;


    public enum Status{
        READY,
        NOT_READY,
    }

    private Status status;
    public GameReadyPacket(String data, Status status) {
        super(PacketType.GAME_READY_STATUS, data);
        this.status = status;
    }

    public GameReadyPacket(String data, Player player, String uuid, Status status) {
        super(PacketType.GAME_READY_STATUS, data);
        this.player = player;
        this.UUID = uuid;
        this.status = status;
    }

    public GameReadyPacket(String data, String uuid, Status status) {
        super(PacketType.GAME_READY_STATUS, data);
        this.UUID = uuid;
        this.status = status;
    }

    public String getUUID() {
        return UUID;
    }

    public Player getPlayer() {
        return player;
    }

    public Status getStatus() {
        return status;
    }
}
