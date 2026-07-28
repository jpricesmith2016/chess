package server;

import chess.*;
import dataaccess.*;
import handler.*;

public class ServerMain {
    public static void main(String[] args) {
        try {
            var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            System.out.println("♕ 240 Chess Server: " + piece);
            int requestedPort = 8080;
            if (args.length >= 1) {
                requestedPort = Integer.parseInt(args[0]);
            }

            var server = new Server();
            int port = server.run(requestedPort);
            System.out.printf("Server started on port %d%n", port);
        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());
        }
    }
}
