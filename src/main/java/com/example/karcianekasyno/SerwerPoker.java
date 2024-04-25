package com.example.karcianekasyno;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SerwerPoker {
    public static final int PORT = 9090;
    private static ArrayList<GraczPokerHandler> gracze = new ArrayList<>();
    private static ExecutorService basen = Executors.newFixedThreadPool(100);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);


        while(true) {
            System.out.println("[SERWERPOKER] oczekiwanie na połączenie...");
            Socket gracz = listener.accept();
            System.out.println("[SERWERPOKER] połączono!");
            GraczPokerHandler graczThread = new GraczPokerHandler(gracz, gracze);
            gracze.add(graczThread);
            basen.execute(graczThread);
        }
    }
    private class Stol {
        private ArrayList<Integer> Zetony;
        private ArrayList<Gracz> Gracze;
        private Talia talia;
        protected ArrayList<Karta> KartyNaStole;
        protected void PolozKarte(Karta karta){
            KartyNaStole.add(karta);
        }
        public Stol(ArrayList<GraczPokerHandler> gracze) {
            this.talia = new Talia(false);
            talia.SzuflujTalie();
            for (GraczPokerHandler gr: gracze) {
                this.Gracze.add(new Gracz(gr));
            }
        }

    }
    private class Gracz {
        private ArrayList<Karta> reka;
        private GraczPokerHandler handler;
        int zetony;
        protected void dajKarte(Karta karta){
            reka.add(karta);
        }
        protected Karta oddajKarte(){
            return reka.removeLast();
        }
        public Gracz(GraczPokerHandler gracz) {
            this.handler = gracz;
        }

    }
    public static String helo() {
        return "No siema";
    }
}
