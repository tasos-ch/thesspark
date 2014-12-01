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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;


public class Activity_ConfirmParking extends Activity implements Dialog_ConfirmParkingSpot.ConfirmParkingListener,LocationWrapper.MyLocationListener {
	
	private LocationWrapper mLocationWrapper;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.activity_confirmparking);
	    	
	    	mLocationWrapper = LocationWrapper.getInstance();
	        	
	 }
	 
	 
	  @Override
	  protected void onResume() {
	        super.onResume();
	        mLocationWrapper.setLocationListener(this, this); 
	  }
	    
	    
	 /*
	  * Called when the Activity is no longer visible.
	  */
	  @Override
	  protected void onPause() {
		  super.onPause();
		  mLocationWrapper.unsetLocationListener();
	  }
	 
	public void onParked(View v) {

		if (mLocationWrapper != null) {
			Location currentLocation = mLocationWrapper.getCurrentLocation();		
			final double latitude = currentLocation.getLatitude();
			final double longitude = currentLocation.getLongitude();
														
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
									finish();
				
								}
								else {
					    			Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
					    			//Toast.makeText(getBaseContext(), "parked error json 3", Toast.LENGTH_LONG).show();

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
			    			 Log.e("pantelis","testVolley response error 2");
			    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
				    		// Toast.makeText(getBaseContext(), "parked error 2", Toast.LENGTH_LONG).show();

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
	
	public void onNotParked(View v) {
		//edw tha emfanizw activity me erwthseis sxetika me to an yparxei h thesh 
		Dialog_ConfirmParkingSpot newFragment = new Dialog_ConfirmParkingSpot(this);
	    newFragment.show(getFragmentManager(), "confirm_parking_spot");
		
	}

	@Override
	public void onCloseActivity() {
		Intent startIntent = new Intent(this, Activity_Main.class);
		//startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
     	startActivity(startIntent);
		finish();	
	}

	@Override
	public void onLocationChanged(Location location) {}


	@Override
	public void onConnectionFailed() {}


	@Override
	public void onConnected() {}


	@Override
	public void onDisconnected() {}
	 
}
