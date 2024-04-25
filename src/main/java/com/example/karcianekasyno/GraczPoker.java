package com.example.karcianekasyno;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class GraczPoker extends Thread {
    private static final String SERWER_IP = "localhost";
    public static final int SERWER_PORT = 9090;
    private ArrayList<Karta> reka;
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(SERWER_IP, SERWER_PORT);
        BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            System.out.println("> ");
            String command = keyboard.readLine();

            if (command.equals("wychodze")) break;

            out.println(command);

            String serverResponse = input.readLine();
            System.out.println("Serwer muwi: " + serverResponse);
        }

    }


}