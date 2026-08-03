package websocket.commands;

public class ValidMoveCommand extends UserGameCommand{
    public int row;
    public int col;

    public ValidMoveCommand(CommandType commandType, String authToken, Integer gameID, int row, int col) {
        super(commandType, authToken, gameID);
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }
}