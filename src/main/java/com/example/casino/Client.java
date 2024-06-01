package com.example.casino;

import com.example.casino.Controllers.LobbyController;
import com.example.casino.Controllers.LoginController;
import com.example.casino.Controllers.MenuController;
import com.example.casino.Controllers.RegisterController;
import com.example.casino.Packets.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client extends Thread{

    private Socket clientSocket;
    public ObjectOutputStream out;
    private ObjectInputStream in;
    private LoginController lc;
    private LobbyController lbc;
    private MenuController mc;
    private RegisterController rc;


    public void run(){
        try {
            sendPacket(new Packet(PacketType.ACK, "hello"));
            Packet respone;
            while(clientSocket.isConnected()){
                try {
                    respone = (Packet) in.readObject();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (respone == null) break;
                parseRespone(respone);
            }
            stopConnection();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseRespone(Packet respone){
        switch(respone.getType()) {
            case ACK:
                System.out.println("Ustanowiono połączenie z serwerem");
                break;
            case LOGIN: {
                LoginPacket loginRespone = (LoginPacket)respone;
                String data = loginRespone.getDesc();
                LoginPacket.Status status = loginRespone.getStatus();
                if (status.equals(LoginPacket.Status.LOGIN)) {
                    System.out.println("Zalogowano");
                    System.out.println(data);
                    Main.player = loginRespone.getPlayer();
                    Platform.runLater(() ->
                            openMenu()
                    );
                }else if (status.equals(LoginPacket.Status.LOGOUT)){
                        System.out.println("Wylogowano");
                        Platform.runLater(() ->
                                logOut()
                        );
                } else if (status.equals(LoginPacket.Status.PASWWORD_ERROR)) {
                    System.out.println(data);
                    Platform.runLater(() ->
                            showLoginErr("PODANO NIEPOPRAWNE HASŁO")
                    );
                }else if(status.equals(LoginPacket.Status.ACOUNT_NOT_FOUND_ERROR)){
                    System.out.println(data);
                    Platform.runLater(() ->
                            showLoginErr("NIE ISTNIEJE TAKIE KONTO")
                    );
                }else{
                    System.err.println("NIE ZNANE DANE: " + data);
                }
                break;
            }
            case REGISTER: {
                RegisterPacket registerRespone = (RegisterPacket) respone;
                String data = registerRespone.getDesc();
                RegisterPacket.Status status = registerRespone.getStatus();
                if (status.equals(RegisterPacket.Status.REGISTER)){
                    System.out.println("Zarejestrowano");
                    Main.player = registerRespone.getPlayer();
                    Platform.runLater(() ->
                            openMenu()
                    );
                } else if (status.equals(RegisterPacket.Status.ACOUNT_ALREADY_EXISTS_ERROR)) {
                    System.out.println("To konto już istnieje");
                    Platform.runLater(() ->
                            showRegisterErr("TAKIE KONTO JUŻ ISTNIEJE")
                    );
                }else{
                    System.err.println("NIE ZNANE DANE: " + data);
                }
                break;
            }case CREATEGAME:{
                String data = respone.getDesc();
                if (data.split(":")[0].equals("GameCreated")){
                    System.out.println("utworzono gre");
                    Platform.runLater(() ->
                            openLobby(data.split(":")[1], new ArrayList<>(List.of(Main.player)))
                    );
                }
                break;
            }case JOINGAME:{
                JoinGamePacket joinGamePacket = (JoinGamePacket) respone;
                String data = joinGamePacket.getDesc();
                JoinGamePacket.Status status = joinGamePacket.getStatus();
                if (status.equals(JoinGamePacket.Status.JOINED)){
                    System.out.println("dołączono do gry");
                    Platform.runLater(() ->
                            openLobby(joinGamePacket.getUUID(), joinGamePacket.getPlayers())
                    );
                } else if (status.equals(JoinGamePacket.Status.USER_JOIN)) {
                    System.out.println("Dołączył nowy gracz");
                    System.out.println("Player:" + joinGamePacket.getPlayer().getPlayerData());
                    Platform.runLater(() ->
                            refreshLobby(true, joinGamePacket.getPlayer())
                    );
                }else if (status.equals(JoinGamePacket.Status.LEFT)) {
                    System.out.println("Opuszczono lobby");
                    Platform.runLater(() ->
                            leaveLobby()
                    );
                } else if (status.equals(JoinGamePacket.Status.USER_LEFT)) {
                    System.out.println("użytkownik opuścił lobby");
                    Platform.runLater(() ->
                            refreshLobby(false, joinGamePacket.getPlayer())
                    );
                }
                break;
            }case GAME_READY_STATUS: {
                GameReadyPacket gameReadyPacket = (GameReadyPacket) respone;
                GameReadyPacket.Status status = gameReadyPacket.getStatus();
                if (status.equals(GameReadyPacket.Status.READY)){
                    Platform.runLater(() ->
                            changeStatus(true, gameReadyPacket.getPlayer())
                    );
                }else{
                    Platform.runLater(() ->
                            changeStatus(false, gameReadyPacket.getPlayer())
                    );
                }
                break;
            }default:{
                System.err.println("NIEZNANY PAKIET");
            }
        }
    }



    private void logOut(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            this.lc = loader.getController();
            Main.stage.close();
            Main.stage.setTitle("Login");
            Main.stage.setScene(new Scene(root));
            Main.stage.show();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("error in loading table");
        }

    }
    private void openMenu(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Menu.fxml"));
            Parent root = loader.load();
            this.mc = loader.getController();
            Main.stage.close();
            Main.stage.setTitle("Menu");
            Main.stage.setScene(new Scene(root));
            Main.stage.show();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("error in loading table");
        }

    }


    private void openLobby(String uuid, List<Player> players){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Lobby.fxml"));
            Parent root = loader.load();
            this.lbc = loader.getController();
            Main.stage.close();
            Main.stage.setTitle("Lobby");
            Main.stage.setScene(new Scene(root));
            Main.stage.show();
            lbc.setUuid(uuid);
            for (Player p : players) {
                lbc.addPlayer(p);
            }
            lbc.refreshPlayerContainer();
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("error in loading table");
        }

    }

    private void refreshLobby(boolean join, Player player){
        if (join) {
            lbc.addPlayer(player);
            lbc.refreshPlayerContainer();
        }else{
            lbc.removePlayer(player);
            lbc.refreshPlayerContainer();
        }
    }

    private void leaveLobby(){
        lbc = null;
        openMenu();
    }

    private void changeStatus(boolean isReady, Player player){
        lbc.changeStatus(isReady, player);
        lbc.refreshPlayerContainer();
    }



    private void showLoginErr(String error){
        lc.enableErr(error);
    }

    private void showRegisterErr(String error){
         rc.enableErr(error);
    }

    public void sendPacket(Packet packet){
        try {
            out.writeObject(packet);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public Client() {
        try {
            clientSocket = new Socket("127.0.0.1", 1234);
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setLoginController(LoginController lc){
        this.lc = lc;
    }

    public void setRegisterController(RegisterController rc){
        this.rc = rc;
    }
}
