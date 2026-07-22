package client;

import requestresult.*;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class LoginClient {

    private static ServerFacade httpClient;

    public LoginClient(ServerFacade httpClient) {
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
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[LOGGED_IN] >>> ");
    }

    String eval(String input) {
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

    private String login(String[] params) throws Exception {
        if (params.length == 2) {
            LoginResult result = httpClient.login(new LoginRequest(params[0], params[1]));
            if (result.resultCode() == 200) {
                ChessRepl.authState = ChessRepl.State.LOGGED_IN;
                ChessClient.username = params[0];
                ChessClient.authToken = result.authToken();
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD>");
        }
        return null;
    }

    private String register(String[] params) throws Exception {
        if (params.length >= 2) {
            RegisterResult result = httpClient.register(new RegisterRequest(params[0], params[1], params[2]));
            if (result.resultCode() == 200) {
                ChessRepl.authState = ChessRepl.State.LOGGED_IN;
                ChessClient.username = params[0];
                ChessClient.authToken = result.returnAuth().authToken();
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
        return null;
    }
}
