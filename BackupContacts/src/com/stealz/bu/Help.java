package com.stealz.bu;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Help extends Activity implements OnClickListener
{
	TextView textAns1,textAns2,textAns3,textAns4,textAns5a,textAns5b,textAns5c;
	TextView textQue1,textQue2,textQue3,textQue4,textQue5;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq);

		textQue1 = (TextView)findViewById(R.id.textQue1);
		textQue2 = (TextView)findViewById(R.id.textQue2);
		textQue3 = (TextView)findViewById(R.id.textQue3);
		textQue4 = (TextView)findViewById(R.id.textQue4);
		textQue5 = (TextView)findViewById(R.id.textQue5);

		textAns1 = (TextView)findViewById(R.id.textAns1);
		textAns2 = (TextView)findViewById(R.id.textAns2);
		textAns3 = (TextView)findViewById(R.id.textAns3);
		textAns4 = (TextView)findViewById(R.id.textAns4);
		textAns5a = (TextView)findViewById(R.id.textAns5A);
		textAns5b = (TextView)findViewById(R.id.textAns5B);
		textAns5c = (TextView)findViewById(R.id.textAns5C);

		textQue1.setOnClickListener(this);
		textQue2.setOnClickListener(this);
		textQue3.setOnClickListener(this);
		textQue4.setOnClickListener(this);
		textQue5.setOnClickListener(this);

	}

	@Override
	public void onClick(View view)
	{
		int id = view.getId();
		switch (id) {
		case R.id.textQue1:
			if(textAns1.getVisibility()==View.VISIBLE)
				textAns1.setVisibility(View.GONE);
			else
				textAns1.setVisibility(View.VISIBLE);
			textAns2.setVisibility(View.GONE);
			textAns3.setVisibility(View.GONE);
			textAns4.setVisibility(View.GONE);
			textAns5a.setVisibility(View.GONE);
			textAns5b.setVisibility(View.GONE);
			textAns5c.setVisibility(View.GONE);
			break;

		case R.id.textQue2:
			if(textAns2.getVisibility()==View.VISIBLE)
				textAns2.setVisibility(View.GONE);
			else
				textAns2.setVisibility(View.VISIBLE);
			textAns1.setVisibility(View.GONE);
			textAns3.setVisibility(View.GONE);
			textAns4.setVisibility(View.GONE);
			textAns5a.setVisibility(View.GONE);
			textAns5b.setVisibility(View.GONE);
			textAns5c.setVisibility(View.GONE);
			break;

		case R.id.textQue3:
			if(textAns3.getVisibility()==View.VISIBLE)
				textAns3.setVisibility(View.GONE);
			else
				textAns3.setVisibility(View.VISIBLE);
			textAns1.setVisibility(View.GONE);
			textAns2.setVisibility(View.GONE);
			textAns4.setVisibility(View.GONE);
			textAns5a.setVisibility(View.GONE);
			textAns5b.setVisibility(View.GONE);
			textAns5c.setVisibility(View.GONE);
			break;

		case R.id.textQue4:
			if(textAns4.getVisibility()==View.VISIBLE)
				textAns4.setVisibility(View.GONE);
			else
				textAns4.setVisibility(View.VISIBLE);
			textAns1.setVisibility(View.GONE);
			textAns2.setVisibility(View.GONE);
			textAns3.setVisibility(View.GONE);
			textAns5a.setVisibility(View.GONE);
			textAns5b.setVisibility(View.GONE);
			textAns5c.setVisibility(View.GONE);
			break;

		case R.id.textQue5:
			if(textAns5a.getVisibility()==View.VISIBLE && textAns5a.getVisibility()==View.VISIBLE && textAns5a.getVisibility()==View.VISIBLE)
			{
				textAns5a.setVisibility(View.GONE);
				textAns5b.setVisibility(View.GONE);
				textAns5c.setVisibility(View.GONE);
			}
			else
			{
				textAns5a.setVisibility(View.VISIBLE);
				textAns5b.setVisibility(View.VISIBLE);
				textAns5c.setVisibility(View.VISIBLE);
			}
			textAns1.setVisibility(View.GONE);
			textAns2.setVisibility(View.GONE);
			textAns3.setVisibility(View.GONE);
			textAns4.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}



}
