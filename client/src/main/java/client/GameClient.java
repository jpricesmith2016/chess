package client;

import java.util.Arrays;

public class GameClient {

    private static ClientHttp httpClient;

    public GameClient(ClientHttp httpClient) {
        GameClient.httpClient = httpClient;
    }

    private final String helpString =
            """
            Options:
            Exit the game: "q", "quit"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print("\n[LOGGED_IN] >>> ");
    }

    private String eval(String input) {
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
