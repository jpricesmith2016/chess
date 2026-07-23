package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREEN;
import static ui.EscapeSequences.SET_BG_COLOR_DARK_GREY;

public class GameClient {

    private static ServerFacade httpClient;
    public static String team;
    private static GameData game;
    private static ChessBoard board;
    private static ChessRepl chessRepl;
    private static ChessClient chessClient;
    private static ChessBoard newBoard;

    public GameClient(ServerFacade httpClient, ChessRepl chessRepl) {
        GameClient.httpClient = httpClient;
        GameClient.chessRepl = chessRepl;
    }

    void setChessClient (ChessClient client) {
        chessClient = client;
    }

    public void setGameInfo(GameData game, String team) {
        GameClient.game = game;
        GameClient.team = team;
        GameClient.board = game.game().getBoard();
    }

    private final String helpString =
            """
            Options:
            Exit the game: "e", "exit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.println(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> Game_ID: " + game.gameID() + " Team: " + team
                + " Turn: " + game.game().getTeamTurn().toString());

        printBoard();

        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> " + RESET_TEXT_COLOR);
    }

    void printBoard() {
        String squareColor = SET_BG_COLOR_DARK_GREY;
        newBoard = game.game().getBoard();

        String letterBorder;
        if (team.equalsIgnoreCase("White") || team.equalsIgnoreCase("Observer")) {

            letterBorder = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE
                    + "   " + "\u2003a " + "\u2003b " + "\u2003c " + "\u2003d "
                    + "\u2003e " + "\u2003f " + "\u2003g " + "\u2003h " + "   " + RESET_BG_COLOR + "\n";

            System.out.print(letterBorder);

            for (int row = 7; row >= 0; row--) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + (row + 1) + " ");

                squareColor = (squareColor.equals(SET_BG_COLOR_DARK_GREY)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;

                for (int col = 0; col < 8; col++) {
                    squareColor = (squareColor.equals(SET_BG_COLOR_DARK_GREY)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;
                    ChessPosition pos = new ChessPosition(row + 1, col + 1);

                    boolean changed = !Objects.equals(
                            newBoard.getPiece(pos),
                            board.getPiece(pos)
                    );

                    System.out.print((changed ? SET_BG_COLOR_MAGENTA : squareColor)
                            + getSquare(pos));
                }

                System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " " + (row + 1) + " " + RESET_BG_COLOR);
            }

        } else {

            letterBorder = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE
                    + "   " + "\u2003h " + "\u2003g " + "\u2003f " + "\u2003e "
                    + "\u2003d " + "\u2003c " + "\u2003b " + "\u2003a " + "   " + RESET_BG_COLOR + "\n";
            System.out.print(letterBorder);

            for (int row = 0; row < 8; row++) {
                System.out.print(SET_BG_COLOR_LIGHT_GREY + " " + (row + 1) + " ");

                squareColor = (squareColor.equals(SET_BG_COLOR_DARK_GREY)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;

                for (int col = 7; col >= 0; col--) {
                    squareColor = (squareColor.equals(SET_BG_COLOR_DARK_GREY)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;
                    ChessPosition pos = new ChessPosition(row + 1, col + 1);

                    boolean changed = !Objects.equals(
                            newBoard.getPiece(pos),
                            board.getPiece(pos)
                    );

                    System.out.print((changed ? SET_BG_COLOR_MAGENTA : squareColor)
                            + getSquare(pos));
                }

                System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " " + (row + 1) + " " + RESET_BG_COLOR);
            }

        }
        System.out.print(letterBorder);

        board = newBoard;
    }

    private String getSquare(ChessPosition pos) {
        String output = SET_TEXT_COLOR_WHITE;
        ChessPiece piece = newBoard.getPiece(pos);
        if (piece == null) {
            return output + "\u2003  ";
        }

        switch (piece.getPieceType()) {
            case KING -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KING : BLACK_KING);
            }
            case QUEEN -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_QUEEN : BLACK_QUEEN);
            }
            case BISHOP -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_BISHOP : BLACK_BISHOP);
            }
            case KNIGHT -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_KNIGHT : BLACK_KNIGHT);
            }
            case ROOK -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_ROOK : BLACK_ROOK);
            }
            case PAWN -> {
                return output + ((piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? WHITE_PAWN : BLACK_PAWN);
            }
        }
        return "";
    }

    String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "e", "exit" -> exit();
                default -> helpString;
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String exit() {
        chessRepl.setAuthState(ChessRepl.State.LOGGED_IN);
        return "User " + chessClient.getUser() + " has exited the game";
    }
}
