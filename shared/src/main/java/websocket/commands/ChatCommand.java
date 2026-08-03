package websocket.commands;

import chess.ChessMove;

public class ChatCommand extends UserGameCommand{
    public String message;

    public ChatCommand(CommandType commandType, String authToken, Integer gameID, String message) {
        super(commandType, authToken, gameID);
        this.message = message;
    }
}