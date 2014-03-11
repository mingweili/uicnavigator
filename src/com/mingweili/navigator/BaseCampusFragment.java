package com.mingweili.navigator;

import java.util.ArrayList;

import android.support.v4.app.Fragment;

import com.mingweili.navigator.models.Building;
import com.mingweili.navigator.models.BuildingInventoryListAdapter;
import com.mingweili.navigator.models.SentFrom;

public class BaseCampusFragment extends Fragment {
	protected String fromOrTo;
	protected SentFrom sentFrom;
	protected ArrayList<Building> mBuildings;
	protected BuildingInventoryListAdapter mListAdapter;

	public BaseCampusFragment() {}
	
	public BuildingInventoryListAdapter getListAdapter() {
		return this.mListAdapter;
	}
}
