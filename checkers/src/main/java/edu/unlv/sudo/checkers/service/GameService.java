package edu.unlv.sudo.checkers.service;

import edu.unlv.sudo.checkers.model.Board;
import edu.unlv.sudo.checkers.model.Game;
import edu.unlv.sudo.checkers.model.Team;

/**
 * This service describes the interface between the client and the REST server.
 */
public interface GameService {

    /**
     * Issue a move on a game by presenting a new board object.
     * @param game the game in play
     * @param listener the {@link Listener} to handle callbacks
     */
    void move(Game game, Listener listener);

    /**
     * Join a game already in progress on a specified team.
     * @param gameId the ID of the game to join
     * @param team the {@link Team} to join
     * @param listener the {@link Listener} to handle callbacks
     */
    void joinGame(String gameId, Team team, Listener listener);

    /**
     * Create a new game.
     * @param team the {@link Team} to join
     * @param listener the {@link Listener} to handle callbacks
     */
    void newGame(Team team, Listener listener);

    /**
     * Update a game from the server.
     * @param gameId the ID of the game in play
     * @param listener the {@link Listener} to handle callbacks
     */
    void update(String gameId, Listener listener);

    /**
     * A listener for games.
     */
    interface Listener {

        /**
         * Called when a game is available.
         * @param game the {@link edu.unlv.sudo.checkers.model.Game} that became available
         */
        void onGame(Game game);

        /**
         * Called when an error occurs retrieving the game.
         * @param exception the {@link Exception}
         */
        void onError(Exception exception);
    }
}
