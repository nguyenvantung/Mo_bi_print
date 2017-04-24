package com.plustech.print.fileexplorer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.plustech.print.R;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
	
}
