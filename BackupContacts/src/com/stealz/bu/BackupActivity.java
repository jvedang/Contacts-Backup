package com.stealz.bu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class BackupActivity extends Activity implements OnClickListener,Runnable
{
	TextView textTotalContacts,textContactName,textContactCount,textLastBUDate,textAutoBU;
	LinearLayout linearSettings,linearHelp,linearContacts;
	Button btnBU;

	String buttonFunction = "Backup",name,autoBU,lastBUDate;
	int currentCount,count;
	CommonMethods cm;
	private Handler mHandler = new Handler();
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.backup);

		cm = new CommonMethods();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		autoBU = prefs.getString("autoBU","Auto Backup Settings Not Available");
		lastBUDate = prefs.getString("lastBackupDate", "Last Backup Date Not Available");
		
		initializeLayouts();
		
		textAutoBU.setText(autoBU);
		if(lastBUDate.equals("Last Backup Date Not Available"))
		{
			textLastBUDate.setText(lastBUDate);
		}
		else
		{
			textLastBUDate.setText("Last Backup Done on "+lastBUDate);
		}
		
		
		count = totalContacts(this);

		btnBU.setOnClickListener(this);
		linearHelp.setOnClickListener(this);
		linearSettings.setOnClickListener(this);

		linearContacts.setVisibility(View.GONE);
		
		textTotalContacts.setText(count+" Contacts");
	}

	public void initializeLayouts()
	{
		textTotalContacts = (TextView)findViewById(R.id.txtTotalContacts);
		textContactName = (TextView)findViewById(R.id.txtContactName);
		textContactCount = (TextView)findViewById(R.id.txtContactCount);
		textAutoBU = (TextView)findViewById(R.id.textAutoBU);
		textLastBUDate = (TextView)findViewById(R.id.txtLastBUDate);

		linearSettings = (LinearLayout)findViewById(R.id.linearSettings);
		linearHelp = (LinearLayout)findViewById(R.id.linearFAQ);
		linearContacts = (LinearLayout)findViewById(R.id.linearContacts);

		btnBU = (Button)findViewById(R.id.btnBU);
	}

	@Override
	public void onClick(View view) 
	{
		Intent i;
		int id = view.getId();
		switch (id) {
		case R.id.btnBU:
			if(btnBU.getText().toString().equals("Backup"))
				startBackup();
			else if(btnBU.getText().toString().equals("Send"))
			{
				String email = prefs.getString("email", "");
				cm.sendEmail(BackupActivity.this, new String[]{email});
			}
				
			break;
			
		case R.id.linearFAQ:
			i = new Intent(BackupActivity.this,Help.class);
			startActivity(i);
			break;

		case R.id.linearSettings:
			i = new Intent(BackupActivity.this,SettingBU.class);
			finish();
			startActivity(i);
			break;
		default:
			break;
		}

	}

	@Override
	public void run()
	{
		boolean backupCompleted = createBackup(BackupActivity.this);
		if(backupCompleted)
			handler.sendEmptyMessage(0);
		else
			handler.sendEmptyMessage(1);
	}

	//Normal Java Methods //
	public void startBackup()
	{
		btnBU.setText("Started");
		btnBU.setEnabled(false);
		Thread th = new Thread(BackupActivity.this);
		th.start();
	}

	public int totalContacts(Context context)
	{
		int count = 0;
		// Get all the Contact Details in a Cursor.
		Cursor phones = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		count = phones.getCount();
		return count;
	}

	private boolean createBackup(Context context)
	{
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

			Cursor phones =  context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI, null, null,
					null, null);

			phones.moveToFirst();
			Log.i("Number of contacts", "cursorCount" + phones.getCount());
			for (int i = 0; i < phones.getCount(); i++) 
			{
				String lookupKey = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));

				name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

				currentCount = i;

				mHandler.post(new Runnable()
				{
					@Override
					public void run()
					{
						linearContacts.setVisibility(View.VISIBLE);
						textContactName.setText(name+"");
						textContactCount.setText((currentCount+1) + "/"+count);
					}
				});


				Log.i("lookupKey", " " + lookupKey);

				Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey);

				AssetFileDescriptor fd;


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
			
			Log.i("TAG", "No Contacts in Your Phone");
			
			Editor edit = prefs.edit();

			DateFormat dateFormat = new SimpleDateFormat("dd MMMM, yyyy hh:mm aa");
			edit.putString("lastBackupDate", dateFormat.format(new Date()));
			edit.commit();

			return true;
		}
		catch(Exception e)
		{
			Log.e("ContactsBackup==>BackupActivity==>run()", e.getMessage());
			return false;
		}
	}

	private Handler handler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			if(msg.what == 0)
			{
				btnBU.setText("Send");
				btnBU.setEnabled(true);
				linearContacts.setVisibility(View.GONE);
				
				lastBUDate = prefs.getString("lastBackupDate", "Last Backup Date Not Available");
				textLastBUDate.setText("Last Backup Done on "+lastBUDate);
			}
			else
			{ 
				btnBU.setText("Backup");
				btnBU.setEnabled(true);
				linearContacts.setVisibility(View.GONE);
				Toast.makeText(BackupActivity.this, "Unable to complete Backup", Toast.LENGTH_SHORT).show();
			}
		}
	};
}