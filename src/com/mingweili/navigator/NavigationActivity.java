package com.mingweili.navigator;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.mingweili.navigator.models.Building;

/**
 * Activity of navigation screen.
 */
public class NavigationActivity extends Activity implements
	GooglePlayServicesClient.ConnectionCallbacks,			// Interface for location services
	GooglePlayServicesClient.OnConnectionFailedListener, 	// Interface for location services
	GMapAlertFragment.NoticeDialogListener {				// For alert dialog button callbacks
		
	private double[] 		mOriginLagLng;					// Latitude and Longitude of origin point in navigation 
	private double[] 		mDestinationLatLng;				// Latitude and Longitude of destination point in navigation 
	private String 			mNavigationUrl;					// Url used to call Google Maps
	private String 			mOriginText;					// Text representation of origin point in navigation
	private String 			mDestinationText;				// Text representation of destination point in navigation
	private LocationClient 	mLocationClient;				// Handle of location services
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// Initialize location services
		this.initializeLocationClient();
		
		// Get the destination sent from BuildingInfo activity
		// BuildingInfo activity can invoke this activity
		// for routing request from my location (default value) 
		// to that building
		Building dest 
			= (Building) getIntent().getSerializableExtra(getResources().getString(R.string.EXTRA_MESSAGE_DESTINATION));
		
		if(dest != null) {
			this.mDestinationText = dest.getId();
			this.mDestinationLatLng = dest.getLatLng();
			
			EditText destinationEditText = (EditText) findViewById(R.id.navigation_to_edittext);
			destinationEditText.setText(this.mDestinationText);
		}
		
		// initialize default travel mode selection from preferences set in Settings screen
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		String prefTravelMode = preference.getString(
				getResources().getString(R.string.settings_default_travel_mode_key), 
				getResources().getString(R.string.settings_default_travel_mode_default_value));
		
		RadioGroup travelMode = (RadioGroup) findViewById(R.id.navigation_travel_mode_radiogroup);
		if(prefTravelMode.equals(getResources().getString(R.string.navigation_travel_mode_option1_label))) {
			travelMode.check(R.id.navigation_travel_mode_option_1);
		}
		else if(prefTravelMode.equals(getResources().getString(R.string.navigation_travel_mode_option2_label))) {
			travelMode.check(R.id.navigation_travel_mode_option_2);
		}
		else if(prefTravelMode.equals(getResources().getString(R.string.navigation_travel_mode_option3_label))) {
			travelMode.check(R.id.navigation_travel_mode_option_3);
		}
		else {
			travelMode.check(R.id.navigation_travel_mode_option_1);
		}
	}
	
	/** 
	 * Method for receiving the selection made by user on BuildingInventory activity
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent selectionResult) {
		if(resultCode == RESULT_OK) {
			Building selected 
				= (Building) selectionResult.getSerializableExtra(getResources().getString(R.string.EXTRA_MESSAGE_BUILDING_OBJECT));
			
			String type 
				= selectionResult.getStringExtra(getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL));
			
			// Distinguish from "from"/"to" to determine whether it's origin 
			// point selection or destination point selections
			if(type.equals("from")) {
				this.mOriginText = selected.getId();
				this.mOriginLagLng = selected.getLatLng();
				((EditText)findViewById(R.id.navigation_from_edittext)).setText(this.mOriginText);
			}
			else {
				this.mDestinationText = selected.getId();
				this.mDestinationLatLng = selected.getLatLng();
				((EditText)findViewById(R.id.navigation_to_edittext)).setText(this.mDestinationText);
			}
		}
	}

	/**
	 * Method for initializing location services
	 */
	private void initializeLocationClient() {
		if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener

            if(!mLocationClient.isConnected())
            	mLocationClient.connect();
        }
	}
	
	/**
	 * Overriding method to define location service connection success event.
	 * Set the default origin point to be "my current location"
	 */
	@Override
	public void onConnected(Bundle bundle) {
		// set the default origin as "my location"
		Location current = this.mLocationClient.getLastLocation();
		this.mOriginLagLng = new double[2];
		this.mOriginLagLng[0] = current.getLatitude();
		this.mOriginLagLng[1] = current.getLongitude();
		this.mOriginText = getResources().getString(R.string.navigation_my_location_text_placeholder);
		((EditText)findViewById(R.id.navigation_from_edittext)).setText(this.mOriginText);
	}

	/**
	 * Overriding methods in terms of location services related interfaces
	 */
	@Override
	public void onDisconnected() {
		Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Method of primary action to start navigation.
	 * 1. Get origin/destination from user's input
	 * 2. Invoke Google Maps via Intent
	 */
	public void startNavigate(View view) {
		String originLatLng = "";
		String destinationLatLng = "";
		
		// Robustness
		if(this.mOriginLagLng == null || this.mDestinationLatLng == null) {
			Toast.makeText(this, getString(R.string.ERROR_MESSAGE_INVALID_INPUT), Toast.LENGTH_LONG).show();
			return;
		}
		if(this.mOriginText.equals(this.mDestinationText)) {
			Toast.makeText(this, getString(R.string.ERROR_MESSAGE_DUPLICATED_INPUT), Toast.LENGTH_LONG).show();
			return;
		}
		
		originLatLng = String.valueOf(this.mOriginLagLng[0]) + "," + String.valueOf(this.mOriginLagLng[1]);
		destinationLatLng = String.valueOf(this.mDestinationLatLng[0]) + "," + String.valueOf(this.mDestinationLatLng[1]);
		
		RadioGroup travelMode = (RadioGroup) findViewById(R.id.navigation_travel_mode_radiogroup);
		int checkedOptionId = travelMode.getCheckedRadioButtonId();
		String travelModePara = "";
		switch(checkedOptionId) {
		case R.id.navigation_travel_mode_option_1 :
			travelModePara = "w";
			break;
		case R.id.navigation_travel_mode_option_2 :
			travelModePara = "r";
			break;
		case R.id.navigation_travel_mode_option_3 :
			travelModePara = "d";
			break;
		}
		//Try start google map app to start the navigation
		this.mNavigationUrl 
			= getResources().getString(R.string.navigation_google_map_url) 
			+ "?" + "saddr=" + originLatLng 
			+ "&" + "daddr=" + destinationLatLng
			+ "&" + "dirflg=" + travelModePara;
		
		// Before start the google map, show the instruction if not shown before
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		boolean notShowAgain = preference.getBoolean(getResources().getString(R.string.settings_not_show_alert_key), false);
		if(!notShowAgain) {
			DialogFragment dialog = new GMapAlertFragment();
			dialog.show(this.getFragmentManager(), GMapAlertFragment.class.getName());
		}		
		else {
			Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.mNavigationUrl));
			startActivity(navIntent);
		}
	}
	
	/**
	 * Overriding method for dialog confirm button click event.
	 * Remember user's choice of whether they want to show this dialog again
	 */
	
	@Override
	public void onDialogPositiveClick(boolean notShowChecked) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		preference.edit()
			.putBoolean(getResources().getString(R.string.settings_not_show_alert_key), notShowChecked).commit();

		// When dialog dismissed, call Google Maps for routing
		Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.mNavigationUrl));
		startActivity(navIntent);
	}
	

	/**
	 * Method for invoking BuildingInventory activity to request user's choice of buildings
	 */
	public void startSelection(String type) {
		Intent invokeBuildingInventory = new Intent(this, BuildingInventoryActivity.class);
		invokeBuildingInventory.putExtra(getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL), type);
		startActivityForResult(invokeBuildingInventory, 1);
	}
	
	/**
	 * Method to begin origin point selection.
	 * Also the "@+id/navigation_select_from_button" click event binder.
	 */
	public void startFromSelection(View view) {
		this.startSelection("from");
	}

	/**
	 * Method to begin destination point selection.
	 * Also the "@+id/navigation_select_to_button" click event binder.
	 */
	public void startToSelection(View view) {
		this.startSelection("to");
	}
	
	/**
	 * Method to retrieve "my current location" for origin point.
	 * Also the "@+id/navigation_locate_from_imagebutton" click event binder.
	 */
	public void useMyLocationFrom(View view) {
		Location current = this.mLocationClient.getLastLocation();
		this.mOriginText = getResources().getString(R.string.navigation_my_location_text_placeholder);
		((EditText)findViewById(R.id.navigation_from_edittext)).setText(this.mOriginText);
		this.mOriginLagLng = new double[2];
		this.mOriginLagLng[0] = current.getLatitude();
		this.mOriginLagLng[1] = current.getLongitude();
	}

	/**
	 * Method to retrieve "my current location" for destination point.
	 * Also the "@+id/navigation_locate_to_imagebutton" click event binder.
	 */
	public void useMyLocationTo(View view) {
		Location current = this.mLocationClient.getLastLocation();
		this.mDestinationText = getResources().getString(R.string.navigation_my_location_text_placeholder);
		((EditText)findViewById(R.id.navigation_to_edittext)).setText(this.mDestinationText);
		this.mDestinationLatLng = new double[2];
		this.mDestinationLatLng[0] = current.getLatitude();
		this.mDestinationLatLng[1] = current.getLongitude();
	}
	
	/**
	 * Method for inflate menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
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