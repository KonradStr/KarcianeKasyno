package com.example.casino.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private ServerSocket serverSocket;
    public static String connectionUrl = "jdbc:mysql://i3m.h.filess.io:3307/JavaProject_fourthtalk";
    public static Connection connection;

    static {
        try {
            connection = DriverManager.getConnection(GameServer.connectionUrl,
                    "JavaProject_fourthtalk", "26c741dadf126863995c714674f8a4c681c4dfb3");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, PokerGame> pokerGames = new HashMap<>();
    public static Map<String, RummyGame> rummyGames = new HashMap<>();

    public static ExecutorService executorService;

    public void start(int port) throws IOException {
        executorService = Executors.newFixedThreadPool(100);
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
