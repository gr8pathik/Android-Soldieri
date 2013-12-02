package com.pg.soldieri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pg.soldieri.library.DatabaseHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class ListToursItemsActivity extends Activity {
	
	int tour_id;

	// Album id
	String tour_name, tour_date, tour_people, tour_desc;
	
	DatabaseHandler rssDb;
	
	// Array list for list view
	//ArrayList<HashMap<String, String>> PeopleList;	
	
	List<Map<String, String>> PeopleList;
	List<List<Map<String, String>>> PeopleListChild;
	
	// array to trace sqlite ids
	String[] sqlitePeopleIds;
	
	// button add new tour
	ImageButton btnAddSitePeople;
		
	// Progress Dialog
	private ProgressDialog pDialog;
		
	public static String TAG_ID = "id";
	public static String TAG_NAME = "name";
	public static String TAG_GENDER = "gender";
	
	final private static int DIALOG_ADD_PEOPLE = 1;
	
	public String SORT_PASS = TAG_ID;
	public String SORT_ORDER = "DESC";
	
	// As class variables - define your buttons
	private RadioButton radio_male = null;
	private RadioButton radio_female = null;
	
	// List view
	ListView lv;
	
	// List view
	ExpandableListView elv;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tours_items);
        
        // Hashmap for ListView
        //PeopleList = new ArrayList<HashMap<String, String>>();
        
        PeopleList = new ArrayList<Map<String, String>>();
        PeopleListChild = new ArrayList<List<Map<String, String>>>();
        
        // selecting single ListView item
        //lv = (ListView) findViewById(R.id.people_list);
        
        // selecting single ListView item
        elv = (ExpandableListView) findViewById(R.id.people_list);
      		
        // Building Parameters
     	rssDb = new DatabaseHandler(getApplicationContext());
     			
        // Get tour id
        Intent i = getIntent();
        tour_id = Integer.parseInt(i.getStringExtra("tour_id"));
        
        btnAddSitePeople = (ImageButton) findViewById(R.id.btnAddSitePeople);
        
        /**
         * Add new people button click event listener
         * */
        btnAddSitePeople.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				showDialog(DIALOG_ADD_PEOPLE);
			}
		});
        
        // calling background thread
        new LoadSingleTour().execute();
    }
    
    @Override
    protected void onDestroy() {
     super.onDestroy();
     if (rssDb  != null) {
    	 rssDb.close();
     }
    }
    
    @Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog dialogDetails = null;

		switch (id) {
		case DIALOG_ADD_PEOPLE:
			LayoutInflater inflater = LayoutInflater.from(this);
			View dialogview = inflater.inflate(R.layout.dialog_layout, null);

			AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(this);
			dialogbuilder.setTitle("Add People");
			dialogbuilder.setView(dialogview);
			dialogDetails = dialogbuilder.create();

			break;
		}

		return dialogDetails;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {

		switch (id) {
		case DIALOG_ADD_PEOPLE:
			final AlertDialog alertDialog = (AlertDialog) dialog;
			Button loginbutton = (Button) alertDialog
					.findViewById(R.id.btn_login);
			Button cancelbutton = (Button) alertDialog
					.findViewById(R.id.btn_cancel);
			final EditText peopleName = (EditText) alertDialog
					.findViewById(R.id.txt_name);			
			// In your onCreate() method get a reference to the buttons
			
			radio_male = (RadioButton) alertDialog.findViewById(R.id.radio_male);
			radio_female = (RadioButton) alertDialog.findViewById(R.id.radio_female);
			
			loginbutton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					String name = peopleName.getText().toString();
					
					// Wherever needed - check to see which button is selected ("checked")
					String gender = null;
					if(radio_male.isChecked()) {
						gender = "Male";
					} else if(radio_female.isChecked()) {
						gender = "Female";
					}
					
					
					
					DatabaseHandler rssDb = new DatabaseHandler(getApplicationContext());
					Peoples people = new Peoples(tour_id, name, gender);
					rssDb.addPeople(people);
					alertDialog.dismiss();
					new loadSingleTourPeoples().execute(SORT_PASS, SORT_ORDER);
					
					peopleName.setText(null);
					/*Toast.makeText(
							ListToursItemsActivity.this,
							name + "is Added." + gender,
							Toast.LENGTH_LONG).show();*/
				}
			});

			cancelbutton.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					alertDialog.dismiss();
					new loadSingleTourPeoples().execute(SORT_PASS, SORT_ORDER);
				}
			});
			break;
		}
	}

    @Override	
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_list_tours_items, menu);
        return true;
    }
    
	/**
	 * Background Async Task to get single song information
	 * */
	class LoadSingleTour extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ListToursItemsActivity.this);
			pDialog.setMessage("Loading tour ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting song json and parsing
		 * */
		protected String doInBackground(String... args) {
			Tours site = rssDb.getTour(tour_id);
			tour_name = site.getName();
			tour_date = site.getDate();
			tour_people = site.getTotalPeoples();
			tour_desc = site.getDescription();
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting song information
			pDialog.dismiss();
			
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					
					TextView tour_display = (TextView) findViewById(R.id.tours_id);
					TextView tour_name_display = (TextView) findViewById(R.id.tour_name);
					TextView tour_date_display = (TextView) findViewById(R.id.tour_date);
					TextView tour_people_display = (TextView) findViewById(R.id.tour_people);
					
			        tour_display.setText(Integer.toString(tour_id));
			        tour_name_display.setText(tour_name);
			        tour_date_display.setText(tour_date);
			        tour_people_display.setText(tour_people);
					
					// Change Activity Title with Song title
					setTitle(tour_name);
					
					new loadSingleTourPeoples().execute(SORT_PASS, SORT_ORDER);
				}
			});

		}

	}
	
	 /**
		 * Background Async Task to get RSS data from URL
		 * */
		class loadSingleTourPeoples extends AsyncTask<String, String, String> {

			/**
			 * Before starting background thread Show Progress Dialog
			 * */
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pDialog = new ProgressDialog(ListToursItemsActivity.this);
				pDialog.setMessage("Loading Peoples ...");
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
						PeopleList.clear();
						DatabaseHandler rssDb = new DatabaseHandler(getApplicationContext());
						
						// listing all websites from SQLite
						List<Peoples> peoplesList = rssDb.getPeoples(tour_id, sorting, sortingOrder);
						
						sqlitePeopleIds = new String[peoplesList.size()];
						
						// loop through each website
						if(peoplesList.size() > 0){
							for (int i = 0; i < peoplesList.size(); i++) {
								
								Peoples s = peoplesList.get(i);
								
								// creating new HashMap
								//HashMap<String, String> map = new HashMap<String, String>();
								
								Map<String, String> map = new HashMap<String, String>();
					            		
								// adding each child node to HashMap key => value
								map.put(TAG_ID, s.getId().toString());
								map.put(TAG_NAME, s.getName());
								map.put(TAG_GENDER, s.getGender());
		
								// adding HashList to ArrayList
								PeopleList.add(map);
								
								List<Map<String, String>> children = new ArrayList<Map<String, String>>();
					            for (int j = 0; j < 15; j++) {
					                Map<String, String> curChildMap = new HashMap<String, String>();
					                children.add(curChildMap);
					                curChildMap.put(TAG_NAME, "Child " + j);
					                curChildMap.put(TAG_GENDER, (j % 2 == 0) ? "This child is even" : "This child is odd");
					            }
					            PeopleListChild.add(children);
					            
								// add sqlite id to array
								// used when deleting a website from sqlite
								sqlitePeopleIds[i] = s.getId().toString();
							}
						}
						
						/**
						 * Updating list view with websites
						 * */
						/*ListAdapter adapter = new SimpleAdapter(ListToursItemsActivity.this,
								PeopleList, R.layout.people_list_row,
								new String[] { TAG_ID, TAG_NAME, TAG_GENDER },
								new int[] { R.id.people_id, R.id.people_name, R.id.people_gender });
						// updating listview
						lv.setAdapter(adapter);
						registerForContextMenu(lv);*/
						
						 // Set up our adapter
						ExpandableListAdapter mAdapter = new SimpleExpandableListAdapter(
								ListToursItemsActivity.this, PeopleList, R.layout.people_list_row,
				                new String[] { TAG_ID, TAG_NAME, TAG_GENDER },
				                new int[] { R.id.people_id, R.id.people_name, R.id.people_gender },
				                PeopleListChild,
				                R.layout.people_child_list_row,
				                new String[] { TAG_NAME, TAG_GENDER },
				                new int[] { android.R.id.text1, android.R.id.text2 }
				                );
						elv.setAdapter(mAdapter);
						registerForContextMenu(elv);
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
