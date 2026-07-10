package server;

import chess.*;
import io.javalin.Javalin;

public class ServerMain {
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
        javalinServer.delete("/db", new dbClearHandler());
        javalinServer.post("/user", new userRegHandler());
        javalinServer.post("/session", new userLoginHandler());
        javalinServer.delete("/session", new userLogoutHandler());
        javalinServer.get("/game", new gameListHandler());
        javalinServer.post("/game", new gameCreateHandler());
        javalinServer.put("/game", new gameJoinHandler());

    }
}
