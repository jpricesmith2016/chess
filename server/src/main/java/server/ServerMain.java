package server;

import chess.*;
import dataaccess.*;
import handler.*;
import io.javalin.Javalin;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ServerMain {

    final AuthDAO authDAO = new MemoryAuthDAO();
    final UserDAO userDAO = new MemoryUserDAO();
    final GameDAO gameDAO = new MemoryGameDAO();

    final AuthService authService = new AuthService(authDAO, userDAO);
    final UserService userService = new UserService(userDAO);
    final GameService gameService = new GameService(gameDAO, authDAO);


    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        int requestedPort = Integer.parseInt(args[0]);
        var server = new ServerMain();
        int port = server.run(requestedPort);

    }

    public int run(int requestedPort) {
        Javalin javalinServer = Javalin.create();
        createHandlers(javalinServer);
        return javalinServer.port();
    }

    private void createHandlers(Javalin javalinServer) {
//        javalinServer.delete("/db", new DbClearHandler());
//        javalinServer.post("/user", new UserRegHandler());
//        javalinServer.post("/session", new UserLoginHandler());
//        javalinServer.delete("/session", new UserLogoutHandler());
//        javalinServer.get("/game", new GameListHandler());
//        javalinServer.post("/game", new GameCreateHandler());
        javalinServer.put("/game", new GameJoinHandler(gameService, authService));

    }
}
