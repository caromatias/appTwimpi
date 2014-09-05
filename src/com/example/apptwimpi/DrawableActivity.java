package com.example.apptwimpi;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

public class DrawableActivity extends Activity {

	// Session Manager Class
	SessionManager session;
	private String[] titulos;
	private DrawerLayout NavDrawerLayout;
	private ListView NavList;
	private ArrayList<Item_objct> NavItms;
	private TypedArray NavIcons;
	NavigationAdapter NavAdapter;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	HashMap<String, String> user;
	// /////////////////
	private String uCorreo;
	// private TextView uNombre;
	// private ImageView profile_pic;
	private String cNombre;
	private TraeUserTask mGetUserTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drawable);
		// Session class instance
		session = new SessionManager(getApplicationContext());
		session.checkLogin();
		user = session.getUserDetails();
		uCorreo = user.get(SessionManager.KEY_EMAIL);
		// Drawer Layout
		NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// Lista
		NavList = (ListView) findViewById(R.id.lista);
		// Declaramos el header el caul sera el layout de header.xml
		View header = getLayoutInflater().inflate(R.layout.header, null);
		// Establecemos header
		NavList.addHeaderView(header);
		// Tomamos listado de imgs desde drawable
		NavIcons = getResources().obtainTypedArray(R.array.navigation_iconos);
		// Tomamos listado de titulos desde el string-array de los recursos
		// @string/nav_options
		titulos = getResources().getStringArray(R.array.nav_options);
		// Listado de titulos de barra de navegacion
		NavItms = new ArrayList<Item_objct>();
		// Agregamos objetos Item_objct al array
		// Perfil
		NavItms.add(new Item_objct(titulos[0], NavIcons.getResourceId(0, -1)));
		// Favoritos
		NavItms.add(new Item_objct(titulos[1], NavIcons.getResourceId(1, -1)));
		// Eventos
		NavItms.add(new Item_objct(titulos[2], NavIcons.getResourceId(2, -1)));
		// Lugares
		NavItms.add(new Item_objct(titulos[3], NavIcons.getResourceId(3, -1)));
		// Etiquetas
		NavItms.add(new Item_objct(titulos[4], NavIcons.getResourceId(4, -1)));
		// Configuracion
		NavItms.add(new Item_objct(titulos[5], NavIcons.getResourceId(5, -1)));
		// Cerrar Sesion
		NavItms.add(new Item_objct(titulos[6], NavIcons.getResourceId(6, -1)));
		// Declaramos y seteamos nuestrp adaptador al cual le pasamos el array
		// con los titulos
		NavAdapter = new NavigationAdapter(this, NavItms);
		NavList.setAdapter(NavAdapter);
		// Siempre vamos a mostrar el mismo titulo
		mTitle = mDrawerTitle = getTitle();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getActionBar().setTitle(mDrawerTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		// Establecemos que mDrawerToggle declarado anteriormente sea el
		// DrawerListener
		NavDrawerLayout.setDrawerListener(mDrawerToggle);
		// Establecemos que el ActionBar muestre el Boton Home
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Establecemos la accion al clickear sobre cualquier item del menu.
		// De la misma forma que hariamos en una app comun con un listview.
		NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long id) {
				switch (position) {
				case 7:
					callFacebookLogout(getApplicationContext());
					session.logoutUser();
					break;
				default:
					// si no esta la opcion mostrara un toast y nos mandara a
					// Home
					
					position = 1;
					break;
				}
			}
		});
		
		
		
	}
	
	
	private void getUserData(final Session session){
	    Request request = Request.newMeRequest(session, 
	        new Request.GraphUserCallback() {
	        @Override
	        public void onCompleted(GraphUser user, Response response) {
	            if(session == Session.getActiveSession()){
	                getFriends();
	                Log.e("Drawable INFO", "sosososo");
	            }
	            if(response.getError() !=null){
	            	Log.e("Drawable INFO", "NO SESSION");
	            }
	        }
	    });
	    request.executeAsync();
	}

	private void getFriends(){
	    Session activeSession = Session.getActiveSession();
	    if(activeSession.getState().isOpened()){
	        Request friendRequest = Request.newMyFriendsRequest(activeSession, 
	            new GraphUserListCallback(){
	                @Override
	                public void onCompleted(List<GraphUser> users,
	                        Response response) {
	                    Log.i("Drawable INFO", response.toString());
	                    for (int i = 0; i < users.size(); i++) {
	                        Log.e("Drawable users", "users " + users.get(i).getName());
	                    }

	                }
	        });
	        Bundle params = new Bundle();
	        params.putString("fields", "id,name,friends");
	        friendRequest.setParameters(params);
	        friendRequest.executeAsync();
	    }
	}

	public static void callFacebookLogout(Context context) {
		Session session = Session.getActiveSession();
		if (session != null) {

			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
				// clear your preferences if saved
			}
		} else {

			session = new Session(context);
			Session.setActiveSession(session);

			session.closeAndClearTokenInformation();
			// clear your preferences if saved

		}

	}

	public void obtenerDatosFacebook() throws IOException {
		final ImageView profile_pic = (ImageView) findViewById(R.id.image_perfil);
		final TextView uNombre = (TextView) findViewById(R.id.nombre_user);
		// new DownloadImageTask((ImageView)
		// findViewById(R.id.image_perfil)).execute("https://graph.facebook.com/"+bundle.getString("fbId")+"/picture?type=large");
		AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>() {
			protected Bitmap doInBackground(Void... p) {
				Bitmap bm = null;
				try {
					URL aURL = new URL("https://graph.facebook.com/"
							+ user.get(SessionManager.KEY_NAME)
							+ "/picture?type=large");
					URLConnection conn = aURL.openConnection();
					conn.setUseCaches(true);
					conn.connect();
					InputStream is = conn.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					bm = BitmapFactory.decodeStream(bis);
					bis.close();
					is.close();
					// /// GET USER //////
					ArrayList<String> parametros = new ArrayList<String>();
					parametros.add("Correo");
					parametros.add(uCorreo);
					JSONParseo jParseo = new JSONParseo();
					String URL = "http://www.pisodigital.cl/twimpiweb/getUser.php";
					JSONObject json = jParseo.recibir(URL, "post", parametros);
					cNombre = json.getString("usuario_nombre");
					Log.e("NOMBRE", json.getString("usuario_nombre"));

				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return bm;
			}

			protected void onPostExecute(Bitmap bm) {
				Drawable drawable = new BitmapDrawable(getResources(), bm);
				profile_pic.setImageDrawable(drawable);
				// //// GET USER //////////
				try {
					if (cNombre != "") {
						uNombre.setText(cNombre);
					} else {

					}

				} catch (Exception error) {
					uNombre.setText("No hubo conexion");
				}
			}
		};
		t.execute();
		// mGetUserTask = new TraeUserTask();
		// mGetUserTask.execute((Void) null);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	public class TraeUserTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.

			/*
			 * try { // Simulate network access. Thread.sleep(2000);
			 * 
			 * } catch (InterruptedException e) { return false; }
			 */

			boolean exito = false;
			final TextView uNombre = (TextView) findViewById(R.id.nombre_user);
			ArrayList<String> parametros = new ArrayList<String>();
			parametros.add("Correo");
			parametros.add(uCorreo);

			JSONParseo jParseo = new JSONParseo();

			String URL = "http://www.pisodigital.cl/twimpiweb/getUser.php";

			JSONObject json = jParseo.recibir(URL, "post", parametros);

			try {
				String cNombre = json.getString("usuario_nombre");
				Log.e("LOG", json.getString("usuario_nombre"));
				if (cNombre != "") {
					exito = true;
					uNombre.setText(json.getString("usuario_nombre"));
				} else {

				}

			} catch (Exception error) {
				uNombre.setText("No hubo conexion");
			}
			return exito;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success) {

			} else {

			}
		}

		@Override
		protected void onCancelled() {
			mGetUserTask = null;
		}
	}

}