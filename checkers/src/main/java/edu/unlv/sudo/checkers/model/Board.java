package edu.unlv.sudo.checkers.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.unlv.sudo.checkers.model.exception.InvalidBoardException;

/**
 * Represents a checkers game board.
 */
public class Board {

    private int spacesPerSide;
    private Set<Piece> pieces;

    @JsonCreator
    public Board(@JsonProperty("spacesPerSide") final int spacesPerSide,
                 @JsonProperty("pieces") final Set<Piece> pieces) {

        if (this.spacesPerSide % 2 != 0) {
            throw new InvalidBoardException(spacesPerSide);
        }

        this.spacesPerSide = spacesPerSide;
        this.pieces = pieces;
    }

    public Set<Piece> getPieces() {
        return pieces;
    }

    public int getSpacesPerSide() {
        return spacesPerSide;
    }

    public Piece getPieceAtLocation(final Location location) {
        for (Piece piece : pieces) {
            if (piece.getLocation().equals(location)) {
                return piece;
            }
        }

        return null;
    }

    private static Set<Piece> initializePieces(final int spacesPerSide) {
        final Set<Piece> pieces = new HashSet<>(spacesPerSide * spacesPerSide / 2 - (2 * spacesPerSide));

        for (int y = 0; y < spacesPerSide / 2 - 1; y++) {
            for (int x = y % 2; x < spacesPerSide; x += 2) {
                final Location location = new Location(x, y);
                pieces.add(new Piece(Team.RED, location));
            }
        }

        for (int y = spacesPerSide - 1; y >= spacesPerSide / 2 + 1; y--) {
            for (int x = y % 2; x < spacesPerSide; x += 2) {
                final Location location = new Location(x, y);
                pieces.add(new Piece(Team.BLACK, location));
            }
        }

        return pieces;
    }
}
