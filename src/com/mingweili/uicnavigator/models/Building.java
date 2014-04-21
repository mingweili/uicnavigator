package com.mingweili.uicnavigator.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class for individual building data
 */
public class Building implements Serializable {
	
	// auto generated ID for serialization
	private static final long serialVersionUID = -6191113350466245234L;
	
	private String 				id;		// ID of the building
	private String 				name;	// Name of the building
	private double[] 			latLng;	// latitude and Longitude of the building
	private BuildingArea 		area; 	// Area (east/west campus) building located
	private ArrayList<String> 	depts;	// Department list located in the building
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public double[] getLatLng() {
		return this.latLng;
	}
	
	public void setLatLng(double[] latLng) {
		this.latLng = latLng;
	}

	public BuildingArea getArea() {
		return area;
	}
	
	public void setArea(BuildingArea area) {
		this.area = area;
	}
	
	public ArrayList<String> getDepts() {
		return this.depts;
	}
	
	public void setDepts(ArrayList<String> depts) {
		this.depts = depts;
	}
}
