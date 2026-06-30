package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame implements Cloneable {

    private TeamColor teamTurn;
    private ChessBoard gameBoard;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = ChessPiece.pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove move : moves) {
            ChessGame gameCopy = this.clone();
            gameCopy.getBoard().addPiece(startPosition,null);
            gameCopy.getBoard().addPiece(move.getEndPosition(),gameBoard.getPiece(startPosition));
            if (!gameCopy.isInCheck(gameBoard.getPiece(startPosition).getTeamColor())) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Grabs start and end position from move
        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        // Gets piece from start position on the game board
        ChessPiece piece = gameBoard.getPiece(startPos);
        // null exception for piece not being present
        if (gameBoard.getPiece(startPos) == null) {
            throw new InvalidMoveException("No piece at specified starting position");
        }

        // Checks current team turn and throws exception if it is out of order
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is not your turn");
        }

        Collection<ChessMove> currentPossibilities = validMoves(startPos);

        //Checks to see if end move is legal
        if (!currentPossibilities.contains(move)) {
            throw new InvalidMoveException("Your move is not possible");
        }

        // Move the piece and check if it needs to be promoted
        gameBoard.addPiece(startPos, null);
        if (move.getPromotionPiece() != null) {
            gameBoard.addPiece(endPos, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            gameBoard.addPiece(endPos, piece);
        }
        gameBoard.addPiece(startPos, null);

        // Change Turns
        teamTurn = (teamTurn == ChessGame.TeamColor.WHITE)
                ? ChessGame.TeamColor.BLACK
                : ChessGame.TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = gameBoard.getKing(teamColor);
        Collection<ChessPosition> enemies = gameBoard.getTeam(teamColor, true);
        for (ChessPosition e : enemies) {
            Collection<ChessMove> eMoves = validMoves(e);
            if (eMoves.contains(new ChessMove(e, kingPos, null)) ||
                    eMoves.contains(new ChessMove(e, kingPos, ChessPiece.PieceType.QUEEN)) ||
                    eMoves.contains(new ChessMove(e, kingPos, ChessPiece.PieceType.BISHOP)) ||
                    eMoves.contains(new ChessMove(e, kingPos, ChessPiece.PieceType.KNIGHT)) ||
                    eMoves.contains(new ChessMove(e, kingPos, ChessPiece.PieceType.ROOK))){
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            Collection<ChessPosition> yourTeam;
            yourTeam = gameBoard.getTeam(teamColor, false);
            for (ChessPosition piece : yourTeam) {
                if (!validMoves(piece).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            Collection<ChessPosition> yourTeam;
            yourTeam = gameBoard.getTeam(teamColor, false);
            for (ChessPosition piece : yourTeam) {
                if (!validMoves(piece).isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public ChessGame clone() {
        ChessGame clone;
        try {
            clone = (ChessGame) super.clone();
            ChessBoard clonedBoard = getBoard().clone();
            clone.setBoard(clonedBoard);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(teamTurn);
    }
}
