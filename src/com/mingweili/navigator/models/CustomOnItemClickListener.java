package com.mingweili.navigator.models;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.mingweili.navigator.BuildingInfoActivity;
import com.mingweili.navigator.BuildingInventoryActivity;
import com.mingweili.navigator.R;

/**
 * Listener to responding to list item click event.
 */
public class CustomOnItemClickListener implements OnItemClickListener {
	// Hold this indicator to distinguish the click response based on different intent source
	private SentFrom sentFrom;
	public CustomOnItemClickListener(SentFrom sf) { this.sentFrom = sf; }
	
	/**
	 * Overriding method to respond to user's click on list item
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// Get the handle of parent activity (BuildingInventory) and set the selected building into parent
		BuildingInventoryActivity activity = (BuildingInventoryActivity) view.getContext();
		Building selected = (Building) parent.getItemAtPosition(position);
		Intent intent = new Intent(parent.getContext(), BuildingInfoActivity.class);
		intent.putExtra(activity.getResources().getString(R.string.EXTRA_MESSAGE_BUILDING_OBJECT), selected);
		// Go to BuildingInfo Activity directly
		if(this.sentFrom == SentFrom.MAIN) {
			parent.getContext().startActivity(intent);
		}
		// Set the selection result for Navigation activity
		else if(this.sentFrom == SentFrom.NAVIGATION){
			// indicate whether this is from building or to building the user are selecting from navigation panel
			intent.putExtra(activity.getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL), activity.getFromOrTo());
			activity.setResult(Activity.RESULT_OK, intent);
			activity.finish();
		}
		else {
			return;
		}
	}
	
}
