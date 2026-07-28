package websocketserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;
import websocket.messages.ServerMessage.*;

public class WsConnectionManager {
    Map<Integer, Set<Session>> sessionMap = new HashMap<>();
    private final Gson gson = new Gson();

    void addSessionToGame(int gameID, Session session) {
        if (!sessionMap.containsKey(gameID)) {
            Set<Session> sessionSet = new HashSet<>();
            sessionSet.add(session);
            sessionMap.put(gameID, sessionSet);
        } else {
            Set<Session> oldSession = sessionMap.get(gameID);
            oldSession.add(session);
            sessionMap.replace(gameID, oldSession);
        }
    }

    void removeSessionFromGame(int gameID, Session session) {
        if (sessionMap.containsKey(gameID)) {
            Set<Session> oldSession = sessionMap.get(gameID);
            oldSession.remove(session);
            sessionMap.replace(gameID, oldSession);
        }
    }

    Set<Session> getSessionsForGame(int gameID) {
        if (sessionMap.containsKey(gameID)) {
            return sessionMap.get(gameID);
        }
        return new HashSet<>();
    }

    void sendMessage(Session session, ServerMessage message) throws IOException {
        if (session.isOpen()) {
            session.getRemote().sendString(gson.toJson(message));
        }
    }

    void broadcastMessage(Session excludedSession, ServerMessage message, int gameID) throws IOException {
        HashSet<Session> broadcastSessions = new HashSet<> (sessionMap.get(gameID));
        if (excludedSession != null) {
            broadcastSessions.remove(excludedSession);
        }
        if (broadcastSessions.isEmpty()) {
            return;
        }
        for (Session c : broadcastSessions) {
            if (!c.isOpen()) {
                continue;
            }
            c.getRemote().sendString(gson.toJson(message));
        }
    }
}
