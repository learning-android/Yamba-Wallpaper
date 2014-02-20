package com.marakana.android.yamba;

import java.util.List;

import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClient.Status;
import com.marakana.android.yamba.clientlib.YambaClientException;

public class YambaApplication extends Application implements
    OnSharedPreferenceChangeListener { 
  private static final String TAG = YambaApplication.class.getSimpleName();
  public YambaClient yamba; 
  private SharedPreferences prefs;
  private boolean serviceRunning;
  
  private StatusData statusData;

  @Override
  public void onCreate() { 
    super.onCreate();
    this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
    this.prefs.registerOnSharedPreferenceChangeListener(this);
    Log.i(TAG, "onCreated");
  }

  @Override
  public void onTerminate() { 
    super.onTerminate();
    Log.i(TAG, "onTerminated");
  }

  public synchronized YambaClient getYambaClient() {
    if (this.yamba == null) {
      String username = this.prefs.getString("username", "student");
      String password = this.prefs.getString("password", "password");
      if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
        this.yamba = new YambaClient(username, password);
      }
    }
    return this.yamba;
  }

  public synchronized void onSharedPreferenceChanged(
      SharedPreferences sharedPreferences, String key) { 
    this.yamba = null;
  }
  
  public boolean isServiceRunning() { 
    return serviceRunning;
    }

  public void setServiceRunning(boolean serviceRunning) { 
    this.serviceRunning = serviceRunning;
    }
  
  public StatusData getStatusData() {
  if (statusData==null) {
    statusData = new StatusData(this);
  }
  return statusData;
}
//Connects to the online service and puts the latest statuses into DB.
//Returns the count of new statuses
public synchronized int fetchStatusUpdates() { 
 Log.d(TAG, "Fetching status updates");
 YambaClient yamba = this.getYambaClient();
 if (yamba == null) {
   Log.d(TAG, "Yamba connection info not initialized");
   return 0;
 }
 try {
   List<Status> statusUpdates = yamba.getTimeline(20);
   long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
   int count = 0;
   ContentValues values = new ContentValues();
   for (Status status : statusUpdates) {
     values.put(StatusData.C_ID, status.getId());
     long createdAt = status.getCreatedAt().getTime();
     values.put(StatusData.C_CREATED_AT, createdAt);
     values.put(StatusData.C_TEXT, status.getMessage());
     values.put(StatusData.C_USER, status.getUser());
     Log.d(TAG, "Got update with id " + status.getId() + ". Saving");
     this.getStatusData().insertOrIgnore(values);
     if (latestStatusCreatedAtTime < createdAt) {
       count++;
     }
   }
   Log.d(TAG, count > 0 ? "Got " + count + " status updates"
       : "No new status updates");
   return count;
 } catch (YambaClientException yce) {
   Log.e(TAG, "Failed to fetch status updates", yce);
   return 0;
 } catch (RuntimeException e) {
   Log.e(TAG, "Failed to fetch status updates", e);
   return 0;
 }
}

	public SharedPreferences getPrefs() {
		return prefs;
	}
}