package com.example.casino.Packets;

public class GamePacket extends Packet{

    public GamePacket(String data) {
        super(PacketType.GAME, data);
    }

    public GamePacket(String data, Status status) {
        super(PacketType.GAME, data);
        this.status = status;
    }

    public enum Status{
        START,
        HAND_CARDS,
        TABLE_CARDS,
        MOVE
    }
    private Status status;

    public Status getStatus() {
        return status;
    }
}
