package com.qloc.parking;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class Service_ParkingArrival extends Service implements LocationWrapper.MyLocationListener{
	
	  private LocationWrapper mLocationWrapper;
	  private WindowManager mWindowManager;
	  private WindowManager.LayoutParams mParams;
	  private View mView;
	  private double mDestinationLatitude;
	  private double mDestinationLongitude;
	  
	 	  
	  @Override 
	  public void onCreate() {
		  super.onCreate();
		  mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		 
		
	  }
	  
	  
	  @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
		  if (intent.hasExtra("destinationLatitude")) {
			  mDestinationLatitude = intent.getDoubleExtra("destinationLatitude", 0);
		  }
		  else {
			  myStopSelf(); 
		  }
		  
		  if (intent.hasExtra("destinationLongitude")) {
			  mDestinationLongitude = intent.getDoubleExtra("destinationLongitude", 0);
		  }
		  else {
			  myStopSelf();
		  }
		  
		  mLocationWrapper = LocationWrapper.getInstance();
		  mLocationWrapper.setLocationListener(this, getBaseContext());
		  
		  return START_STICKY;
	  }
	  
	 

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onLocationChanged(Location location) {
		Double currentLatitude = location.getLatitude();
		Double currentLongitude = location.getLongitude();
		
		double  dist = distFrom(currentLatitude, currentLongitude, mDestinationLatitude, mDestinationLongitude);
		//Toast.makeText(this, "distance from destination = " + String.valueOf(dist), Toast.LENGTH_LONG).show();
		
		if (dist < 50){
			//edw emfanise to view gia na epileksei o xrhsths an parkare h oxi
			showArrivedAtParking();
			myStopSelf();
		}
		
	}

	@Override
	public void onConnectionFailed() {
		// TODO Auto-generated method stub
		Log.i("dfd","sdfd");
		
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		Log.i("dfd","sdfd");
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Log.i("dfd","sdfd");
		
	}
	
	private void myStopSelf() {
		 if (mLocationWrapper != null) {
		 	mLocationWrapper.unsetLocationListener();
		 }
		 stopSelf();
	}
	
	//in meters
	  public static double distFrom(double lat1, double lon1, double lat2, double lon2) {
		  double R = 6372.8;  
		  double dLat = Math.toRadians(lat2 - lat1);
	        double dLon = Math.toRadians(lon2 - lon1);
	        lat1 = Math.toRadians(lat1);
	        lat2 = Math.toRadians(lat2);
	 
	        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
	        double c = 2 * Math.asin(Math.sqrt(a));
	        return R * c * 1000;
	    }
	
	/*
	public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
	    double earthRadius = 6371; //kilometers
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = (double) (earthRadius * c);

	    return dist;
	}
	*/
	
	
	private void showArrivedAtParking() {
		
 	    LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
 	    mView = inflater.inflate(R.layout.arrived_at_parking, null);
 	        
 	    mParams = new WindowManager.LayoutParams(
 	        WindowManager.LayoutParams.WRAP_CONTENT,
 	        WindowManager.LayoutParams.WRAP_CONTENT,
 	        WindowManager.LayoutParams.TYPE_PHONE,
 	        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
 	        PixelFormat.TRANSLUCENT);
 	    
 	     mParams.windowAnimations = android.R.style.Animation_Toast;
 	   
	 	 mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
	 	 //mParams.x = 0;
	 	 //mParams.y = 30;

 	     OnTouchListener touchListener=new OnTouchListener(){
 	        public boolean onTouch(View v,MotionEvent event){
 	          if (event.getAction() == MotionEvent.ACTION_DOWN) {
 	        	  
	 	        	 Intent confirmIntent = new Intent(getBaseContext(),Activity_ConfirmParking.class);
	 	        	 confirmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	 	        	 startActivity(confirmIntent);
	 	        	 if (mView != null) {  	
	 	    		    mWindowManager.removeView(mView);
	 	    		 }	
	 	        	 stopSelf();
 	        	 
 	          }
 	          return false;
 	        }
 	      }
 	    ;
 	        
 	   mView.setOnTouchListener(touchListener);
 	   mWindowManager.addView(mView, mParams);
	}
	
	 @Override
	 public void onDestroy() {
		 super.onDestroy();
		
	 }

}
