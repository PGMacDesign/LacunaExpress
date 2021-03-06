package com.JazzDevStudio.LacunaExpress;

import AccountMan.AccountInfo;
import JavaLEWrapper.Empire;
import LEWrapperResponse.Response;
import Server.AsyncServer;
import Server.ServerRequest;
import Server.serverFinishedListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class AddAccount extends Activity implements serverFinishedListener, OnClickListener, OnCheckedChangeListener, android.widget.CompoundButton.OnCheckedChangeListener {

    private AccountInfo account = new AccountInfo();
    
    //Global Variables
    Button bLogin, bAddAccount;
    RadioGroup rg;
    CheckBox cbdfAccount;
    EditText etusername, etpassword;
    
    String server;
    boolean remember_me;
    
    public void onResponseRecieved(String reply) {
        //This is the listener to the server event
        //if server request errors it returns error

        if(!reply.equals("error")) {
            Context context = getApplicationContext();
            CharSequence text = reply;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            TextView serverReply = (TextView) findViewById(R.id.textViewServerReply);
            serverReply.setText((CharSequence) serverReply, EditText.BufferType.NORMAL);

            //Deserializing response and pulling session data out
            Response r = new Gson().fromJson(reply, Response.class);

            account.sessionID = r.result.session_id;

            AccountMan.AccountMan acm = new AccountMan.AccountMan();
            acm.AddAccount(account);

            Intent intent = new Intent(this, SelectAccount.class);
            startActivity(intent);          
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        
        //Clearing up the onCreate by adding this method, initializes variables
        Initialize();
    }

    /*
     * Helps un-clutter the onCreate. All of this code could go int
     * the onCreate method, but it makes for easier viewing/ reading.
     * Note, NO new variables are defined here else they would be 
     * isolated to this class alone
     */
    //To Initialize Variables
    private void Initialize() {

    	//Server String set to nothing upon initialization
    	server ="";
    	
    	//Default the remember me box to false
    	remember_me = false;
    	
    	//Initialize Global Variables
    	rg = (RadioGroup) findViewById(R.id.add_account_server_choices);
    	etusername = (EditText) findViewById(R.id.add_account_username);
    	etpassword = (EditText) findViewById(R.id.add_account_password);
    	cbdfAccount = (CheckBox) findViewById(R.id.add_account_default);
    	bLogin = (Button) findViewById(R.id.add_account_login);
    	bAddAccount = (Button) findViewById(R.id.add_account_add_account);
    	
    	//Set the buttons to clickable
    	bLogin.setOnClickListener(this);
    	bAddAccount.setOnClickListener(this);
    	
    	//For the Radio button so it is waiting for the choice to be changes
    	rg.setOnCheckedChangeListener(this);
    	
    	//For the Checkbox
    	cbdfAccount.setOnCheckedChangeListener(this);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //
    	/*
    	 * Commenting this out for now, we will need to redo the design a bit if we want this as a menu.
    	 * I will look into a more efficient way of inflating this. Go ahead and just ignore it for now.
    	 */
    	//getMenuInflater().inflate(R.menu.menu_add_account, menu);
    	//
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	//This handles the click methods of the buttons so when they are clicked, the actions are defined here
	public void onClick(View v) {

		switch (v.getId()){
		
		//When the login button is clicked
		case R.id.add_account_login:
			
			try {
				
				account.userName = etusername.getText().toString();
				account.password = etpassword.getText().toString();
				
		        //if all required fields are filled in then the request will be sent to the server
		        if(!account.userName.isEmpty()&&!account.password.isEmpty()&&!account.server.isEmpty()){
		            Empire e = new Empire();
		            String request = e.Login(account.userName, account.password, 1);
		            ServerRequest sRequest = new ServerRequest(server, Empire.url, request);
		            AsyncServer s = new AsyncServer();
		            s.addListener(this);
		            s.execute(sRequest);
		            Log.d("Login", "Login Success");
		            //AsyncServer clears all listeners after the requests have been recieved.
		        }
				
			} catch (Exception e){
				e.printStackTrace();
				Log.d("Error", "Login Error");
			}
			break;
			
		//When the add account button is clicked
		case R.id.add_account_add_account:
			try {
			
				account.userName = etusername.getText().toString();
				account.password = etpassword.getText().toString();
				
				Log.d("Login", "Add Account Success");
			} catch (Exception e){
				e.printStackTrace();
				Log.d("Error", "Add Account Error");
			}
			break;
			
		}
		
	}

	//This handles the radio buttons
	public void onCheckedChanged(RadioGroup rg, int checkedId) {

		switch(checkedId){
		
		//US1, set the server choice to US1
		case R.id.add_account_us1:
			server = "https://us1.lacunaexpanse.com";
			account.server = "https://us1.lacunaexpanse.com";
			Log.d("Radio", "US1 Checked");
			break;
			
		//PT, set the server choice to PT			
		case R.id.add_account_pt:
			server = "https://us1.lacunaexpanse.com";
			account.server = "https://pt.lacunaexpanse.com";
			Log.d("Radio", "PT Checked");
			break;
						
		}
		
	}

	//This covers the Checkbox. Same method as radiogroup, but overloaded for checkbox
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//cbdfAccount <-- Checkbox
		if (buttonView.isChecked()) { 
			//checked
			Log.d("Checkbox", "Default Button Checked");
			remember_me = true;
		} else {
			//not checked
			Log.d("Checkbox", "Default Button Unchecked");
			remember_me = false;
		} 

	}
	
}
