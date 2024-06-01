package com.example.casino.Packets;

public class CreateGamePacket extends Packet{

    private String UUID;

    public CreateGamePacket(String data, String UUID) {
        super(PacketType.CREATEGAME, data);
        this.UUID = UUID;
    }
}
