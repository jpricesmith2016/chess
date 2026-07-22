package client;

import java.util.Scanner;

public class ChessRepl {

    private boolean quit;
    private static String serverURL;
    private static ClientHttp httpClient;
    private static LoginClient loginClient;
    private static ChessClient chessClient;
    private static GameClient gameClient;
    public enum State {
        LOGGED_OUT,
        LOGGED_IN
    }
    public static State authState;
    private Scanner scanner;

    public ChessRepl(String serverURL) {
        ChessRepl.serverURL = serverURL;
        httpClient = new ClientHttp(serverURL);
        loginClient = new LoginClient(httpClient);
        chessClient = new ChessClient(httpClient);
        gameClient = new GameClient(httpClient);
        quit = false;
        authState = State.LOGGED_OUT;
    }

    public boolean run() {

        String req = null;
        String evalOut = null;
        scanner = new Scanner(System.in);

        while (!quit) {

            switch (authState) {
                case LOGGED_OUT -> loginClient.printPrompt();
            }

            req = read();

            switch (authState) {
                case LOGGED_OUT -> evalOut = loggedOutEval(req);
                case LOGGED_IN -> evalOut = loggedInEval(req);
            }

            printEval(evalOut);
        }
        return quit;

    }

}
