package com.qloc.parking;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utilities {
	
	//=======	CHECK IF THE DEVICE HAS INTERNET CONNECTIVITY AND WHAT KIND	===============================================
	
		public enum NetState 
		{
			NONE, WIFI, MOBILE
		}

		
		/**
		 * returns the devices type of connectivity
		 * @param context : the context of the app
		 * @return NONE, WIFI, MOBILE
		 */
		public static NetState getNetState(Context context) 
		{

			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo ni = cm.getActiveNetworkInfo();
			if (ni == null)
				return NetState.NONE;
			if (ni.isConnected() == false)
				return NetState.NONE;
			if (ni.getType() == ConnectivityManager.TYPE_MOBILE)
				return NetState.MOBILE;
			return NetState.WIFI;
		}
		
		/**
		 * check if device has internet connectivity
		 * @param context : the context of the app
		 * @return true if there is connectivity, false otherwise
		 */
		public static boolean isNetworkAvailable(Context context)
		{
			return getNetState(context) != NetState.NONE;
		}
		
		
		
		
		public static void saveUID(String uid,Context context) {			
			SharedPreferences sharedPref = context.getSharedPreferences(
					    context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
				        Editor editor = sharedPref.edit();
				        editor.putString("uid", uid);
				        editor.commit();
				       		
		}
		
		public static String getUID(Context context) {			
			 SharedPreferences sharedPref = context.getSharedPreferences(
					 context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);			 
			 String uid = sharedPref.getString("uid", null);	 
			 return uid;		       		 
		}
		
		
		public static void savePID(String pid,Context context) {			
			SharedPreferences sharedPref = context.getSharedPreferences(
					    context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
				        Editor editor = sharedPref.edit();
				        editor.putString("pid", pid);
				        editor.commit();
				       		
		}
		
		public static String getPID(Context context) {			
			 SharedPreferences sharedPref = context.getSharedPreferences(
					 context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);			 
			 String pid = sharedPref.getString("pid", null);	 
			 return pid;		       		 
		}
		
		
		
		
		public static void saveParkingAddress(String address,Context context) {			
			SharedPreferences sharedPref = context.getSharedPreferences(
					    context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);
				        Editor editor = sharedPref.edit();
				        editor.putString("parkingAddress", address);
				        editor.commit();
				       		
		}
	
		public static String getParkingAddress(Context context) {			
			 SharedPreferences sharedPref = context.getSharedPreferences(
					 context.getString(R.string.sharedPreferencesFilename), Context.MODE_PRIVATE);			 
			 String address = sharedPref.getString("parkingAddress", null);	 
			 return address;		       		 
		}


}
