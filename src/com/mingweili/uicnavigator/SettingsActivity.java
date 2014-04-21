package com.mingweili.uicnavigator;

import com.mingweili.uicnavigator.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

/**
 * Activity of Settings screen.
 */
public class SettingsActivity extends PreferenceActivity
	implements OnSharedPreferenceChangeListener {
	/**
	 * Fragment of Settings panel
	 */
	public static class SettingsFragment extends PreferenceFragment {
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        // Load the preferences from an XML resource
	        addPreferencesFromResource(R.xml.preferences);
	    }
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Load the preferences from an XML resource.
		// Add Settings Fragment.
		getFragmentManager().beginTransaction()
        	.replace(android.R.id.content, new SettingsFragment())
        	.commit();
    }
	
	/** 
	 * Register preference change listener in onResume callback as recommended by official guide
	 */
	@Override
	protected void onResume() {
	    super.onResume();
	    PreferenceManager.getDefaultSharedPreferences(this)
    		.registerOnSharedPreferenceChangeListener(this);
	}
	
	/**
	 * Method of defining menu items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    // Respond to the action bar's Up/Home button
	    case android.R.id.home:
	        NavUtils.navigateUpFromSameTask(this);
	        return true;
	    }
	    return super.onOptionsItemSelected(item);
	}

	/**
	 * Method responding setting changes
	 */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		// Respond to settings changes: open campus map immediately
		if(key.equals(getResources().getString(R.string.settings_default_location_key))) {
			Intent intent = new Intent(this, CampusMapActivity.class);
			startActivity(intent);
		}
		
	}
}
