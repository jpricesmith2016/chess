package chess;

import java.util.ArrayList;
import java.util.Arrays;
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
    private ChessBoard gameBoard = new ChessBoard();
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private boolean[] whiteRookMoved = new boolean[2];
    private boolean[] blackRookMoved = new boolean[2];
    private ChessPosition enPassantTarget;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        gameBoard.resetBoard();
        resetState();
    }

    /**
     * Resets the state of the game to the initial state
     */
    private void resetState() {
        whiteKingMoved = false;
        blackKingMoved = false;
        whiteRookMoved[0] = false;
        whiteRookMoved[1] = false;
        blackRookMoved[0] = false;
        blackRookMoved[1] = false;
        enPassantTarget = null;
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
        // Pulls currently believed valid moves and checks to ensure there are values
        Collection<ChessMove> moves = ChessPiece.pieceMoves(gameBoard, startPosition, this);
        if (moves == null) {
            return new ArrayList<>();
        }

        // Build a new array to put confirmed legal moves
        Collection<ChessMove> legalMoves = new ArrayList<>();
        // iterate over moves and test to ensure check does not happen my using a cloned game
        for (ChessMove move : moves) {
            ChessGame gameCopy = this.clone();
            ChessPiece piece = gameCopy.getBoard().getPiece(startPosition);
            if (piece == null) {
                continue;
            }
            gameCopy.applyMove(startPosition, move);
            if (!gameCopy.isInCheck(piece.getTeamColor())) {
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

        // Gets piece from start position on the game board
        ChessPiece piece = gameBoard.getPiece(startPos);
        if (piece == null) {
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

        // Calls new helper method to apply the move to the board
        applyMove(startPos, move);
    }

    /**
     * Applies a move to the game board, updating the board and game state accordingly
     * @param startPosition Starting position of the piece
     * @param move move selected
     */
    private void applyMove(ChessPosition startPosition, ChessMove move) {
        ChessPiece piece = gameBoard.getPiece(startPosition);
        if (piece == null) {
            return;
        }

        ChessPosition endPosition = move.getEndPosition();
        ChessPosition previousEnPassantTarget = enPassantTarget;
        enPassantTarget = null;

        // Check if selected move is attempting to castle
        boolean isCastling = piece.getPieceType() == ChessPiece.PieceType.KING
                && Math.abs(endPosition.getColumn() - startPosition.getColumn()) == 2;
        // Check if selected move is attempting to accomplish enPassant
        boolean isEnPassant = piece.getPieceType() == ChessPiece.PieceType.PAWN
                && endPosition.getColumn() != startPosition.getColumn()
                && gameBoard.getPiece(endPosition) == null
                && endPosition.equals(previousEnPassantTarget);

        // Logic for Castling
        if (isCastling) {
            // Select queenside of kingside based on move selection
            int rookStartCol = endPosition.getColumn() > startPosition.getColumn() ? 8 : 1;
            ChessPosition rookStart = new ChessPosition(startPosition.getRow(), rookStartCol);
            // Logic for rook offset after castling
            ChessPosition rookEnd = new ChessPosition(startPosition.getRow(),
                    endPosition.getColumn() > startPosition.getColumn() ? endPosition.getColumn() - 1 : endPosition.getColumn() + 1);
            // Make the Rook move, King initiated move so handled later
            ChessPiece rookPiece = gameBoard.getPiece(rookStart);
            if (rookPiece != null) {
                gameBoard.addPiece(rookStart, null);
                gameBoard.addPiece(rookEnd, rookPiece);
                markRookMoved(piece.getTeamColor(), rookStartCol);
            }
        }

        // Logic for EnPassant
        if (isEnPassant) {
            ChessPosition capturedPawnPosition = new ChessPosition(
                    endPosition.getRow() - (piece.getTeamColor() == TeamColor.WHITE ? 1 : -1),
                    endPosition.getColumn());
            gameBoard.addPiece(capturedPawnPosition, null);
        }

        // Make the piece move
        gameBoard.addPiece(startPosition, null);
        ChessPiece movedPiece = move.getPromotionPiece() == null
                ? piece
                : new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        gameBoard.addPiece(endPosition, movedPiece);

        // Set the flag preventing castling because King has been moved on this board
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            markKingMoved(piece.getTeamColor());
        }
        // Set the flag preventing castling with this specific rook because it has been moved in this game
        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            markRookMoved(piece.getTeamColor(), startPosition.getColumn());
        }

        // Set a pawn that has moved two places this round as a potential enPassant target for the other team next round
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && Math.abs(endPosition.getRow() - startPosition.getRow()) == 2) {
            enPassantTarget = new ChessPosition((startPosition.getRow() + endPosition.getRow()) / 2, startPosition.getColumn());
        }

        // Change the team
        teamTurn = teamTurn == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Marks the king of the specified team as moved
     * @param teamColor the color of the team whose king is moved
     */
    private void markKingMoved(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            whiteKingMoved = true;
        } else {
            blackKingMoved = true;
        }
    }

    /**
     * Marks the rook of the specified team and starting column as moved
     * @param teamColor The team color of the flag to adjust
     * @param startColumn start column helps us change the flag of the correct rook
     */
    private void markRookMoved(TeamColor teamColor, int startColumn) {
        if (teamColor == TeamColor.WHITE) {
            if (startColumn == 1) {
                whiteRookMoved[0] = true;
            } else if (startColumn == 8) {
                whiteRookMoved[1] = true;
            }
        } else {
            if (startColumn == 1) {
                blackRookMoved[0] = true;
            } else if (startColumn == 8) {
                blackRookMoved[1] = true;
            }
        }
    }

    /**
     * Determines if the given team can castle kingside
     * @param teamColor the team color of the king being checked
     * @return true if possible to castle kingside false otherwise
     */
    boolean castleKingside(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return !whiteKingMoved && !whiteRookMoved[1]
                    && hasKingAtHome(teamColor) && hasRookAtHome(teamColor, 8);
        }
        return !blackKingMoved && !blackRookMoved[1]
                && hasKingAtHome(teamColor) && hasRookAtHome(teamColor, 8);
    }

    /**
     * Determines if the given team can castle queenside
     * @param teamColor the team color of the king being checked
     * @return true if possible to castle queenside false otherwise
     */
    boolean castleQueenside(TeamColor teamColor) {
        if (teamColor == TeamColor.WHITE) {
            return !whiteKingMoved && !whiteRookMoved[0]
                    && hasKingAtHome(teamColor) && hasRookAtHome(teamColor, 1);
        }
        return !blackKingMoved && !blackRookMoved[0]
                && hasKingAtHome(teamColor) && hasRookAtHome(teamColor, 1);
    }

    /**
     * Checks if the king of the specified team is at its home position
     * @param teamColor the color of the team whose king to check
     * @return true if the king is at its home position, false otherwise
     */
    private boolean hasKingAtHome(TeamColor teamColor) {
        ChessPosition kingPosition = teamColor == TeamColor.WHITE ? new ChessPosition(1, 5) : new ChessPosition(8, 5);
        ChessPiece king = gameBoard.getPiece(kingPosition);
        return king != null && king.getPieceType() == ChessPiece.PieceType.KING && king.getTeamColor() == teamColor;
    }

    /**
     * Checks if the rook of the specified team is at its home position
     * @param teamColor the color of the team whose rook to check
     * @param column the column where the rook should be
     * @return true if the rook is at its home position, false otherwise
     */
    private boolean hasRookAtHome(TeamColor teamColor, int column) {
        ChessPosition rookPosition = new ChessPosition(teamColor == TeamColor.WHITE ? 1 : 8, column);
        ChessPiece rook = gameBoard.getPiece(rookPosition);
        return rook != null && rook.getPieceType() == ChessPiece.PieceType.ROOK && rook.getTeamColor() == teamColor;
    }

    /**
     * Gets the current en passant target position, if any
     * @return the en passant target position, or null if none
     */
    ChessPosition getEnPassant() {
        return enPassantTarget;
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
            Collection<ChessMove> eMoves = ChessPiece.pieceMoves(gameBoard, e, null);
            assert eMoves != null;
            for (ChessMove m : eMoves) {
                if (m.getEndPosition().equals(kingPos)) {
                    return true;
                }
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
            return noMoves(teamColor);
        }
        return false;
    }

    /**
     * Helper Function for Checking if no valid moves are available.
     * 
     * @param teamColor the team to check for valid moves
     * @return true if no valid moves are available, false otherwise
     */
    private boolean noMoves(TeamColor teamColor) {
        Collection<ChessPosition> yourTeam;
        yourTeam = gameBoard.getTeam(teamColor, false);
        for (ChessPosition piece : yourTeam) {
            if (!validMoves(piece).isEmpty()) {
                return false;
            }
        }
        return true;
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
            return noMoves(teamColor);
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
            clone.teamTurn = teamTurn;
            clone.whiteKingMoved = whiteKingMoved;
            clone.blackKingMoved = blackKingMoved;
            clone.whiteRookMoved = new boolean[2];
            clone.whiteRookMoved[0] = whiteRookMoved[0];
            clone.whiteRookMoved[1] = whiteRookMoved[1];
            clone.blackRookMoved = new boolean[2];
            clone.blackRookMoved[0] = blackRookMoved[0];
            clone.blackRookMoved[1] = blackRookMoved[1];
            clone.enPassantTarget = enPassantTarget == null ? null : new ChessPosition(enPassantTarget.getRow(), enPassantTarget.getColumn());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn
                && whiteKingMoved == chessGame.whiteKingMoved
                && blackKingMoved == chessGame.blackKingMoved
                && Arrays.equals(whiteRookMoved, chessGame.whiteRookMoved)
                && Arrays.equals(blackRookMoved, chessGame.blackRookMoved)
                && Objects.equals(enPassantTarget, chessGame.enPassantTarget)
                && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, whiteKingMoved, blackKingMoved,
                Arrays.hashCode(whiteRookMoved), Arrays.hashCode(blackRookMoved),
                enPassantTarget, gameBoard);
    }
}
