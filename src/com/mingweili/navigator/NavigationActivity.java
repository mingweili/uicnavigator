package com.mingweili.navigator;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.mingweili.navigator.models.Building;

public class NavigationActivity extends Activity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener, 
	// for alert dialog button callbacks
	GMapAlertFragment.NoticeDialogListener {
		
	private double[] mOriginLagLng;
	private double[] mDestinationLatLng;
	private String mNavigationUrl;
	private String mOriginText;
	private String mDestinationText;
	private LocationClient mLocationClient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigation);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		this.initializeLocationClient();
		
		// get the destination sent from BuildingInfo activity
		Building dest 
			= (Building) getIntent().getSerializableExtra(getResources().getString(R.string.EXTRA_MESSAGE_DESTINATION));
		
		if(dest != null) {
			this.mDestinationText = dest.getId();
			this.mDestinationLatLng = dest.getLatLng();
			
			EditText destinationEditText = (EditText) findViewById(R.id.navigation_to_edittext);
			destinationEditText.setText(this.mDestinationText);
		}
	}
	
	// receive the selection made by user on BuildingInventory activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent selectionResult) {
		if(resultCode == RESULT_OK) {
			Building selected 
				= (Building) selectionResult.getSerializableExtra(getResources().getString(R.string.EXTRA_MESSAGE_BUILDING_OBJECT));
			
			String type 
				= selectionResult.getStringExtra(getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL));
			
			// tell from "from"/"to"
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

	@Override
	public void onDisconnected() {
		Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();		
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 9000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
                Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();
            }
        } else {
        	Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();
        }
	}
	
	public void startNavigate(View view) {
		String originLatLng = "";
		String destinationLatLng = "";
		
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
		
		//try start google map app to start the navigation
		this.mNavigationUrl 
			= getResources().getString(R.string.navigation_google_map_url) 
			+ "?" + "saddr=" + originLatLng 
			+ "&" + "daddr=" + destinationLatLng;
		
		// before start the google map, show the instruction if not shown before
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
	
	@Override
	public void onDialogPositiveClick(boolean notShowChecked) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		preference.edit()
			.putBoolean(getResources().getString(R.string.settings_not_show_alert_key), notShowChecked).commit();

		Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.mNavigationUrl));
		startActivity(navIntent);
	}
	

	public void startSelection(String type) {
		Intent invokeBuildingInventory = new Intent(this, BuildingInventoryActivity.class);
		invokeBuildingInventory.putExtra(getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL), type);
		startActivityForResult(invokeBuildingInventory, 1);
	}
	
	public void startFromSelection(View view) {
		this.startSelection("from");
	}
	
	public void startToSelection(View view) {
		this.startSelection("to");
	}
	
	public void useMyLocationFrom(View view) {
		Location current = this.mLocationClient.getLastLocation();
		this.mOriginText = getResources().getString(R.string.navigation_my_location_text_placeholder);
		((EditText)findViewById(R.id.navigation_from_edittext)).setText(this.mOriginText);
		this.mOriginLagLng = new double[2];
		this.mOriginLagLng[0] = current.getLatitude();
		this.mOriginLagLng[1] = current.getLongitude();
	}
	
	public void useMyLocationTo(View view) {
		Location current = this.mLocationClient.getLastLocation();
		this.mDestinationText = getResources().getString(R.string.navigation_my_location_text_placeholder);
		((EditText)findViewById(R.id.navigation_to_edittext)).setText(this.mDestinationText);
		this.mDestinationLatLng = new double[2];
		this.mDestinationLatLng[0] = current.getLatitude();
		this.mDestinationLatLng[1] = current.getLongitude();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigation, menu);
		return true;
	}
	
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