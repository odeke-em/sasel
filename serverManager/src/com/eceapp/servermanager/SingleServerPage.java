package com.eceapp.servermanager;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.eceapp.servermanager.R;

public class SingleServerPage extends Activity {
	public static final String SINGLE_SERVER_DATA_BACK = "singleServerDateBack";
	private Server theServer; 
	private ArrayList<String> serverNames;
	private String originalName;
	private ArrayList<String> displayedIpList;
	private ArrayList<String> displayedOsList;
	private ArrayList<String> displayedUserList = new ArrayList<String>();
	private ArrayList<String> displayedAliaseList;
	private ArrayList<String> displayedServerRoleList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_single_server_page);
		
		try {
			populatePage();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.single_server_page, menu);
		return true;
	}

	
	public void populatePage() throws UnsupportedEncodingException{
		//get the server data.
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		theServer = (Server)extras.get(MainMenu.SINGLE_SERVER_DATA);    
		serverNames = (ArrayList<String>) extras.get(MainMenu.SERVER_NAME_LIST);    
		originalName = theServer.getName();
				
		//Set server name
	    String serverName = theServer.getName();
	    EditText serverNameEditText = (EditText) findViewById(R.id.serverNameEditText);
	    serverNameEditText.setText(serverName);
	    
	    //Set server location
	    String location = theServer.getLocation();
	    EditText serverLocationEditText = (EditText) findViewById(R.id.serverLocationEditText);
	    serverLocationEditText.setText(location);
	    
	    //Set IP list
	    displayedIpList = theServer.getIPList();
		ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedIpList);
		ListView ipListView = (ListView) findViewById(R.id.ipListView);
		ipListView.setAdapter(targetListAdapter);
		
		ipListView.setOnItemLongClickListener(deleteIpListener);
		
		
		//Set OS list
		displayedOsList=theServer.getOSList();
	    targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedOsList);
		ListView osListView = (ListView) findViewById(R.id.osListView);
		osListView.setAdapter(targetListAdapter);    
		osListView.setOnItemLongClickListener(deleteOsListener);
		
		
		
		
		//Set User list 
		ArrayList<User> userList = theServer.users;
		for (User aUser:userList){
			String userName = aUser.getUserName();
				String passWord = new String (aUser.getPassword(),"UTF-8");
			String userRole = aUser.getRole();
			displayedUserList.add(userName+"   -   "+""+passWord+"   -   "+""+userRole);
			System.out.println(userList.size());
		}
	    targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedUserList);
		ListView userListView = (ListView) findViewById(R.id.userListView);
		userListView.setAdapter(targetListAdapter);  
		
		userListView.setOnItemLongClickListener(deleteUserListener);
		
		
		//set aliases list
		displayedAliaseList = theServer.getAliaseList();
		System.out.println("When activity started : :"+displayedAliaseList);
	    targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedAliaseList);
		ListView aliaseListView = (ListView) findViewById(R.id.aliaseListView);
		aliaseListView.setAdapter(targetListAdapter);    
		aliaseListView.setOnItemLongClickListener(deleteAliaseListener);
		
		//set server role list
		displayedServerRoleList = theServer.getRolesList();
	    targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedServerRoleList);
		ListView serverRoleListView = (ListView) findViewById(R.id.serverRoleListView);
		serverRoleListView.setAdapter(targetListAdapter);  
		
		serverRoleListView.setOnItemLongClickListener(deleteServerRoleListener);
		
		
		System.out.println("Checking local server copy0:"+theServer.getAliaseList());
		
	}
	private boolean isServerNameUnique(String serverName){
		for (int i=0;i<serverNames.size();i++){
			if (serverNames.get(i).equals(serverName) && (!serverName.equals(originalName)) ){
				
				return false;
			}
		}
		return true;
	}
	
	public void saveSingleServer(View v){
		EditText serverNameEditText = (EditText) findViewById(R.id.serverNameEditText);
		String serverName = serverNameEditText.getText().toString();
		EditText serverLocationEditText = (EditText) findViewById(R.id.serverLocationEditText);
		String serverLocation = serverLocationEditText.getText().toString();		
		
		if (isServerNameUnique(serverName)==false){ 
			serverNameEditText.setError("Server name already exists");
			
		}
		else if(serverName.length()==0) {
			serverNameEditText.setError("Can not be empty");
		}
		else{
			this.theServer.setLocation(serverLocation);
			this.theServer.setServerName(serverName);		
			Intent intent = getIntent();
			intent.putExtra(SINGLE_SERVER_DATA_BACK,this.theServer);
			System.out.println("singSRV back "+this.theServer);
			setResult(RESULT_OK,intent);
			finish();	
			
		}

	}		


	
	public void addUserHandler(View v){
		try {
			EditText userNameEditText = (EditText)findViewById(R.id.userNameEditText);
			EditText passWordEditText = (EditText)findViewById(R.id.passWordEditText);
			EditText userRoleEditText = (EditText)findViewById(R.id.userRoleEditText);
			String newUserName = userNameEditText.getText().toString();
			String newUserRole = userRoleEditText.getText().toString();
			System.out.println(newUserName+"-"+newUserRole);
			byte[] newPassWord = ((String)passWordEditText.getText().toString()).getBytes();
			
			if (newUserName.length()==0){
				userNameEditText.setError("Can not be empty");	
			}else{	//keep on going 
			
			
				User newUser;
			
				newUser = new User(newUserName, newUserRole, newPassWord, "123", "123");
			
				theServer.addUser(newUser);
				
				//======Retrieve the data from the newly created user, and display onto the UI =======
				String userName = newUser.getUserName();
				String passWord;
		
				passWord = new String (newUser.getPassword(),"UTF-8");
				
		
				String userRole = newUser.getRole();
				displayedUserList.add(userName+"   -   "+""+passWord+"   -   "+""+userRole);
				ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedUserList);
				ListView 	targetListView = (ListView) findViewById(R.id.userListView);
				targetListView.setAdapter(targetListAdapter);
				
				userNameEditText.setText("");
				passWordEditText.setText("");
				userRoleEditText.setText("");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void addOsHandler(View v){	
		EditText addOsEditText = (EditText)findViewById(R.id.addOsEditText); 
		String newOs = addOsEditText.getText().toString();
		
		if (newOs.length()==0 ){
			addOsEditText.setError("Can not be empty");	
		}
		else{
			this.theServer.addOS(newOs);	
			displayedOsList.add(0,newOs);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedOsList);
			ListView targetListView = (ListView) findViewById(R.id.osListView);
			targetListView.setAdapter(targetListAdapter);
			addOsEditText.setText("");	
		}
	}	
	
		
	public void addIpHandler(View v){	
		EditText addIpEditText = (EditText)findViewById(R.id.addIpEditText); 
		String newIp = addIpEditText.getText().toString();
		
		Pattern ipPattern = Pattern.compile(Server.IPV4_REGEX);
		boolean ipValidation = ipPattern.matcher(newIp).find();
		
		if (newIp.length()==0 ){
			addIpEditText.setError("Can not be empty");	
		}else if (ipValidation==false){
			addIpEditText.setError("Invalid Ip");	
			
		}else{
			
			this.theServer.addIP(newIp);	
			displayedIpList.add(0,newIp);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedIpList);
			ListView targetListView = (ListView) findViewById(R.id.ipListView);
			targetListView.setAdapter(targetListAdapter);
			addIpEditText.setText("");	
		}
	}	

	public void addAliaseHandler(View v){
		EditText addAliaseEditText = (EditText)findViewById(R.id.addAliaseEditText); 
		String newAliase = addAliaseEditText.getText().toString();

		if (newAliase.length()==0){
			addAliaseEditText.setError("Can not be empty");	
			
		}else{
			this.theServer.addAliase(newAliase);
			displayedAliaseList.add(0,newAliase);
			ArrayAdapter<String> aliaseListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedAliaseList);
			ListView aliaseListView = (ListView) findViewById(R.id.aliaseListView);
			aliaseListView.setAdapter(aliaseListAdapter);
			addAliaseEditText.setText("");	
		}
			
	}
	
	public void addServerRoleHandler(View v){
		EditText addServerRoleEditText = (EditText)findViewById(R.id.addServerRoleEditText); 
		String newServerRole = addServerRoleEditText.getText().toString();
		
		if (newServerRole.length()==0){
			addServerRoleEditText.setError("Can not be empty");	
		}
		else{
			this.theServer.addRole(newServerRole);	
			displayedServerRoleList.add(0,newServerRole);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedServerRoleList);
			ListView targetListView = (ListView) findViewById(R.id.serverRoleListView);
			targetListView.setAdapter(targetListAdapter);
			addServerRoleEditText.setText("");	
		}		
	}
	
	
	

	
	
	private OnItemLongClickListener deleteUserListener = new OnItemLongClickListener(){
		@Override
	    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
			
			final String nameForDeletion = displayedUserList.get(position);
			final int itemPostion = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Delete User: "+nameForDeletion+"?");  
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteItemInServer(R.id.userListView,itemPostion);
					return;
				}
			});
			AlertDialog deleteDialog = builder.create();
			deleteDialog.show();
			return true;
	    }
	};
	
	private OnItemLongClickListener deleteOsListener = new OnItemLongClickListener(){
		@Override
	    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
			
			final String nameForDeletion = displayedOsList.get(position);
			final int itemPostion = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Delete OS: "+nameForDeletion+"?");  
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteItemInServer(R.id.osListView,itemPostion);
					return;
				}
			});
			AlertDialog deleteDialog = builder.create();
			deleteDialog.show();
			return true;
	    }
	};	
	
	
	private OnItemLongClickListener deleteIpListener = new OnItemLongClickListener(){
		@Override
	    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
			
			final String nameForDeletion = displayedIpList.get(position);
			final int itemPostion = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Delete Ip: "+nameForDeletion+"?");  
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteItemInServer(R.id.ipListView,itemPostion);
					return;
				}
			});
			AlertDialog deleteDialog = builder.create();
			deleteDialog.show();
			return true;
	    }
	};
	
	
	
	
	private OnItemLongClickListener deleteAliaseListener = new OnItemLongClickListener(){
		@Override
	    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
			
			final String nameForDeletion = displayedAliaseList.get(position);
			final int itemPostion = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Delete Aliase: "+nameForDeletion+"?");  
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteItemInServer(R.id.aliaseListView,itemPostion);
					return;
				}
			});
			AlertDialog deleteDialog = builder.create();
			deleteDialog.show();
			return true;
	    }
	};
	
	private OnItemLongClickListener deleteServerRoleListener = new OnItemLongClickListener(){
		@Override
	    public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id){
			
			final String nameForDeletion = displayedServerRoleList.get(position);
			final int itemPostion = position;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setTitle("Delete Server Role: "+nameForDeletion+"?");  
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			});
			
			builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					deleteItemInServer(R.id.serverRoleListView,itemPostion);
					return;
				}
			});
			AlertDialog deleteDialog = builder.create();
			deleteDialog.show();
			return true;
	    }
	};
	
	
	
	
	
	
	
	private void deleteItemInServer(int viewListId,int itemPosition){
		
		switch(viewListId){

		case(R.id.userListView):{
			displayedUserList.remove(itemPosition);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedUserList);
			ListView targetListView = (ListView) findViewById(R.id.userListView);
			targetListView.setAdapter(targetListAdapter);
			theServer.removeUserByIndex(itemPosition);
			break;
		}
		
		case(R.id.osListView):{
			
			displayedOsList.remove(itemPosition);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedOsList);
			ListView targetListView = (ListView) findViewById(R.id.osListView);
			targetListView.setAdapter(targetListAdapter);
			theServer.removeOsByIndex(itemPosition);
			break;
		}	
		case(R.id.ipListView):{
			
			displayedIpList.remove(itemPosition);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedIpList);
			ListView targetListView = (ListView) findViewById(R.id.ipListView);
			targetListView.setAdapter(targetListAdapter);
			theServer.removeIpByIndex(itemPosition);
			break;
		}
		case(R.id.aliaseListView):{
			displayedAliaseList.remove(itemPosition);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedAliaseList);
			ListView targetListView = (ListView) findViewById(R.id.aliaseListView);
			targetListView.setAdapter(targetListAdapter);
		
			theServer.removeAliaseByIndex(itemPosition);
			break;
		}
		case(R.id.serverRoleListView):{
			
			displayedServerRoleList.remove(itemPosition);
			ArrayAdapter<String> targetListAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, displayedServerRoleList);
			ListView targetListView = (ListView) findViewById(R.id.serverRoleListView);
			targetListView.setAdapter(targetListAdapter);
			theServer.removeServerRoleByIndex(itemPosition);
			break;
		}				
		
		
			
		}
		
		
	}
	
	
}
