package com.eceapp.sasel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Server implements Serializable{
   private static final long serialVersionUID = 0xfffeL;
   /*
    Single entry immutable: Hostname, IP(s), Location, Function, OS(es)
    *Single entry co-related sets: Role, Username, Password, Date_of_Change
   */
    public static final int SUCCESS = 0xff;
    public static final int ERROR_CODE = ~SUCCESS;
    public static final int MIN_PORT=0;
    public static final int MAX_PORT=(1 << 16); 
    
    protected final static JSONParser parser = new JSONParser();
    protected ArrayList<User> users = null; 
    
    private String _hostname; 
    private String _location;
    public final static String ROLE_KEY = "server_role";

    public static final String IPs_KEY = "ips";
    public static final String USERS_KEY = "users";
    public static final String OS_LIST_KEY = "oslist";
    public static final String ALIASES_KEY = "aliases";
    public static final String LOCATION_KEY = "location";
    public static final String SERVERNAME_KEY = "servername";
    public static final String SERVER_ROLES_KEY = "serverroles";

    public final HashMap<String,String> nonIterValueKeys = new HashMap<String,String>();
    	
    public final 
      HashMap<String, HashSet<String>> arrayValueKeys = new HashMap<String, HashSet<String>>();
                                          
    public static final char comma = ',';
    public static final char apostrophe='"'; 

    private HashSet<String> _ipSet = null;
    private HashSet<String> _osList = null;
    private HashSet<String> _roleList = null;
	protected HashSet<String> _aliasList = null;

    public final static String 
       t255regex = "(1?[0-9]?[0-9]|2([0-5]{2}|[0-4][0-9]))",//0-255 range regex
       IPV4_REGEX = "^("+t255regex+"\\.){3}"+ t255regex+"$";

    public Server(
    		String hostname, String location, ArrayList<String> ipSet, ArrayList<String> osList, 
    		ArrayList<String> aliasList, ArrayList<String>roleList )throws Exception{ 

      if (hostname == null)
        throw new Exception("Null hostname passed in.");

      if (location == null)
        throw new Exception("Null location passed in.");

      if (ipSet == null)
        throw new Exception("Null ipSet passed in.");

      if (osList == null)
        throw new Exception("Null osList passed in.");

      if (aliasList == null)
        throw new Exception("Null aliaslist passed in.");
      if (roleList == null)
        throw new Exception("Null roleList passed in.");
      try{
        validate_ipSet(ipSet, IPV4_REGEX);
      } catch (Exception e){
        e.printStackTrace();
      }
      
      this._ipSet = new HashSet<String>();
      this._osList = new HashSet<String>(); 
      this._roleList = new HashSet<String>();
      this._aliasList = new HashSet<String>();
      
      this._hostname = hostname;
      this._location = location;
      
      this._ipSet.addAll(ipSet);
      this._osList.addAll(osList);
      this._aliasList.addAll(aliasList);
      this._roleList.addAll(roleList);
      
      this.users = new ArrayList<User>();

      this.nonIterValueKeys.put(SERVERNAME_KEY, _hostname);
      this.nonIterValueKeys.put(LOCATION_KEY, _location);
      this.arrayValueKeys.put(IPs_KEY, _ipSet);
      this.arrayValueKeys.put(OS_LIST_KEY, _osList);
      this.arrayValueKeys.put(ALIASES_KEY, _aliasList);
      this.arrayValueKeys.put(ROLE_KEY, _roleList);
    }

    public Server(){
        this._ipSet = new HashSet<String>(); 
        this._osList = new HashSet<String>(); 
        this._roleList = new HashSet<String>();
        this._aliasList = new HashSet<String>();

        this._hostname = new String();
        this._location = new String();
        
        this.users = new ArrayList<User>();
    }
    
    @SuppressWarnings("unchecked")
	public static Server serverFromJSON(String jsonString){
    	Server result = null;
    	try{
    		JSONObject serverData = (JSONObject)parser.parse(jsonString);
    		String hostname = (String)serverData.get(SERVERNAME_KEY);
    		String location = (String)serverData.get(LOCATION_KEY);
    		JSONArray ips = (JSONArray)serverData.get(IPs_KEY);
    		JSONArray oses = (JSONArray)serverData.get(OS_LIST_KEY);
    		JSONArray aliases = (JSONArray)serverData.get(ALIASES_KEY);
    		JSONArray roles  = (JSONArray)serverData.get(ROLE_KEY);
    		
    		result = new Server(hostname,location,(ArrayList<String>)ips,
    				(ArrayList<String>)oses,(ArrayList<String>)aliases,(ArrayList<String>)roles);
    		
    		ArrayList<String> userJSONArray = (ArrayList<String>)serverData.get(USERS_KEY);
    		if (userJSONArray == null){ //At least the array should be empty not null
    			throw new Exception("Possibly corrupted data. Expected the key "+USERS_KEY);
    		}
    		for (String userJSON : userJSONArray){
    			User u = User.userFromJSON((String)userJSON);
    			if (u != null){
    				result.addUser(u);
    			}
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return result;
    }
    public boolean removeAllUsers(boolean SUPER_USER_ACCESS){
      boolean allCleared = false;
      try{
	      if (SUPER_USER_ACCESS == true){ 
	        users.clear(); 
	        allCleared = true;
        }
      } catch(Exception e){
        e.printStackTrace();
      }
      return allCleared;
    } 

    public boolean isValidIP(String ip){
      Pattern ipPat = Pattern.compile(IPV4_REGEX);
      Matcher m     = ipPat.matcher(ip);

      return m.find();
    }

    public String getName(){
      String nameClone = _hostname;
      return nameClone;
    }

    public ArrayList<String> getAliasList(){
    	ArrayList<String> aliasCopy = new ArrayList<String>();
    	aliasCopy.addAll(this._aliasList);
    	return aliasCopy;
    }
    
    public boolean removeUser(User u){
      boolean userRemoved = false;
      int foundIndex;
      if (u != null){
        while (users.isEmpty() == false){
          foundIndex = users.indexOf(u);
          if (foundIndex == -1)
            break; 
          users.remove(foundIndex);
          userRemoved = true;
        }
      }
      return userRemoved;
    }

    public boolean addUser(User u){
      boolean userAdded = false;
      if (u != null){
        try{
          users.add(0,u);
	  userAdded = true;
        } catch(Exception e){
	  e.printStackTrace();
        }
      }
      return userAdded;
    }
    
    public JSONObject getJSON(){
      HashMap<String,Object>jObj = new HashMap<String,Object>();
      jObj.put(SERVERNAME_KEY, this._hostname);
      jObj.put(OS_LIST_KEY, getOSList());
      
      ArrayList<String> usersArray = new ArrayList<String>();
      for (User u : users){
	      usersArray.add(u.getJSON().toJSONString());
      }

      jObj.put(USERS_KEY, usersArray);
      jObj.put(IPs_KEY, getIPList());
      jObj.put(ALIASES_KEY, getAliasList());

      jObj.put(ROLE_KEY, getRolesList());
      jObj.put(LOCATION_KEY, this._location);
      
      return new JSONObject(jObj);
    }
    
    
    public String toJSONString(){
      return (getJSON()).toString();
    }

    @Override
    public String toString(){
      //Private to make sure that all the needed variables have been instantiated
      return (getJSON()).toString();
    }

    public int numUsers(){
      return users.size();
    }

    public boolean validate_ipSet(ArrayList<String> array, String ipRegex) 
    throws Exception{
      //Return true iff every member in the array confirms to the desired pattern
      if ((ipRegex == null) | (array == null))
        return false;

      Pattern pat = Pattern.compile(ipRegex);
      Matcher match;

      for (String ip : array){ 
        match = pat.matcher(ip); 
        if (! match.find())
          throw new Exception(ip + " is an invalid ip");
      }
      return true;
    }

    public boolean changeName(String newName){
      boolean nameChanged = false;
      if ((newName != null) && (_hostname != newName)){
        _hostname = newName;
        nameChanged = true;
      }
      return nameChanged;
    }

    public boolean attrSearch(Pattern queryPattern, String attrName){
      if (LOCATION_KEY.equals(attrName))
        return (queryPattern.matcher(this._location)).find();

      else if (SERVERNAME_KEY.equals(attrName))
        return (queryPattern.matcher(this._hostname)).find();

      else if (ROLE_KEY.equals(attrName)){
        for (String role : this._roleList){
          if ((queryPattern.matcher(role)).find())
            return true;
        }
      }else if (IPs_KEY.equals(attrName)){
    	for (String ip : this._ipSet){
    	  if ((queryPattern.matcher(ip)).find())
    	    return true;
    	  }
      }
      
    return false;
    }

    public boolean searchByAttribute(String attributeName, String query){
      String stQuery = "[\\s]*("+query+")"; //Searching from the start of each word in a sentence
      Pattern queryPattern = Pattern.compile(stQuery, Pattern.CASE_INSENSITIVE);
      Pattern attrPattern = Pattern.compile(attributeName, Pattern.CASE_INSENSITIVE);
      
      if (attrPattern.matcher(SERVERNAME_KEY).find())
    	 return queryPattern.matcher(this._hostname).find();
      
      else if (attrPattern.matcher(LOCATION_KEY).find()) 
    	 return queryPattern.matcher(this._location).find();
      
      else if (attrPattern.matcher(OS_LIST_KEY).find()){
    	  for (String os : this._osList){
    		  if (queryPattern.matcher(os).find()) return true;
    	  }
      }
      
      else if (attrPattern.matcher(IPs_KEY).find()){
    	  Pattern queryForIP = Pattern.compile(query, Pattern.CASE_INSENSITIVE); 
    	  for (String ip : this._ipSet){
    		  System.out.println("CurIP "+ip);
    		  if ((queryForIP.matcher(ip)).find()) return true;
    	  }
      }
      
      return false;	
    }

    public boolean searchAllAttributes(String query){
      for (String nonIterValueKey : nonIterValueKeys.keySet()){
    	
        if (searchByAttribute(nonIterValueKey, query))
          return true;
      }
      
      for (String arrayValueKey : arrayValueKeys.keySet()){
    	System.out.println("arrItVK "+arrayValueKey + " query "+query);
        if (searchByAttribute(arrayValueKey, query))
          return true;
      }

      ArrayList<User> userMatches = searchUsers(query);
      if (userMatches != null){ //Will handle attribute reporting later
    	  return (!userMatches.isEmpty());
      }
      
      return false;
    }

    public ArrayList<User> searchUsers(String query){
      if (this.users == null) return null; 

      ArrayList<User> userMatches = new ArrayList<User>();
      for (User u : this.users){
        if (u.searchAllAttributes(query)) userMatches.add(u);
      }
      return userMatches;
    }
    
    public String getLocation(){
    	return this._location;
    }
    
    public ArrayList<String> getIPList(){
    	ArrayList<String>ipCopy = new ArrayList<String>();
    	ipCopy.addAll(this._ipSet);
    	
    	return ipCopy;
    }
    
    public ArrayList<String> getRolesList(){
    	ArrayList<String>rolesCopy = new ArrayList<String>();
    	rolesCopy.addAll(this._roleList);
    	
    	return rolesCopy;
    }
    
    public ArrayList<String> getOSList(){
    	ArrayList<String>osListCopy = new ArrayList<String>();
    	osListCopy.addAll(this._osList);
    	
    	return osListCopy;
    }
  
    public boolean addIP(String newIP){	
    	return this._ipSet.add(newIP);
    }
    
    public boolean addAlias(String newAliases){
    	return this._aliasList.add(newAliases);
    }	
    
   public boolean addOS(String newOS){
   		return this._osList.add(newOS);   	
   }
   
   public boolean addRole(String newRole){
	   	return this._roleList.add(newRole);
   }
   
   public void setLocation(String newLocation){
	   this._location = newLocation;
   }
   public void setServerName(String newServerName){
	   this._hostname = newServerName;
   }
   
   public void removeUserByIndex(int index){
	   if (index >= 0 && index < this.users.size())
		   this.users.remove(index);
   } 
   
   public void removeOsByIndex(int index){
	   if (index >= 0 && index < this._osList.size())
		   this._osList.remove(index);
   }
   
   public void removeIpByIndex(int index){
	   if (index >= 0 && index < this._ipSet.size())
		   this._ipSet.remove(index);
   }
   
   public void removeAliasByIndex(int index){
	   if (index >= 0 && index < this._aliasList.size())
		   this._aliasList.remove(index);
   }
   
   public void removeServerRoleByIndex(int index){
	   if (index >= 0 && index < this._roleList.size())
		   this._roleList.remove(index);
   }  

}
