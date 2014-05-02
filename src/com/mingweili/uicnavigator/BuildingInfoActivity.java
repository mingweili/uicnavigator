/**
 * © Mingwei Li, 2014. All rights reserved.
 */

package com.mingweili.uicnavigator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mingweili.uicnavigator.R;
import com.mingweili.uicnavigator.models.Building;
import com.mingweili.uicnavigator.models.BuildingArea;

public class BuildingInfoActivity extends Activity {
	
	private Building mBuilding;
	private GoogleMap mMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building_info);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// set up the building id and name based on the intent sent from MainActivity
		Intent intent = getIntent();
		this.mBuilding 
			= (Building) intent.getSerializableExtra(getResources().getString(R.string.EXTRA_MESSAGE_BUILDING_OBJECT));
		
		String buildingId = mBuilding.getId();
		this.setTitle(buildingId);
		
		String buildingName = this.mBuilding.getName();
		TextView nameTextView = (TextView) findViewById(R.id.building_info_name_textview);
		nameTextView.setText(buildingName);
		
		ImageView buildingImage = (ImageView)findViewById(R.id.building_info_image_imageview);
		String buildingNamePrefix 
			= this.mBuilding.getArea() == BuildingArea.EAST ? "campus_east_" : "campus_west_";
		int imageId 
			= getResources().getIdentifier(buildingNamePrefix + this.mBuilding.getId().toLowerCase(), "drawable", this.getPackageName());
		if(imageId != 0)
			buildingImage.setImageResource(imageId);
		else
			buildingImage.setImageResource(R.drawable.building_image_placeholder);
		
		this.setUpMap();
	}

	private void setUpMap() {
		// TODO Auto-generated method stub
		if(mMap == null)
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.building_info_map)).getMap();
		
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.setMyLocationEnabled(false);
		mMap.setBuildingsEnabled(true);
		mMap.setIndoorEnabled(true);
		
		LatLng buildingLatLng = new LatLng(this.mBuilding.getLatLng()[0], this.mBuilding.getLatLng()[1]);
		
		int zoomLevel = getResources().getInteger(R.integer.initial_zoom_level) - 2;
		CameraUpdate buildingPosition 
			= CameraUpdateFactory.newLatLngZoom (buildingLatLng, zoomLevel);
		mMap.moveCamera(buildingPosition);
		
		mMap.addMarker(
				new MarkerOptions()
					.position(buildingLatLng)
					.icon(
							this.mBuilding.getArea() == BuildingArea.EAST ? 
									BitmapDescriptorFactory.defaultMarker(231)
								  : BitmapDescriptorFactory.defaultMarker(0)
						 )
				);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_info, menu);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent = new Intent();
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
	    	case R.id.building_info_menu_action_navigate:
	    		this.sendToNavigate();
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

	public void sendToNavigate() {
		Intent invokeNavigationActivityIntent = new Intent(this, NavigationActivity.class);
		invokeNavigationActivityIntent
			.putExtra(getResources().getString(R.string.EXTRA_MESSAGE_DESTINATION), this.mBuilding);
		startActivity(invokeNavigationActivityIntent);
	}

}
