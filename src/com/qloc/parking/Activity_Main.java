package com.qloc.parking;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

public class Activity_Main extends Activity implements LocationWrapper.MyLocationListener {
	
	private LocationWrapper mLocationWrapper;
	private ConnectivityReceiver mConnectivityReceiver;
	private TextView mConnectivityStatusTextView;
	private TextView mLocationStatusTextView;
	private Button mSearchParkingButton;
	private Button mParkingFoundButton;
	private RelativeLayout mConnecting;
	private TextView mParkAddressTextView;
	
	private boolean mCarIsParked = false;
	
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.activity_main);
	    	
	    	mLocationWrapper = LocationWrapper.getInstance();	    	
	    	mConnectivityStatusTextView = (TextView) findViewById(R.id.connectivity_status);	
	    	mLocationStatusTextView = (TextView) findViewById(R.id.gps_status);
	    	mSearchParkingButton = (Button) findViewById(R.id.button_find_parking);
	    	mParkingFoundButton = (Button) findViewById(R.id.button_parking_found);
	    	mConnecting = (RelativeLayout) findViewById(R.id.connecting_container);
	    	mParkAddressTextView = (TextView) findViewById(R.id.park_address);
	    	
	    	//kanw visible mexri na epivevaiwsw to uid
	    	mConnecting.setVisibility(View.VISIBLE);
	    	
	    	if (Utilities.isNetworkAvailable(this)) {
				mConnectivityStatusTextView.setText(R.string.status_ok);
			}
			else {
				mConnectivityStatusTextView.setText(R.string.status_problem);
			}
	    	
	    	//=================================== UID CHECKING ====================================================================
	    	
	    	String uid = Utilities.getUID(this);
	    	 	
	    	if (uid == null) { //kane to handshake
	    		
	    		//do the handshake with server and get uid	    	
		    	StringRequest handshakeRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
		    			 new Response.Listener<String>() { 		 		
		    		 		@Override
				    		 public void onResponse(String response) {
		    		 			Log.i("pantelis","handshakeRequest response = " + response);
		    		 			try {
									JSONObject jsonResponse = new JSONObject(response);	
									
									String status = jsonResponse.getJSONObject("head").getJSONObject("status").getString("value");
									if (status.equalsIgnoreCase("1")) {
										String currentUID = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("uid");
										Log.i("pantelis","testVolley response = " + "uid = " +currentUID);
										
										//valid uid
										Utilities.saveUID(currentUID,getBaseContext());
										uidValidated();
										
									}
									else {
						    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
						    			Log.i("pantelis","handshakeRequest status not 1");
									}
									
									
									//pairnw to uid kai to apothikeuw 	
									//energopoiw ta koumpia						
		    		 			} catch (JSONException e) {
									e.printStackTrace();
									//emfanise minima lathous
					    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    			Log.i("pantelis","handshakeRequest JSONException");
								}
				    		 }
		    		 }, 
		    		 
			    		 new Response.ErrorListener() {
				    		 @Override
				    		 public void onErrorResponse(VolleyError error) {
				    			 Log.e("pantelis","handshakeRequest response error from error listener");
				    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
				    		 }
		    		 }){
		    		
		    		 @Override
		    		 protected Map<String,String> getParams(){
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
			    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
			    		 params.put("app_vn", "api/handshake");
			    		 return params;
		    		 }
		    		  
		    		 @Override
		    		 public Map<String, String> getHeaders() throws AuthFailureError {
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("Content-Type","application/x-www-form-urlencoded");
			    		 return params;
		    		 	}
		    	};
		    	
	    		MyNetworkRequestsSingleton.getInstance(this).addToRequestQueue(handshakeRequest);
	    	}
	    	else { //kane to check uid
	    		
	    		//check if the current uid is valid	    	
		    	StringRequest checkUIDRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
		    			 new Response.Listener<String>() { 		 		
		    		 		@Override
				    		 public void onResponse(String response) {
		    		 			Log.i("pantelis","checkUIDRequest response = " + response);
		    		 			try {
									JSONObject jsonResponse = new JSONObject(response);	
									
									//String status = jsonResponse.getJSONObject("head").getJSONObject("status").getString("value");
									//if (status.equalsIgnoreCase("1")) {
										String isUIDValid = jsonResponse.getJSONObject("body").getJSONObject("user").getJSONObject("context").getString("status");
										//Log.i("pantelis","testVolley response = " + "uid = " +currentUID);
										
										if (isUIDValid.equalsIgnoreCase("1")) {
											uidValidated();
										}
										else {
											//kane handshake request
											StringRequest handshakeRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
									    			 new Response.Listener<String>() { 		 		
									    		 		@Override
											    		 public void onResponse(String response) {
									    		 			Log.i("pantelis","handshakeRequest after checkuid response = " + response);
									    		 			try {
																JSONObject jsonResponse = new JSONObject(response);	
																String status = jsonResponse.getJSONObject("head").getJSONObject("status").getString("value");
																if (status.equalsIgnoreCase("1")) {
																	String currentUID = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("uid");
																	Log.i("pantelis","handshakeRequest after checkuid new uid = " + "uid = " +currentUID);
																	//valid uid
																	Utilities.saveUID(currentUID,getBaseContext());
																	uidValidated();	
																}
																else {
													    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
													    			//Toast.makeText(getBaseContext(), "1", Toast.LENGTH_LONG).show();
																	Log.i("pantelis","handshakeRequest after checkuid  status not 1");



																}
																
									    		 			} catch (JSONException e) {
																e.printStackTrace();
																//emfanise minima lathous
												    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
												    			//Toast.makeText(getBaseContext(), "2", Toast.LENGTH_LONG).show();
																Log.i("pantelis","handshakeRequest after checkuid jsonexception");



															}
											    		 }
									    		 }, 
									    		 
										    		 new Response.ErrorListener() {
											    		 @Override
											    		 public void onErrorResponse(VolleyError error) {
											    			 Log.e("pantelis","testVolley response error 3");
											    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
												    			//Toast.makeText(getBaseContext(), "3", Toast.LENGTH_LONG).show();
																Log.i("pantelis","handshakeRequest after checkuid error listener");

											    		 }
									    		 }){
									    		
									    		 @Override
									    		 protected Map<String,String> getParams(){
										    		 Map<String,String> params = new HashMap<String, String>();
										    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
										    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
										    		 params.put("app_vn", "api/handshake");
										    		 return params;
									    		 }
									    		  
									    		 @Override
									    		 public Map<String, String> getHeaders() throws AuthFailureError {
										    		 Map<String,String> params = new HashMap<String, String>();
										    		 params.put("Content-Type","application/x-www-form-urlencoded");
										    		 return params;
									    		 	}
									    	};
									    	
								    		MyNetworkRequestsSingleton.getInstance(getBaseContext()).addToRequestQueue(handshakeRequest);
										}
									
		    		 			} catch (JSONException e) {
									e.printStackTrace();
									//emfanise minima lathous
					    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    			//Toast.makeText(getBaseContext(), "5", Toast.LENGTH_LONG).show();

								}
		    	
				    		 }
		    		 }, 
		    		 
			    		 new Response.ErrorListener() {
				    		 @Override
				    		 public void onErrorResponse(VolleyError error) {
				    			 
				    			 Log.e("pantelis","testVolley response error 4 " + error.getMessage());
				    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();

				    		 }
		    		 }){
		    		
		    		 @Override
		    		 protected Map<String,String> getParams(){
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
			    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
			    		 params.put("app_vn", "api/status");
			    		 params.put("uid", Utilities.getUID(getBaseContext()));
			    		// params.put("uid", "456");
			    		 return params;
		    		 }
		    		  
		    		 @Override
		    		 public Map<String, String> getHeaders() throws AuthFailureError {
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("Content-Type","application/x-www-form-urlencoded");
			    		 return params;
		    		 	}
		    	};
		    	
	    		MyNetworkRequestsSingleton.getInstance(this).addToRequestQueue(checkUIDRequest);
	    	}
	    	
	 //=================================== END UID CHECKING ====================================================================
	    	
	    	
	 //=================================== CHECK IF CAR IS PARKED ========================================================   	
	 
	 
	 }
	 
	 
	  @Override
	    protected void onResume() {
	        super.onResume();
	        mLocationWrapper.setLocationListener(this, this);
	        mConnectivityReceiver = new ConnectivityReceiver();	
			registerReceiver(mConnectivityReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));       
	    }
	    
	    
	    /*
	     * Called when the Activity is no longer visible.
	     */
	    @Override
	    protected void onPause() {
	    	  super.onPause();
	    	  mLocationWrapper.unsetLocationListener();
	    	  unregisterReceiver(mConnectivityReceiver);
	    }
	 
	 

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed() {
		// TODO Auto-generated method stub	
	}

	@Override
	public void onConnected() {
		mLocationStatusTextView.setText(R.string.status_ok);	
	}

	@Override
	public void onDisconnected() {
		mLocationStatusTextView.setText(R.string.status_problem);
		
	}
	
	public void onParkingStatusChanged(View v) {
		if (mCarIsParked) { //request unparked
			
			StringRequest unparkedRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
	    			 new Response.Listener<String>() { 		 		
	    		 		@Override
			    		 public void onResponse(String response) {
	    		 			Log.i("pantelis","testVolley unparked response = " + response);
	    		 			try {
								JSONObject jsonResponse = new JSONObject(response);									
								String status = jsonResponse.getJSONObject("head").getJSONObject("status").getString("value");
								if (status.equalsIgnoreCase("1")) {
									String leftParkingSpot = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("status");
									if (leftParkingSpot.equalsIgnoreCase("1")) {
										leftParkingSpot();
									}
									mSearchParkingButton.setEnabled(true);
								}
								else {
					    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    		//	Toast.makeText(getBaseContext(), "parked error json 3", Toast.LENGTH_LONG).show();

								}		
	    		 			} catch (JSONException e) {
								e.printStackTrace();
								//emfanise minima lathous
				    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
				    			//Toast.makeText(getBaseContext(), "parked error json", Toast.LENGTH_LONG).show();

							}
			    		 }
	    		 }, 
	    		 
		    		 new Response.ErrorListener() {
			    		 @Override
			    		 public void onErrorResponse(VolleyError error) {
			    			 Log.e("pantelis","testVolley response error 1");
			    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
				    		// Toast.makeText(getBaseContext(), "parked error 2", Toast.LENGTH_LONG).show();

			    		 }
	    		 }){
	    		
	    		 @Override
	    		 protected Map<String,String> getParams(){
		    		 Map<String,String> params = new HashMap<String, String>();
		    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
		    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
		    		 params.put("app_vn", "api/unparked");
		    		 params.put("uid", Utilities.getUID(getBaseContext()));
		    		 return params;
	    		 }
	    		  
	    		 @Override
	    		 public Map<String, String> getHeaders() throws AuthFailureError {
		    		 Map<String,String> params = new HashMap<String, String>();
		    		 params.put("Content-Type","application/x-www-form-urlencoded");
		    		 return params;
	    		 	}
	    	};
	    	
			MyNetworkRequestsSingleton.getInstance(this).addToRequestQueue(unparkedRequest);

		}
		else { //request parked
			if (mLocationWrapper != null) {
				Location currentLocation = mLocationWrapper.getCurrentLocation();		
				final double latitude = currentLocation.getLatitude();
				final double longitude = currentLocation.getLongitude();
				
				//Toast.makeText(this, "Latitide:" + String.valueOf(latitude) + " , Longitude:" + String.valueOf(longitude),Toast.LENGTH_LONG).show();
												
				StringRequest parkedRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
		    			 new Response.Listener<String>() { 		 		
		    		 		@Override
				    		 public void onResponse(String response) {
		    		 			Log.i("pantelis","testVolley parked response = " + response);
		    		 			try {
									JSONObject jsonResponse = new JSONObject(response);									
									String status = jsonResponse.getJSONObject("head").getJSONObject("status").getString("value");
									if (status.equalsIgnoreCase("1")) {
										String address = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("address");
										Utilities.saveParkingAddress(address, getBaseContext());
										parkingSaved(address);
										mSearchParkingButton.setEnabled(false);
										
									}
									else {
						    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
						    			//Toast.makeText(getBaseContext(), "parked error json 3", Toast.LENGTH_LONG).show();

									}		
		    		 			} catch (JSONException e) {
									e.printStackTrace();
									//emfanise minima lathous
					    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    			Toast.makeText(getBaseContext(), "parked error json", Toast.LENGTH_LONG).show();

								}
				    		 }
		    		 }, 
		    		 
			    		 new Response.ErrorListener() {
				    		 @Override
				    		 public void onErrorResponse(VolleyError error) {
				    			 Log.e("pantelis","testVolley response error 2");
				    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    		 Toast.makeText(getBaseContext(), "parked error 2", Toast.LENGTH_LONG).show();

				    		 }
		    		 }){
		    		
		    		 @Override
		    		 protected Map<String,String> getParams(){
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
			    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
			    		 params.put("app_vn", "api/parked");
			    		 params.put("uid", Utilities.getUID(getBaseContext()));
			    		 params.put("latitude",String.valueOf(latitude));
			    		 params.put("longitude", String.valueOf(longitude));
			    		 return params;
		    		 }
		    		  
		    		 @Override
		    		 public Map<String, String> getHeaders() throws AuthFailureError {
			    		 Map<String,String> params = new HashMap<String, String>();
			    		 params.put("Content-Type","application/x-www-form-urlencoded");
			    		 return params;
		    		 	}
		    	};
				// Access the RequestQueue through your singleton class.
				MyNetworkRequestsSingleton.getInstance(this).addToRequestQueue(parkedRequest);
			}
		}
		
		
		
	}
	
	public void onParkingSearching(View v) {
		//start activity for searching parking
		Intent startIntent = new Intent(this, Activity_SearchParking.class);
		startActivity(startIntent);
		finish();
	}
	
	private class ConnectivityReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Utilities.isNetworkAvailable(context)) {
				mConnectivityStatusTextView.setText(R.string.status_ok);
			}
			else {
				mConnectivityStatusTextView.setText(R.string.status_problem);
			}
			
		}	
    }
	
	private boolean isCarAlreadyParked(Context ctx) {
		String parkAddress = Utilities.getParkingAddress(ctx);
		if (parkAddress != null) {
			mCarIsParked = true;
			if (mParkingFoundButton != null) {
				mParkingFoundButton.setText(R.string.leavingparking);
			}
			
			if (mParkAddressTextView != null) {
				mParkAddressTextView.setText(getString(R.string.parkaddressFirstPart) + "  " + parkAddress);
				mParkAddressTextView.setVisibility(View.VISIBLE);
			}
			return true;
		}
		mCarIsParked = false;
		return false;
	}
	

	private void uidValidated() {
		if (mSearchParkingButton != null){
			mSearchParkingButton.setEnabled(true);
		}
		
		if (mParkingFoundButton != null){
			mParkingFoundButton.setEnabled(true);
		}
		
		if (mConnecting != null) {
			mConnecting.setVisibility(View.INVISIBLE);
		}
		
		isCarAlreadyParked(getBaseContext());
	}
	
	private void parkingSaved(String address) {
		if (mParkAddressTextView != null) {
			mParkAddressTextView.setText(getString(R.string.parkedConfirmation) + address);
			mParkAddressTextView.setVisibility(View.VISIBLE);
			if (mParkingFoundButton != null) {
				mParkingFoundButton.setText(R.string.leavingparking);
			}
			
		}
		mCarIsParked = true;
	}
	
	
	private void leftParkingSpot() {
		Utilities.saveParkingAddress(null, getBaseContext());
		if (mParkAddressTextView != null) {
			mParkAddressTextView.setVisibility(View.INVISIBLE);
		}
		if (mParkingFoundButton != null) {
			mParkingFoundButton.setText(R.string.parked);
		}
		mCarIsParked = false;
	}
}
