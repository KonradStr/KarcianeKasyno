package com.example.karcianekasyno;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class RemikMain {
    @FXML // Player card slots
    ImageView pc1, pc2, pc3, pc4, pc5, pc6, pc7, pc8, pc9, pc10, pc11, pc12, pc13, pc14;

    boolean pc1Chosen = false, pc2Chosen = false, pc3Chosen = false, pc4Chosen = false, pc5Chosen = false, pc6Chosen = false, pc7Chosen = false, pc8Chosen = false, pc9Chosen = false, pc10Chosen = false, pc11Chosen = false, pc12Chosen = false, pc13Chosen = false, pc14Chosen = false;


    @FXML // Opponent1 card slots
    ImageView o1c1, o1c2, o1c3, o1c4, o1c5, o1c6, o1c7, o1c8, o1c9, o1c10, o1c11, o1c12, o1c13, o1c14;

    @FXML // Opponent2 card slots
    ImageView o2c1, o2c2, o2c3, o2c4, o2c5, o2c6, o2c7, o2c8, o2c9, o2c10, o2c11, o2c12, o2c13, o2c14;

    @FXML // Opponent3 card slots
    ImageView o3c1, o3c2, o3c3, o3c4, o3c5, o3c6, o3c7, o3c8, o3c9, o3c10, o3c11, o3c12, o3c13, o3c14;

    @FXML
    ImageView deckTop;

    @FXML
    Button drawCardButton, takeFromTopButton, tryToLayOffButton, finishLayOffButton, addToLayOffButton, cancelLayOffButton, confirmDiscardButton;

    @FXML
    Label cardsLeft;

    boolean[] pcChosenArray = {pc1Chosen, pc2Chosen, pc3Chosen, pc4Chosen, pc5Chosen, pc6Chosen, pc7Chosen, pc8Chosen, pc9Chosen, pc10Chosen, pc11Chosen, pc12Chosen, pc13Chosen, pc14Chosen};

    RemikPlayer player1;
    RemikDeck deck;

    public void startGameTest() {
        deck = new RemikDeck();
        player1 = new RemikPlayer("player1");
        RemikPlayer player2 = new RemikPlayer("player2");
        RemikPlayer player3 = new RemikPlayer("player3");
        RemikPlayer player4 = new RemikPlayer("player4");

        dealCards(deck, player1);
        dealCards(deck, player2);
        dealCards(deck, player3);
        dealCards(deck, player4);

        displayCards(player1);
        displayCards(player2);
        displayCards(player3);
        displayCards(player4);

        deckTop.setImage(new Image(getClass().getResourceAsStream("/cards/back.png")));

        cardsLeft.setText("Cards left in deck: " + deck.cardsLeftInDeck());

        routine();
    }

    private void displayCards(RemikPlayer player) {
        for (int i = 0; i < player.getCardsOnHand().size(); i++) {
            RemikCard card = player.getCardsOnHand().get(i);
            String imagePath;
            if (player.getName().equals("player1")) {
                imagePath = getImagePath(card);
            } else {
                imagePath = "/cards/back.png";
                //imagePath = getImagePath(card);
            }

            ImageView imageView;
            switch (player.getName()) {
                case "player1":
                    imageView = getPlayerImageViewByIndex(i + 1);
                    imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                    break;
                case "player2":
                    imageView = getOpponent1ImageViewByIndex(i + 1);
                    Image rotatedImage1 = rotateImage(new Image(getClass().getResourceAsStream(imagePath)), 90);
                    imageView.setImage(rotatedImage1);
                    break;
                case "player3":
                    imageView = getOpponent2ImageViewByIndex(i + 1);
                    imageView.setImage(new Image(getClass().getResourceAsStream(imagePath)));
                    break;
                case "player4":
                    imageView = getOpponent3ImageViewByIndex(i + 1);
                    Image rotatedImage3 = rotateImage(new Image(getClass().getResourceAsStream(imagePath)), 270);
                    imageView.setImage(rotatedImage3);
                    break;
            }
        }
    }

    private void dealCards(RemikDeck deck, RemikPlayer player) {
        for (int i = 0; i < 13; i++) { //potem 13
            player.addCard(deck.dealOne());
        }
        player.sortCardsBySortValue();
    }

    private String getImagePath(RemikCard card) {
        String imagePath;

        String suit = card.getSuit().toLowerCase();
        suit = suit.substring(0, suit.length() - 1);

        if ((card.getValue() >= 2 && card.getValue() <= 9) || card.getRank().equals("Ten")) {
            imagePath = "/cards/" + card.getValue() + suit + ".png";
        } else {
            imagePath = "/cards/" + card.getRank().charAt(0) + suit + ".png";
        }

        return imagePath;
    }

    private ImageView getPlayerImageViewByIndex(int index) {
        return switch (index) {
            case 1 -> pc1;
            case 2 -> pc2;
            case 3 -> pc3;
            case 4 -> pc4;
            case 5 -> pc5;
            case 6 -> pc6;
            case 7 -> pc7;
            case 8 -> pc8;
            case 9 -> pc9;
            case 10 -> pc10;
            case 11 -> pc11;
            case 12 -> pc12;
            case 13 -> pc13;
            case 14 -> pc14;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    private ImageView getOpponent1ImageViewByIndex(int index) {
        return switch (index) {
            case 1 -> o1c1;
            case 2 -> o1c2;
            case 3 -> o1c3;
            case 4 -> o1c4;
            case 5 -> o1c5;
            case 6 -> o1c6;
            case 7 -> o1c7;
            case 8 -> o1c8;
            case 9 -> o1c9;
            case 10 -> o1c10;
            case 11 -> o1c11;
            case 12 -> o1c12;
            case 13 -> o1c13;
            case 14 -> o1c14;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    private ImageView getOpponent2ImageViewByIndex(int index) {
        return switch (index) {
            case 1 -> o2c1;
            case 2 -> o2c2;
            case 3 -> o2c3;
            case 4 -> o2c4;
            case 5 -> o2c5;
            case 6 -> o2c6;
            case 7 -> o2c7;
            case 8 -> o2c8;
            case 9 -> o2c9;
            case 10 -> o2c10;
            case 11 -> o2c11;
            case 12 -> o2c12;
            case 13 -> o2c13;
            case 14 -> o2c14;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    private ImageView getOpponent3ImageViewByIndex(int index) {
        return switch (index) {
            case 1 -> o3c1;
            case 2 -> o3c2;
            case 3 -> o3c3;
            case 4 -> o3c4;
            case 5 -> o3c5;
            case 6 -> o3c6;
            case 7 -> o3c7;
            case 8 -> o3c8;
            case 9 -> o3c9;
            case 10 -> o3c10;
            case 11 -> o3c11;
            case 12 -> o3c12;
            case 13 -> o3c13;
            case 14 -> o3c14;
            default -> throw new IllegalArgumentException("Invalid index: " + index);
        };
    }

    private Image rotateImage(Image image, double angle) {
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        WritableImage rotatedImage = new WritableImage(height, width);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedImage.getPixelWriter().setColor(y, width - 1 - x, image.getPixelReader().getColor(x, y));
            }
        }
        return rotatedImage;
    }

    @FXML
    private void pc1Clicked(MouseEvent event) {
        toggleCardSelection(1, pc1);
    }

    @FXML
    private void pc2Clicked(MouseEvent event) {
        toggleCardSelection(2, pc2);
    }

    @FXML
    private void pc3Clicked(MouseEvent event) {
        toggleCardSelection(3, pc3);
    }

    @FXML
    private void pc4Clicked(MouseEvent event) {
        toggleCardSelection(4, pc4);
    }

    @FXML
    private void pc5Clicked(MouseEvent event) {
        toggleCardSelection(5, pc5);
    }

    @FXML
    private void pc6Clicked(MouseEvent event) {
        toggleCardSelection(6, pc6);
    }

    @FXML
    private void pc7Clicked(MouseEvent event) {
        toggleCardSelection(7, pc7);
    }

    @FXML
    private void pc8Clicked(MouseEvent event) {
        toggleCardSelection(8, pc8);
    }

    @FXML
    private void pc9Clicked(MouseEvent event) {
        toggleCardSelection(9, pc9);
    }

    @FXML
    private void pc10Clicked(MouseEvent event) {
        toggleCardSelection(10, pc10);
    }

    @FXML
    private void pc11Clicked(MouseEvent event) {
        toggleCardSelection(11, pc11);
    }

    @FXML
    private void pc12Clicked(MouseEvent event) {
        toggleCardSelection(12, pc12);
    }

    @FXML
    private void pc13Clicked(MouseEvent event) {
        toggleCardSelection(13, pc13);
    }

    @FXML
    private void pc14Clicked(MouseEvent event) {
        toggleCardSelection(14, pc14);
    }

    private void toggleCardSelection(int cardIndex, ImageView cardView) {
        boolean isSelected = !pcChosenArray[cardIndex - 1];
        pcChosenArray[cardIndex - 1] = isSelected;
        handlePcClick(cardView, isSelected);
    }

    private void handlePcClick(ImageView pc, boolean chosen) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), pc);
        transition.setToY(chosen ? -20 : 0);
        transition.play();
    }

    private boolean onlyOneCardUp() {
        int count = 0;
        for (boolean b : pcChosenArray) {
            if (b) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return count == 1;
    }

    @FXML
    private void drawCard(ActionEvent event) {
        RemikCard drawnCard = deck.dealOne();
        player1.addCard(drawnCard);
        player1.sortCardsBySortValue();
        displayCards(player1);
        cardsLeft.setText("Cards left in deck: " + deck.cardsLeftInDeck());
        routine();
    }

    @FXML
    private void takeFromTop(ActionEvent event) {
        if(!deck.getDiscardedCards().isEmpty()){
            player1.addCard(deck.discardedCards.removeLast());
            player1.sortCardsBySortValue();
            displayCards(player1);
            cardsLeft.setText("Cards left in deck: " + deck.cardsLeftInDeck());
            routine();
        }
    }

    public void tryToLayOff(ActionEvent event) {

    }

    public void finishLayOff(ActionEvent event) {
        for (int i = 0; i < pcChosenArray.length; i++) {
            RemikCard card = player1.getCardsOnHand().size() > i ? player1.getCardsOnHand().get(i) : null;
            if (pcChosenArray[i]) {
                System.out.println("pc" + (i + 1) + ": " + (card != null ? card.getRank() + " of " + card.getSuit() : "No card") + ", Chosen: true");
            } else {
                if (card != null) {
                    System.out.println("pc" + (i + 1) + ": " + card.getRank() + " of " + card.getSuit() + ", Chosen: false");
                } else {
                    System.out.println("pc" + (i + 1) + ": No card, Chosen: false");
                }
            }
        }
        System.out.println(player1.getCardsOnHand().size());
    }

    public void addToLayOff(ActionEvent event) {

    }

    public void cancelLayOff(ActionEvent event) {
        System.out.println(deck.getDiscardedCards());
    }

    @FXML
    private void confirmDiscard(ActionEvent event) {
        discardSelectedCard();
        routine();
    }

    private void routine(){
        if (deck.isDeckEmpty()) {
            deck.refillDeckFromDiescardedCards();
        }

        String topCard;
        ImageView topImage = deckTop;
        if(!deck.getDiscardedCards().isEmpty()){
            topCard = getImagePath(deck.getDiscardedCards().getLast());
        } else {
            topCard = "/cards/back.png";
        }
        topImage.setImage(new Image(getClass().getResourceAsStream(topCard)));


        updateButtonsVisibility();

    }

    private void updateButtonsVisibility() {
        if (player1.getCardsOnHand().size() == 13) {
            drawCardButton.setVisible(true);
            takeFromTopButton.setVisible(true);
            confirmDiscardButton.setVisible(false);
        } else if (player1.getCardsOnHand().size() == 14) {
            drawCardButton.setVisible(false);
            takeFromTopButton.setVisible(false);
            confirmDiscardButton.setVisible(true);
        }
    }

    private void discardSelectedCard() {
        if (onlyOneCardUp()) {
            for (int i = 0; i < pcChosenArray.length; i++) {
                if (pcChosenArray[i]) {
                    removeCardFromHand(i + 1);
                    break;
                }
            }
        }
    }

    private void removeCardFromHand(int viewIndex) {
        int cardIndex = viewIndex - 1;
        if (cardIndex >= 0 && cardIndex < player1.getCardsOnHand().size()) {
            ImageView cardView = getPlayerImageViewByIndex(viewIndex);
            resetCardPosition(cardView, () -> {
                deck.discardedCards.add(player1.getCardsOnHand().remove(cardIndex));
                cardView.setImage(null);
                cardView.setTranslateY(0);

                pcChosenArray[cardIndex] = false;

                for (int i = cardIndex; i < player1.getCardsOnHand().size(); i++) {
                    ImageView currentView = getPlayerImageViewByIndex(i + 1);
                    ImageView nextView = getPlayerImageViewByIndex(i + 2);
                    if (nextView.getImage() != null) {
                        currentView.setImage(nextView.getImage());
                        currentView.setTranslateY(0);
                        pcChosenArray[i] = pcChosenArray[i + 1];
                    } else {
                        currentView.setImage(null);
                    }
                }

                ImageView lastView = getPlayerImageViewByIndex(player1.getCardsOnHand().size() + 1);
                lastView.setImage(null);
                lastView.setTranslateY(0);

                routine();
            });
        } else {
            System.out.println("Index out of bounds: " + viewIndex);
        }
    }

    private void resetCardPosition(ImageView cardView, Runnable onFinished) {
        TranslateTransition transition = new TranslateTransition(Duration.seconds(0.3), cardView);
        transition.setToY(0);
        transition.setOnFinished(event -> {
            onFinished.run();
            updateButtonsVisibility();
        });
        transition.play();
    }
}