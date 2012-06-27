package org.cozyAndroid;

import org.cozyAndroid.providers.NoteSQL.Notes;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Edition extends Activity {
	private EditText name;
	private EditText body;
	private String id;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edition);
		id = getIntent().getExtras().getString("id");
		String oldName = getIntent().getExtras().getString("titre");
		String oldBody= getIntent().getExtras().getString("body");
		name = (EditText) findViewById(R.id.nameEdition);
		body = (EditText) findViewById(R.id.bodyEdition);
		name.setText(oldName);
		body.setText(oldBody);
		Button editer = (Button) findViewById(R.id.buttonEditer);
		editer.setOnClickListener(OKClicked);
		Button cancel = (Button) findViewById(R.id.DeleteButton);
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getContentResolver().delete(Notes.CONTENT_URI, "_id = " + id, null);
				finish();
			}
		});
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
	
	
	private OnClickListener OKClicked = new OnClickListener() {
		public void onClick(View v) {
			ContentValues values = new ContentValues();
			values.put(Notes.TITLE, name.getText()+ "");
			values.put(Notes.BODY, body.getText() + "");
			getContentResolver().update(Notes.CONTENT_URI, values, "_id = " + id, null);
			finish();
		}
	};
	
	
}
