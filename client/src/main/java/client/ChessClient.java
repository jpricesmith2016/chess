package client;

public class ChessClient {

    private boolean quit;
    private static String serverURL;
    private static ClientHttp httpClient;
    private enum State {
        LOGGED_OUT,
        LOGGED_IN
    }
    private State authState;

    public ChessClient(String serverURL) {
        ChessClient.serverURL = serverURL;
        httpClient = new ClientHttp(serverURL);
        quit = false;
        authState = State.LOGGED_OUT;
    }

    public boolean run() {

        while (!quit) {

            String req;

            switch (authState) {
                case LOGGED_OUT -> loggedOutCommands(req);
                case LOGGED_IN -> loggedInCommands(req);
            }
        }
        return quit;

    }

    private void loggedOutCommands(String req) {

    }

    private void loggedInCommands(String req) {

    }


}
