package com.marakana.android.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The base activity with common features shared by TimelineActivity and
 * StatusActivity
 */
public class BaseActivity extends Activity { 
  YambaApplication yamba; 

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    yamba = (YambaApplication) getApplication(); 
  }

  // Called only once first time menu is clicked on
  @Override
  public boolean onCreateOptionsMenu(Menu menu) { 
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  // Called every time user clicks on a menu item
  @Override
  public boolean onOptionsItemSelected(MenuItem item) { 

    switch (item.getItemId()) {
    case R.id.itemPrefs:
      startActivity(new Intent(this, PrefsActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
      break;
    case R.id.itemToggleService:
      if (yamba.isServiceRunning()) {
        stopService(new Intent(this, UpdaterService.class));
      } else {
        startService(new Intent(this, UpdaterService.class));
      }
      break;
    case R.id.itemTimeline:
      startActivity(new Intent(this, TimelineActivity.class).addFlags(
          Intent.FLAG_ACTIVITY_SINGLE_TOP).addFlags(
          Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
      break;
    case R.id.itemStatus:
      startActivity(new Intent(this, StatusActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
      break;
    }
    return true;
  }

  // Called every time menu is opened
  @Override
  public boolean onMenuOpened(int featureId, Menu menu) { 
	  if(menu == null) return true;
    MenuItem toggleItem = menu.findItem(R.id.itemToggleService); 
    if (yamba.isServiceRunning()) { 
      toggleItem.setTitle(R.string.titleServiceStop);
      toggleItem.setIcon(android.R.drawable.ic_media_pause);
    } else { 
      toggleItem.setTitle(R.string.titleServiceStart);
      toggleItem.setIcon(android.R.drawable.ic_media_play);
    }
    return true;
  }

}