package com.pg.soldieri;


/**
 * This class file used while inserting data or retrieving data from 
 * SQLite database
 * **/
public class Peoples {
	Integer _id;
	Integer _tour_id;
	String _name;
	String _gender;
	
	// constructor
	public Peoples(){
		
	}

	// constructor with parameters
	public Peoples(Integer tour_id, String name, String gender){
		this._tour_id = tour_id;
		this._name = name;
		this._gender = gender;
	}
	
	/**
	 * All set methods
	 * */
	public void setId(Integer id){
		this._id = id;
	}
	
	public void setTourId(Integer id){
		this._tour_id = id;
	}
	
	public void setName(String name){
		this._name = name;
	}
	
	public void setGender(String gender){
		this._gender = gender;
	}
	
	/**
	 * All get methods
	 * */
	public Integer getId(){
		return this._id;
	}
	
	public Integer getTourId(){
		return this._tour_id;
	}
	
	public String getName(){
		return this._name;
	}
	
	public String getGender(){
		return this._gender;
	}
}
