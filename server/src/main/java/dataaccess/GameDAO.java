package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void createGame(GameData g);

    GameData getGame(int gameID);

    Collection<GameData> getGameList(String username);

    void updateGame(GameData game);

    void deleteGame(int gameID);

    void clearGame();

    int length();
}
