package com.stealz.bu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver 
{

	@Override
	public void onReceive(Context context, Intent arg1) 
	{
		// TODO Auto-generated method stub
		Intent myIntent = new Intent(context, NotificationService.class);
        context.startService(myIntent);

	}

	/**
	This method stops the NotificationService
	@param context - The Context required to start the Service
	 * **/
	public static void stopService(Context context)
	{
		Intent myIntent = new Intent(context,NotificationService.class);
		context.stopService(myIntent);
	}
	
	/**
	This method starts the NotificationService
	@param context - The Context required to start the Service 
	 * **/
	public static void startService(Context context)
	{
		Intent myIntent = new Intent(context,NotificationService.class);
		context.startService(myIntent);
	}
}
