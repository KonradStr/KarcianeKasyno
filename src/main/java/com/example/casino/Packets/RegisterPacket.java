package com.example.casino.Packets;

import com.example.casino.Player;

public class RegisterPacket extends Packet {

    private String email;
    private String date;
    private String login;
    private String password;
    private Player player;


    public enum Status {
        REGISTER,
        ACOUNT_ALREADY_EXISTS_ERROR,
    }

    private Status status;

    public RegisterPacket(String data, String email, String login, String password, String date, Status status) {
        super(PacketType.REGISTER, data);
        this.email = email;
        this.date = date;
        this.login = login;
        this.password = password;
        this.status = status;
    }

    public RegisterPacket(String data, Player player, Status status) {
        super(PacketType.REGISTER, data);
        this.player = player;
        this.status = status;
    }

    public RegisterPacket(String data, Status status) {
        super(PacketType.REGISTER, data);
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Player getPlayer() {
        return player;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public Status getStatus() {
        return status;
    }
}
