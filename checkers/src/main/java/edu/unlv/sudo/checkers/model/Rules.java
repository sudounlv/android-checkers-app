package edu.unlv.sudo.checkers.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.unlv.sudo.checkers.model.exception.InvalidMoveException;
import edu.unlv.sudo.checkers.model.exception.OutOfTurnException;

/**
 * This class represents the rules utilities for the checkers game.
 */
public final class Rules {

    /**
     * This method returns all the valid moves a {@link Piece} can make on the provided {@link Board}.
     * @param piece the {@link Piece} in question
     * @param board the {@link Board} on which the piece resides
     * @return the {@link Set} of valid {@link Location}s where the {@link Piece} can move
     */
    public static Set<Location> getValidMoves(final Piece piece, final Board board) {
        final Set<Location> validMoves = new HashSet<>();

        int moveY = 0;
        int jumpY = 0;

        //check for single moves
        if (piece.getTeam() == Team.BLACK) {
            moveY = 1;
            jumpY = 2;
        } else if (piece.getTeam() == Team.RED) {
            moveY = -1;
            jumpY = -2;
        }

        final int startX = piece.getLocation().getX();
        final int startY = piece.getLocation().getY();

        final Location moveNW = new Location(startX - 1, startY + moveY);
        final Location moveNE = new Location(startX + 1, startY + moveY);
        final Location moveSW = new Location(startX - 1, startY - moveY);
        final Location moveSE = new Location(startX + 1, startY - moveY);

        final Location jumpNW = new Location(startX - 2, startY + jumpY);
        final Location jumpNE = new Location(startX + 2, startY + jumpY);
        final Location jumpSW = new Location(startX - 2, startY - jumpY);
        final Location jumpSE = new Location(startX + 2, startY - jumpY);

        if (isSpaceOnBoard(moveNW, board) && board.getPieceAtLocation(moveNW) == null) {
            validMoves.add(moveNW);
        }
        if (isSpaceOnBoard(moveNE, board) && board.getPieceAtLocation(moveNE) == null) {
            validMoves.add(moveNE);
        }
        if (isSpaceOnBoard(moveSW, board) && board.getPieceAtLocation(moveSW) == null && piece.isKing()) {
            validMoves.add(moveSW);
        }
        if (isSpaceOnBoard(moveSE, board) && board.getPieceAtLocation(moveSE) == null && piece.isKing()) {
            validMoves.add(moveSE);
        }

        if (isSpaceOnBoard(jumpNW, board) && board.getPieceAtLocation(jumpNW) == null && board.getPieceAtLocation(moveNW) != null) {
            validMoves.add(jumpNW);
        }
        if (isSpaceOnBoard(jumpNE, board) && board.getPieceAtLocation(jumpNE) == null && board.getPieceAtLocation(moveNE) != null) {
            validMoves.add(jumpNE);
        }
        if (isSpaceOnBoard(jumpSW, board) && board.getPieceAtLocation(jumpSW) == null && board.getPieceAtLocation(moveSW) != null && piece.isKing()) {
            validMoves.add(jumpSW);
        }
        if (isSpaceOnBoard(jumpSE, board) && board.getPieceAtLocation(jumpSE) == null && board.getPieceAtLocation(moveSE) != null && piece.isKing()) {
            validMoves.add(jumpSE);
        }

        return validMoves;
    }

    /**
     * Determine if the provided {@link Location} is on the {@link Board}.
     * @param location the {@link Location} to check
     * @param board the {@link Board} to check against
     * @return true if and only if the {@link Location} is on the {@link Board}
     */
    public static boolean isSpaceOnBoard(final Location location, final Board board) {
        return location.getX() >= 0 && location.getX() < board.getSpacesPerSide()
                && location.getY() >= 0 && location.getY() < board.getSpacesPerSide();
    }

    /**
     * Determine if the proposed new move is valid.
     * @param piece the {@link Piece} making the moves
     * @param previousMoves the {@link Set} of previous moves the piece has made
     * @param move the new move to determine if valid
     * @param board the {@link Board} on which the moves are taking place
     * @return true if the new move is valid
     */
    public static boolean isValidMove(final Piece piece, final List<Location> previousMoves, final Location move, final Board board) {
        final Piece theoreticalPiece;

        if (previousMoves.size() > 0) {
            final Location previousLocation = previousMoves.get(previousMoves.size() - 1);
            if (isJump(previousLocation, move)) {
                theoreticalPiece = new Piece(piece.getTeam(), previousLocation);
            } else {
                return false;
            }
        } else {
            theoreticalPiece = piece;
        }

        return getValidMoves(theoreticalPiece, board).contains(move);
    }

    /**
     * Determine if a move is a jump.
     * @param location the {@link Location} of the start
     * @param move the {@link Location} being moved to
     * @return true if the move is a jump
     */
    public static boolean isJump(final Location location, final Location move) {
        return Math.abs(move.getX() - move.getY()) > 1;
    }

    /**
     * Move a {@link Piece} in a {@link Game}.
     * @param game the {@link Game} in which to move the piece
     * @param piece the {@link Piece} to move in the game
     * @param moves the {@link Set} of moves the piece will make
     */
    public static void move(final Game game, final Piece piece, final List<Location> moves) {
        if (piece.getTeam() != game.getTurn()) {
            throw new OutOfTurnException(game.getTurn(), piece.getTeam());
        }

        for (Location location : moves) {
            if (!Rules.getValidMoves(piece, game.getBoard()).contains(location)) {
                throw new InvalidMoveException(piece, location);
            }

            final Iterator<Piece> iterator = game.getBoard().getPieces().iterator();
            while (iterator.hasNext()) {
                final Piece jumped = iterator.next();
                if (jumped.getLocation().isBetween(piece.getLocation(), location)) {
                    iterator.remove();
                }
            }

            piece.setLocation(location);

            if (piece.getTeam() == Team.RED) {
                if (piece.getLocation().getY() == game.getBoard().getSpacesPerSide() - 1) {
                    piece.makeKing();
                }
            } else if (piece.getTeam() == Team.BLACK) {
                if (piece.getLocation().getY() == 0) {
                    piece.makeKing();
                }
            }
        }
    }

    /**
     * A private constructor to prevent instantiation of this utility class.
     */
    private Rules() { }

}
