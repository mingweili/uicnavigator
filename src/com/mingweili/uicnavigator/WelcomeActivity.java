/**
 * © Mingwei Li, 2014. All rights reserved.
 */

package com.mingweili.uicnavigator;

import com.mingweili.uicnavigator.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CheckBox;


/**
 * Activity of welcome screen.
 * welcome screen will only show on first time use.
 */
public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// check if this is first time use
		SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
		if(preference.getBoolean(getResources().getString(R.string.settings_not_show_welcome_key), false)) {
			this.startMainActivity();
			finish();
		}
		else {
			setContentView(R.layout.activity_welcome);
		}
	}
	
	/**
	 * Method when "@+id/welcome_start_button" clicked
	 * Home screen will show, also first time use will be recorded
	 */
	public void getStartedAction(View view) {
		CheckBox notShowCheckBox = (CheckBox) this.findViewById(R.id.welcome_not_show_again_checkbox);
		if(notShowCheckBox.isChecked()) {
			SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
			preference.edit()
				.putBoolean(getResources().getString(R.string.settings_not_show_welcome_key), true).commit();
		}
		this.startMainActivity();
	}
	
	/**
	 * Method for opening home screen 
	 */
	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}


}
