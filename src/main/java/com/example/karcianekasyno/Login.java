package com.example.karcianekasyno;

import javafx.fxml.FXML;

import java.io.IOException;

public class Login {

    @FXML
    public void goToMainMenu() throws IOException {
        KarcianeKasyno m = new KarcianeKasyno();
        m.changeScene("mainMenu.fxml");
    }
}
