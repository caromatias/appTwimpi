package com.example.apptwimpi;

import java.util.HashMap;

import com.example.apptwimpi.EventoActivity.CreateEventTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends Activity {

	SessionManager session;
	HashMap<String, String> user;

	
	private String[] nombres;
	private String[] descripcion;
	private String[] picPerfil;
	
	private TextView txt1;
	private TextView txt2;
	private ImageView img;
	private ImageLoader imgLoader;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();
		
		
		txt1 = (TextView) findViewById(R.id.nombre_user);
		txt2 = (TextView) findViewById(R.id.txt_biografia);
		img = (ImageView) findViewById(R.id.image_perfil);
		imgLoader = new ImageLoader(this);
		
		Bundle bundle = getIntent().getExtras();
		txt1.setText(bundle.getString("nombrePerfil"));
		txt2.setText(bundle.getString("Biografia"));
		imgLoader.DisplayImage(bundle.getString("fotografia"), img);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.profile, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
