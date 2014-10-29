package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import com.example.apptwimpi.EventoActivity.CreateEventTask;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class CreateEventActivity extends Activity {

	private CreateEventTask mCreateTask = null;
	private ProgressDialog pDialog;
	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	static final int DATE_DIALOG_ID = 0;
	private int mYear, mMonth, mDay;
	private String date;
	private String Nombre;
	private String Descripcion;
	private String Cupos;
	private String asistentes = "";
	private EditText EditNombre;
	private EditText EditDescripcion;
	private EditText EditCupos;
	private Button btn_tp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_event);
		
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();
		
		pDialog = new ProgressDialog(CreateEventActivity.this);
		pDialog.setMessage("Creando Evento...");
		pDialog.setCancelable(false);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		
		btn_tp = (Button) findViewById(R.id.btn_fecha);
		btn_tp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});
		
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
		
		Bundle b=this.getIntent().getExtras();
		String[] items=b.getStringArray("IdAmigosT");
		for (String item : items)
		{
			if(!user.get(SessionManager.KEY_NAME).equals(item)){
				asistentes = asistentes +";"+ item;
			}
		}
		asistentes = asistentes.substring(1)+";";
		Log.d("ESTO ES!", asistentes.substring(1));
	}
	
	private void updateDisplay() {

		// add +1 to mMonth, because months start with 0
		date = pad(mYear) + "-" + pad(mMonth + 1) + "-" + pad(mDay);
		btn_tp.setText(date);
	}

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_event, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.create_event:
			pDialog.show();
			mCreateTask = new CreateEventTask();
			mCreateTask.execute((Void) null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public class CreateEventTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			/*
			 * try { // Simulate network access. Thread.sleep(2000);
			 * 
			 * } catch (InterruptedException e) { return false; }
			 */
			boolean exito = false;
			EditNombre = (EditText) findViewById(R.id.edtNombre);
			Nombre = EditNombre.getText().toString();
			EditDescripcion = (EditText) findViewById(R.id.edtDescripcion);
			Descripcion = EditDescripcion.getText().toString();
			EditCupos = (EditText) findViewById(R.id.edtCupos);
			Cupos = EditCupos.getText().toString();
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("NombreEvento");
			parametros.add(Nombre);
			parametros.add("DescripcionEvento");
			parametros.add(Descripcion);
			parametros.add("CuposEvento");
			parametros.add(Cupos);
			parametros.add("FechaEvento");
			parametros.add(date);
			parametros.add("EventoCreador");
			parametros.add(user.get(SessionManager.KEY_NAME));
			parametros.add("Asistentes");
			parametros.add(asistentes);

			Log.d("LOG", parametros.toString());

			JSONParseo jParseo = new JSONParseo();

			String URL = "http://www.pisodigital.cl/twimpiweb/createEvent.php";

			JSONObject json = jParseo.recibir(URL, "post", parametros);

			try {
				String success = json.getString("success");
				Log.e("LOG", json.getString("success"));
				if (success.equals("0")) {
					exito = true;
				}

			} catch (Exception error) {
				exito = false;
				Toast.makeText(getApplicationContext(),
						"error:" + error.getLocalizedMessage(),
						Toast.LENGTH_LONG).show();
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			Intent i = new Intent(CreateEventActivity.this,
					DrawableActivity.class);
			startActivity(i);
			pDialog.dismiss();
			if (success) {
				// finish();
				pDialog.dismiss();
			} else {
				// showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {

		}
	}
}
