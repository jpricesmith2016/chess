package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece implements Cloneable {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Adds possible moves for a piece based on its movement patterns
     * 
     * @param board the chess board
     * @param start the starting position of the piece
     * @param piece the chess piece
     * @param moves the collection of possible moves
     * @param directions the directions in which the piece can move
     * @param repeat whether the piece can repeat its movement
     * @return the updated collection of possible moves
     */
    private static Collection<ChessMove> addPossibleMoves
            (ChessBoard board, ChessPosition start, ChessPiece piece, Collection<ChessMove> moves, int[][] directions, boolean repeat) {
        // Iterate over all possible directions for a piece to take
        for (int[] dir : directions) {
            // Adjust the row and col based on the direction at that index
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            // Check for bounds
            while (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                // make new position and check if there is something there
                ChessPosition end = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(end);

                // If there is nothing add the location and continue, otherwise add the location and break
                if (target == null) {
                    moves.add(new ChessMove(start, end, null));
                } else {
                    if (target.getTeamColor() != piece.getTeamColor()) {
                        moves.add(new ChessMove(start, end, null));
                    }
                    break;
                }

                // If the path does not repeat (Knight/King) break
                if (!repeat) {
                    break;
                }

                row += dir[0];
                col += dir[1];
            }
        }
        return moves;
    }

    /**
     * Adds possible moves for a pawn piece
     * @param board the chess board
     * @param start the starting position of the pawn
     * @param pawn the pawn piece
     * @param moves the collection of possible moves
     * @param game the chess game object
     * @return the updated collection of possible moves
     */
    private static Collection<ChessMove> addPawnMoves(ChessBoard board, ChessPosition start, ChessPiece pawn, Collection<ChessMove> moves, ChessGame game) {
        // Initialize helper variables to either reduce calls or length of code
        boolean whiteTeam = pawn.getTeamColor() == ChessGame.TeamColor.WHITE;
        int direction = whiteTeam ? 1 : -1;
        int startRow = whiteTeam ? 2 : 7;
        int promotionRow = whiteTeam ? 8 : 1;
        int pawnRow = start.getRow();
        int pawnRowOnce = pawnRow+(direction);
        int pawnRowTwice = pawnRow+(2*direction);
        int [][] diagonals = new int [][] {
                {direction, -1}, {direction, 1}
        };

        // Initialize the basic movement of the pawn
        ChessPosition oneSpace = new ChessPosition(pawnRowOnce, start.getColumn());
        ChessPosition twoSpace = new ChessPosition(pawnRowTwice, start.getColumn());

        // Logic for basic forward movement, ensure that movement doesn't exceed bounds and restrict capture ability
        if (pawnRowOnce >= 1 && pawnRowOnce <= 8 && start.getColumn() >= 1 && start.getColumn() <= 8
                && board.getPiece(oneSpace) == null) {

            // Control Promotion
            if (pawnRowOnce == promotionRow) {
                moves.add(new ChessMove(start, oneSpace, PieceType.QUEEN));
                moves.add(new ChessMove(start, oneSpace, PieceType.BISHOP));
                moves.add(new ChessMove(start, oneSpace, PieceType.KNIGHT));
                moves.add(new ChessMove(start, oneSpace, PieceType.ROOK));
            }
            else {
                moves.add(new ChessMove(start, oneSpace, null));
            }

            // Adds the two space possibility, promotion logic not needed as two spaces are only for start row
            if (start.getRow() == startRow && board.getPiece(twoSpace) == null) {
                moves.add(new ChessMove(start, twoSpace, null));
            }
        }

        // Logic for Capture
        for (int[] dir : diagonals) {
            // Manipulate the row and column by the array
            int row = start.getRow() + dir[0];
            int col = start.getColumn() + dir[1];

            // Check if you are in bounds
            if ((row >= 1) && (row <= 8) && (col >= 1) && (col <= 8)) {
                ChessPosition end = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(end);

                if (target == null) {
                    // adds stipulation that null could mean enPassant and checks to see if that move was made
                    if (game != null && end.equals(game.getEnPassant())) {
                        moves.add(new ChessMove(start, end, null));
                    }
                    continue;
                }
                if (target.getTeamColor() != pawn.getTeamColor()) {
                    if (pawnRowOnce == promotionRow) {
                        moves.add(new ChessMove(start, end, PieceType.QUEEN));
                        moves.add(new ChessMove(start, end, PieceType.BISHOP));
                        moves.add(new ChessMove(start, end, PieceType.KNIGHT));
                        moves.add(new ChessMove(start, end, PieceType.ROOK));
                    }
                    else {
                        moves.add(new ChessMove(start, end, null));
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Adds possible castling moves for a king piece
     * @param board the chess board object
     * @param start the starting position of the king
     * @param king the king piece
     * @param moves the collection of possible moves
     * @param game the chess game object
     */
    private static void addCastlingMoves(ChessBoard board, ChessPosition start, ChessPiece king,
                                         Collection<ChessMove> moves, ChessGame game) {
        // if undefined game was passed return
        if (game == null) {
            return;
        }

        ChessGame.TeamColor teamColor = king.getTeamColor();
        int row = start.getRow();

        if (game.castleKingside(teamColor)) {
            ChessPosition throughOne = new ChessPosition(row, start.getColumn() + 1);
            ChessPosition throughTwo = new ChessPosition(row, start.getColumn() + 2);
            if (board.getPiece(throughOne) == null && board.getPiece(throughTwo) == null
                    && isSquareSafe(board, start, teamColor)
                    && isSquareSafe(board, throughOne, teamColor)
                    && isSquareSafe(board, throughTwo, teamColor)) {
                moves.add(new ChessMove(start, throughTwo, null));
            }
        }

        if (game.castleQueenside(teamColor)) {
            ChessPosition throughOne = new ChessPosition(row, start.getColumn() - 1);
            ChessPosition throughTwo = new ChessPosition(row, start.getColumn() - 2);
            if (board.getPiece(throughOne) == null && board.getPiece(throughTwo) == null
                    && isSquareSafe(board, start, teamColor)
                    && isSquareSafe(board, throughOne, teamColor)
                    && isSquareSafe(board, throughTwo, teamColor)) {
                moves.add(new ChessMove(start, throughTwo, null));
            }
        }
    }

    /**
     * Checks if the square is being attacked by an enemy, can potentially be reused for AI later down the line
     * @param board current board state
     * @param position Chess position
     * @param teamColor Team requesting info
     * @return true if enemy can attack space false otherwise
     */
    private static boolean isSquareSafe(ChessBoard board, ChessPosition position, ChessGame.TeamColor teamColor) {
        // gets position of enemy team
        Collection<ChessPosition> opponents = board.getTeam(teamColor, true);
        // iterates over enemies
        for (ChessPosition opponentPosition : opponents) {
            ChessPiece opponentPiece = board.getPiece(opponentPosition);
            if (opponentPiece == null) {
                continue;
            }
            Collection<ChessMove> opponentMoves = pieceMoves(board, opponentPosition);
            if (opponentMoves != null) {
                for (ChessMove move : opponentMoves) {
                    if (move.getEndPosition().equals(position)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return pieceMoves(board, myPosition, null);
    }

    /**
     * Overloaded version of pieceMoves that allows us to pass in a game object
     * @param board Current board
     * @param myPosition Piece position
     * @param game Current game object
     * @return a Collection of possible moves without taking into account check or game ending moves
     */
    public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition, ChessGame game) {
        ChessPiece piece = board.getPiece(myPosition);
        if (piece == null) {
            return null;
        }

        Collection<ChessMove> moves = new ArrayList<>();

        int[][] directions;

        if (piece.getPieceType() == PieceType.KING) {
            directions = new int[][]{
                    {1,0}, {0,1}, {-1,0}, {0,-1},
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };
            addPossibleMoves(board, myPosition, piece, moves, directions, false);
            addCastlingMoves(board, myPosition, piece, moves, game);
            return moves;
        }
        if (piece.getPieceType() == PieceType.QUEEN) {
            directions = new int[][]{
                    {1,0}, {0,1}, {-1,0}, {0,-1},
                    {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
            };
            return addPossibleMoves(board, myPosition, piece, moves, directions, true);
        }
        if(piece.getPieceType() == PieceType.BISHOP) {
            directions = new int[][]{
                    {1, 1}, {1, -1},
                    {-1, 1}, {-1, -1}
            };
            return addPossibleMoves(board, myPosition, piece, moves, directions, true);
        }
        if(piece.getPieceType() == PieceType.KNIGHT) {
            directions = new int[][]{
                    {2, 1}, {1, 2}, {-2, 1}, {-1, 2},
                    {1, -2}, {2, -1}, {-2, -1}, {-1, -2}
            };
            return addPossibleMoves(board, myPosition, piece, moves, directions, false);
        }
        if(piece.getPieceType() == PieceType.ROOK) {
            directions = new int[][]{
                    {1, 0}, {0, 1},
                    {-1, 0}, {0, -1}
            };
            return addPossibleMoves(board, myPosition, piece, moves, directions, true);
        }
        if(piece.getPieceType() == PieceType.PAWN) {
            return addPawnMoves(board, myPosition, piece, moves, game);
        }
        return List.of();
    }

    @Override
    public ChessPiece clone() {
        try {
            return (ChessPiece) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()){
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
