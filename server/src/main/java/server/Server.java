package server;

import com.google.gson.Gson;
import dataaccess.*;
import dataaccess.exceptions.DataAccessException;
import handler.*;
import io.javalin.*;
import service.AuthService;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {


    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        registerExceptionHandlers();
        try {
            AuthDAO authDAO = new SQLAuthDAO();
            UserDAO userDAO = new SQLUserDAO();
            GameDAO gameDAO = new SQLGameDAO();
            AuthService authService = new AuthService(authDAO, userDAO);
            UserService userService = new UserService(userDAO);
            GameService gameService = new GameService(gameDAO, authDAO);
            javalin.delete("/db", new DbClearHandler(gameService, authService, userService));
            javalin.post("/user", new UserRegHandler(userService, authService));
            javalin.post("/session", new UserLoginHandler(authService, userService));
            javalin.delete("/session", new UserLogoutHandler(authService));
            javalin.get("/game", new GameListHandler(gameService, authService));
            javalin.post("/game", new GameCreateHandler(gameService, authService));
            javalin.put("/game", new GameJoinHandler(gameService, authService));
        } catch (Exception e) {
            System.out.printf("Unable to start server: %s%n", e.getMessage());
        }
    }

    private void registerExceptionHandlers() {
        javalin.exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.contentType("application/json");
            ctx.result(new Gson().toJson(Map.of("message", "Error: " + sanitizeMessage(e.getMessage()))));
        });
    }

    private String sanitizeMessage(String message) {
        if (message == null || message.isBlank()) {
            return "internal server error";
        }
        return message;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
