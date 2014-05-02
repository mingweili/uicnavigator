/**
 * © Mingwei Li, 2014. All rights reserved.
 */

package com.mingweili.uicnavigator;

import com.mingweili.uicnavigator.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 *  Activity of home screen.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * Method for dispatching different actions
	 */
	public void executeAction(View view) {
		Intent intent = new Intent();
		switch(view.getId()) {
		// open campus map option
		case R.id.main_action_1_imageview :
			intent.setClass(this, CampusMapActivity.class);
			break;
		// search buildings option
		case R.id.main_action_2_imageview :
			intent.setClass(this, BuildingInventoryActivity.class);
			break;
		// get directions option
		case R.id.main_action_3_imageview :
			intent.setClass(this, NavigationActivity.class);
			break;
		default :
			return;
		}
		startActivity(intent);
	}
	
	/**
	 * Method for defining menu items
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent = new Intent();
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
	        case R.id.campus_map_menu_action_settings:
	            intent.setClass(this, SettingsActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.campus_map_menu_action_about:
	            intent.setClass(this, AboutActivity.class);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
