package com.mingweili.navigator.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mingweili.navigator.R;
import com.mingweili.navigator.utils.Util;

public class BuildingInventoryListAdapter extends BaseAdapter {
	
	private ArrayList<Building> mListData;
	private ArrayList<Building> mFullList;
    private LayoutInflater mLayoutInflater;
    
    @SuppressWarnings("unchecked")
	public BuildingInventoryListAdapter(Context context, ArrayList<Building> listData) {
    	this.mListData = listData;
    	this.mFullList = (ArrayList<Building>) this.mListData.clone();
        this.mLayoutInflater = LayoutInflater.from(context);
    }
    
    // the updater based on user's query on real time
	public void query(String q) {
		q = q.trim().toLowerCase();
		if(q.length() <= 0) {
			this.mListData = this.mFullList;
			this.notifyDataSetChanged();
		}
		else {
			this.mListData = Util.queryBuildingsMatched(q, this.mFullList);
			this.notifyDataSetChanged();
		}
	}
	
	public BuildingInventoryListAdapter resetList() {
		this.mListData = this.mFullList;
		this.notifyDataSetChanged();
		return this;
	}
	@Override
	public int getCount() {
		return this.mListData.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
        	// bind custom list row to the adapter
            convertView = this.mLayoutInflater.inflate(R.layout.custom_list_row, null);
            
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
	
	private class ViewHolder {
        TextView buildingId;
        TextView buildingName;
        TextView buildingDepts;
    }

}
