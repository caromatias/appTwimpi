package com.example.apptwimpi;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class EditProfileActivity extends Activity {

	private GetGroupTask mGetTask = null;

	private EditText edt1;
	private Button btn1;
	SessionManager session;
	HashMap<String, String> user;
	private ProgressDialog pDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();

		edt1 = (EditText) findViewById(R.id.editText1);
		btn1 = (Button) findViewById(R.id.button1);

		
		
		btn1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				pDialog = new ProgressDialog(EditProfileActivity.this);
				pDialog.setMessage("Guardando información...");
				pDialog.setCancelable(false);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.show();
				mGetTask = new GetGroupTask();
				mGetTask.execute((Void)null);
				
				
			}
		});
		
	}

	public class GetGroupTask extends AsyncTask<Void, Void, Boolean> {
		@SuppressLint("NewApi")
		@Override
		protected Boolean doInBackground(Void... params) {

			boolean exito = false;
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("x");
			parametros.add(edt1.getText().toString());
			parametros.add("y");
			parametros.add(user.get(SessionManager.KEY_NAME));

			JSONParseoArray jParseo = new JSONParseoArray();

			String URL = "https://www.pisodigital.cl/twimpiweb/editDescripcion.php";

			JSONArray json = jParseo.getJSONFromUrl(URL, "post", parametros);
			
			
			try {
				JSONObject jsonObject = json.getJSONObject(0);
				String success = jsonObject.getString("success");
				Log.e("LOG", jsonObject.toString());
				if (success.equals("0")) {
					exito = true;
				}else{
					exito=false;
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

			if(success){
				pDialog.dismiss();
				Intent i = new Intent(EditProfileActivity.this,
						DrawableActivity.class);
				startActivity(i);
			}else{
				Toast.makeText(getApplicationContext(),
						"No modifico",
						Toast.LENGTH_LONG).show();
			}
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_profile, menu);
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

