package client;

import chess.*;

public class ClientMain {

    private static ChessRepl repl;

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        try {
            repl = new ChessRepl(serverUrl);
        } catch (Throwable e) {
            System.out.printf("Unable to start client: %s%n", e.getMessage());
        }

        try {
            repl.run();
        } catch (Throwable e) {
            System.out.printf("Client Crashed During runtime: %s%n", e.getMessage());
        }
    }
}
