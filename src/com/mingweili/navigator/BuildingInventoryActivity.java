package com.mingweili.navigator;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.mingweili.navigator.models.BuildingInventoryListAdapter;
import com.mingweili.navigator.models.SentFrom;

public class BuildingInventoryActivity extends FragmentActivity implements TabListener {
	
	// this class is used to handle the fixed tab
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private String fromOrTo;
		private SentFrom sentFrom;
		private EastCampusFragment eastFragment;
		private WestCampusFragment westFragment;
		
		public SectionsPagerAdapter(FragmentManager fm, String fot, SentFrom sf) {
			super(fm);
			this.fromOrTo = fot;
			this.sentFrom = sf;
			Bundle args = new Bundle();
			args.putSerializable("sentFrom", this.sentFrom);
			args.putString("fromOrTo", this.fromOrTo);
			this.eastFragment = new EastCampusFragment();
			this.westFragment = new WestCampusFragment();
			this.eastFragment.setArguments(args);
			this.westFragment.setArguments(args);
		}

		@Override
		public Fragment getItem(int position) {
			Bundle args = new Bundle();
			args.putSerializable(getResources().getString(R.string.EXTRA_MESSAGE_SENT_FROM), this.sentFrom);
			args.putString(getResources().getString(R.string.EXTRA_MESSAGE_FROM_OR_TO), this.fromOrTo);
			Fragment f;
			switch(position) {
				case 0:
					f = new EastCampusFragment();
					f.setArguments(args);
					this.eastFragment = (EastCampusFragment) f;
					return f;
				case 1:
					f = new WestCampusFragment();
					f.setArguments(args);
					this.westFragment = (WestCampusFragment) f;
					return f;
				default:
					return null;
					
			}
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.building_inventory_tab_label_east_campus);
			case 1:
				return getString(R.string.building_inventory_tab_label_west_campus);
			default:
				return null;
			}
		}
		
		public BuildingInventoryListAdapter[] getListAdapters() {
			return new BuildingInventoryListAdapter[] 
					{
						this.eastFragment.getListAdapter(), 
						this.westFragment.getListAdapter()
					};
		}
	}
	
	// Class used for listening query change
	class SearchOnQueryTextListener implements OnQueryTextListener {
		
		private BuildingInventoryListAdapter[] mListAdapters;
		
		public SearchOnQueryTextListener(BuildingInventoryListAdapter[] adapter) {
			this.mListAdapters = adapter;
		}
		
		@Override
		public boolean onQueryTextChange(String query) {
			// listen the query change, update the list on real time
			// let the adapter update the list
			for(BuildingInventoryListAdapter adapter : this.mListAdapters)
				adapter.query(query);
			return true;
		}
		@Override
		public boolean onQueryTextSubmit(String arg0) {
			// do nothing, all the UI change is done in onQueryTextChange callback
			return true;
		}
	}
	
		
	// enum of what activity started this activity
	private SentFrom mSentFrom;	
	private String mFromOrTo;
	public String getFromOrTo() {
		return mFromOrTo;
	}

	// variables that hold components about fixed tabs
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;

	// variable that holds search widget
	private SearchView mSearchView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building_inventory);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		// 1. handle the intent (either sent from Navigation activity or from main activity)
		this.handleIntent(getIntent());
		// 2. set up fixed tab
		this.setUpFixedTab();
	}
	
	private void handleIntent(Intent intent) {
	    // it was started from navigation activity, set the result based on user's selection and return result
		String type 
			= intent.getStringExtra(getResources().getString(R.string.EXTRA_MESSAGE_START_FOR_RESULT_LABEL));
	    if(type != null) {
	    	this.mSentFrom = SentFrom.NAVIGATION;
	    	this.mFromOrTo = type;
	    }
	    else {
	    	this.mSentFrom = SentFrom.MAIN;
	    }
	}
	
	private void setUpFixedTab() {
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this.mFromOrTo, this.mSentFrom);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.campus_tabs_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						// set the current tab
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	// callbacks in terms of fixed tab navigation
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}
	
	// Do nothing
	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.building_inventory, menu);
		
		// Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        this.mSearchView = (SearchView) menu
        		.findItem(R.id.building_inventory_action_search).getActionView();
        this.mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        // set up the query change listener to respond to user's query and update the list on the fly
        mSearchView.setOnQueryTextListener(new SearchOnQueryTextListener(this.mSectionsPagerAdapter.getListAdapters()));
        
        //this.mSearchView.setFocusable(true);
        this.mSearchView.setIconifiedByDefault(true);
        //this.mSearchView.requestFocusFromTouch();

        // set the search view text color
        LinearLayout ll = (LinearLayout)this.mSearchView.getChildAt(0);
        LinearLayout ll2 = (LinearLayout)ll.getChildAt(2);
        LinearLayout ll3 = (LinearLayout)ll2.getChildAt(1);
        AutoCompleteTextView autoComplete = (AutoCompleteTextView)ll3.getChildAt(0);
        autoComplete.setHintTextColor(getResources().getColor(R.color.uic_white));
        autoComplete.setTextColor(getResources().getColor(R.color.uic_white));
	    int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
	    ImageView v = (ImageView) mSearchView.findViewById(searchImgId);
	    v.setImageResource(R.drawable.ic_action_search);
        return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
		Intent intent = new Intent();
	    switch (item.getItemId()) {
		    case android.R.id.home:
		        NavUtils.navigateUpFromSameTask(this);
		        return true;
	        case R.id.campus_map_menu_action_settings:
	            intent.setClass(this, SettingsActivity.class);
	            startActivity(intent);
	            return true;
	        case R.id.campus_map_menu_action_about:
	            intent.setClass(this, AboutActivity.class);
	            startActivity(intent);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
}
