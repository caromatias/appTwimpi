package com.example.apptwimpi;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class SettingsActivity extends Activity {
	
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Button btn4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		btn1=(Button)findViewById(R.id.button1);
		btn2=(Button)findViewById(R.id.button2);
		btn3=(Button)findViewById(R.id.button3);
		btn4=(Button)findViewById(R.id.button4);
		
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i= new Intent(SettingsActivity.this, NotificacionesActivity.class);
				startActivity(i);
			}
		});
		
		btn2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i= new Intent(SettingsActivity.this, EditProfileActivity.class);
				startActivity(i);
			}
		});
		
		btn3.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i= new Intent(SettingsActivity.this, HelpActivity.class);
				startActivity(i);
			}
		});
		btn4.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i= new Intent(SettingsActivity.this, AcercaDeActivity.class);
				startActivity(i);
			}
		});
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
