package client;

import chess.*;
import model.GameData;
import ui.EscapeSequences;

import java.util.*;

import static ui.EscapeSequences.*;

public class GameClient {

    private static ServerFacade httpClient;
    public static String team;
    private static GameData game;
    private static ChessBoard board;
    private static ChessRepl chessRepl;
    private static ChessClient chessClient;
    private static ChessBoard newBoard;
    private static String squareColor;
    private static String textColor;

    public enum GameState {
        IN_PROGRESS,
        WHITE_WIN,
        BLACK_WIN,
        STALEMATE
    }
    static GameState gameState = GameState.IN_PROGRESS;

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

    private final String[] helpString = {
            """
            Options:
            Make a move: "m", "move" <StartCol> <StartRow> <EndCol> <EndRow> <PromotionType>
            highlight legal moves: "h", "highlight" <Col> <Row>
            Redraw board: "r", "redraw"
            Leave the game: "l", "leave"
            Resign the game: "resign"
            Print this message: "help"
            """,
            """
            Options:
            highlight legal moves: "h", "highlight" <Col> <Row>
            Redraw board: "r", "redraw"
            Leave the game: "l", "leave"
            Print this message: "help"
            """
    };

    void printPrompt() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> " + RESET_TEXT_COLOR);
    }

    String printBoard(Collection<ChessMove> possibleMoves) {
        System.out.print(EscapeSequences.ERASE_SCREEN);

        squareColor = SET_BG_COLOR_DARK_GREY;
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
                    printSquare(row + 1,col + 1,possibleMoves);
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
                    printSquare(row + 1,col + 1,possibleMoves);
                }

                System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + " " + (row + 1) + " " + RESET_BG_COLOR);
            }

        }
        System.out.print(letterBorder);

        board = newBoard;
        if (gameState == GameState.IN_PROGRESS) {
            return (SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> Game_ID: " + game.gameID() + " Team: " + team
                    + " Turn: " + game.game().getTeamTurn().toString());
        } else {
            return (SET_TEXT_COLOR_MAGENTA + "\n[Chess_Game] >>> Game_ID: " + game.gameID()
                    + " THE GAME HAS CONCLUDED THE RESULT WAS: " + (gameState == GameState.STALEMATE ? "A STALEMATE"
                    : gameState == GameState.BLACK_WIN ? "BLACK'S WIN - " + game.blackUsername()
                    : "WHITE'S WIN - " + game.whiteUsername()));
        }
    }

    private void printSquare(int row, int col, Collection<ChessMove> possibleMoves) {
        String saveSquareColor = (squareColor.equals(SET_BG_COLOR_DARK_GREY)) ? SET_BG_COLOR_DARK_GREEN : SET_BG_COLOR_DARK_GREY;
        textColor = SET_TEXT_COLOR_WHITE;

        if (possibleMoves.isEmpty()) {
            squareColor = saveSquareColor;
        } else {
            for (ChessMove move : possibleMoves) {
                if (move.getStartPosition().getRow() == row && move.getStartPosition().getColumn() == col) {
                    textColor = SET_TEXT_COLOR_BLACK;
                    squareColor = SET_BG_COLOR_YELLOW;
                    break;
                } else if (move.getEndPosition().getRow() == row && move.getEndPosition().getColumn() == col){
                    textColor = SET_TEXT_COLOR_BLACK;
                    squareColor = SET_BG_COLOR_BLUE;
                    break;
                }
                squareColor = saveSquareColor;
            }
        }


        ChessPosition pos = new ChessPosition(row, col);

        boolean changed = !Objects.equals(
                newBoard.getPiece(pos),
                board.getPiece(pos)
        );

        System.out.print((changed ? SET_BG_COLOR_MAGENTA : squareColor) + getSquare(pos));
        squareColor = saveSquareColor;
    }

    private String getSquare(ChessPosition pos) {
        String output = textColor;
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
            if (gameState == GameState.IN_PROGRESS && !Objects.equals(team, "Observer")) {
                return switch (cmd) {
                    case "l", "leave" -> exit();
                    case "r", "redraw" -> printBoard(new ArrayList<>());
                    case "resign" -> resign();
                    case "h", "highlight" -> legalMoves(params);
                    case "m", "move" -> move(params);
                    default -> helpString[0];
                };
            } else {
                return switch (cmd) {
                    case "l", "leave" -> exit();
                    case "r", "redraw" -> printBoard(new ArrayList<>());
                    case "h", "highlight" -> legalMoves(params);
                    default -> helpString[1];
                };
            }

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private int colConvert(String col) throws Exception {
        return switch (col) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new Exception("Unexpected value: " + col + " Expected <Col> value a-h");
        };
    }

    private String legalMoves(String[] params) throws Exception {
        if (params.length == 2) {

            ChessPosition pos = new ChessPosition(Integer.parseInt(params[1]), colConvert(params[0]));

            if (game.game().getBoard().getPiece(pos) == null) {
                throw new Exception ("Incorrect Position: You selected an empty or invalid location");
            }

            Collection<ChessMove> moves = game.game().validMoves(pos);

            return printBoard(moves);

        } else {
            throw new Exception ("Incorrect parameters: Expected <Col> <Row>");
        }
    }

    private String resign() {
        System.out.print("You have selected to Resign, Please confirm by entering \"y/yes\": ");
        Scanner scanner = new Scanner(System.in);
        String req = scanner.nextLine();
        switch (req){
            case "y", "Y", "yes", "Yes" -> {
                gameState = (Objects.equals(team, "White") ? GameState.BLACK_WIN : GameState.WHITE_WIN);
                return "You have chosen to resign and Forfeit the game.";
            }
            default -> {
                return "You have chosen to back out of your Forfeit.";
            }
        }
    }

    private String move(String[] params) throws Exception {
        if (params.length == 4 || params.length == 5) {
            ChessPiece.PieceType type = null;
            ChessPosition startPos = new ChessPosition(Integer.parseInt(params[1]), colConvert(params[0]));
            ChessPosition endPos = new ChessPosition(Integer.parseInt(params[3]), colConvert(params[2]));
            if (params.length == 5) {
                switch (params[4]) {
                    case "Queen" -> type = ChessPiece.PieceType.QUEEN;
                    case "Rook" -> type = ChessPiece.PieceType.ROOK;
                    case "Knight" -> type = ChessPiece.PieceType.KNIGHT;
                    case "Bishop" -> type = ChessPiece.PieceType.BISHOP;
                    default -> throw new Exception("Invalid Promotion Value (Can be null): EX: Queen");
                }
            }

            ChessMove requestedMove = new ChessMove(startPos, endPos, type);

            game.game().makeMove(requestedMove);

            return "Move has been made" + printBoard(new ArrayList<>());
        } else {
            throw new Exception ("Incorrect parameters: Expected <StartCol> <StartRow> <EndCol> <EndRow> [PromotionPiece]");
        }
    }

    private String exit() {
        chessRepl.setAuthState(ChessRepl.State.LOGGED_IN);
        return "User " + chessClient.getUser() + " has exited the game";
    }
}
