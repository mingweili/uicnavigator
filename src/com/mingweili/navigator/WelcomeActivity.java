package com.mingweili.navigator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if(preference.getBoolean(getResources().getString(R.string.settings_not_show_welcome_key), false)) {
			this.startMainActivity();
			finish();
		}
		else {
			setContentView(R.layout.activity_welcome);
		}
	}
	
	public void getStartedAction(View view) {
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		preference.edit()
			.putBoolean(getResources().getString(R.string.settings_not_show_welcome_key), true).commit();
		
		this.startMainActivity();
	}
	
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


}
