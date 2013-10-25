package com.eceapp.sasel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Server extends Object implements Serializable{
   private static final long serialVersionUID = 0xfffeL;
   /*
    Single entry immutable: Hostname, IP(s), Location, Function, OS(es)
    *Single entry co-related sets: Role, Username, Password, Date_of_Change
   */
   	public static final int NOT_FOUND = -1;
    public static final int SUCCESS = 0xff;
    public static final int ERROR_CODE = ~SUCCESS;
    public static final int MIN_PORT = 0;
    public static final int MAX_PORT = (1 << 16); 
    
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
      HashMap<String, ArrayList<String>> arrayValueKeys = new HashMap<String, ArrayList<String>>();
                                          
    public static final char comma = ',';
    public static final char apostrophe='"'; 

    private ArrayList<String> _ipList = null;
    private ArrayList<String> _osList = null;
    private ArrayList<String> _roleList = null;
	protected ArrayList<String> _aliasList = null;

    public final static String 
       t255regex = "(1?[0-9]?[0-9]|2([0-5]{2}|[0-4][0-9]))",//0-255 range regex
       IPV4_REGEX = "^("+t255regex+"\\.){3}"+ t255regex+"$";

    public Server(
    		String hostname, String location, ArrayList<String> ipList, ArrayList<String> osList, 
    		ArrayList<String> aliasList, ArrayList<String>roleList )throws Exception{ 

      if (hostname == null)
        throw new Exception("Null hostname passed in.");

      if (location == null)
        throw new Exception("Null location passed in.");

      if (ipList == null)
        throw new Exception("Null ipList passed in.");

      if (osList == null)
        throw new Exception("Null osList passed in.");

      if (aliasList == null)
        throw new Exception("Null aliaslist passed in.");
      if (roleList == null)
        throw new Exception("Null roleList passed in.");
      try{
        validateIpList(ipList, IPV4_REGEX);
      } catch (Exception e){
        e.printStackTrace();
      }
      
      this._ipList = new ArrayList<String>();
      this._osList = new ArrayList<String>(); 
      this._roleList = new ArrayList<String>();
      this._aliasList = new ArrayList<String>();
      
      this._hostname = hostname;
      this._location = location;
      
      this._ipList.addAll(ipList);
      this._osList.addAll(osList);
      this._aliasList.addAll(aliasList);
      this._roleList.addAll(roleList);
      
      this.users = new ArrayList<User>();

      this.nonIterValueKeys.put(SERVERNAME_KEY, _hostname);
      this.nonIterValueKeys.put(LOCATION_KEY, _location);
      this.arrayValueKeys.put(IPs_KEY, _ipList);
      this.arrayValueKeys.put(OS_LIST_KEY, _osList);
      this.arrayValueKeys.put(ALIASES_KEY, _aliasList);
      this.arrayValueKeys.put(ROLE_KEY, _roleList);
    }

    public Server(){
        this._ipList = new ArrayList<String>(); 
        this._osList = new ArrayList<String>(); 
        this._roleList = new ArrayList<String>();
        this._aliasList = new ArrayList<String>();

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
	        this.users.clear(); 
	        allCleared = true;
        }
      } catch(Exception e){
        e.printStackTrace();
      }
      
      return allCleared;
    } 

    public boolean isValidIP(String ip){
      Pattern ipPat = Pattern.compile(IPV4_REGEX);
      Matcher ipMatcher    = ipPat.matcher(ip);

      return ipMatcher.find();
    }

    public String getName(){
      return new String(this._hostname);
    }

    public ArrayList<String> getAliasList(){
    	ArrayList<String> aliasCopy = new ArrayList<String>();
    	aliasCopy.addAll(this._aliasList);
    	return aliasCopy;
    }
    
    public boolean removeUser(User u){
      return (users != null) && users.remove(u);
    }

    public boolean addUser(User u){
    	return (users != null) && (users.indexOf(u) == NOT_FOUND) && (users.add(u));
    }
    
    public JSONObject getJSON(){
      HashMap<String,Object>reprJSON = new HashMap<String,Object>();
      reprJSON.put(SERVERNAME_KEY, this._hostname);
      reprJSON.put(LOCATION_KEY, this._location);
      reprJSON.put(OS_LIST_KEY, getOSList());
      
      ArrayList<String> usersJSONArray = new ArrayList<String>();
      for (User u : users){
	      usersJSONArray.add(u.toString());
      }

      reprJSON.put(USERS_KEY, usersJSONArray);
      reprJSON.put(IPs_KEY, getIPList());
      reprJSON.put(ALIASES_KEY, getAliasList());

      reprJSON.put(ROLE_KEY, getRolesList());
      
      return new JSONObject(reprJSON);
    }
    
    public String toJSONString(){
      return (getJSON()).toString();
    }

    @Override
    public String toString(){
      return (getJSON()).toString();
    }

    public int numUsers(){
      return users.size();
    }

    public boolean validateIpList(ArrayList<String> array, String ipRegex) {
      //Return true iff every member in the array confirms to the desired pattern
      if ((ipRegex == null) | (array == null))
        return false;

      Pattern pat = Pattern.compile(ipRegex);
      Matcher match;

      for (String ip : array){ 
        match = pat.matcher(ip); 
        if (! match.find()) {
          return false;
        }
      }
      return true;
    }

    public boolean changeName(String newName){
      boolean nameChanged = false;
      if ((newName != null) && (_hostname != newName)){
        this._hostname = newName;
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
    	for (String ip : this._ipList){
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
    	  for (String ip : this._ipList){
    		  //System.out.println("CurIP "+ip);
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
    	//System.out.println("arrItVK "+arrayValueKey + " query "+query);
        if (searchByAttribute(arrayValueKey, query))
          return true;
      }

      ArrayList<User> userMatches = searchUsers(query);
      //System.out.println("UserMatches "+userMatches);
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
    	ipCopy.addAll(this._ipList);
    	
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
    	if (this._ipList == null) return false;
    	return (this._ipList.indexOf(newIP) == NOT_FOUND) && (this._ipList.add(newIP));
    }
    
    public boolean addAlias(String newAlias){
    	if (this._aliasList == null) return false;
    	return (this._aliasList.indexOf(newAlias) == NOT_FOUND) && (this._aliasList.add(newAlias));
    }	
    
   public boolean addOS(String newOS){
	   if (this._osList == null) return false;
   		return (this._osList.indexOf(newOS) == NOT_FOUND) && (this._osList.add(newOS));   	
   }
   
   public boolean addRole(String newRole){
	   if (this._roleList == null) return false;
	   return (this._roleList.indexOf(newRole) == NOT_FOUND) && (this._roleList.add(newRole));
   }
   
   public void setLocation(String newLocation){
	   if (newLocation != null)
		   this._location = newLocation;
   }
   
   public void setServerName(String newServerName){
	   this._hostname = newServerName;
   }
   
   public boolean removeUserByIndex(int index){
	 if (!(index >= 0 && index < this.users.size())) return false;
	 
	 this.users.remove(index);
	 return true;
   } 
   
   public void removeOsByIndex(int index){
	   if (index >= 0 && index < this._osList.size())
		   this._osList.remove(index);
   }
   
   public void removeIpByIndex(int index){
	   if (index >= 0 && index < this._ipList.size())
		   this._ipList.remove(index);
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
