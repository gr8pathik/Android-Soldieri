package com.pg.soldieri;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import java.text.DateFormat;
import java.util.Calendar;

import com.pg.soldieri.library.DatabaseHandler;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

public class AddNewTourActivity extends Activity {

	Button btnSubmit;
	Button btnCancel;
	EditText txtTourName;
	EditText txtTourDate;
	EditText txtTourNoPeople;
	EditText txtDesc;
	TextView lblMessage;
	Calendar myCalendar = Calendar.getInstance();
	
	DateFormat fmtDateAndTime = DateFormat.getDateInstance();
	
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth) {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			updateLabel();
		}
	};
	
	private void updateLabel() {
		txtTourDate.setText(fmtDateAndTime.format(myCalendar.getTime()));
	}
		
	// Progress Dialog
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_tour);

		// buttons
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		txtTourName = (EditText) findViewById(R.id.txtTourName);
		txtTourDate = (EditText) findViewById(R.id.txtTourDate);
		txtTourNoPeople = (EditText) findViewById(R.id.txtTourNoPeople);
		txtDesc = (EditText) findViewById(R.id.txtDesc);
		
		Button btnDate = (Button) findViewById(R.id.btnDate);
		btnDate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(AddNewTourActivity.this, d, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
		lblMessage = (TextView) findViewById(R.id.lblMessage);

		// Submit button click event
		btnSubmit.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				String url = txtTourName.getText().toString();
				String date = txtTourDate.getText().toString();
				String people = txtTourNoPeople.getText().toString();
				String desc = txtDesc.getText().toString();
				
				
				// Validation url
				Log.d("URL Length", "" + url.length());
				// check if user entered any data in EditText
				if (url.length() > 0) {
					lblMessage.setText("");
					// valid url
					new processTour().execute(url,date,people,desc);
				} else {
					// Please enter url
					lblMessage.setText("Please enter tour name url");
				}

			}
		});

		// Cancel button click event
		btnCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		updateLabel();
	}

	/**
	 * Background Async Task to get RSS data from URL
	 * */
	class processTour extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AddNewTourActivity.this);
			pDialog.setMessage("Adding tour ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Inbox JSON
		 * */
		@Override
		protected String doInBackground(String... args) {
			String name = args[0];
			String date = args[1];
			String peoples = args[2];
			String desc = args[3];
			DatabaseHandler rssDb = new DatabaseHandler(getApplicationContext());
			Tours site = new Tours(name, date, peoples, desc);
			rssDb.addSite(site);
			Intent i = getIntent();
			// send result code 100 to notify about product update
			setResult(100, i);
			finish();
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String args) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
						
				}
			});

		}

	}
}
