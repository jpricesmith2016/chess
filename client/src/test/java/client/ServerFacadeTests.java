package client;

import org.junit.jupiter.api.*;
import requestresult.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDB() throws Exception {
        facade.clear();
    }


    @Test
    public void registerNewUserSuccess() throws Exception {
        RegisterResult result = facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        Assertions.assertEquals(200, result.resultCode());
    }

    @Test
    public void registerExistingUserFail() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        RegisterResult result2 = facade.register(new RegisterRequest("joe", "pass2", "ex@gmail.com"));
        Assertions.assertNotEquals(200, result2.resultCode());
    }

    @Test
    public void loginExistingUserSuccess() throws Exception {
        RegisterResult regResult = facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        facade.logout(new LogoutRequest(regResult.returnAuth().authToken()));
        LoginResult loginResult = facade.login(new LoginRequest("joe", "pass"));
        Assertions.assertEquals(200, loginResult.resultCode());
    }

    @Test
    public void loginUnregisteredUserFail() throws Exception {
        LoginResult loginResult = facade.login(new LoginRequest("joe", "pass"));
        Assertions.assertNotEquals(200, loginResult.resultCode());
    }

    @Test
    public void loginPasswordIncorrectFail() throws Exception {
        RegisterResult regResult = facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        facade.logout(new LogoutRequest(regResult.returnAuth().authToken()));
        LoginResult loginResult = facade.login(new LoginRequest("joe", "badPass"));
        Assertions.assertNotEquals(200, loginResult.resultCode());
    }

    @Test
    public void logoutSuccess() throws Exception {
        RegisterResult regResult = facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        LogoutResult result = facade.logout(new LogoutRequest(regResult.returnAuth().authToken()));
        Assertions.assertEquals(200, result.resultCode());
    }

    @Test
    public void logoutNotLoggedInFail() throws Exception {
        LogoutResult result = facade.logout(new LogoutRequest(""));
        Assertions.assertNotEquals(200, result.resultCode());
    }

    @Test
    public void createSuccess() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        CreateResult result = facade.createGame(new CreateRequest("Game1"));
        Assertions.assertEquals(200, result.resultCode());
    }

    @Test
    public void createNameEmptyParamsFail() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        CreateResult result = facade.createGame(new CreateRequest(""));
        Assertions.assertNotEquals(200, result.resultCode());
    }

    @Test
    public void joinExistingGameAsWhiteSuccess() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        CreateResult createResult = facade.createGame(new CreateRequest("Game1"));
        GameJoinResult result = facade.joinGame(new GameJoinRequest("White", createResult.gameID()));
        Assertions.assertEquals(200, result.resultCode());
    }

    @Test
    public void joinBadGameAsWhiteFail() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        CreateResult createResult = facade.createGame(new CreateRequest("Game1"));
        GameJoinResult result = facade.joinGame(new GameJoinRequest("White", 3));
        Assertions.assertNotEquals(200, result.resultCode());
    }

    @Test
    public void joinExistingGameAsBadColorFail() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        CreateResult createResult = facade.createGame(new CreateRequest("Game1"));
        GameJoinResult result = facade.joinGame(new GameJoinRequest("Green", createResult.gameID()));
        Assertions.assertNotEquals(200, result.resultCode());
    }

    @Test
    public void listReturnsExistingGamesSuccess() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        facade.createGame(new CreateRequest("Game1"));
        facade.createGame(new CreateRequest("Game2"));
        ListGamesResult result = facade.listGames();
        assertEquals(200, result.resultCode());
        assertTrue(result.games().stream().anyMatch(game -> game.gameName().equals("Game1")));
    }

    @Test
    public void listReturnsEmpty() throws Exception {
        facade.register(new RegisterRequest("joe", "pass", "example@gmail.com"));
        ListGamesResult result = facade.listGames();
        assertEquals(200, result.resultCode());
        assertTrue(result.games().isEmpty());
    }

}
