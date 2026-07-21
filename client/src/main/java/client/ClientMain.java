package client;

import chess.*;

public class ClientMain {

    private static String username;
    private static String authToken;

    public static void main(String[] args) {
        String serverUrl = "http://localhost:8080";

        if (args.length == 1) {
            serverUrl = args[0];
        }

        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        boolean quit = false;
        
        ChessClient client = null;

        try {
            client = new ChessClient(serverUrl);
        } catch (Throwable e) {
            System.out.printf("Unable to start client: %s%n", e.getMessage());
        }

        try {
            quit = client.run();
        } catch (Throwable e) {
            quit = true;
            System.out.printf("Client Crashed During runtime: %s%n", e.getMessage());
        }
    }
}
