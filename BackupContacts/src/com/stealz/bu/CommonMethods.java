package com.stealz.bu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

public class CommonMethods 
{
	private String strMonth[] = { "JANUARY", "FEBRUARY", "MARCH", "APRIL",
			"MAY", "JUNE", "JULY", "AUGUST", "SEPTEBMBER", "OCTOBER",
			"NOVEMBER", "DECEMBER" };
	
	public String setCorrectTime(int hourOfTheDay,int minutes)
	{
		String min = "";
		String am_pm = "";
		if(hourOfTheDay >12)
		{
			hourOfTheDay = hourOfTheDay - 12;
			am_pm = "PM";
		}
		else if(hourOfTheDay == 0)
		{
			hourOfTheDay = 12;
			am_pm = "AM";
		}
		else if(hourOfTheDay == 12)
		{
			hourOfTheDay = 12;
			am_pm = "PM";
		}
		else
		{
			am_pm = "AM";
		}
		
		if(minutes < 10)
		{
			min = "0"+minutes;
		}
		else
		{
			min = minutes+"";
		}
		return hourOfTheDay+":"+min+" "+am_pm;
	}
	
	public boolean createBackup(Context context)
	{
		boolean backupDone = true;
		try
		{
			String path = null;
			String vfile = null;
			
			File file = new File(Environment.getExternalStorageDirectory()
					.toString() + File.separator + "ContactsBackup");
			if (!file.exists())
			{
				file.mkdir();
			}
			vfile = "ContactsBackup" + File.separator + "Contacts.vcf";

			File vfileFullPath = new File(Environment.getExternalStorageDirectory()
					.toString()
					+ File.separator
					+ "ContactsBackup"
					+ File.separator + "Contacts.vcf");
			if (vfileFullPath.exists()) {
				vfileFullPath.delete();
			}

			Cursor phones = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null,
					null, null);

			phones.moveToFirst();
			Log.i("Number of contacts", "cursorCount" + phones.getCount());
			for (int i = 0; i < phones.getCount(); i++) 
			{
				String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				
				Log.i("lookupKey", " " + lookupKey);
				
				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);
				
				AssetFileDescriptor fd;

				try
				{
					fd = context.getContentResolver().openAssetFileDescriptor(uri,
							"r");
					FileInputStream fis = fd.createInputStream();
					byte[] buf = new byte[(int) fd.getDeclaredLength()];
					fis.read(buf);
					String VCard = new String(buf);

					path = Environment.getExternalStorageDirectory().toString()
							+ File.separator + vfile;
					FileOutputStream mFileOutputStream = new FileOutputStream(path,
							true);
					mFileOutputStream.write(VCard.toString().getBytes());

					phones.moveToNext();

				} 
				catch (Exception e1)
				{
					e1.printStackTrace();
					backupDone = false;
				}
			}
			Log.i("TAG", "No Contacts in Your Phone");
			SharedPreferences prefs;
			prefs = PreferenceManager.getDefaultSharedPreferences(context);
			Editor edit = prefs.edit();
			
			DateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy hh:mm aa");
			edit.putString("lastBackupDate", dateFormat.format(new Date()));
			edit.commit();
		
		}
		catch(Exception e)
		{
			Log.e("BackupContacts==>BackupScreen==>run()", e.getMessage());
			backupDone = false;
		}
		return backupDone;
	}

	/**
	 * @return <b>dateTimeStamp</b> - Current time in DD MMMM YYYY, HH:MM:SS
	 *         AM/PM Format,<br>
	 *         <b>eg :</b> 8 March 2013, 12:32:45 PM
	 * 
	 * */
	public String saveCurrentDateTimeStamp() {
		String dateTimeStamp = "";
		Calendar calendar = Calendar.getInstance();
		int seconds = calendar.get(Calendar.SECOND);
		int minutes = calendar.get(Calendar.MINUTE);
		int hours = calendar.get(Calendar.HOUR);
		hours = hours == 0 ? 12 : hours;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int month = calendar.get(Calendar.MONTH);
		String strMonthNow = strMonth[month];
		int year = calendar.get(Calendar.YEAR);
		String am_pm = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";

		dateTimeStamp = day + " " + strMonthNow + " " + year + ", " + hours
				+ ":" + minutes + ":" + seconds + " " + am_pm;

		return dateTimeStamp;
	}

	/**
	 * Sends mail to Specified Contact.
	 * */
	boolean sendEmail(Context context,String[] sendTo)
	{
		try
		{
			String filelocation = "ContactsBackup+"+File.separator+"Contacts.vcf";
			Intent sharingIntent = new Intent(Intent.ACTION_SEND);
			sharingIntent.setType("text/x-vcard");
			
			String[] to = sendTo;
			sharingIntent.putExtra(Intent.EXTRA_EMAIL, to);
			Uri uri = Uri.fromFile(new File(Environment
					.getExternalStorageDirectory(), filelocation));
			sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
			sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
					"Android Contacts BackUp");
			sharingIntent
					.putExtra(
							Intent.EXTRA_TEXT,
							"Hi,\n\n Please find Contacts.vcf BackUp File attached with this Mail "
										+ " \n\n Thanks,\n\nSimple Backup");
			context.startActivity(Intent.createChooser(sharingIntent, "Sending options.."));
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
