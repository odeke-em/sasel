package com.eceapp.sasel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class User implements Serializable{
  private static final long serialVersionUID = 0xfffeL;
  private String _username;
  private String _role;
  private String _changeDate;
  private String _changeMakerName;
  private String DATE_FORMAT = "DDMMYYYY";

  private byte[] _password; 
  private ArrayList<String> userroles = null;
  
  protected static JSONParser parser = new JSONParser();

  public static final String ROLE_KEY="user_role";
  public static final String CHANGE_MAKER_KEY = "changeMakerName"; 
  public static final String PASSWORD_KEY     = "password"; 
  public static final String USERNAME_KEY     = "username";
  public static final String DATE_OF_CHANGE_KEY  = "dateOfPasswordChange"; 

  public static final String[] nonIterValueKeys = {
    USERNAME_KEY, DATE_OF_CHANGE_KEY,CHANGE_MAKER_KEY};

  public static final String[] arrayValueKeys = { ROLE_KEY };

  public static final int MIN_PASSWORD_LEN = 0; 
  //no restricting password length since our application is more like a notebook.

  public User(String name, String role, byte[] password, 
               String changerName,String initDate)  throws Exception{
    if (name == null)
      throw new Exception("Null String passed in for username");
    
    if (role == null)
      throw new Exception("Null String passed in for role");

    if ((password == null) || (password.length < MIN_PASSWORD_LEN))
      throw new Exception(
	    "Empty/null or short passwords not allowed. "+
            "Minimum password length is " + MIN_PASSWORD_LEN);

    if (initDate == null)
      throw new Exception("Null String passed in for date");

    if (! isvalidDate(initDate))
      throw new Exception(
	    "Invalid date, the correct date format is "+DATE_FORMAT);

    _username = name;
    _role = role;
    _password = password.clone();
    _changeMakerName = changerName;
    _changeDate = initDate;
    userroles = new ArrayList<String>();
  }
    
  @Override
  public String toString(){
    return toJSONString();
  }
  public JSONObject getJSON(){
    /*
    Returns a JSONObject of the contents of the user. JSONObject's toString()
    method returns it's contents formatted in the form of a JSON
    */
    HashMap<String, Object>jObj = new HashMap<String, Object>();
    jObj.put(USERNAME_KEY, _username);
    jObj.put(ROLE_KEY, _role);
    jObj.put(PASSWORD_KEY, new String(_password));
    jObj.put(DATE_OF_CHANGE_KEY, _changeDate.toString());
    jObj.put(CHANGE_MAKER_KEY, _changeMakerName);
    return new JSONObject(jObj);
  }

  public String toJSONString(){
    return (getJSON()).toString();
  }

  public boolean isvalidDate(String date){
    //Unimplemented
    return true;
  }

  public boolean changePassword(
		  byte[] newPassword, String changerName, String newChangeDate 
	){
    //Returns true if a new password is not null and has been changed
    //Prints out to standard error a notification if the old and 
    //new passwords are the same
    if ((newPassword == null) || (changerName == null))
      return false;

    if (( newChangeDate == null) || (isvalidDate(newChangeDate) == false))
      return false;

    if (! Arrays.equals(newPassword, _password)){
      _password = newPassword.clone();
      _changeDate = newChangeDate;
      _changeMakerName = changerName;
    }else
      System.err.println("Password unchanged as both passwords are the same");
    return true;
  }
  
	public static User userFromJSON(String jsonString){
		User u = null;
		try{
			JSONObject data = (JSONObject)parser.parse(jsonString);
			String username = (String) data.get(USERNAME_KEY);
			if (username == null){
				throw new Exception("Needs a "+USERNAME_KEY);
			};
			
			String role = (String)data.get(ROLE_KEY);
			if (role == null){
				throw new Exception("Needs a "+ROLE_KEY);
			}
			
			String password = (String)data.get(User.PASSWORD_KEY);
			if (password == null){
				throw new Exception("Needs a "+PASSWORD_KEY);
			}
			
			String initDate = (String)data.get(DATE_OF_CHANGE_KEY);
			if (initDate == null){
				throw new Exception("Needs a "+DATE_OF_CHANGE_KEY);
			}
			
			String changerName = (String)data.get(CHANGE_MAKER_KEY);
			if (changerName == null){
				throw new Exception("Needs a "+CHANGE_MAKER_KEY);
			}
			
			try{
				u = new User(username,role,password.getBytes(),changerName,initDate);
			}catch(Exception e){
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return u;
	}
  
  public String getUserName(){
    //Returns a clone of the username on every call
    return new String(this._username);
  }

  //No mutator method to change username because it is not yet clear whether a 
  //password and account are inheritable by a different username

  public byte[] getPassword(){
    //Accessor method that returns a fresh clone of the password on every call
    byte[] passwordCopy = this._password.clone();
    return passwordCopy;
  }

  public boolean attrSearch(Pattern queryPattern, String attrName){
    if (USERNAME_KEY.equals(attrName))
      return (_username != null) && (queryPattern.matcher(_username)).find();

    if (CHANGE_MAKER_KEY.equals(attrName))
      return (_changeMakerName != null) && (queryPattern.matcher(_changeMakerName)).find();
    
    if (DATE_OF_CHANGE_KEY.equals(attrName))
      return (_changeDate != null) && (queryPattern.matcher(_changeDate)).find();

    if (ROLE_KEY.equals(attrName) && userroles != null){
      for (String role : userroles){
        if ((queryPattern.matcher(role)).find())
          return true;
      }
    }
    
    for (String urole : userroles){
         boolean queryMatch = queryPattern.matcher(urole).find();
         if (queryMatch)
           return true;
    }
    
    return false;
  }
  
  public boolean searchByAttribute(String attributeName, String query){
    //Do a case-insensitive match while iterating and searching through attributes
    //Cannot search through passwords, this could pose a possible security hole
    //Pattern attrPattern = Pattern.compile(attributeName, Pattern.CASE_INSENSITIVE);
    Pattern queryPattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
    
    return attrSearch(queryPattern, attributeName);
  }

  public boolean searchAllAttributes(String query){
    for (String nonIterValueKey: nonIterValueKeys){
      if (searchByAttribute(nonIterValueKey, query))
        return true;
    }

    for (String arrayValueKey : arrayValueKeys){
      if (searchByAttribute(arrayValueKey, query))
        return true;
    }

    return false;
  }
  public String getRole(){
	return this._role;  
  }  
}