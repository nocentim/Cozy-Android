package org.cozyAndroid;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CozySyncPreferencesActivity extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences); 
	}

}
