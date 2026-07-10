package Request_Result;

import model.GameData;

import java.util.Collection;

public record ListGamesResult(int resultCode, Collection<GameData> games) {
}
