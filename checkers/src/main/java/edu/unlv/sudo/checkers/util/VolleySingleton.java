package edu.unlv.sudo.checkers.util;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import edu.unlv.sudo.checkers.CheckersApplication;

/**
 * This manages the request queue for Volley.
 */
public class VolleySingleton {

    private static VolleySingleton instance;

    private RequestQueue requestQueue;

    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(CheckersApplication.getAppContext());
        requestQueue.start();
    }

    /**
     * @return the instance of the {@link VolleySingleton}.
     */
    public static VolleySingleton getInstance() {
        return instance == null ? instance = new VolleySingleton() : instance;
    }

    /**
     * @return the {@link RequestQueue} for this singleton.
     */
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    protected void finalize() throws Throwable {
        requestQueue.stop();
        super.finalize();
    }
}
