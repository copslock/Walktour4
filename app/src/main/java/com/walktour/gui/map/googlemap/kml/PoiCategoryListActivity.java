package com.walktour.gui.map.googlemap.kml;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.walktour.gui.R;

public class PoiCategoryListActivity extends ListActivity {
	private PoiManager mPoiManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poicategory_list);
        registerForContextMenu(getListView());
        mPoiManager = new PoiManager(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPoiManager.FreeDatabases();
	}

	@Override
	protected void onResume() {
		FillData();
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	private void FillData() {
		Cursor c = mPoiManager.getGeoDatabase().getPoiCategoryListCursor();
        startManagingCursor(c);

        ListAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.poicategorylist_item, c, 
                        new String[] { "name", "iconid"}, 
                        new int[] { R.id.title1, R.id.pic });
        setListAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.poicategorylist_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch(item.getItemId()){
		case R.id.menu_addpoi:
			startActivity((new Intent(this, PoiCategoryActivity.class)));
			return true;
		}
		
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		int id = (int) ((AdapterView.AdapterContextMenuInfo)menuInfo).id;
		PoiCategory category = mPoiManager.getPoiCategory(id);
		
		menu.add(0, R.id.menu_editpoi, 0, getText(R.string.menu_edit));
		if(category.Hidden == true)
			menu.add(0, R.id.menu_show, 0, getText(R.string.menu_show));
		else
			menu.add(0, R.id.menu_hide, 0, getText(R.string.menu_hide));
		menu.add(0, R.id.menu_deletepoi, 0, getText(R.string.menu_delete));
		
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int id = (int) ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;
		PoiCategory category = mPoiManager.getPoiCategory(id);
		
		switch(item.getItemId()){
		case R.id.menu_editpoi:
			startActivity((new Intent(this, PoiCategoryActivity.class)).putExtra("id", id));
			break;
		case R.id.menu_deletepoi:
			mPoiManager.deletePoiCategory(id);
			FillData();
	        break;
		case R.id.menu_hide:
			category.Hidden = true;
			mPoiManager.updatePoiCategory(category);
			FillData();
	        break;
		case R.id.menu_show:
			category.Hidden = false;
			mPoiManager.updatePoiCategory(category);
			FillData();
	        break;
		}
		
		return super.onContextItemSelected(item);
	}

}
