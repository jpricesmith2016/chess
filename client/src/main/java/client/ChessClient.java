package client;

import java.util.Arrays;

import model.GameData;
import ui.EscapeSequences;
import static ui.EscapeSequences.*;

public class ChessClient {

    private static ServerFacade httpClient;

    public ChessClient(ServerFacade httpClient) {
        ChessClient.httpClient = httpClient;
    }

    private final String helpString =
            """
            Options:
            Exit the program: "q", "quit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
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
}
