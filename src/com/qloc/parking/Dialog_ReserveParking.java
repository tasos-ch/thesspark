package com.qloc.parking;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import android.net.Uri;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.qloc.parking.Dialog_ConfirmParkingSpot.ConfirmParkingListener;

public class Dialog_ReserveParking extends DialogFragment  {
	
	 private Context mContext;
	 private String mParkingAddress;
	 private String mCurrentLatitude;
	 private String mCurrentLongitude;
	 private String mDestinationLatitude;
	 private String mDestinationLongitude;
	 private String mPID;
	 
	 public interface ReserveParkingListener {
		 public void onCloseActivity();
		 public void onCancelDialog();
		// public void onCloseLocationListener();
	 }
	 
	 ReserveParkingListener mListener;
	 
	 @Override
	 public void onAttach(Activity activity) {
	        super.onAttach(activity);
	        // Verify that the host activity implements the callback interface
	        try {
	            // Instantiate the NoticeDialogListener so we can send events to the host
	            mListener = (ReserveParkingListener) activity;
	        } catch (ClassCastException e) {
	            // The activity doesn't implement the interface, throw exception
	            throw new ClassCastException(activity.toString()
	                    + " must implement ConfirmParkingListener");
	        }
	    }
	 
	
	 public Dialog_ReserveParking(Context ctx,String pid,String parking_address, String clatitude,String clongitude,String dlatitude,String dlongitude) {
		 mContext = ctx;
		 mParkingAddress = parking_address;
		 mCurrentLatitude = clatitude;
		 mCurrentLongitude = clongitude;
		 mDestinationLatitude = dlatitude;
		 mDestinationLongitude = dlongitude;
		 mPID = pid;
	 }
	
	 @Override
	 public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Use the Builder class for convenient dialog construction
	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("THESSPark").setMessage(mParkingAddress)
	               .setPositiveButton(R.string.reserve, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       //kane klhsh ston server gia reserve kai afou pareis thetikh apadisi ksekina google maps gia navigation
	                	   // kai ksekina kai to service pou tha tsekarei an ftasame ston proorismo mas 
	                	              	   
	                	   StringRequest reserveRequest = new StringRequest(Request.Method.POST,MyNetworkRequestsSingleton.TESTING_SERVER_URL, 
	      		    			 new Response.Listener<String>() { 		 		
	      		    		 		@Override
	      				    		 public void onResponse(String response) {
	      		    		 			Log.i("pantelis","reserveRequest response = " + response);
	      		    		 
	      		    		 			try {
	      									JSONObject jsonResponse = new JSONObject(response);	
	      									
	      									String status = jsonResponse.getJSONObject("body").getJSONObject("response").getJSONObject("context").getString("status");
	      									
	      									if (status.equalsIgnoreCase("1")) {
	      										
	 	      			                        // start google maps for navigation
	 	      			                         //40.593590, 22.960568
	      										// mListener.onCloseLocationListener();
	      										
	      										//apothikeuw to pid gia metepeita xrhsh
	      										 Utilities.savePID(mPID,mContext);
	 	      			                       
	 	      			                     
	      										 Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
	 	      			                         Uri.parse("http://maps.google.com/maps?saddr="+mCurrentLatitude+","+ mCurrentLongitude + "&daddr=" + mDestinationLatitude + "," + mDestinationLongitude));
	 	      			                         intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 	      			                         mContext.startActivity(intent);
	 	      			                                            
	 	      			                         mListener.onCloseActivity();
	 	      			                         
	 	      			                         Intent intentParkingArrival = new Intent(mContext,Service_ParkingArrival.class);
	 	      			                         intentParkingArrival.putExtra("destinationLatitude",Double.parseDouble(mDestinationLatitude));    
	 	      			                         intentParkingArrival.putExtra("destinationLongitude",Double.parseDouble(mDestinationLongitude));
	 	      			                         mContext.startService(intentParkingArrival);
	 	      			              
	      									}
	      									else {
		      					    			Toast.makeText(mContext, R.string.parking_not_available, Toast.LENGTH_LONG).show();
	      									}
	      									
	      											
	      		    		 			} catch (JSONException e) {
	      									e.printStackTrace();
	      					    			//Toast.makeText(getBaseContext(), R.string.server_error, Toast.LENGTH_LONG).show();
	      					    			Log.i("pantelis","reserveRequest JSONException");
	      								}
	      				    		 }
	      		    		 }, 
	      		    		 
	      			    		 new Response.ErrorListener() {
	      				    		 @Override
	      				    		 public void onErrorResponse(VolleyError error) {
	      				    			 Log.e("pantelis","reserveRequest response error from error listener");
	      				    			 Toast.makeText(mContext, R.string.server_error, Toast.LENGTH_LONG).show();
	      				    		 }
	      		    		 }){
	      		    		
	      		    		 @Override
	      		    		 protected Map<String,String> getParams(){
	      			    		 Map<String,String> params = new HashMap<String, String>();
	      			    		 params.put("app_id", MyNetworkRequestsSingleton.APP_ID);
	      			    		 params.put("akey", MyNetworkRequestsSingleton.A_KEY);
	      			    		 params.put("app_vn", "api/reserve");
	      			    		 params.put("uid", Utilities.getUID(mContext));
	      			    		 params.put("pid",mPID);
	      			    		 return params;
	      		    		 }
	      		    		  
	      		    		 @Override
	      		    		 public Map<String, String> getHeaders() throws AuthFailureError {
	      			    		 Map<String,String> params = new HashMap<String, String>();
	      			    		 params.put("Content-Type","application/x-www-form-urlencoded");
	      			    		 return params;
	      		    		 	}
	      		    	};
	      		    	
	      		    	MyNetworkRequestsSingleton.getInstance(mContext).addToRequestQueue(reserveRequest);
	                	   
	                	   
	                	  
	                   }
	               })
	               .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	                   public void onClick(DialogInterface dialog, int id) {
	                       // User cancelled the dialog
	                	   mListener.onCancelDialog();
	                   }
	               });
	        // Create the AlertDialog object and return it
	        return builder.create();
	    }

	


}
