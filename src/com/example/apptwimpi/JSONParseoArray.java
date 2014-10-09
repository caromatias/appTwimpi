package com.example.apptwimpi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParseoArray {

    static InputStream is = null;
    static JSONObject jObj = null;
    static JSONArray jsonArray = null;
    static String json = "";
    // constructor
    public JSONParseoArray() {

    }    
        public JSONArray getJSONFromUrl(String url,String Metodo,ArrayList parametros) {    
        	ArrayList nameValuePairs;
   		 try{
   			if (Metodo=="get"){
   				//listado
   				DefaultHttpClient httpClient=new DefaultHttpClient();
   				String parametroString=URLEncodedUtils.format(parametros, "utf-8");
   				url+="?"+parametroString;
   				HttpGet httpget=new HttpGet(url);
   				
   				HttpResponse httpresponse=httpClient.execute(httpget);
   				HttpEntity httpentity=httpresponse.getEntity();
   				is=httpentity.getContent();
   			} else if(Metodo=="post"){
   				//grabar
   				HttpClient httpclient = new DefaultHttpClient();
   				HttpPost httppost = new HttpPost(url);
   				nameValuePairs = new ArrayList();

   				if (parametros != null) {
   					for (int i = 0; i < parametros.size() - 1; i += 2) {
   						nameValuePairs.add(new BasicNameValuePair((String) parametros.get(i), (String) parametros.get(i + 1)));
   					}
   					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
   				}
   				HttpResponse response = httpclient.execute(httppost);
   				HttpEntity entity = response.getEntity();
   				is = entity.getContent();
   				
   			}
   		 
   		 }catch(Exception error){
   			// Toast.makeText(getAplicationContext(), "Error:"+error.getMessage(),Toast.LENGTH_LONG).show();
   			 Log.e("Buffer Error", "Error converting resultado " + error.toString());
   		 }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                is.close();
                json = sb.toString();
                //Log.e("JSON::: ", json);
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }      

            // try parse the string to a JSON object
            try {
                if(!json.equals("null")){
                    jsonArray = new JSONArray(json);
                     Log.d("jsonArray:: ",  jsonArray+"");
                }else{
                    jsonArray = null;
                }

            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }


            // return JSON String
            return jsonArray;

        }

      }