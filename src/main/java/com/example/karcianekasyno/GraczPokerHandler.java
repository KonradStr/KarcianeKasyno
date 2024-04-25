package com.example.karcianekasyno;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class GraczPokerHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<GraczPokerHandler> gracze;

    public GraczPokerHandler(Socket clientSocket, ArrayList<GraczPokerHandler> wszyscyGracze) throws IOException {
        this.client = clientSocket;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.gracze = wszyscyGracze;
    }
    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                if (request.contains("siema"))
                {
                    out.println(SerwerPoker.helo()) ;
                    out.println("Napisz \"siema\" abym napisał ci siema");
                } else {
                    out.println("Napisz \"siema\" abym napisał ci siema");
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        } finally {
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

