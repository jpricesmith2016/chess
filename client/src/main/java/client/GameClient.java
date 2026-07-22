package client;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class GameClient {

    private static ServerFacade httpClient;

    public GameClient(ServerFacade httpClient) {
        GameClient.httpClient = httpClient;
    }

    private final String helpString =
            """
            Options:
            Exit the game: "q", "quit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[LOGGED_IN] >>> ");
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
