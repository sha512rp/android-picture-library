package ws.krizek.android.picturelibrary.util;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static Context context;
    private static Context browseActivity;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    public static Context getBrowseActivity() {return browseActivity;}

    public static void setBrowseActivity(Context browseActivity) {
        MyApplication.browseActivity = browseActivity;
    }
}