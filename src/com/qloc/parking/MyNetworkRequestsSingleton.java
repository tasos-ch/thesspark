package com.qloc.parking;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MyNetworkRequestsSingleton {
	
	public static final String TESTING_SERVER_URL = "http://api.redback.gr/dev/apps/load.php";
	public static final String SERVER_URL = "https://api.redback.gr/apps/load.php";
	public static final String APP_ID = "45"; //app_id for redback platform
	public static final String A_KEY = "b84d04418e3e38e7d15982841b43a727"; //developers key for redback platform
	
	private static MyNetworkRequestsSingleton mInstance;
    private RequestQueue mRequestQueue;
   // private ImageLoader mImageLoader;
    private static Context mCtx;
    

    private MyNetworkRequestsSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

      /*
       mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                   cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
        */
    }

    public static synchronized MyNetworkRequestsSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNetworkRequestsSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

   
    /*
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
    */

}
