package com.mingweili.navigator.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParserException;

import android.content.res.XmlResourceParser;

import com.mingweili.navigator.models.Building;
import com.mingweili.navigator.models.BuildingArea;

public class Util {
	
	private static ArrayList<Building> sBuildings;
	
	public static ArrayList<Building> readBuildings(XmlResourceParser parser, BuildingArea area) 
			throws XmlPullParserException, IOException {
		if(sBuildings == null || sBuildings.size() <= 0) {
			readBuildings(parser);
		}
		
		// Main activity doesn't specify area, it will read all of the building all at once.
		if(area == null) return sBuildings;
		
		ArrayList<Building> result = new ArrayList<Building>();
		for(Building b : sBuildings) {
			if(b.getArea() == area) {
				result.add(b);
			}
		}
		
		return result;
	}
	
	public static void readBuildings(XmlResourceParser parser) throws XmlPullParserException, IOException {
		sBuildings = new ArrayList<Building>();
		while(parser.next() != XmlResourceParser.END_DOCUMENT) {
			if(parser.getEventType() == XmlResourceParser.START_TAG) {
				
				// add each building from xml
				if(parser.getName().equals("building")) {
					Building b = initBuilding(parser);
					sBuildings.add(b);
				}
			}
		}
		
		parser.close();
	}

	private static Building initBuilding(XmlResourceParser parser) throws XmlPullParserException, IOException {
		Building building = new Building();
		// get the building id from attribute
		String id = parser.getAttributeValue(null, "id");		
		String name = "";
		double lat = 0.0;
		double lng = 0.0;
		String area = "";
		ArrayList<String> depts = new ArrayList<String>();
		
		while(parser.next() != XmlResourceParser.END_TAG) {
			if (parser.getEventType() != XmlResourceParser.START_TAG) {
	            continue;
	        }
			
			String tagName = parser.getName();
			
			// get the building full name
			if(tagName.equals("name")) {
				parser.require(XmlResourceParser.START_TAG, null, "name");
				if (parser.next() == XmlResourceParser.TEXT) {
			        name = parser.getText();
			        parser.nextTag();
			    }
				parser.require(XmlResourceParser.END_TAG, null, "name");
			}
			
			// get the building latitude
			else if(tagName.equals("lat")) {
				parser.require(XmlResourceParser.START_TAG, null, "lat");
				if (parser.next() == XmlResourceParser.TEXT) {
					lat = Double.valueOf(parser.getText());
			        parser.nextTag();
			    }
				parser.require(XmlResourceParser.END_TAG, null, "lat");
			}
			
			// get the building longtitude
			else if(tagName.equals("lng")) {
				parser.require(XmlResourceParser.START_TAG, null, "lng");
				if (parser.next() == XmlResourceParser.TEXT) {
					lng = Double.valueOf(parser.getText());
			        parser.nextTag();
			    }
				parser.require(XmlResourceParser.END_TAG, null, "lng");
			}
			else if(tagName.equals("area")) {
				parser.require(XmlResourceParser.START_TAG, null, "area");
				if (parser.next() == XmlResourceParser.TEXT) {
					area = parser.getText();
			        parser.nextTag();
			    }
				parser.require(XmlResourceParser.END_TAG, null, "area");
			}
			
			// get the dept list if there's any
			else if(tagName.equals("depts")) {
				parser.require(XmlResourceParser.START_TAG, null, "depts");
				while(parser.next() != XmlResourceParser.END_TAG) {
					if(parser.getEventType() == XmlResourceParser.START_TAG) {
						if (parser.next() == XmlResourceParser.TEXT) {
							depts.add(parser.getText());
					        parser.nextTag();
					    }
						parser.require(XmlResourceParser.END_TAG, null, "dept");
					} // end of if
				} // end of while
			} // end of else if
		} // end of while
		
		building.setId(id);
		building.setName(name);
		building.setLatLng(new double[]{lat, lng});
		building.setArea( area.equals("east") ? BuildingArea.EAST :  BuildingArea.WEST );
		building.setDepts(depts);
		
		return building;
	}

	public static ArrayList<Building> queryBuildingsMatched(String q, ArrayList<Building> list) {
		ArrayList<Building> result = new ArrayList<Building>();
		for(Building b : list) {
			if(b.getId().toLowerCase().contains(q) || b.getName().toLowerCase().contains(q)) {
				result.add(b);
			}
			else {
				boolean addIt = false;
				for(String dept : b.getDepts()) {
					if(dept.toLowerCase().contains(q)) {
						addIt = true;
					}
				}
				if(addIt) result.add(b);
			}
		}
		return result;
	}
}
