package client;

import java.util.Scanner;

import ui.EscapeSequences;

public class ChessRepl {

    private boolean quit;
    private static String serverURL;
    private static ServerFacade httpClient;
    private static LoginClient loginClient;
    private static ChessClient chessClient;
    private static GameClient gameClient;
    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        GAME
    }
    public static State authState;
    private Scanner scanner;

    public ChessRepl(String serverURL) {
        ChessRepl.serverURL = serverURL;
        httpClient = new ServerFacade(serverURL);
        loginClient = new LoginClient(httpClient);
        chessClient = new ChessClient(httpClient);
        gameClient = new GameClient(httpClient);
        quit = false;
        authState = State.LOGGED_OUT;
    }

    public void run() {

        String req = null;
        scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {

            switch (authState) {
                case LOGGED_OUT -> loginClient.printPrompt();
            }

            req = scanner.nextLine();

            try {
                switch (authState) {
                    case LOGGED_OUT -> result = loginClient.eval(req);
                    case LOGGED_IN -> result = chessClient.eval(req);
                    case GAME -> result = gameClient.eval(req);
                }
                System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED + msg);
            }

            System.out.println();
        }
    }

}
