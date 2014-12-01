package com.qloc.parking;


import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;


public class LocationWrapper implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

	private static LocationWrapper sInstance ; //the instace of the LocationWrapper
	private MyLocationListener mLocationListener; //object that will use the LocationWrapper
	
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;    
	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	public static final int UPDATE_INTERVAL_IN_SECONDS = 1;
	// Update frequency in milliseconds
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
	     
	//Stores the current instantiation of the location client in this object
	private LocationClient mLocationClient;
	    
	//the current location
	private Location mCurrentLocation;
	    
	// Define an object that holds accuracy and frequency parameters
	private LocationRequest mLocationRequest;
	
	private Context mContext;
	
	
	public interface MyLocationListener 
	{
		//public void getCurentLocation(Location location);
		public void onLocationChanged(Location location);
		public void onConnectionFailed();
		public void onConnected();
		public void onDisconnected();	
	}
	
	private LocationWrapper() {}
	
	//singleton pattern
	public static LocationWrapper getInstance() 
	{
	      if(sInstance == null){
	    	  sInstance = new LocationWrapper();
	      }
	      return sInstance;
	}
	
	//add the location user to the wrapper
	public void setLocationListener(MyLocationListener locationListener,Context ctx){
		mLocationListener = locationListener;
		mContext = ctx;
		
		mLocationClient = new LocationClient(mContext, this, this);	        
	    // Create the LocationRequest object
	    mLocationRequest = LocationRequest.create();
	    // Use high accuracy
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    // Set the update interval to 5 seconds
	    mLocationRequest.setInterval(UPDATE_INTERVAL);
	    // Set the fastest update interval to 1 second
	    mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	    
	    if (mLocationClient != null)
        {
        	 mLocationClient.connect();
        }
	    
	   
	}
		
	public void unsetLocationListener(){
		mLocationListener = null;
		mContext = null;
		if (mLocationClient.isConnected()) 
    	{
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
			mLocationClient.removeLocationUpdates(this);
        }
        // Disconnecting the client invalidates it.
    	if (mLocationClient != null)
        {
    		mLocationClient.disconnect();
        }
    	
    	
	}
	

	@Override
	public void onLocationChanged(Location location) {
		if (mLocationListener !=null) {
			mLocationListener.onLocationChanged(location);
		}
		
	}

	
	 /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		if (mLocationListener !=null) {
			mLocationListener.onConnectionFailed();
		}
		
	}

	
	 /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
	@Override
	public void onConnected(Bundle arg0) {
		
		if ( mLocationClient !=null && mLocationRequest!=null ) {
				mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
		
		if (mLocationListener !=null) {
			mLocationListener.onConnected();
		}
		
	}

	
	
	/*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
	@Override
	public void onDisconnected() {
		if (mLocationListener !=null) {
			mLocationListener.onDisconnected();
		}	
	}
	
	
	//a client can call this only if it is registered as location listener
	public Location getCurrentLocation() {
		if (mLocationClient != null && servicesConnected() && mLocationListener != null){
			mCurrentLocation = mLocationClient.getLastLocation();
			
			return mCurrentLocation;
			
			//double latitude = mCurrentLocation.getLatitude();
			//double longitude = mCurrentLocation.getLongitude();
		
		}
		
		return null;
	}
	
	 private boolean servicesConnected() {
		 	if (mContext == null) {
		 		return false;
		 	}
		 
	        // Check that Google Play services is available
	        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
	        // If Google Play services is available
	        if (ConnectionResult.SUCCESS == resultCode) 
	        {
	            // In debug mode, log the status
	            Log.d("Location Updates","Google Play services is available.");
	            // Continue
	            return true;
	        // Google Play services was not available for some reason
	        } 
	        else 
	        {
	            // Get the error code
	            //int errorCode = connectionResult.getErrorCode();
	           // int errorCode = resultCode;
	            // Get the error dialog from Google Play services
	           // Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
	           //         errorCode,
	           //         this,
	           //         CONNECTION_FAILURE_RESOLUTION_REQUEST);

	            // If Google Play services can provide an error dialog
	          //  if (errorDialog != null) 
	          //  {
	                // Create a new DialogFragment for the error dialog
	          //      ErrorDialogFragment errorFragment =
	          //              new ErrorDialogFragment();
	                // Set the dialog in the DialogFragment
	          //      errorFragment.setDialog(errorDialog);
	                // Show the error dialog in the DialogFragment
	           //     errorFragment.show(getFragmentManager(),"Location Updates");
	          //  }   
	            return false;
	        }
	    }
	
	
}
