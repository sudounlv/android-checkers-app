package edu.unlv.sudo.checkers.views;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.unlv.sudo.checkers.model.Board;
import edu.unlv.sudo.checkers.model.Game;
import edu.unlv.sudo.checkers.model.Location;
import edu.unlv.sudo.checkers.model.Piece;
import edu.unlv.sudo.checkers.model.Rules;
import edu.unlv.sudo.checkers.model.Team;
import edu.unlv.sudo.checkers.service.GameService;
import edu.unlv.sudo.checkers.service.impl.GameServiceImpl;

/**
 * This view represents the checkers board.
 */
public class BoardView extends View {

    private static final Logger LOGGER = Logger.getLogger(BoardView.class.getName());

    private static final int BOARD_COLOR_LIGHT = Color.LTGRAY;
    private static final int BOARD_COLOR_DARK = Color.DKGRAY;
    private static final int PIECE_COLOR_RED = Color.RED;
    private static final int PIECE_COLOR_BLACK = Color.BLACK;
    private static final int PIECE_COLOR_KING = Color.WHITE;
    private static final int SELECT_COLOR_PIECE = Color.GREEN;
    private static final int SELECT_COLOR_SPACE = Color.YELLOW;

    private Game game;
    private int boardWidth;

    private Piece selectedPiece;
    private List<Location> moves;

    public BoardView(final Context context) {
        super(context);
        setOnTouchListener(new CheckersOnTouchListener(this));
    }

    public BoardView(final Context context, final AttributeSet attributes) {
        super(context, attributes);
        setOnTouchListener(new CheckersOnTouchListener(this));
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnTouchListener(new CheckersOnTouchListener(this));
    }

    public void setGame(final Game game) {
        this.game = game;
        invalidate();
    }

    public void setSelectedPiece(final Piece piece) {
        this.selectedPiece = piece;
        invalidate();
    }

    public void setMoves(final List<Location> moves) {
        this.moves = moves;
        invalidate();
    }

    public Game getGame() {
        return this.game;
    }

    public int getBoardWidth() {
        return this.boardWidth;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        this.boardWidth = Math.min(canvas.getWidth(), canvas.getHeight());

        if (game == null) {
            LOGGER.warning("Attempting to render a null game.");
            return;
        }

        drawBoard(game.getBoard(), canvas);
        drawSelectedPiece(selectedPiece, canvas);
        drawSelectedMoves(moves, canvas);
        drawPieces(game.getBoard(), canvas);
    }

    private void drawBoard(final Board board, final Canvas canvas) {

        if (board == null) {
            LOGGER.warning("Attempting to render board with no board set.");
            return;
        }

        final int boardWidth = Math.min(canvas.getHeight(), canvas.getWidth());
        final float squareWidth = (float) boardWidth / board.getSpacesPerSide();

        boolean lightSquare = true;
        for (int xPos = 0; xPos < board.getSpacesPerSide(); xPos++) {
            lightSquare = !lightSquare;
            for (int yPos = 0; yPos < board.getSpacesPerSide(); yPos++) {
                lightSquare = !lightSquare;

                final Paint paint = new Paint();
                paint.setColor(lightSquare ? BOARD_COLOR_LIGHT : BOARD_COLOR_DARK);

                final float left = xPos * squareWidth;
                final float right = (xPos + 1) * squareWidth - 1;
                final float top = yPos * squareWidth;
                final float bottom = (yPos + 1) * squareWidth - 1;

                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    private void drawPieces(final Board board, final Canvas canvas) {

        if (board == null) {
            LOGGER.warning("Attempting to render board with no board set.");
            return;
        }

        final int boardWidth = Math.min(canvas.getHeight(), canvas.getWidth());
        final float squareWidth = (float) boardWidth / board.getSpacesPerSide();
        final float pieceRadius = squareWidth * .75F / 2;

        for (Piece piece : board.getPieces()) {
            final Paint paint = new Paint();
            paint.setColor(piece.getTeam() == Team.RED ? PIECE_COLOR_RED : PIECE_COLOR_BLACK);

            final int x = piece.getLocation().getX();
            final int y = piece.getLocation().getY();

            final float left = x * squareWidth + (squareWidth / 2);
            final float top = y * squareWidth + (squareWidth / 2);

            canvas.drawCircle(left, top, pieceRadius, paint);

            if (piece.isKing()) {
                paint.setColor(PIECE_COLOR_KING);
                canvas.drawLine(left, top - pieceRadius, left, top + pieceRadius, paint);
                canvas.drawLine(left - pieceRadius, top, left + pieceRadius, top, paint);
            }
        }
    }

    private void drawSelectedPiece(final Piece piece, final Canvas canvas) {
        if (piece == null) {
            return;
        }

        if (game == null) {
            LOGGER.warning("Attempting to render board with no game set.");
            return;
        }

        final int boardWidth = Math.min(canvas.getHeight(), canvas.getWidth());
        final float squareWidth = (float) boardWidth / game.getBoard().getSpacesPerSide();

        final Paint paint = new Paint();
        paint.setColor(SELECT_COLOR_PIECE);

        final int x = piece.getLocation().getX();
        final int y = piece.getLocation().getY();

        final float left = x * squareWidth + 4;
        final float right = (x + 1) * squareWidth - 5;
        final float top = y * squareWidth + 4;
        final float bottom = (y + 1) * squareWidth - 5;

        canvas.drawRect(left, top, right, bottom, paint);
    }

    private void drawSelectedMoves(final List<Location> moves, final Canvas canvas) {
        if (moves == null) {
            return;
        }

        if (game == null) {
            LOGGER.warning("Attempting to render board with no game set.");
            return;
        }

        final int boardWidth = Math.min(canvas.getHeight(), canvas.getWidth());
        final float squareWidth = (float) boardWidth / game.getBoard().getSpacesPerSide();

        final Paint paint = new Paint();
        paint.setColor(SELECT_COLOR_SPACE);

        for (Location location : moves) {
            final int x = location.getX();
            final int y = location.getY();

            final float left = x * squareWidth + 4;
            final float right = (x + 1) * squareWidth - 5;
            final float top = y * squareWidth + 4;
            final float bottom = (y + 1) * squareWidth - 5;

            canvas.drawRect(left, top, right, bottom, paint);
        }
    }

    /**
     * This {@link GameService.Listener} renders game events to the screen.
     */
    private class GameListener implements GameService.Listener {

        final Context context;

        /**
         * Construct a game listener with a context on which to show errors.
         * @param context the context on which to show errors
         */
        public GameListener(final Context context) {
            this.context = context;
        }

        @Override
        public void onGame(Game game) {
            setGame(game);
        }

        @Override
        public void onError(Exception exception) {
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setTitle("Error");
            alertBuilder.setMessage("Unable to get game from server: " + exception.getMessage());
            alertBuilder.show();
        }
    }

    /**
     * This {@link OnTouchListener} handles touches on the checkers game board.
     */
    private class CheckersOnTouchListener implements OnTouchListener {
        private final GameService gameService = GameServiceImpl.getInstance();
        private final BoardView boardView;

        private Piece selectedPiece;
        private List<Location> moves;

        /**
         * Construct a {@link CheckersOnTouchListener} with the provided {@link BoardView}.
         * @param boardView the {@link BoardView} to operate upon
         */
        public CheckersOnTouchListener(final BoardView boardView) {
            this.boardView = boardView;
        }

        @Override
        public boolean onTouch(final View view, final MotionEvent motionEvent) {
            if (motionEvent.getAction() != MotionEvent.ACTION_DOWN) {
                return true;
            }

            if (boardView.getGame() == null) {
                return false;
            }

            final Game game = boardView.getGame();
            final Board board = game.getBoard();

            final float x = motionEvent.getX();
            final float y = motionEvent.getY();

            final int boardWidth = boardView.getBoardWidth();
            final float squareWidth = (float) boardWidth / board.getSpacesPerSide();

            final Location location = new Location((int) Math.floor(x / squareWidth), (int) Math.floor(y / squareWidth));
            final Piece piece = board.getPieceAtLocation(location);

            //TODO: case where king piece comes around to it's original spot
            if (piece != null && piece.getTeam() != game.getTurn()) {
                return true;
            } else if (piece != null) {
                selectedPiece = piece;
                moves = new ArrayList<>();
            } else if (selectedPiece != null && moves.size() > 0 && moves.get(moves.size() - 1).equals(location)) {
                Rules.move(game, selectedPiece, moves);
                gameService.move(game, new GameListener(boardView.getContext()));
                selectedPiece = null;
                moves = new ArrayList<>();
                game.clearTurn();
            } else if (selectedPiece != null && Rules.isValidMove(selectedPiece, moves, location, board)) {
                moves.add(location);
            }

            setSelectedPiece(selectedPiece);
            setMoves(moves);

            return true;
        }
    }
}
