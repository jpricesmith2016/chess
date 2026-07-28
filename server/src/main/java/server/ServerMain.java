package server;

import chess.*;
import dataaccess.*;
import handler.*;
import io.javalin.Javalin;
import service.AuthService;
import service.GameService;
import service.UserService;
import websocketserver.WsGameHandler;

public class ServerMain {

    private static AuthDAO authDAO;
    private static UserDAO userDAO;
    private static GameDAO gameDAO;

    final AuthService authService = new AuthService(authDAO, userDAO);
    final UserService userService = new UserService(userDAO);
    final GameService gameService = new GameService(gameDAO, authDAO);
    private final Javalin javalinServer;
    private final WsGameHandler webSocketHandler;


    public static void main(String[] args) {
        try {
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
            int requestedPort = 8080;
            if (args.length >= 1) {
                requestedPort = Integer.parseInt(args[0]);
            }

            var server = new ServerMain();
            int port = server.run(requestedPort);
            System.out.printf("Server started on port %d%n", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }

    public ServerMain() {
        javalinServer = Javalin.create(config -> config.staticFiles.add("web"));
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();

        try {
            authDAO = new SQLAuthDAO();
            userDAO = new SQLUserDAO();
            gameDAO = new SQLGameDAO();
        } catch (Exception e){
            System.out.printf("Unable to configure Databases: %s%n", e.getMessage());
        }

        webSocketHandler = new WsGameHandler(authDAO, gameDAO);

        javalinServer.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
        createHandlers();
    }

    public int run(int requestedPort) {
        javalinServer.start(requestedPort);
        return javalinServer.port();
    }

    private void createHandlers() {
        javalinServer.delete("/db", new DbClearHandler(gameService, authService, userService));
        javalinServer.post("/user", new UserRegHandler(userService, authService));
        javalinServer.post("/session", new UserLoginHandler(authService, userService));
        javalinServer.delete("/session", new UserLogoutHandler(authService));
        javalinServer.get("/game", new GameListHandler(gameService, authService));
        javalinServer.post("/game", new GameCreateHandler(gameService, authService));
        javalinServer.put("/game", new GameJoinHandler(gameService, authService));

    }
}
