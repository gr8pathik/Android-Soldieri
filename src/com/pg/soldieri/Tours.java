package com.pg.soldieri;


/**
 * This class file used while inserting data or retrieving data from 
 * SQLite database
 * **/
public class Tours {
	Integer _id;
	String _name;
	String _date;
	String _total_peoples;
	String _description;
	
	// constructor
	public Tours(){
		
	}

	// constructor with parameters
	public Tours(String name, String date, String total_peoples, String description){
		this._name = name;
		this._date = date;
		this._total_peoples = total_peoples;
		this._description = description;
	}
	
	/**
	 * All set methods
	 * */
	public void setId(Integer id){
		this._id = id;
	}
	
	public void setName(String name){
		this._name = name;
	}
	
	public void setDate(String date){
		this._date = date;
	}
	
	public void setTotalPeoples(String total_peoples){
		this._total_peoples = total_peoples;
	}
	
	public void setDescription(String description){
		this._description = description;
	}
	
	/**
	 * All get methods
	 * */
	public Integer getId(){
		return this._id;
	}
	
	public String getName(){
		return this._name;
	}
	
	public String getDate(){
		return this._date;
	}
	
	public String getTotalPeoples(){
		return this._total_peoples;
	}
	
	public String getDescription(){
		return this._description;
	}
}
