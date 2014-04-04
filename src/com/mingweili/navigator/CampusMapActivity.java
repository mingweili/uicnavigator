package com.mingweili.navigator;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mingweili.navigator.models.Building;
import com.mingweili.navigator.models.BuildingArea;
import com.mingweili.navigator.utils.Util;

public class CampusMapActivity extends Activity implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	OnInfoWindowClickListener {

	/*
	 * this class is used for customized info window
	 */
	class CustomBuildingInfoWindowAdapter implements InfoWindowAdapter {

		private final View mWindow;
		
		public CustomBuildingInfoWindowAdapter() {
			mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
		}
		
		@Override
		public View getInfoContents(Marker marker) {
			return null;
		}

		@Override
        public View getInfoWindow(Marker marker) {
            render(marker, mWindow);
            return mWindow;
        }
		
		private void render(Marker marker, View view) {
			ImageView buildingThumbnail = (ImageView) view.findViewById(R.id.info_window_bldg_thumbnail);
			buildingThumbnail.setImageResource(R.drawable.info_window_bldg_thumbnail);
			
			String buildingId = marker.getTitle();
			TextView idText = (TextView) view.findViewById(R.id.info_window_bldg_id);
			idText.setText(buildingId);
			
			String buildingName = marker.getSnippet();
			TextView nameText = (TextView) view.findViewById(R.id.info_window_bldg_name);
			nameText.setText(buildingName);
		}
		
	}

	private GoogleMap 		mMap;
	private LocationClient 	mLocationClient;
	
	private ArrayList<Building> mBuildings;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_campus_map);
		getActionBar().setDisplayHomeAsUpEnabled(true);

        this.initializeLocationClient();
	}
	
	@Override
    protected void onResume() {
        super.onResume();        
        this.initializeLocationClient();
    }
	
	@Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.disconnect();
        }
    }
	
	@Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
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
	
	/*
	 * overriding methods for ConnectionCallbacks and OnConnectionFailedListener
	 */
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

	@Override
	public void onConnected(Bundle arg0) {
		// initialize map only until location service is online 
		// because map will need user's current location
        this.initializeMap();
	}

	@Override
	public void onDisconnected() {
		// Display the connection status
    	Toast.makeText(this, getString(R.string.ERROR_MESSAGE_LOCATION_CONNECT), Toast.LENGTH_LONG).show();
	}

	private void initializeMap() {
		if(mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		}
		if(mMap != null) {
			setupMap();
		}
	}
	private void setupMap() {
		// register customized info window
		mMap.setInfoWindowAdapter(new CustomBuildingInfoWindowAdapter());
		// register info window click event
		mMap.setOnInfoWindowClickListener(this);
		
		// set up UI controls programmatically
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(true);
		mMap.setBuildingsEnabled(true);
		mMap.setIndoorEnabled(true);

		// mark the buildings in UIC
		this.markBuildings();
		
		// set the default camera location
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		String defaultLocation = preference.getString(
				getString(R.string.settings_default_location_key), 
				getString(R.string.settings_default_location_default_value));
		
		double defaultLat = 0, defaultLng = 0;
		int zoomLevel = getResources().getInteger(R.integer.initial_zoom_level);
		String[] options = getResources().getStringArray(R.array.settings_default_location_choices);
		if(defaultLocation.equals(options[0])) {
			Location myLocation = mLocationClient.getLastLocation();
			defaultLat = myLocation.getLatitude();
			defaultLng = myLocation.getLongitude();
		}
		else if(defaultLocation.equals(options[1])) {
			defaultLat = Double.valueOf(getString(R.string.east_campus_lat));
			defaultLng = Double.valueOf(getString(R.string.east_campus_lng));
			zoomLevel -= 2;
		}
		else {
			defaultLat = Double.valueOf(getString(R.string.west_campus_lat));
			defaultLng = Double.valueOf(getString(R.string.west_campus_lng));
			zoomLevel -= 2;
		}
		
		CameraUpdate initialPosition 
			= CameraUpdateFactory.newLatLngZoom (new LatLng(defaultLat, defaultLng),zoomLevel);
		mMap.animateCamera(initialPosition);
		
	}

	private void markBuildings() {
		XmlResourceParser parser = getResources().getXml(R.xml.buildings);
		try {
			this.mBuildings = Util.readBuildings(parser, null);
			for(Building building : this.mBuildings) {
				LatLng buildingPosition = new LatLng(building.getLatLng()[0], building.getLatLng()[1]);
				MarkerOptions buildingMarker = new MarkerOptions();
				buildingMarker
					.position(buildingPosition)
					.title(building.getId())
					.snippet(building.getName())
					.alpha(0.3f)
					.icon(
							building.getArea() == BuildingArea.EAST ? 
									BitmapDescriptorFactory.defaultMarker(231) 
								  : BitmapDescriptorFactory.defaultMarker(0)
						 );
				mMap.addMarker(buildingMarker);
			}
		} catch (XmlPullParserException e) {
			Toast.makeText(this, getString(R.string.ERROR_MESSAGE_RESOURCE_READING), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(this, getString(R.string.ERROR_MESSAGE_RESOURCE_READING), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.campus_map, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent = new Intent();
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
	        case R.id.campus_map_menu_action_show_list:
	            intent.setClass(this, BuildingInventoryActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.campus_map_menu_action_navigate:
	            intent.setClass(this, NavigationActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.campus_map_menu_action_satellite_switch:
	        	switchMapType(item);
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

	private void switchMapType(MenuItem item) {
		// TODO Auto-generated method stub
		if(this.mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
			this.mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			item.setTitle(getString(R.string.campus_map_menu_action_satellite_switch_title_0));
		}
		else {
			this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			item.setTitle(getString(R.string.campus_map_menu_action_satellite_switch_title_1));
		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		// send the building information to the new activity
		Intent invokeBuildingInfoActivityIntent = new Intent(this, BuildingInfoActivity.class);
		Building buildingClicked = null;
		for(Building b : this.mBuildings) {
			if(b.getId().equals(marker.getTitle())) {
				buildingClicked = b;
				break;
			}
				
		}
		invokeBuildingInfoActivityIntent
			.putExtra(getResources().getString(R.string.EXTRA_MESSAGE_BUILDING_OBJECT), 
					buildingClicked);
		
		startActivity(invokeBuildingInfoActivityIntent);
	}

}
