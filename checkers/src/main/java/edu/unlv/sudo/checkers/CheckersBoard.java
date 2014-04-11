package edu.unlv.sudo.checkers;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import edu.unlv.sudo.checkers.model.Game;
import edu.unlv.sudo.checkers.model.Team;
import edu.unlv.sudo.checkers.service.GameService;
import edu.unlv.sudo.checkers.service.impl.GameServiceImpl;
import edu.unlv.sudo.checkers.views.BoardView;

public class CheckersBoard extends ActionBarActivity {

    private final GameService gameService = GameServiceImpl.getInstance();
    private static int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkers_board);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new CheckersFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.checkers_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final GameService.Listener listener = new GameListener(this);

        if (id == R.id.action_create) {
            gameService.newGame(Team.RED, listener);
            return true;
        } else if (id == R.id.action_join) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Join Game");
            builder.setMessage("Please enter the ID of the game to join");

            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gameService.joinGame(input.getText().toString(), Team.BLACK, listener);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //nothing to do here
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Display a notification to the user
     * @param title the title of the notification
     * @param message the message of the notification
     * @return the ID of the notification
     */
    private int showNotification(final String title, final String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        final int notificationId = CheckersBoard.notificationId++;

        //set up the activity stack so that backing out of the application works right
        final Intent intent = new Intent(this, CheckersBoard.class);

        final TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(CheckersBoard.class);
        stackBuilder.addNextIntent(intent);

        final PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(pendingIntent);

        //get the notification manager and publish this notification
        final NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, builder.build());

        return notificationId;
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
            final BoardView boardView = (BoardView) findViewById(R.id.checkers_board);
            boardView.setGame(game);

            showNotification("New Checkers Game Ready", "Game ID: " + game.getId());
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
     * A placeholder fragment containing a simple view.
     */
    public class CheckersFragment extends Fragment {

        public CheckersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_checkers_board, container, false);
        }
    }

}
