package client;

import java.util.Scanner;

import ui.EscapeSequences;

public class ChessRepl {

    private static LoginClient loginClient;
    private static ChessClient chessClient;
    private static GameClient gameClient;
    public enum State {
        LOGGED_OUT,
        LOGGED_IN,
        GAME
    }
    private static State authState;

    public ChessRepl(String serverURL) {
        ServerFacade httpClient = new ServerFacade(serverURL);
        gameClient = new GameClient(httpClient, this);
        chessClient = new ChessClient(httpClient, this, gameClient, serverURL);
        loginClient = new LoginClient(httpClient, this, chessClient);
        gameClient.setChessClient(chessClient);
        authState = State.LOGGED_OUT;
    }

    public void setAuthState(State state) {
        authState = state;
    }

    public void run() {

        String req;
        Scanner scanner = new Scanner(System.in);

        var result = "";

        while (!result.equals("quit")) {

            switch (authState) {
                case LOGGED_OUT -> loginClient.printPrompt();
                case LOGGED_IN -> chessClient.printPrompt();
                case GAME -> gameClient.printPrompt();
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
