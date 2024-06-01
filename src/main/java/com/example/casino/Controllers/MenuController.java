package com.example.casino.Controllers;

import com.example.casino.Main;
import com.example.casino.Packets.JoinGamePacket;
import com.example.casino.Packets.LoginPacket;
import com.example.casino.Packets.Packet;
import com.example.casino.Packets.PacketType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.UUID;

public class MenuController {

    @FXML
    private Button exit;
    @FXML
    private Button minimize;
    @FXML
    private Button CreateButton;

    @FXML
    private Button JoinButton;

    @FXML
    private Button LogoutButton;

    @FXML
    private TextField gameID;

    @FXML
    public void createGame(){
        String uniqueID = UUID.randomUUID().toString();
        System.out.println(uniqueID);
        Packet p = new Packet(PacketType.CREATEGAME, uniqueID);
        Main.client.sendPacket(p);
    }

    @FXML
    public void joinGame(){
        String uuid = gameID.getText();
        if (uuid.isEmpty()){
            System.out.println("tutaj error");
        }else {
            JoinGamePacket packet = new JoinGamePacket("JOIN", uuid, JoinGamePacket.Status.JOIN);
            Main.client.sendPacket(packet);
        }
    }

    @FXML
    public void LogOut(){
        Main.client.sendPacket(new LoginPacket("Loging out", LoginPacket.Status.LOGOUT));
    }

    @FXML
    public void closeWindow(){
        Stage stage = (Stage) exit.getScene().getWindow();
        //stage.setIconified(true);
        try {
            Main.client.stopConnection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Main.client.interrupt();
        System.out.println("zamykanie");
        stage.close();
        System.exit(1);
    }


    @FXML
    public void minimizeWindow(){
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }
}
