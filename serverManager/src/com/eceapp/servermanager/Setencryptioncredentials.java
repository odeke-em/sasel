package com.eceapp.servermanager;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import com.eceapp.servermanager.R;

public class Setencryptioncredentials extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setencryptioncredentials);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setencryptioncredentials, menu);
		return true;
	}

}
