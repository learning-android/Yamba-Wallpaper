package com.marakana.android.yamba;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class TimelineActivity extends BaseActivity { 
	static final String SEND_TIMELINE_NOTIFICATIONS =
		      "com.marakana.android.yamba.SEND_TIMELINE_NOTIFICATIONS";	
	
  Cursor cursor;
  ListView listTimeline;
  SimpleCursorAdapter adapter;
  static final String[] FROM = { DbHelper.C_CREATED_AT, DbHelper.C_USER,
      DbHelper.C_TEXT };
  static final int[] TO = { R.id.textCreatedAt, R.id.textUser, R.id.textText };
  
  IntentFilter filter;
  BroadcastReceiver receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.timeline);

    // Check if preferences have been set
//    if (yamba.getPrefs().getString("username", null) == null) { 
//      startActivity(new Intent(this, PrefsActivity.class));
//      Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
//    }

    // Find your views
    listTimeline = (ListView) findViewById(R.id.listTimeline);
    
    filter = new IntentFilter("com.marakana.android.yamba.NEW_STATUS");
    
    receiver = new TimelineReceiver();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Setup List
    this.setupList();
    
    registerReceiver(receiver, filter); //, SEND_TIMELINE_NOTIFICATIONS, null);
  }

  @Override
  protected void onPause() {
    super.onPause();

    // UNregister the receiver
    unregisterReceiver(receiver);  
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    
    //close db
    yamba.getStatusData().close();
  }

  // Responsible for fetching data and setting up the list and the adapter
  private void setupList() {
    // Get the data
    cursor = yamba.getStatusData().getStatusUpdates();
    startManagingCursor(cursor);

    // Setup Adapter
    adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
    adapter.setViewBinder(VIEW_BINDER); 
    listTimeline.setAdapter(adapter);
  }

  // View binder constant to inject business logic for timestamp to relative
  // time conversion
  static final ViewBinder VIEW_BINDER = new ViewBinder() { 

    public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
      if (view.getId() != R.id.textCreatedAt)
        return false;

      // Update the created at text to relative time
      long timestamp = cursor.getLong(columnIndex);
      CharSequence relTime = DateUtils.getRelativeTimeSpanString(view
          .getContext(), timestamp);
      ((TextView) view).setText(relTime);

      return true;
    }

  };
  
  class TimelineReceiver extends BroadcastReceiver { 
	  @Override
	  public void onReceive(Context context, Intent intent) { 
		  Log.d("TimelineReceiver","WOOOOOOOOOOOO");
	    cursor = yamba.getStatusData().getStatusUpdates(); 
	    adapter.changeCursor(cursor);
	    adapter.notifyDataSetChanged(); 
	    Log.d("TimelineReceiver", "onReceived");
	  }
	}
}