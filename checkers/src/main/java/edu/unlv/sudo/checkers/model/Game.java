package edu.unlv.sudo.checkers.model;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Iterator;
import java.util.List;

import edu.unlv.sudo.checkers.model.exception.InvalidMoveException;
import edu.unlv.sudo.checkers.model.exception.OutOfTurnException;

/**
 * This class represents a game of checkers.
 */
public class Game {

    private String id;
    private Board board;
    private Team turn;

    @JsonCreator
    public Game(@JsonProperty("id") final String id,
                @JsonProperty("board") final Board board,
                @JsonProperty("turn") final Team turn) {

        this.id = id;
        this.board = board;
        this.turn = turn;
    }

    public Board getBoard() {
        return board;
    }

    public Team getTurn() {
        return turn;
    }

    public String getId() {
        return id;
    }

    public void clearTurn() {
        this.turn = null;
    }
}
