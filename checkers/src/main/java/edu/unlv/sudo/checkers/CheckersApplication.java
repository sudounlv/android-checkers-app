package edu.unlv.sudo.checkers;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

/**
 * This class keeps track of any global information used in the application.
 */
public class CheckersApplication extends Application {
    private static CheckersApplication instance;
    private static Context appContext;
    private static ContentResolver contentResolver;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
        appContext = getApplicationContext();
        contentResolver = getContentResolver();
    }

    /**
     * @return the instance for this application.
     */
    public static CheckersApplication getInstance(){
        return instance;
    }

    /**
     * @return the application context.
     */
    public static Context getAppContext() {
        return appContext;
    }

    /**
     * @return the unique identifier for this device.
     */
    public static String getDeviceUid() {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
    }
}
