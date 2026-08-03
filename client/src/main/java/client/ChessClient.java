package client;

import java.util.ArrayList;
import java.util.Arrays;

import model.GameData;
import requestresult.*;
import websocketclient.WsCommunicator;

import static ui.EscapeSequences.*;

public class ChessClient {

    private static ServerFacade httpClient;
    static String authToken;
    static String username;
    private final String serverURL;
    private static ChessRepl chessRepl;
    private static GameClient gameClient;
    private static WsCommunicator webSocket;

    public ChessClient(ServerFacade httpClient, ChessRepl repl, GameClient gameClient, String serverURL) {
        ChessClient.httpClient = httpClient;
        ChessClient.gameClient = gameClient;
        chessRepl = repl;
        this.serverURL = serverURL;
    }

    String getUser() {
        return username;
    }

    void setUsername(String user) {
        username = user;
    }

    void setAuthToken(String auth) {
        authToken = auth;
    }

    private final String helpString =
            """
            Options:
            Create a new Game: "c", "create" <Name>
            List all current Games: "l", "list"
            Join an Existing Game: "j", "join" <ID> <white|black>
            Observe and Existing Game: "o", "observe" <ID>
            Delete Game: "d", "delete" <ID>
            logout of the Client: "logout"
            clear Games: "clear"
            Print this message: "h", "help"
            """;

    void printPrompt() {
        System.out.print(SET_TEXT_COLOR_LIGHT_GREY + "\n[LOGGED_IN] >>> ");
    }

    String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "c", "create" -> create(params);
                case "l", "list" -> list();
                case "j", "join" -> join(params);
                case "o", "observe" -> observe(params);
                case "d", "delete" -> delete(params);
                case "clear" -> clearDb();
                case "logout" -> logout();
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
                return "GameID " + result.gameID() + " Created";
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <Name>");
        }
    }

    private String delete(String[] params) throws Exception {
        if (params.length == 1) {
            LogoutResult result = httpClient.deleteGame(new DeleteRequest(Integer.parseInt(params[0])));
            if (result.resultCode() == 200) {
                return "Game deleted Successfully";
            } else {
                throw new Exception (result.message());
            }
        } else {
            throw new Exception ("Incorrect parameters: Expected <GameID>");
        }
    }
    private String list() throws Exception {
        ListGamesResult result = httpClient.listGames();
        if (result.resultCode() == 200) {
            ArrayList<GameData> gameList = new ArrayList<>(result.games());

            String title = "%-6s %-15s %-15s %-15s";
            StringBuilder output = new StringBuilder(String.format(title, "GameID", "GameName", "WhiteUser", "BlackUser") + "\n");
            String formatRule = "%6d %-15s %-15s %-15s";

            for (int i = 1; i <= gameList.size(); i++) {
                String nextRow = String.format(formatRule, i
                        , gameList.get(i-1).gameName(), gameList.get(i-1).whiteUsername(), gameList.get(i-1).blackUsername());
                output.append(nextRow).append("\n");
            }

            return output.toString();
        } else {
            throw new Exception (result.message());
        }
    }

    private String join(String[] params) throws Exception {
        if (params.length == 2) {

            int gameID;

            try {
                gameID = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new Exception ("Invalid ID format: Expected <int> parameter", e);
            }

            GameJoinResult result = httpClient.joinGame(new GameJoinRequest(params[1], gameID));

            if (result.resultCode() != 200) {
                throw new Exception (result.message());
            }

            ArrayList<GameData> gameList = new ArrayList<>(httpClient.listGames().games());

            for (int i = 0; i < gameList.size(); i++){
                if(i+1 == Integer.parseInt(params[0])) {
                    gameID = i+1;
                    webSocket = new WsCommunicator(serverURL, gameClient, gameID);
                    webSocket.connect(authToken);
                    gameClient.setGameInfo(gameList.get(i), params[1], webSocket);
                    chessRepl.setAuthState(ChessRepl.State.GAME);
                    return "GameID: " + gameID + " Joined as the " + params[1] + " player";
                }
            }
            return "Invalid GameID: GameID " + gameID + " does not exist";

        } else {
            throw new Exception ("Incorrect parameters: Expected <ID> <White|Black>");
        }
    }

    private String observe(String[] params) throws Exception {
        if (params.length == 1) {

            int gameID;

            try {
                gameID = Integer.parseInt(params[0]);
            } catch (Exception e){
                throw new Exception ("Invalid ID format: Expected <int> parameter", e);
            }

            ArrayList<GameData> gameList = new ArrayList<>(httpClient.listGames().games());

            for (int i = 0; i < gameList.size(); i++){
                if(i+1 == Integer.parseInt(params[0])) {
                    gameID = i+1;
                    webSocket = new WsCommunicator(serverURL, gameClient, gameID);
                    webSocket.connect(authToken);
                    gameClient.setGameInfo(gameList.get(i), "Observer", webSocket);
                    chessRepl.setAuthState(ChessRepl.State.GAME);
                    return username + " Joined GameID: " + gameID + " as an observer";
                }
            }
            return "Invalid GameID: GameID " + gameID + " does not exist";

        } else {
            throw new Exception ("Incorrect parameters: Expected <ID>");
        }
    }

    private String logout() throws Exception {
        LogoutResult result = httpClient.logout(new LogoutRequest(authToken));

        if (result.resultCode() != 200) {
            throw new Exception (result.message());
        }
        chessRepl.setAuthState(ChessRepl.State.LOGGED_OUT);
        return username + " has been logged out of their session";
    }

    private String clearDb() throws Exception {
        httpClient.clear();
        chessRepl.setAuthState(ChessRepl.State.LOGGED_OUT);
        return "DB has been cleared by user " + username;
    }
}
