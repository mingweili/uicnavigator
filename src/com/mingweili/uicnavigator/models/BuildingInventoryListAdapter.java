/**
 * © Mingwei Li, 2014. All rights reserved.
 */

package com.mingweili.uicnavigator.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mingweili.uicnavigator.R;
import com.mingweili.uicnavigator.utils.Util;

/**
 * List adapter of building list.
 * Used in East/WestCampusFragment
 */
public class BuildingInventoryListAdapter extends BaseAdapter {
	
	private ArrayList<Building> mListData;			// Building list, change upon user's query change
	private ArrayList<Building> mFullList;			// Full building list
    private LayoutInflater 		mLayoutInflater;	// Layout inflater
    
    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
	public BuildingInventoryListAdapter(Context context, ArrayList<Building> listData) {
    	this.mListData = listData;
    	this.mFullList = (ArrayList<Building>) this.mListData.clone();
        this.mLayoutInflater = LayoutInflater.from(context);
    }
    
    /** 
     * The updater based on user's query on real time
     */
	public void query(String q) {
		q = q.trim().toLowerCase();
		if(q.length() <= 0) {
			this.mListData = this.mFullList;
			this.notifyDataSetChanged();
		}
		else {
			// filter matched building item and update the list
			this.mListData = Util.queryBuildingsMatched(q, this.mFullList);
			this.notifyDataSetChanged();
		}
	}
	
	/**
	 * Method to reset the list to full list when user reset query
	 */
	public BuildingInventoryListAdapter resetList() {
		this.mListData = this.mFullList;
		this.notifyDataSetChanged();
		return this;
	}
	
	/**
	 * Overriding method to get list size
	 */
	@Override
	public int getCount() {
		return this.mListData.size();
	}

	/**
	 * Overriding method to get certain list item
	 */
	@Override
	public Object getItem(int position) {
		return this.mListData.get(position);
	}

	/**
	 * Overriding method to get item id
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Overriding method to provide list item UI to system
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
        	// Bind custom list row to the adapter
            convertView = this.mLayoutInflater.inflate(R.layout.custom_list_row, null);
            
            // Initialize the view holder that holds list item
            holder = new ViewHolder();
            holder.buildingId = (TextView) convertView.findViewById(R.id.list_row_title);
            holder.buildingName = (TextView) convertView.findViewById(R.id.list_row_desc);
            holder.buildingDepts = (TextView) convertView.findViewById(R.id.list_row_depts);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
 
        Building building = this.mListData.get(position);
        holder.buildingId.setText(building.getId());
        holder.buildingName.setText(building.getName());
        
        String depts = "";
        for(String dept : building.getDepts()) {
        	depts += (dept + "\n");
        }
        
        if(depts.equals(""))
        	depts = " ";
        
	    holder.buildingDepts.setText(depts);

        return convertView;
	}
	
	/**
	 * Class that holds UI for single list item
	 */
	private class ViewHolder {
        TextView buildingId;
        TextView buildingName;
        TextView buildingDepts;
    }

}
