package com.mingweili.navigator;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.mingweili.navigator.models.Building;
import com.mingweili.navigator.models.BuildingArea;
import com.mingweili.navigator.models.BuildingInventoryListAdapter;
import com.mingweili.navigator.models.CustomOnItemClickListener;
import com.mingweili.navigator.models.SentFrom;
import com.mingweili.navigator.utils.Util;

/**
 * Fragment for west campus building list inside BuildingInventory Activity
 */
public class WestCampusFragment extends BaseCampusFragment {

	public WestCampusFragment() {}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate layout shared with west campus building list
		View rootView = inflater.inflate(R.layout.fragment_campus_list, container, false);
		this.sentFrom 
			= (SentFrom) this.getArguments().getSerializable(rootView.getResources().getString(R.string.EXTRA_MESSAGE_SENT_FROM));
		this.fromOrTo 
			= this.getArguments().getString(rootView.getResources().getString(R.string.EXTRA_MESSAGE_FROM_OR_TO));
		
		// Load building data from XML file
		try {
			XmlResourceParser parser = getResources().getXml(R.xml.buildings);
			this.mBuildings = Util.readBuildings(parser, BuildingArea.WEST);
			ArrayList<Building> listData = new ArrayList<Building>(this.mBuildings);
			ListView listView = (ListView) rootView.findViewById(R.id.building_inventory_list);
			// Initialize list adapter
			this.mListAdapter = new BuildingInventoryListAdapter(rootView.getContext(), listData);
			listView.setAdapter(this.mListAdapter);
			// Register item click listener
			listView.setOnItemClickListener(new CustomOnItemClickListener(this.sentFrom));
			
			return rootView;
		} catch (XmlPullParserException e) {
			Toast.makeText(rootView.getContext(), getString(R.string.ERROR_MESSAGE_RESOURCE_READING), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(rootView.getContext(), getString(R.string.ERROR_MESSAGE_RESOURCE_READING), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		
		return rootView;
	}
}