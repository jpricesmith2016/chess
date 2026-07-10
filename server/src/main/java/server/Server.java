package server;

import dataaccess.*;
import handler.DbClearHandler;
import handler.GameJoinHandler;
import handler.UserRegHandler;
import io.javalin.*;
import io.javalin.http.Handler;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Server {

    final AuthDAO authDAO = new MemoryAuthDAO();
    final UserDAO userDAO = new MemoryUserDAO();
    final GameDAO gameDAO = new MemoryGameDAO();

    final AuthService authService = new AuthService(authDAO, userDAO);
    final UserService userService = new UserService(userDAO);
    final GameService gameService = new GameService(gameDAO, authDAO);

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.delete("/db", new DbClearHandler(gameService, authService, userService));
        javalin.post("/user", new UserRegHandler(userService, authService));
//        javalin.post("/session", new UserLoginHandler());
//        javalin.delete("/session", new UserLogoutHandler());
//        javalin.get("/game", new GameListHandler());
//        javalin.post("/game", new GameCreateHandler());
        javalin.put("/game", new GameJoinHandler(gameService, authService));
        // Register your endpoints and exception handlers here.

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
