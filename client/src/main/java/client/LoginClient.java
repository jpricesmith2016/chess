package client;

import java.util.Arrays;

public class LoginClient {

    private static ClientHttp httpClient;

    public LoginClient(ClientHttp httpClient) {
        LoginClient.httpClient = httpClient;
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

    private String read() {
        return null;
    }

    private String loggedOutEval(String input) {
        try {
            var tokens = input.toLowerCase().split("");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "l", "login" -> login(params);
                case "r", "register" -> register(params);
                case "q", "quit" -> "quit";
                default -> helpString;
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String loggedInEval(String input) {

    }

    private void printEval(String output) {

    }

    private String login(String[] params) throws Exception {
        if (params.length == 2) {
            ChessRepl.authState = ChessRepl.State.LOGGED_IN;
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD>");
        }
        return null;
    }

    private String register(String[] params) throws Exception {

    }


}
