package websocket.messages;

import chess.ChessMove;

import java.util.Collection;

public class ValidMovesMessage extends ServerMessage{
    public Collection<ChessMove> possibleMoves;
    public ValidMovesMessage(Collection<ChessMove> validMoves) {
        super(ServerMessageType.VALID_MOVES);
        this.possibleMoves = validMoves;
    }
    public Collection<ChessMove> getValidMoves() {
        return possibleMoves;
    }
}
