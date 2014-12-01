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
import android.net.Uri;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class Activity_SearchParking extends Activity implements LocationWrapper.MyLocationListener,Dialog_ReserveParking.ReserveParkingListener {
	
	private LocationWrapper mLocationWrapper;
	
	private GoogleMap map;
	
	private long mLastRequestTimeStamp;
	
	private boolean mReserveDialogShown = false;
	
	//private 
	
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	    	super.onCreate(savedInstanceState);
	    	setContentView(R.layout.activity_searchparking);
	    	mReserveDialogShown = false;
	    	mLocationWrapper = LocationWrapper.getInstance();
	    	map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();   
	        map.setMyLocationEnabled(true);
	        
	        //Dialog_ReserveParking newFragment = new Dialog_ReserveParking("Βρέθηκε θέση κοντά Τσιμισκή 124");
	       // newFragment.show(getFragmentManager(), "reserve_parking");
	        mLastRequestTimeStamp = 0;
	       
	    	
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
	 
	 

	@Override
	public void onLocationChanged(Location location) {
		CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        map.animateCamera(zoom);
        
        if(!mReserveDialogShown) {
        	 if ((mLastRequestTimeStamp == 0) || (System.currentTimeMillis() - mLastRequestTimeStamp > 5000)) {
             	if (mLocationWrapper != null) {
     				Location currentLocation = mLocationWrapper.getCurrentLocation();		
     				final double latitude = currentLocation.getLatitude();
     				final double longitude = currentLocation.getLongitude();
     	        	
     				StringRequest searchRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
     		    			 new Response.Listener<String>() { 		 		
     		    		 		@Override
     				    		 public void onResponse(String response) {
     		    		 			Log.i("pantelis","searchRequest response = " + response);
     		    		 			mLastRequestTimeStamp = System.currentTimeMillis();
     		    		 			try {
     									JSONObject jsonResponse = new JSONObject(response);	
     										
     									String address = jsonResponse.getJSONObject("body").getJSONObject("spots").getJSONObject("context").getJSONObject("0").getString("address");
     									String parkspot_lat = jsonResponse.getJSONObject("body").getJSONObject("spots").getJSONObject("context").getJSONObject("0").getString("latitude");
     									String parkspot_long = jsonResponse.getJSONObject("body").getJSONObject("spots").getJSONObject("context").getJSONObject("0").getString("longitude");
     									String pid = jsonResponse.getJSONObject("body").getJSONObject("spots").getJSONObject("context").getJSONObject("0").getString("id");
     									
     									mReserveDialogShown = true;
     									Dialog_ReserveParking newFragment = new Dialog_ReserveParking(getBaseContext(),pid,address, String.valueOf(latitude), String.valueOf(longitude),parkspot_lat,parkspot_long);
     								    newFragment.show(getFragmentManager(), "reserve_spot");
     											
     		    		 			} catch (JSONException e) {
     									e.printStackTrace();
     					    			//Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
     					    			Log.i("pantelis","searchRequest JSONException");
     								}
     				    		 }
     		    		 }, 
     		    		 
     			    		 new Response.ErrorListener() {
     				    		 @Override
     				    		 public void onErrorResponse(VolleyError error) {
     				    			 Log.e("pantelis","searchRequest response error from error listener");
     				    			 Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
     				    		 }
     		    		 }){
     		    		
     		    		 @Override
     		    		 protected Map<String,String> getParams(){
     			    		 Map<String,String> params = new HashMap<String, String>();
     			    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
     			    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
     			    		 params.put("app_vn", "api/search");
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
     		    	
     		    	MyNetworkRequestsSingleton.getInstance(this).addToRequestQueue(searchRequest);
     	        }
             }	
        }
        
       
	}

	@Override
	public void onConnectionFailed() {
		
		
	}

	@Override
	public void onConnected() {
		
		
		
	}

	@Override
	public void onDisconnected() {
		
		
	}
	
	
	public void onCancelSearching(View v) {
		Intent startIntent = new Intent(this, Activity_Main.class);
		startActivity(startIntent);
		finish();
	}

	@Override
	public void onCloseActivity() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	public void onCancelDialog() {
		// TODO Auto-generated method stub
		mReserveDialogShown = false;
	}

	/*
	@Override
	public void onCloseLocationListener() {
		if (mLocationWrapper != null) {
			mLocationWrapper.unsetLocationListener();
		}
		 
	}
	*/

}
