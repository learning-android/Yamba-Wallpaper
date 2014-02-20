package com.marakana.android.yamba;

import com.marakana.android.yamba.R;

import android.os.Bundle;

public class StatusActivity extends BaseActivity 
//	implements OnSharedPreferenceChangeListener 
	{
//	protected SharedPreferences prefs;
//	protected YambaClient yamba;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_activity_status);
		
//		prefs = PreferenceManager.getDefaultSharedPreferences(this); 
//      prefs.registerOnSharedPreferenceChangeListener(this); 
	}
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu, menu);
//		return true;
//	}
	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//	  switch (item.getItemId()) {      
//	  case R.id.itemServiceStart:
//		    startService(new Intent(this, UpdaterService.class));
//		    break;
//	  case R.id.itemServiceStop:
//	    stopService(new Intent(this, UpdaterService.class)); 
//	    break;
//	  case R.id.itemPrefs:
//	    startActivity(new Intent(this, PrefsActivity.class));  
//	  break;
//	  }
//
//	  return true;
//	}
	
//	@Override
//	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
//        // invalidate yamba object
//        yamba = null;
//	}
//	
//	public YambaClient getYambaClient() {
//		  if (yamba == null) { 
//		    String username, password;
//		    username = prefs.getString("username", "student"); 
//		    password = prefs.getString("password", "password");
//
//		    // Connect
//		    yamba = new YambaClient(username, password); 
//		  }
//		  return yamba;
//		}
}
