package com.mingweili.uicnavigator;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.mingweili.uicnavigator.models.Building;
import com.mingweili.uicnavigator.models.BuildingInventoryListAdapter;
import com.mingweili.uicnavigator.models.SentFrom;

/**
 * Base class of East/WestCampusFragment. 
 */
public class BaseCampusFragment extends Fragment {
	protected String fromOrTo;								// This indicates whether user chooses origin point or destination when Navigation activity serves as selector
	protected SentFrom sentFrom;							// This indicates whether Navigation activity serves as selector or a full functional activity
	protected ArrayList<Building> mBuildings;				// list of building data
	protected BuildingInventoryListAdapter mListAdapter;	// list adapter

	public BaseCampusFragment() {}
	
	/**
	 * Method for list adapter accessor
	 */
	public BuildingInventoryListAdapter getListAdapter() {
		return this.mListAdapter;
	}
}
