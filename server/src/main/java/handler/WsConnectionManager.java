package handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

public class WsConnectionManager {
    Map<Integer, Set<Session>> sessionMap = new HashMap<>();

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
}
