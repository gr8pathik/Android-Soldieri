package com.pg.soldieri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pg.soldieri.library.DatabaseHandler;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Soldieri extends Activity {
	// Progress Dialog
	private ProgressDialog pDialog;
	
	// Array list for list view
	ArrayList<HashMap<String, String>> TourList;
	
	// button add new tour
	ImageButton btnAddSite;
	
	// array to trace sqlite ids
	String[] sqliteIds;
	
	public static String TAG_ID = "id";
	public static String TAG_NAME = "name";
	public static String TAG_DATE = "date";
	
	public String SORT_PASS = TAG_ID;
	public String SORT_ORDER = "DESC";
	
	// List view
	ListView lv;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_list);
        
        btnAddSite = (ImageButton) findViewById(R.id.btnAddSite);
        
        // Hashmap for ListView
 		TourList = new ArrayList<HashMap<String, String>>();

 		
 		// selecting single ListView item
         lv = (ListView) findViewById(R.id.list); 
  
         // Launching new screen on Selecting Single ListItem
        lv.setOnItemClickListener(new OnItemClickListener() {
  
             public void onItemClick(AdapterView<?> parent, View view,
                     int position, long id) {
            	 
            	// on selecting a single album
 				// TrackListActivity will be launched to show tracks inside the album
 				Intent i = new Intent(getApplicationContext(), ListToursItemsActivity.class);
 				
 				// send album id to tracklist activity to get list of songs under that album
 				String sqlite_id = ((TextView) view.findViewById(R.id.sqlite_id)).getText().toString();
 				i.putExtra("tour_id", sqlite_id);				
 				
 				startActivity(i);
             }
         });
        
        /**
         * Add new website button click event listener
         * */
		btnAddSite.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), AddNewTourActivity.class);
				// starting new activity and expecting some response back
				// depending on the result will decide whether new website is
				// added to SQLite database or not
                startActivityForResult(i, 100);
			}
		});
    }
    
    /* Initiating Menu XML file (menu.xml) */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        
        switch (item.getItemId())
        {
        case R.id.menu_sort:
        	// Single menu item is selected do something
        	// Ex: launching new activity/screen or show alert message
        	
        	final CharSequence[] items = { "Date - Ascending", "Date - Descending", "Title - aToz", "Title - zToa", "Newest First"};

        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setTitle("Sort By");
        	builder.setItems(items, new DialogInterface.OnClickListener() {
        	    public void onClick(DialogInterface dialog, int item) {
        	         // Do something with the selection
        	    	System.out.println(item);
        	    	if(item == 0){
        	    		SORT_PASS = "date";
        	    		SORT_ORDER = "asc";
        	    	}else if(item == 1){
        	    		SORT_PASS = "date";
        	    		SORT_ORDER = "desc";
        	    	}else if(item == 2){
        	    		SORT_PASS = "name";
        	    		SORT_ORDER = "asc";
        	    	}else if(item == 3){
        	    		SORT_PASS = "name";
        	    		SORT_ORDER = "desc";
        	    	}else if(item == 4){
        	    		SORT_PASS = "id";
        	    		SORT_ORDER = "desc";
        	    	}
        	    	onResume();
        	    	/*item = item.split(' - ');
        	    	if(item[0] == "Date")
        	    	{
        	    		
        	    	}*/
        	    }
        	});
        	AlertDialog alert = builder.create();
        	alert.show();
        	
            Toast.makeText(Soldieri.this, "Sort is Selected", Toast.LENGTH_SHORT).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() 
    {
       super.onResume();  
       /**
		 * Calling a background thread which will load 
		 * tours stored in SQLite database
		 * */
		new loadStoreTours().execute(SORT_PASS, SORT_ORDER);
     }
    
    /**
	 * Background Async Task to get RSS data from URL
	 * */
	class loadStoreTours extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Soldieri.this);
			pDialog.setMessage("Loading Tours ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting all stored website from SQLite
		 * */
		@Override
		protected String doInBackground(String... args) {
			// updating UI from Background Thread
			final String sorting = args[0]; 
			final String sortingOrder = args[1];
			Log.d("argument", sorting);
			runOnUiThread(new Runnable() {
				public void run() {
					TourList.clear();
					DatabaseHandler rssDb = new DatabaseHandler(getApplicationContext());
					
					// listing all websites from SQLite
					List<Tours> siteList = rssDb.getAllSites(sorting, sortingOrder);
					
					sqliteIds = new String[siteList.size()];
					
					// loop through each website
					if(siteList.size() > 0){
						for (int i = 0; i < siteList.size(); i++) {
							
							Tours s = siteList.get(i);
							
							// creating new HashMap
							HashMap<String, String> map = new HashMap<String, String>();
	
							// adding each child node to HashMap key => value
							map.put(TAG_ID, s.getId().toString());
							map.put(TAG_NAME, s.getName());
							map.put(TAG_DATE, s.getDate());
	
							// adding HashList to ArrayList
							TourList.add(map);
							
							// add sqlite id to array
							// used when deleting a website from sqlite
							sqliteIds[i] = s.getId().toString();
						}
					}
					/**
					 * Updating list view with websites
					 * */
					ListAdapter adapter = new SimpleAdapter(Soldieri.this,
							TourList, R.layout.site_list_row,
							new String[] { TAG_ID, TAG_NAME, TAG_DATE },
							new int[] { R.id.sqlite_id, R.id.name, R.id.date });
					// updating listview
					lv.setAdapter(adapter);
					registerForContextMenu(lv);
				}
			});
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
		}

	}
}
