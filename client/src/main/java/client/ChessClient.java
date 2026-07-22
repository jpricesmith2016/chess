package client;

import java.util.Arrays;

import ui.EscapeSequences;
import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class ChessClient {

    private static ServerFacade httpClient;
    public static int gameID;
    public static String username;
    public static boolean whiteTeam;

    public ChessClient(ServerFacade httpClient) {
        ChessClient.httpClient = httpClient;
    }

    public void setGameInfo(int gameID, String user, Boolean whiteTeam) {
        ChessClient.gameID = gameID;
        ChessClient.username = user;
        ChessClient.whiteTeam = whiteTeam;
    }

    private final String helpString =
            """
            Options:
            Exit the program: "q", "quit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print(EscapeSequences.ERASE_SCREEN);
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> ");

        if (whiteTeam) {

            System.out.println("  a b c d e f g h");

            for (int row = 7; row >= 0; row--) {
                System.out.print((row + 1) + " ");

                for (int col = 0; col < 8; col++) {
                    drawSquare(getBoard[row][col], row, col);
                }

                System.out.println(" " + (row + 1));
            }

            System.out.println("  a b c d e f g h");

        } else {

            System.out.println("  h g f e d c b a");

            for (int row = 0; row < 8; row++) {
                System.out.print((row + 1) + " ");

                for (int col = 7; col >= 0; col--) {
                    drawSquare(getBoard[row][col], row, col);
                }

                System.out.println(" " + (row + 1));
            }

            System.out.println("  h g f e d c b a");
        }


        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[Chess_Game] >>> ");
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

    private static int[][] transpose(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        // Create a new matrix with swapped dimensions
        int[][] result = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[j][i] = matrix[i][j]; // Swap indices
            }
        }
        return result;
    }
}
