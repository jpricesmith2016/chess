package client;

import requestresult.*;

import java.util.Arrays;

import static ui.EscapeSequences.ERASE_SCREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY;

public class LoginClient {

    private static ServerFacade httpClient;
    private static ChessRepl chessRepl;
    private static ChessClient chessClient;

    public LoginClient(ServerFacade httpClient, ChessRepl repl, ChessClient client) {
        LoginClient.httpClient = httpClient;
        chessRepl = repl;
        chessClient = client;
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
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[LOGGED_OUT] >>> ");
    }

    String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
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
                chessRepl.setAuthState(ChessRepl.State.LOGGED_IN);
                chessClient.setUsername(params[0]);
                chessClient.setAuthToken(result.authToken());
                return "logged in as " + params[0];
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD>");
        }
    }

    private String register(String[] params) throws Exception {
        if (params.length >= 2) {
            RegisterResult result = httpClient.register(new RegisterRequest(params[0], params[1], params[2]));
            if (result.resultCode() == 200) {
                chessRepl.setAuthState(ChessRepl.State.LOGGED_IN);
                ChessClient.username = params[0];
                ChessClient.authToken = result.returnAuth().authToken();
                System.out.print(ERASE_SCREEN);
                return "logged in as " + params[0];
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD> <EMAIL>");
        }
    }
}
