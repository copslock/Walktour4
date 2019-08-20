package com.walktour.gui.data.model;

import java.util.ArrayList;
import java.util.List;

public class Building {

	public String node_id = "";
	public String parent_id = "";
	public String node_name = "";
	public String node_info = "";
	public List<Building> floors = new ArrayList<Building>();
	
}
