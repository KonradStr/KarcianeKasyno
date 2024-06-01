package com.example.casino.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;

public class GameServer {
    private ServerSocket serverSocket;
    public static String connectionUrl = "jdbc:mysql://i3m.h.filess.io:3307/JavaProject_fourthtalk";
    public static Map<String, PokerGame> pokerGames = new HashMap<>();

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("---------- URUCHOMIONO SERWER ----------\n");
        System.out.println("ADRES IP SERWERA: " + serverSocket.getInetAddress());
        System.out.println("PORT: " + serverSocket.getLocalPort());
        while(true) {
            new ClientHandler(serverSocket.accept()).start();
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        try {
            gs.start(1234);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
