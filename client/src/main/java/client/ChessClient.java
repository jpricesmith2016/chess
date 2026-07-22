package client;

import java.net.http.HttpClient;
import java.util.Arrays;

public class ChessClient {

    private static ClientHttp httpClient;

    public ChessClient(ClientHttp httpClient) {
        ChessClient.httpClient = httpClient;
    }

    private final String helpString =
            """
            Options:
            Login as an existing user: "l", "login" <USERNAME> <PASSWORD>
            Register a new user: "r", "register" <USERNAME> <PASSWORD> <EMAIL>
            Exit the program: "q", "quit"
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
