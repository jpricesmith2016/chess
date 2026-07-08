package model;

import chess.ChessGame;

public record UserData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}
