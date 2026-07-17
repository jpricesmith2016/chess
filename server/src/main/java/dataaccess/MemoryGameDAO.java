package dataaccess;

import model.GameData;

import java.util.*;

public class MemoryGameDAO implements GameDAO {
    final Map<Integer, GameData> gameInfo = new HashMap<>();

    @Override
    public void createGame(GameData g) {
        gameInfo.putIfAbsent(g.gameID(), g);
    }

    @Override
    public GameData getGame(int gameID) {
        return gameInfo.get(gameID);
    }

    @Override
    public Collection<GameData> getGameListUser(String username) {
        return new ArrayList<>(gameInfo.entrySet().stream()
                .filter(entry -> entry.getKey() > 1
                        && (Objects.equals(entry.getValue().blackUsername(), username)
                        || Objects.equals(entry.getValue().whiteUsername(), username)))
                .map(Map.Entry::getValue)
                .toList());
    }

    @Override
    public Collection<GameData> getGameList() {
        return new ArrayList<>(gameInfo.values().stream()
                .toList());
    }

    @Override
    public void updateGame(GameData game){
        gameInfo.putIfAbsent(game.gameID(), game);
        gameInfo.replace(game.gameID(), game);
    }

    @Override
    public void clearGame() {
        gameInfo.clear();
    }

    @Override
    public int length() {
        return gameInfo.size();
    }
}
