package client;

import java.util.Arrays;

import requestresult.*;
import requestresult.LoginResult;
import ui.EscapeSequences;
import static ui.EscapeSequences.*;

public class ChessClient {

    private static ServerFacade httpClient;
    static String authToken;
    static String username;

    public ChessClient(ServerFacade httpClient) {
        ChessClient.httpClient = httpClient;
    }

    private final String helpString =
            """
            Options:
            Create a new Game: "c", "create" <Name>
            List all current Games: "l", "list"
            Join an Existing Game: "j", "join" <ID> <white|black>
            Observe and Existing Game: "o", "observe" <ID>
            logout of the Client: "logout"
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
                case "c", "create" -> create(params);
                case "q", "quit" -> "quit";
                default -> helpString;
            };

        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private String create(String[] params) throws Exception {
        if (params.length == 1) {
            CreateResult result = httpClient.createGame(new CreateRequest(params[0]));
            if (result.resultCode() == 200) {

            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <USERNAME> <PASSWORD>");
        }
        return null;
    }
}
