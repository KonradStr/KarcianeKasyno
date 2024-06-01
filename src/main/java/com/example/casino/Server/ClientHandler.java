package com.example.casino.Server;

import com.example.casino.*;
import com.example.casino.Packets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.*;

public class ClientHandler extends Thread {
    private Player player;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Connection connection;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            connection = DriverManager.getConnection(GameServer.connectionUrl, "JavaProject_fourthtalk", "26c741dadf126863995c714674f8a4c681c4dfb3");
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Błąd połączenia komunikacyjnego z klientem");
        }catch (SQLException e) {
            System.err.println("Błąd łączenia z bazą danych");
            System.out.println(e);
        }
    }

    public void run() {
        Packet request;
        while (true) {
            try {
                request = (Packet)in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Nie udało się odczytac pakietu");
                throw new RuntimeException(e);
            }

            if (request == null) break;
            parseRequest(request);
        }

        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseRequest(Packet request){
        switch(request.getType()) {
            case ACK:
                System.out.println("Dołączył nowy użytkownik: " + clientSocket.getInetAddress());
                sendPacket(new Packet(PacketType.ACK, "hello"));
                break;
            case LOGIN: {
                System.out.println("Prośba logowania: " + clientSocket.getInetAddress() );
                LoginPacket loginRequest = (LoginPacket)request;
                String desc = request.getDesc();
                String username = loginRequest.getLogin();
                String passw = loginRequest.getPassword();
                LoginPacket.Status status = loginRequest.getStatus();
                if (status.equals(LoginPacket.Status.LOGOUT)){
                    sendPacket(new LoginPacket("Loging out", LoginPacket.Status.LOGOUT));
                }else {
                    try {
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery("select UserID, Password from users where Username='" + username + "'");
                        if (rs.next()) {
                            Integer userID = rs.getInt(1);
                            String password = rs.getString(2);
                            System.out.println(password);
                            if (password.equals(passw)) {
                                player = new Player(userID, username, false);
                                sendPacket(new LoginPacket("Logged In", player, LoginPacket.Status.LOGIN));
                            } else {
                                sendPacket(new LoginPacket("PasswordError", LoginPacket.Status.PASWWORD_ERROR));
                            }
                        } else {
                            sendPacket(new LoginPacket("AccountNotFoundError", LoginPacket.Status.ACOUNT_NOT_FOUND_ERROR));
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            }
            case REGISTER: {
                System.out.println("Prośba rejestracji " + clientSocket.getInetAddress() );
                RegisterPacket registerRequest = (RegisterPacket) request;
                String data = registerRequest.getDesc();
                String email = registerRequest.getEmail();
                String username = registerRequest.getLogin();
                String date = registerRequest.getDate();
                String passw = registerRequest.getPassword();
                System.out.println(data);
                try {
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("select * from users where Email='" + email + "'");

                    if (rs.next()) {
                        sendPacket(new RegisterPacket("Account already exists", RegisterPacket.Status.ACOUNT_ALREADY_EXISTS_ERROR));
                    } else {
                        String sql = "INSERT INTO users (Username, Email, Password, DateOfBirth) VALUES (?,?,?,?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, username);
                        preparedStatement.setString(2, email);
                        preparedStatement.setString(3, passw);
                        preparedStatement.setString(4, date);
                        preparedStatement.execute();
                        st = connection.createStatement();
                        rs = st.executeQuery("select userID from users where Username='" + username + "'");
                        if (rs.next()) {
                            Integer userID = rs.getInt(1);
                            player = new Player(userID, username, false);
                            System.out.println("rejestracja jest spoko ok");
                        }
                        sendPacket(new RegisterPacket("Registered", player, RegisterPacket.Status.REGISTER));

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            }case CREATEGAME: {
                System.out.println("Tworzenie gry");
                String data = request.getDesc();
                GameServer.pokerGames.put(data, new PokerGame(data, this));
                System.out.println("Stworzono nową gre: " + data);
                sendPacket(new Packet(PacketType.CREATEGAME, "GameCreated:"+data));
                break;
            }case JOINGAME: {
                JoinGamePacket joinRequest = (JoinGamePacket)request;
                System.out.println("Dołączanie do gry");
                String data = joinRequest.getDesc();
                System.out.println(joinRequest.getUUID());
                JoinGamePacket.Status status = joinRequest.getStatus();
                PokerGame game = GameServer.pokerGames.get(joinRequest.getUUID());
                if (status.equals(JoinGamePacket.Status.LEAVE)){
                    System.out.println("user opuszcza lobby");
                    game.players.remove(this);
                    game.playersData.remove(this.player);
                    if (game.players.isEmpty()){
                        GameServer.pokerGames.remove(joinRequest.getUUID());
                    }else {
                        game.broadcast(new JoinGamePacket("USERLEFT", joinRequest.getUUID(), this.player, JoinGamePacket.Status.USER_LEFT));
                    }
                    sendPacket(new JoinGamePacket("LEFT", joinRequest.getUUID(), JoinGamePacket.Status.LEFT));
                }else {
                    game.broadcast(new JoinGamePacket("USERJOINED", joinRequest.getUUID(), this.player, JoinGamePacket.Status.USER_JOIN));
                    game.players.add(this);
                    game.playersData.add(this.player);
                    sendPacket(new JoinGamePacket("JOINED", joinRequest.getUUID(), game.playersData, JoinGamePacket.Status.JOINED));
                }
                break;
            }case GAME_READY_STATUS: {
                GameReadyPacket gameReadyRequest = (GameReadyPacket) request;
                String data = gameReadyRequest.getDesc();
                String uuid = gameReadyRequest.getUUID();
                GameReadyPacket.Status status = gameReadyRequest.getStatus();
                if(status.equals(GameReadyPacket.Status.READY)){
                    player.setReady(true);
                    GameServer.pokerGames.get(uuid).broadcast(new GameReadyPacket("ready", player, uuid, GameReadyPacket.Status.READY));
                }else{
                    player.setReady(false);
                    GameServer.pokerGames.get(uuid).broadcast(new GameReadyPacket("notready", player, uuid, GameReadyPacket.Status.NOT_READY));
                }
                break;
            }default:{
                System.err.println("NIEZNANY PAKIET");
            }
        }

    }

    public void sendPacket(Packet packet){
        try {
            out.writeObject(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Player getPlayer() {
        return player;
    }
}
