package com.example.karcianekasyno;

import javafx.fxml.FXML;

import java.io.IOException;

public class MainMenu {
    @FXML
    public void goToPokerMain() throws IOException {
        KarcianeKasyno m = new KarcianeKasyno();
        m.changeScene("pokerMain.fxml");
    }

    @FXML
    public void goToRemikMenu() throws IOException {
        KarcianeKasyno m = new KarcianeKasyno();
        m.changeScene("remikMain.fxml");
    }
}
