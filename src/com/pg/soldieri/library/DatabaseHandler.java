package com.pg.soldieri.library;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pg.soldieri.Peoples;
import com.pg.soldieri.Tours;

public class DatabaseHandler extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "soldieri";

	// Tours table name
	private static final String TABLE_TOURS = "tours";
	// Tours People table name
	private static final String TABLE_TOURS_PEOPLE = "tours_people";

	// Tours Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_TITLE = "name";
	private static final String KEY_DATE = "date";
	private static final String KEY_TOTAL_PEOPLES = "total_peoples";
	private static final String KEY_DESCRIPTION = "description";
	
	// Tours People Table Columns names
	private static final String PEOPLE_ID = "id";
	private static final String PEOPLE_TOUR_ID = "tour_id";
	private static final String PEOPLE_NAME = "name";
	private static final String PEOPLE_GENDER = "gender";
	
	// Log Name
	private static final String dblog = "Database Handler";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	SQLiteDatabase db;

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TOURS_TABLE = "CREATE TABLE " + TABLE_TOURS + "(" + KEY_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_TITLE + " TEXT," + KEY_DATE
				+ " TEXT," + KEY_TOTAL_PEOPLES + " INTEGER," + KEY_DESCRIPTION
				+ " TEXT" + ")";
		db.execSQL(CREATE_TOURS_TABLE);
		
		String CREATE_PEOPLE_TABLE = "CREATE TABLE " + TABLE_TOURS_PEOPLE + "(" + PEOPLE_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + PEOPLE_TOUR_ID + " INTEGER," + PEOPLE_NAME
				+ " TEXT," + PEOPLE_GENDER + " TEXT" + ")";
		db.execSQL(CREATE_PEOPLE_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURS);
		
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOURS_PEOPLE);

		// Create tables again
		onCreate(db);
	}
	
	@Override
	public void close() {
		
	 if (db != null) {
		 db.close();
	 }
	}

	/**
	 * Adding a new tour in tours table Function will check if a tour
	 * already existed in database. If existed will update the old one else
	 * creates a new row
	 * */
	public void addSite(Tours tour) {
		db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, tour.getName()); // site title
		values.put(KEY_DATE, tour.getDate()); // site url
		values.put(KEY_TOTAL_PEOPLES, tour.getTotalPeoples()); // rss link url
		values.put(KEY_DESCRIPTION, tour.getDescription()); // site description

		// Check if row already existed in database
		if (!isTourExists(db, tour.getId())) {
			// site not existed, create a new row
			db.insert(TABLE_TOURS, null, values);
			Log.d(dblog, TABLE_TOURS + "Tabel Inserted");
			db.close();
		} else {
			// site already existed update the row
			Log.d(dblog, TABLE_TOURS + "Tabel Updated");
			//updateSite(tour);
			db.close();
		}
	}
	
	/**
	 * Adding a new tour in tours table Function will check if a tour
	 * already existed in database. If existed will update the old one else
	 * creates a new row
	 * */
	public void addPeople(Peoples people) {
		db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(PEOPLE_TOUR_ID, people.getTourId()); // rss link url
		values.put(PEOPLE_NAME, people.getName()); // site title
		values.put(PEOPLE_GENDER, people.getGender()); // site url

		// Check if row already existed in database
		if (!isPeopleExists(db, people.getId())) {
			// site not existed, create a new row
			db.insert(TABLE_TOURS_PEOPLE, null, values);
			Log.d(dblog, TABLE_TOURS_PEOPLE + " Tabel Inserted");
			db.close();
		} else {
			// site already existed update the row
			Log.d(dblog, TABLE_TOURS_PEOPLE + " Tabel Updated");
			//updateSite(tour);
			db.close();
		}
	}

	/**
	 * Reading all rows from database
	 * */
	public List<Tours> getAllSites(String val, String order) {
		List<Tours> siteList = new ArrayList<Tours>();
		// Select All Query
		if(val.isEmpty()) val = KEY_DATE;
		if(order.isEmpty()) order = "ASC";
		
		String selectQuery = "SELECT  * FROM " + TABLE_TOURS
				+ " ORDER BY "+val+" "+order+", "+KEY_ID+" DESC";

		db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Tours site = new Tours();
				site.setId(Integer.parseInt(cursor.getString(0)));
				site.setName(cursor.getString(1));
				site.setDate(cursor.getString(2));
				site.setTotalPeoples(cursor.getString(3));
				site.setDescription(cursor.getString(4));
				// Adding contact to list
				siteList.add(site);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return siteList;
	}
	
	/**
	 * Reading all rows from database
	 * */
	public List<Peoples> getPeoples(Integer TourId, String val, String order) {
		List<Peoples> peoplesList = new ArrayList<Peoples>();
		// Select All Query
		if(TourId == 0) throw new Error("getPeoples() :: Tour Id not defined");
		if(val.isEmpty()) val = PEOPLE_ID;
		if(order.isEmpty()) order = "ASC";
		
		String selectQuery = "SELECT  * FROM " + TABLE_TOURS_PEOPLE
				+ " WHERE "+PEOPLE_TOUR_ID+" = "+TourId+" ORDER BY "+val+" "+order;

		db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Peoples people = new Peoples();
				people.setId(Integer.parseInt(cursor.getString(0)));
				people.setTourId(Integer.parseInt(cursor.getString(1)));
				people.setName(cursor.getString(2));
				people.setGender(cursor.getString(3));
				// Adding contact to list
				peoplesList.add(people);
			} while (cursor.moveToNext());
		}
		cursor.close();
		db.close();

		// return contact list
		return peoplesList;
	}

	/**
	 * Updating a single row row will be identified by rss link
	 * */
	/*public int updateSite(WebSite site) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_TITLE, site.getTitle());
		values.put(KEY_LINK, site.getLink());
		values.put(KEY_RSS_LINK, site.getRSSLink());
		values.put(KEY_DESCRIPTION, site.getDescription());

		// updating row return
		int update = db.update(TABLE_RSS, values, KEY_RSS_LINK + " = ?",
				new String[] { String.valueOf(site.getRSSLink()) });
		db.close();
		return update;

	}*/

	/**
	 * Reading a row (website) row is identified by row id
	 * */
	public Tours getTour(int id) {
		db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_TOURS, new String[] { KEY_ID, KEY_TITLE,
				KEY_DATE, KEY_TOTAL_PEOPLES, KEY_DESCRIPTION }, KEY_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();

		Tours site = new Tours(cursor.getString(1), cursor.getString(2),
				cursor.getString(3), cursor.getString(4));

		site.setId(Integer.parseInt(cursor.getString(0)));
		site.setName(cursor.getString(1));
		site.setDate(cursor.getString(2));
		site.setTotalPeoples(cursor.getString(3));
		site.setDescription(cursor.getString(4));
		cursor.close();
		db.close();
		
		return site;
	}

	/**
	 * Deleting single row
	 * */
	/*public void deleteSite(WebSite site) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_RSS, KEY_ID + " = ?",
				new String[] { String.valueOf(site.getId())});
		db.close();
	}*/

	/**
	 * Checking whether a site is already existed check is done by matching rss
	 * link
	 * */
	public boolean isTourExists(SQLiteDatabase db, Integer id) {

		Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_TOURS
				+ " WHERE id = '" + id + "'", new String[] {});
		boolean exists = (cursor.getCount() > 0);
		return exists;
	}
	
	/**
	 * Checking whether a people is already existed check is done by matching rss
	 * link
	 * */
	public boolean isPeopleExists(SQLiteDatabase db, Integer id) {

		Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_TOURS_PEOPLE
				+ " WHERE id = '" + id + "'", new String[] {});
		boolean exists = (cursor.getCount() > 0);
		return exists;
	}

}