package com.plustech.print;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


public class PrintApplication extends Application{

    private static Context context;
    private static int screenActivity ;

    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getAppContext() {
        return context;
    }

	public int getScreenActivity() {
		return screenActivity;
	}

	public void setScreenActivity(int screenActivity) {
		PrintApplication.screenActivity = screenActivity;
	}
	
	
    
}