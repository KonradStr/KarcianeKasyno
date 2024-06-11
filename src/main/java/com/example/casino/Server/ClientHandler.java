package com.example.casino.Server;

import com.example.casino.*;
import com.example.casino.Packets.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.example.casino.Server.GameServer.connection;
import static com.example.casino.Server.GameServer.furas;

public class ClientHandler extends Thread {
    private Player player;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String UUID;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.err.println("Błąd połączenia komunikacyjnego z klientem");
        }
    }

    public PokerHand.Ranks hand() {
        return player.pokerHand.rank();
    }

    public void run() {
        Packet request;
        while (true) {
            try {
                request = (Packet) in.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Nie udało się odczytac pakietu");
                GameServer.removeNick(this.player.getPlayerData());
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

    private void parseRequest(Packet request) {
        switch (request.getType()) {
            case ACK:
                System.out.println("Dołączył nowy użytkownik: " + clientSocket.getInetAddress());
                sendPacket(new Packet(PacketType.ACK, "hello"));
                break;
            case LOGIN: {
                System.out.println("Prośba logowania: " + clientSocket.getInetAddress());
                LoginPacket loginRequest = (LoginPacket) request;
                String desc = request.getDesc();
                String username = loginRequest.getLogin();
                String passw = loginRequest.getPassword();
                LoginPacket.Status status = loginRequest.getStatus();
                if (status.equals(LoginPacket.Status.LOGOUT)) {
                    sendPacket(new LoginPacket("Loging out", LoginPacket.Status.LOGOUT));
                    GameServer.removeNick(this.player.getPlayerData());
                } else {
                    try {
                        Statement st = connection.createStatement();
                        ResultSet rs = st.executeQuery("select UserID, Password, Salt from users where Username='"
                                + username + "'");
                        if (rs.next()) {
                            Integer userID = rs.getInt(1);
                            String password = rs.getString(2);
                            String salt = rs.getString(3);
                            System.out.println(password);
                            if (PassHash.comparePasswd(passw, salt, password) && GameServer.checkAvailability(username)) {
                                player = new Player(userID, username, false);
                                GameServer.addNick(username);
                                sendPacket(new LoginPacket("Logged In", player, LoginPacket.Status.LOGIN));
                                sendPacket(new LoginPacket("PasswordError", LoginPacket.Status.PASWWORD_ERROR));
                            } else {
                                sendPacket(new LoginPacket("PasswordError", LoginPacket.Status.PASWWORD_ERROR));
                            }
                        } else {
                            sendPacket(new LoginPacket("AccountNotFoundError",
                                    LoginPacket.Status.ACOUNT_NOT_FOUND_ERROR));
                        }
                    } catch (SQLException | InvalidKeySpecException | NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            }
            case REGISTER: {
                System.out.println("Prośba rejestracji " + clientSocket.getInetAddress());
                RegisterPacket registerRequest = (RegisterPacket) request;
                String data = registerRequest.getDesc();
                String email = registerRequest.getEmail();
                String username = registerRequest.getLogin();
                String date = registerRequest.getDate();
                String passw = registerRequest.getPassword();   //zahashowane
                String slt = registerRequest.getSalt();
                System.out.println(data);
                try {
                    Statement st = connection.createStatement();
                    ResultSet rs = st.executeQuery("select * from users where Email='" + email + "'");
                    //TODO: ResultSet rs2 = st.executeQuery("select * from users where Username='" + username + "'");

                    if (rs.next()) {
                        sendPacket(new RegisterPacket("Account already exists",
                                RegisterPacket.Status.ACOUNT_ALREADY_EXISTS_ERROR));
                    } else {
                        String sql = "INSERT INTO users (Username, Email, Password, Salt, DateOfBirth) " +
                                "VALUES (?,?,?,?,?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(sql);
                        preparedStatement.setString(1, username);
                        preparedStatement.setString(2, email);
                        preparedStatement.setString(3, passw);
                        preparedStatement.setString(4, slt);
                        preparedStatement.setString(5, date);
                        preparedStatement.execute();
                        st = connection.createStatement();
                        rs = st.executeQuery("select userID from users where Username='" + username + "'");
                        if (rs.next()) {
                            Integer userID = rs.getInt(1);
                            player = new Player(userID, username, false);
                            System.out.println("rejestracja jest spoko ok");
                            GameServer.addNick(username);
                        }
                        sendPacket(new RegisterPacket("Registered", player, RegisterPacket.Status.REGISTER));

                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            }
            case CREATEGAME: {
                System.out.println("Tworzenie gry");
                CreateGamePacket createGamePacket = (CreateGamePacket) request;
                String data = createGamePacket.getDesc();
                CreateGamePacket.GameType gameType = createGamePacket.getGameType();
                String UUID = createGamePacket.getUUID();
                this.UUID = createGamePacket.getUUID();
                if (gameType.equals(CreateGamePacket.GameType.POKER)) {
                    GameServer.pokerGames.put(UUID, new PokerGame(UUID, this));
                    System.out.println("Stworzono nową gre pokera: " + UUID);
                    sendPacket(new CreateGamePacket("GameCreated:" + UUID, UUID,
                            CreateGamePacket.GameType.POKER));
                } else {
                    GameServer.rummyGames.put(UUID, new RummyGame(UUID, this));
                    System.out.println("Stworzono nową gre remika: " + UUID);
                    sendPacket(new CreateGamePacket("GameCreated:" + UUID, UUID,
                            CreateGamePacket.GameType.RUMMY));
                }
                break;
            }
            case JOINGAME: {
                JoinGamePacket joinRequest = (JoinGamePacket) request;
                System.out.println("Dołączanie do gry");
                System.out.println(joinRequest.getUUID());
                this.UUID = joinRequest.getUUID();
                JoinGamePacket.GameType gameType = joinRequest.getGameType();
                JoinGamePacket.Status status = joinRequest.getStatus();
                System.out.println(gameType);
                if (gameType.equals(JoinGamePacket.GameType.POKER)) {
                    PokerGame game = GameServer.pokerGames.get(joinRequest.getUUID());
                    if (status.equals(JoinGamePacket.Status.LEAVE)) {
                        System.out.println("user opuszcza lobby");
                        game.players.remove(this);
                        game.playersData.remove(this.player);
                        if (game.players.isEmpty()) {
                            GameServer.pokerGames.remove(joinRequest.getUUID());
                        } else {
                            game.broadcast(new JoinGamePacket("USERLEFT", joinRequest.getUUID(),
                                    JoinGamePacket.GameType.POKER, this.player, JoinGamePacket.Status.USER_LEFT));
                        }
                        sendPacket(new JoinGamePacket("LEFT", joinRequest.getUUID(),
                                JoinGamePacket.GameType.POKER, JoinGamePacket.Status.LEFT));
                    } else {
                        game.broadcast(new JoinGamePacket("USERJOINED", joinRequest.getUUID(),
                                JoinGamePacket.GameType.POKER, this.player, JoinGamePacket.Status.USER_JOIN));
                        game.players.add(this);
                        game.playersData.add(this.player);
                        sendPacket(new JoinGamePacket("JOINED", joinRequest.getUUID(),
                                JoinGamePacket.GameType.POKER, game.playersData, JoinGamePacket.Status.JOINED));
                    }
                } else {
                    RummyGame game = GameServer.rummyGames.get(joinRequest.getUUID());
                    if (status.equals(JoinGamePacket.Status.LEAVE)) {
                        System.out.println("user opuszcza lobby");
                        game.players.remove(this);
                        game.playersData.remove(this.player);
                        if (game.players.isEmpty()) {
                            GameServer.rummyGames.remove(joinRequest.getUUID());
                        } else {
                            game.broadcast(new JoinGamePacket("USERLEFT", joinRequest.getUUID(),
                                    JoinGamePacket.GameType.RUMMY, this.player, JoinGamePacket.Status.USER_LEFT));
                        }
                        sendPacket(new JoinGamePacket("LEFT", joinRequest.getUUID(),
                                JoinGamePacket.GameType.RUMMY, JoinGamePacket.Status.LEFT));
                    } else {
                        game.broadcast(new JoinGamePacket("USERJOINED", joinRequest.getUUID(),
                                JoinGamePacket.GameType.RUMMY, this.player, JoinGamePacket.Status.USER_JOIN));
                        game.players.add(this);
                        game.playersData.add(this.player);
                        sendPacket(new JoinGamePacket("JOINED", joinRequest.getUUID(),
                                JoinGamePacket.GameType.RUMMY, game.playersData, JoinGamePacket.Status.JOINED));
                    }
                }
                break;
            }
            case GAME_READY_STATUS: {
                GameReadyPacket gameReadyRequest = (GameReadyPacket) request;
                String data = gameReadyRequest.getDesc();
                String uuid = gameReadyRequest.getUUID();
                GameReadyPacket.Status status = gameReadyRequest.getStatus();
                GameReadyPacket.GameType gameType = gameReadyRequest.getGameType();
                if (gameType.equals(GameReadyPacket.GameType.POKER)) {
                    if (status.equals(GameReadyPacket.Status.READY)) {
                        System.out.println(++GameServer.pokerGames.get(uuid).playersReady);
                        player.setReady(true);
                        GameServer.pokerGames.get(uuid).broadcast(new GameReadyPacket("ready", gameType,
                                player, uuid, GameReadyPacket.Status.READY));
                        if (GameServer.pokerGames.get(uuid).playersReady == 2) {

                            furas.add(GameServer.executorService.submit(GameServer.pokerGames.get(uuid)));
                        }
                    } else {
                        GameServer.pokerGames.get(uuid).playersReady--;
                        player.setReady(false);
                        GameServer.pokerGames.get(uuid).broadcast(new GameReadyPacket("notready", gameType,
                                player, uuid, GameReadyPacket.Status.NOT_READY));
                    }
                } else {
                    if (status.equals(GameReadyPacket.Status.READY)) {
                        System.out.println(++GameServer.rummyGames.get(uuid).playersReady);
                        player.setReady(true);
                        GameServer.rummyGames.get(uuid).broadcast(new GameReadyPacket("ready", gameType,
                                player, uuid, GameReadyPacket.Status.READY));
                        if (GameServer.rummyGames.get(uuid).playersReady == 2) {
                            Future<Player> future = GameServer.executorService.submit(GameServer.rummyGames.get(uuid));
                            try {
                                System.out.println(future.get().getPlayerData());
                            } catch (InterruptedException | ExecutionException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        GameServer.rummyGames.get(uuid).playersReady--;
                        player.setReady(false);
                        GameServer.rummyGames.get(uuid).broadcast(new GameReadyPacket("notready", gameType,
                                player, uuid, GameReadyPacket.Status.NOT_READY));
                    }
                }
                break;
            }
            case GAME: {
                GamePacket gamePacket = (GamePacket) request;
                GamePacket.Status gamePacketStatus = gamePacket.getStatus();
                System.out.println("odebrano pakiet game");
                if (gamePacketStatus.equals(GamePacket.Status.MOVE)) {
                    System.out.println("odebrano pakiet move");
                    System.out.println("1.");
                    switch (gamePacket.getMove_type()){
                        case FOLD -> GameServer.pokerGames.get(UUID).handlerFold(this);
                        case CALL -> GameServer.pokerGames.get(UUID).handlerCall(this);
                        case RAISE -> GameServer.pokerGames.get(UUID).handlerRaise(this, 50);
                    }
                    GameServer.pokerGames.get(this.UUID).unlockLock();
                    System.out.println("2.");
                }
                break;

            }
            default: {
                System.err.println("NIEZNANY PAKIET");
            }
        }

    }


    public void sendPacket(Packet packet) {
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
