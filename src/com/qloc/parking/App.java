package com.qloc.parking;

public class App {
	
	private App(){}
	
	private static App sInstance = null;
		
	public static App instance() {
		if (sInstance == null) {
			sInstance = new App();
		}
		return sInstance;
	}
	
	private String uid; //the uid that i an sending to all calls to the server
	
	
	public String getUID() {
		return uid;
	}
	
	public void setUID(String aUID) {
		uid = aUID;
	}

}
