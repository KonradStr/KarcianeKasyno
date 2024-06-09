package com.example.casino.Controllers;

import com.example.casino.Player;
import com.example.casino.Server.Rank;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PlayerFields {
    private Label money;
    private Label username;
    private ImageView firstCard;
    private ImageView secondCard;

    public PlayerFields(Label money, Label username, ImageView firstCard, ImageView secondCard) {
        this.money = money;
        this.username = username;
        this.firstCard = firstCard;
        this.secondCard = secondCard;
    }

    public void setPlayer(Player player, boolean back) {
        if (back) {
            this.username.setText(player.getPlayerData());
            this.money.setText(String.valueOf(player.getMoney()));
            this.firstCard.setImage(new Image(getClass().getResourceAsStream("/images/cards/back.png")));
            this.secondCard.setImage(new Image(getClass().getResourceAsStream("/images/cards/back.png")));
        } else {
            String rank = Rank.rank.get(player.getCard1().rank.toString());
            String color = player.getCard1().kolor.toString();
            this.firstCard.setImage(new Image(getClass().getResourceAsStream("/images/cards/" + rank + color + ".png")));
            rank = Rank.rank.get(player.getCard2().rank.toString());
            color = player.getCard2().kolor.toString();
            this.secondCard.setImage(new Image(getClass().getResourceAsStream("/images/cards/" + rank + color + ".png")));
        }
    }

    public void setMoney(String money) {
        this.money.setText(money);
    }

    public String getMoney() {
        return money.getText();
    }
}

