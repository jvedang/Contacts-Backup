package com.stealz.bu;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotificationService extends Service 
{
	@Override
	public IBinder onBind(Intent arg0)
	{return null;}

	@Override
	public void onCreate()
	{super.onCreate();}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		CommonMethods cbu = new CommonMethods();
		boolean buSaved = cbu.createBackup(NotificationService.this);
		
		//If the Backup is Successfull
		if(buSaved)
		{
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			
			//String dateTimeStamp = cbu.saveCurrentDateTimeStamp();
			// Stores the Backup Date.
			Editor edit = prefs.edit();
					
			DateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy hh:mm aa");
			edit.putString("lastBackupDate", dateFormat.format(new Date()));
			edit.commit();
		
			String strEmail = prefs.getString("email","");
				String filelocation = "ContactsBackup/Contacts.vcf";
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("text/x-vcard");
				String[] to = { strEmail };
				sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
				Uri uri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory(), filelocation));
				sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
				sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
						"Android Contacts BackUp");
				
				sharingIntent
						.putExtra(
								Intent.EXTRA_TEXT,
								"Hi,\n\nPlease find Contacts.vcf BackUp File attached with this Mail "
										+ " \n\n Thanks,\n\nSimple Backup");
				PendingIntent pIntent = PendingIntent.getActivity(this, 0,
						sharingIntent, 0);

				Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				
				long pattern[] = {50,100,150,200};
				
				Notification noti = new NotificationCompat.Builder(this)
						.setContentTitle("Contacts Backup")
						.setContentText("Backup Complete, Send Now")
						.setSmallIcon(R.drawable.icon2_noti)
						.setSound(ringtoneUri)
						.setVibrate(pattern)
						.setContentIntent(pIntent).build();

				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				// Hide the notification after its selected
				noti.flags |= Notification.FLAG_AUTO_CANCEL;

				notificationManager.notify(0, noti);
		} 
		else
		{
			intent = new Intent(this,BackupActivity.class);

			Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			
			long pattern[] = {50,100,150,200};
			
			Intent i = new Intent(NotificationService.this,BackupActivity.class);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0,
					i, 0);

			
			Notification noti = new NotificationCompat.Builder(this)
			.setContentTitle("Contacts Backup")
			.setContentText("Unable to Complete Backup")
			.setSmallIcon(R.drawable.icon2_noti)
			.setSound(ringtoneUri)
			.setVibrate(pattern)
			.setContentIntent(pIntent).build();

			NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

			// Hide the notification after its selected
			noti.flags |= Notification.FLAG_AUTO_CANCEL;

			notificationManager.notify(0, noti);
		}

		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
