package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {

    void createGame (GameData g);

    GameData getGame (int gameID);

    Collection<GameData> getGameList (String username);

    public void updateGame (GameData game);

    public void deleteGame (int gameID);

    public void clearGame ();

    public int length ();
}
