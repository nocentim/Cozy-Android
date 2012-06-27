package org.cozyAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Edition extends Activity {

	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edition);
		String oldName = getIntent().getExtras().getString("name");
		String oldBody= getIntent().getExtras().getString("body");
		EditText name = (EditText) findViewById(R.id.nameEdition);
		EditText body = (EditText) findViewById(R.id.bodyEdition);
		name.setText(oldName);
		body.setText(oldBody);
		Button annuler = (Button) findViewById(R.id.buttonAnnuler);
		annuler.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Retour à la liste
				//Intent liste = new Intent(Edition.this, CozyAndroidActivity.class);
		    	//startActivity(liste);
				finish();
			}
		});
	}
}
