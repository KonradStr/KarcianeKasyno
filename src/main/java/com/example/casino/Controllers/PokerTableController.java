package com.example.casino.Controllers;

import com.example.casino.Main;
import com.example.casino.Packets.GamePacket;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class PokerTableController {
    @FXML
    private Button next;

    @FXML
    public void nextPlayer(){
        Main.client.sendPacket(new GamePacket("next", GamePacket.Status.MOVE));
    }
}
