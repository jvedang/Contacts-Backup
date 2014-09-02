package com.stealz.bu;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingBU extends Activity implements OnClickListener
{
	TextView textSetTime;
	Spinner spinnerSetDay;
	EditText editSetMail;

	TimePickerDialog.OnTimeSetListener t;
	Calendar cal;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		cal = Calendar.getInstance();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		textSetTime = (TextView)findViewById(R.id.txtSetTime);
		spinnerSetDay = (Spinner)findViewById(R.id.spinSetDay);
		editSetMail = (EditText)findViewById(R.id.editSetMail);

		textSetTime.setOnClickListener(this);

		t = new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfTheDay, int minute) 
			{

				cal.set(Calendar.HOUR_OF_DAY, hourOfTheDay);
				cal.set(Calendar.MINUTE,minute);
				CommonMethods cm = new CommonMethods();
				String correctTime = cm.setCorrectTime(hourOfTheDay, minute);
				textSetTime.setText(correctTime);
			}
		};

	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		switch (id)
		{
		case R.id.txtSetTime:
			new TimePickerDialog(SettingBU.this, t,12, 00, false).show();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed()
	{
		Editor edit = prefs.edit();
		if(!editSetMail.getText().toString().equals(""))
		{
			String email = editSetMail.getText().toString();
			edit.putString("email", email);
			edit.commit();
		}

		if(validateAllValues())
		{
			try
			{
				String autoBUDate = "Auto - Backup Scheduled on "+spinnerSetDay.getSelectedItem().toString()+" @ "+textSetTime.getText().toString();
				edit.putString("autoBU",autoBUDate);
				edit.commit();

				int dayOfTheWeek = (int)spinnerSetDay.getSelectedItemId();
				cal.set(Calendar.DAY_OF_WEEK,dayOfTheWeek);

				AlarmReceiver.stopService(this);

				Intent myIntent = new Intent(SettingBU.this, AlarmReceiver.class);

				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						SettingBU.this, 0, myIntent, 0);

				AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

				alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),7*24*60*60*1000 , pendingIntent);

				Toast.makeText(SettingBU.this,"Settings Saved", Toast.LENGTH_SHORT).show();
			}
			catch(Exception e)
			{
				Toast.makeText(SettingBU.this,"Settings Not Saved", Toast.LENGTH_SHORT).show();
			}
		}



		Intent i = new Intent(SettingBU.this,BackupActivity.class);
		finish();
		startActivity(i);
	}

	private boolean validateAllValues()
	{
		boolean result = true;
		if(spinnerSetDay.getSelectedItemPosition() == 0)
		{
			result = false;
		}
		else if(textSetTime.getText().toString().equals("Set Time"))
		{
			result = false;
		}
		return result;
	}
}
