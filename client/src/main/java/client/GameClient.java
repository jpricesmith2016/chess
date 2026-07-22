package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;

import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_GREEN;

public class GameClient {

    private static ServerFacade httpClient;
    public static String username;
    public static boolean whiteTeam;
    private static GameData game;
    private static ChessBoard board;

    public GameClient(ServerFacade httpClient) {
        GameClient.httpClient = httpClient;
    }

    public void setGameInfo(GameData game, String user, Boolean whiteTeam) {
        GameClient.game = game;
        GameClient.username = user;
        GameClient.whiteTeam = whiteTeam;
    }

    private final String helpString =
            """
            Options:
            Exit the program: "q", "quit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> Game_ID: " + game.gameID());

        printBoard();

        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> " + RESET_TEXT_COLOR);
    }

    void printBoard() {
        String squareColor = SET_BG_COLOR_GREEN;
        ChessBoard newBoard = game.game().getBoard();

        if (whiteTeam) {

            System.out.println(SET_BG_COLOR_LIGHT_GREY + " abcdefgh ");

            for (int row = 7; row >= 0; row--) {
                System.out.print((row + 1) + " ");

                for (int col = 0; col < 8; col++) {
                    squareColor = (squareColor.equals(SET_BG_COLOR_GREEN)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    ChessPosition pos = new ChessPosition(row, col);
                    System.out.print((newBoard.getPiece(pos) != board.getPiece(pos) ? SET_BG_COLOR_MAGENTA : squareColor)
                            + getSquare(pos));
                }

                System.out.println(SET_BG_COLOR_LIGHT_GREY + (row + 1));
            }

            System.out.println(SET_BG_COLOR_LIGHT_GREY + " abcdefgh " + RESET_BG_COLOR);

        } else {

            System.out.println(SET_BG_COLOR_LIGHT_GREY + " hgfedcba ");

            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");

                for (int col = 7; col >= 0; col--) {
                    squareColor = (squareColor.equals(SET_BG_COLOR_GREEN)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_GREEN;
                    ChessPosition pos = new ChessPosition(row, col);
                    System.out.print((newBoard.getPiece(pos) != board.getPiece(pos) ? SET_BG_COLOR_MAGENTA : squareColor)
                            + getSquare(pos));
                }

                System.out.println(SET_BG_COLOR_LIGHT_GREY + (row + 1));
            }

            System.out.println(SET_BG_COLOR_LIGHT_GREY + " hgfedcba " + RESET_BG_COLOR);
        }

        board = newBoard;
    }

    private String getSquare(ChessPosition pos) {
        ChessPiece piece = board.getPiece(pos);

        switch (piece.getPieceType()) {
            case KING -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING;
            }
            case QUEEN -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN;
            }
            case BISHOP -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP;
            }
            case KNIGHT -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT;
            }
            case ROOK -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK;
            }
            case PAWN -> {
                return (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN;
            }
        }
        return " ";
    }

    String eval(String input) {
        try {
            var tokens = input.toLowerCase().split("");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "q", "quit" -> "quit";
                default -> helpString;
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
