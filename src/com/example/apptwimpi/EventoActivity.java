package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptwimpi.MainActivity.UserLoginTask;
import com.example.apptwimpi.RegisterActivity.UserRegisterTask;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class EventoActivity extends ListActivity {

	private CreateEventTask mAuthTask = null;
	private CreateEventTask mCreateTask = null;
	private Handler handler;

	private TextView mLoginStatusMessageView;
	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombreAmigos;
	String[] idAmigos;
	String[] amigosSeleccionados;
	Button btn_tp;
	private Button CreateButton;
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
	private View mCreateEventFormView;
	private View mCreateEventStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evento);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		mCreateEventFormView = findViewById(R.id.scrollView1);
		mCreateEventStatusView = findViewById(R.id.refresh_status);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

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

		ListView lview = getListView();
		lview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lview.setTextFilterEnabled(true);
		EditNombre = (EditText) findViewById(R.id.edtNombre);
		EditDescripcion = (EditText) findViewById(R.id.edtDescripcion);
		EditCupos = (EditText) findViewById(R.id.edtCupos);

		String fqlQuery = "select uid, name, pic_square, is_app_user from user where uid in (select uid2 from friend where uid1 = me())";
		Bundle params = new Bundle();
		Session session = Session.getActiveSession();
		params.putString("q", fqlQuery);
		params.putString("access_token", user.get(SessionManager.KEY_EMAIL));

		Request request = new Request(session, "/fql", params, HttpMethod.GET,
				new Request.Callback() {
					public void onCompleted(Response response) {
						Log.i("AMIGOS FQL",
								"Got results: " + response.toString());
						try {
							GraphObject go = response.getGraphObject();
							JSONObject jso = go.getInnerJSONObject();
							JSONArray arr = jso.getJSONArray("data");

							// ListView lista = (ListView)
							// findViewById(R.id.list_amigos);
							// lista.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
							/*
							 * ListView lview = getListView();
							 * lview.setChoiceMode
							 * (ListView.CHOICE_MODE_MULTIPLE);
							 * lview.setTextFilterEnabled(true);
							 * ArrayList<Amigo> arraydir = new
							 * ArrayList<Amigo>(); Amigo directivo;
							 * 
							 * 
							 * for (int i = 0; i < (arr.length()); i++) {
							 * JSONObject json_obj = arr.getJSONObject(i);
							 * 
							 * directivo = new Amigo(json_obj
							 * .getString("pic_square"), json_obj
							 * .getString("name"), "Presidenta");
							 * arraydir.add(directivo); Log.i("SOSO",
							 * json_obj.getString("name"));
							 * 
							 * }
							 * 
							 * // Creo el adapter personalizado AdapterAmigo
							 * adapter = new AdapterAmigo( EventoActivity.this,
							 * arraydir);
							 * 
							 * // Lo aplico lview.setAdapter(adapter);
							 */
							nombreAmigos = new String[arr.length()];
							idAmigos = new String[arr.length()];
							for (int i = 0; i < (arr.length()); i++) {
								JSONObject json_obj = arr.getJSONObject(i);
								Log.i("SOSO", json_obj.getString("name"));
								// Arrays.fill(nombreAmigos,
								// json_obj.getString("name"));
								nombreAmigos[i] = json_obj.getString("name");
								idAmigos[i] = json_obj.getString("uid");
							}
							/*
							 * for(String log : nombreAmigos) {
							 * Log.v("FREEDOM",log); }
							 */
							setListAdapter(new ArrayAdapter<String>(
									EventoActivity.this,
									android.R.layout.simple_list_item_multiple_choice,
									nombreAmigos));

							// nombreAmigos =
							// getResources().getStringArray(R.array.nav_options);

						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				});
		Request.executeBatchAsync(request);
		handler = new Handler();
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		// ---toggle the check displayed next to the item---
		String s = "";
		asistentes = "";
		int len = parent.getCount();
		SparseBooleanArray checked = parent.getCheckedItemPositions();

		for (int i = 0; i < len; i++) {
			if (checked.get(i)) {
				String item = idAmigos[i];
				s = s + " " + item;
				asistentes = asistentes + "" + item + ";";
				/* do whatever you want with the checked item */
			}
		}
		Toast.makeText(this, "Amigos selecionados- " + asistentes,
				Toast.LENGTH_SHORT).show();
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
		getMenuInflater().inflate(R.menu.evento, menu);
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
			/*
			 * Runnable runnable = new Runnable() {
			 * 
			 * @Override public void run() { handler.post(new Runnable() { //
			 * This thread runs in the UI
			 * 
			 * @Override public void run() { boolean exito = false; EditNombre =
			 * (EditText)findViewById(R.id.edtNombre); Nombre =
			 * EditNombre.getText().toString(); EditDescripcion =
			 * (EditText)findViewById(R.id.edtDescripcion); Descripcion =
			 * EditDescripcion.getText().toString(); EditCupos =
			 * (EditText)findViewById(R.id.edtCupos); Cupos =
			 * EditCupos.getText().toString(); ArrayList parametros = new
			 * ArrayList(); parametros.add("NombreEvento");
			 * parametros.add(Nombre); parametros.add("DescripcionEvento");
			 * parametros.add(Descripcion); parametros.add("CuposEvento");
			 * parametros.add(Cupos); parametros.add("FechaEvento");
			 * parametros.add(date); parametros.add("EventoCreador");
			 * parametros.add(user.get(SessionManager.KEY_NAME));
			 * parametros.add("Asistentes"); parametros.add(asistentes);
			 * 
			 * Log.d("LOG", parametros.toString());
			 * 
			 * JSONParseo jParseo = new JSONParseo();
			 * 
			 * String URL =
			 * "http://www.pisodigital.cl/twimpiweb/createEvent.php";
			 * 
			 * JSONObject json = jParseo.recibir(URL, "post", parametros);
			 * 
			 * try { String success = json.getString("success"); Log.e("LOG",
			 * json.getString("success")); if (success.equals("0")) { exito =
			 * true; }
			 * 
			 * } catch (Exception error) { exito = false;
			 * Toast.makeText(getApplicationContext(), "error:" +
			 * error.getLocalizedMessage(), Toast.LENGTH_LONG).show(); } } }); }
			 * }; new Thread(runnable).start();
			 */
			validaCampos();
			/*
			showProgress(true);
			mCreateTask = new CreateEventTask();
			mCreateTask.execute((Void) null);
			*/
			break;

		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */

	public void validaCampos() {

		EditNombre.setError(null);
		EditDescripcion.setError(null);
		EditCupos.setError(null);
		Nombre = EditNombre.getText().toString();
		Descripcion = EditDescripcion.getText().toString();
		Cupos = EditCupos.getText().toString();

		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(Nombre)) {
			EditNombre.setError(getString(R.string.error_field_required));
			focusView = EditNombre;
			cancel = true;
		} else {

			cancel = false;
		}
		
		if (TextUtils.isEmpty(Descripcion)) {
			EditDescripcion.setError(getString(R.string.error_field_required));
			focusView = EditDescripcion;
			cancel = true;
		} else {

			cancel = false;
		}
		
		if (TextUtils.isEmpty(Cupos)) {
			EditCupos.setError(getString(R.string.error_field_required));
			focusView = EditCupos;
			cancel = true;
		} else {

			cancel = false;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mCreateTask = new CreateEventTask();
			mCreateTask.execute((Void) null);
		}

	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mCreateEventStatusView.setVisibility(View.VISIBLE);
			mCreateEventStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateEventStatusView
									.setVisibility(show ? View.VISIBLE
											: View.GONE);
						}
					});

			mCreateEventFormView.setVisibility(View.VISIBLE);
			mCreateEventFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateEventFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mCreateEventStatusView.setVisibility(show ? View.VISIBLE
					: View.GONE);
			mCreateEventFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
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
			
			Nombre = EditNombre.getText().toString();
			Descripcion = EditDescripcion.getText().toString();
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

			Intent i = new Intent(EventoActivity.this, DrawableActivity.class);
			startActivity(i);
			showProgress(false);
			if (success) {
				// finish();
				showProgress(false);
			} else {
				// showProgress(false);
			}
		}

		@Override
		protected void onCancelled() {

		}
	}
}
