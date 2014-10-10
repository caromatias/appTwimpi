package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

public class CreateGroupActivity extends ListActivity {

	private CreateGroupTask mCreateTask = null;

	// Session Manager Class
	SessionManager session;
	HashMap<String, String> user;
	String[] nombreAmigos;
	String[] idAmigos;
	String[] amigosSeleccionados;
	private EditText EditNombre;
	private String Nombre;
	private String asistentes = "";
	private View mCreateGroupFormView;
	private View mCreateGroupStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_group);
		
		mCreateGroupFormView = findViewById(R.id.scrollView1);
		mCreateGroupStatusView = findViewById(R.id.refresh_status);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		ListView lview = getListView();
		lview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		lview.setTextFilterEnabled(true);

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
									CreateGroupActivity.this,
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_group, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			// overridePendingTransition(R.anim.right_in, R.anim.right_out);
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.create_group:
			showProgress(true);
			mCreateTask = new CreateGroupTask();
			mCreateTask.execute((Void) null);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mCreateGroupStatusView.setVisibility(View.VISIBLE);
			mCreateGroupStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateGroupStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mCreateGroupFormView.setVisibility(View.VISIBLE);
			mCreateGroupFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mCreateGroupFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mCreateGroupStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mCreateGroupFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public class CreateGroupTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			/*
			 * try { // Simulate network access. Thread.sleep(2000);
			 * 
			 * } catch (InterruptedException e) { return false; }
			 */
			boolean exito = false;
			EditNombre = (EditText) findViewById(R.id.edt_create_group);
			Nombre = EditNombre.getText().toString();
			ArrayList parametros = new ArrayList();
			parametros.add("NombreGrupo");
			parametros.add(Nombre);
			parametros.add("GrupoAdmin");
			parametros.add(user.get(SessionManager.KEY_NAME));
			parametros.add("Asistentes");
			parametros.add(asistentes);

			Log.d("LOG", parametros.toString());

			JSONParseo jParseo = new JSONParseo();

			String URL = "http://www.pisodigital.cl/twimpiweb/createGroup.php";

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

			if (success) {
				Intent i = new Intent(CreateGroupActivity.this,
						GroupActivity.class);
				startActivity(i);
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
