package com.example.casino.Server;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class FutureTaskCallback extends FutureTask<ArrayList<ClientHandler>> {
    public FutureTaskCallback(Callable<ArrayList<ClientHandler>> callable) {
        super(callable);
    }

    public void done(){
        if (isCancelled()) System.out.println("gra przerwana ;{");
        else{
            try {
                System.out.println("wynik z wątku wynikowego: " + get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
