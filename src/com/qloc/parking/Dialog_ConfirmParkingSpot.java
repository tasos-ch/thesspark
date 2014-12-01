package com.qloc.parking;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Dialog_ConfirmParkingSpot extends DialogFragment  {
	
	 private Context mContext;
	
	 public Dialog_ConfirmParkingSpot(Context ctx) {
		 mContext = ctx;
	 }
	 
	 public interface ConfirmParkingListener {
		 public void onCloseActivity();
	 }
	 
	 ConfirmParkingListener mListener;
	 
	 @Override
	 public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (ConfirmParkingListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement ConfirmParkingListener");
	        }
	    }

	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("THESSPark").setMessage(getString(R.string.parking_spot_avail_quest))
	               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                      //kane klhsh ston server gia na ginei apo reserved free
	                	   //kai girna sthn kentrikh activity
	                	   
	                	   StringRequest cancelReservationRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
	          	    			 new Response.Listener<String>() { 		 		
	          	    		 		@Override
	          			    		 public void onResponse(String response) {
	          	    		 			Log.i("pantelis","testVolley cancelReservationRequest response = " + response);
	          	    		 			try {
	          								JSONObject jsonResponse = new JSONObject(response);									
	      									String status = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("status");
	          								if (status.equalsIgnoreCase("1")) {
	          								
	          				
	          								}
	          								else {
	          					    			Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
	          					    			//Toast.makeText(mContext, "cancelReservationRequest error failed 3", Toast.LENGTH_LONG).show();

	          								}		
	          	    		 			} catch (JSONException e) {
	          								e.printStackTrace();
	          								//emfanise minima lathous
	          				    			Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
	          				    			
	          							}
	          	    		 			
	          	    		 		
	          		               		
	          			    		 }
	          	    		 }, 
	          	    		 
	          		    		 new Response.ErrorListener() {
	          			    		 @Override
	          			    		 public void onErrorResponse(VolleyError error) {
	          			    			 Log.e("pantelis","testVolley cancelReservationRequest response error 2");
	          			    			 Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
	          				    		// Toast.makeText(mContext, "cancelReservationRequest error 2", Toast.LENGTH_LONG).show();
	          				    		
	          		               		
	          			    		 }
	          	    		 }){
	          	    		
	          	    		 @Override
	          	    		 protected Map<String,String> getParams(){
	          		    		 Map<String,String> params = new HashMap<String, String>();
	          		    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
	          		    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
	          		    		 params.put("app_vn", "api/unreserve");
	          		    		 params.put("uid", Utilities.getUID(mContext));
	          		    		 params.put("pid",Utilities.getPID(mContext));
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
	          			MyNetworkRequestsSingleton.getInstance(mContext).addToRequestQueue(cancelReservationRequest);
	          			mListener.onCloseActivity();     	       
	                   }
	               })
	               .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                	   
	                	   StringRequest removePositionRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
		          	    			 new Response.Listener<String>() { 		 		
		          	    		 		@Override
		          			    		 public void onResponse(String response) {
		          	    		 			Log.i("pantelis","testVolley removePositionRequest response = " + response);
		          	    		 			try {
		          								JSONObject jsonResponse = new JSONObject(response);									
		      									String status = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("status");
		          								if (status.equalsIgnoreCase("1")) {
		          								
		          				
		          								}
		          								else {
		          					    			Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
		          					    			//Toast.makeText(mContext, "removePositionRequest error failed 3", Toast.LENGTH_LONG).show();

		          								}		
		          	    		 			} catch (JSONException e) {
		          								e.printStackTrace();
		          								//emfanise minima lathous
		          				    			Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
		          				    			//Toast.makeText(mContext, "removePositionRequest error json", Toast.LENGTH_LONG).show();

		          							}
		          	    		 			
		          		               		
		          			    		 }
		          	    		 }, 
		          	    		 
		          		    		 new Response.ErrorListener() {
		          			    		 @Override
		          			    		 public void onErrorResponse(VolleyError error) {
		          			    			 Log.e("pantelis","testVolley response error 2");
		          			    			 Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
		          				    		 
		          				    		 Intent startIntent = new Intent(mContext, Activity_SearchParking.class);
		          		               	     startActivity(startIntent);
		          		               	   
		          			    		 }
		          	    		 }){
		          	    		
		          	    		 @Override
		          	    		 protected Map<String,String> getParams(){
		          		    		 Map<String,String> params = new HashMap<String, String>();
		          		    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
		          		    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
		          		    		 params.put("app_vn", "api/remove");
		          		    		 params.put("uid", Utilities.getUID(mContext));
		          		    		 params.put("pid",Utilities.getPID(mContext));
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
		          			MyNetworkRequestsSingleton.getInstance(mContext).addToRequestQueue(removePositionRequest);
		          			mListener.onCloseActivity();
	                   }
	                   
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }

	


}
