package org.cozyAndroid;

import org.cozyAndroid.providers.NoteSQL.Notes;

import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.content.ContentValues;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class TabPlus extends Activity implements View.OnClickListener {
	private EditText newText ;
	private EditText newName ;
	Button clear   = null ;
	Button valider = null ;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		setContentView(R.layout.plus );

		newText = (EditText)findViewById(R.id.bodyNewNote)  ;
		newName = (EditText)findViewById(R.id.nameNewNote)  ;
		clear   = (Button) findViewById(R.id.buttonClear)   ;
		valider = (Button) findViewById(R.id.buttonValider) ;

		clear.setOnClickListener(this)   ;
		valider.setOnClickListener(this) ;
	}


	public void onClick(View v) {
		switch(v.getId()) {

			case R.id.buttonClear : 
				newText.setText("") ;
				break ;
	
			case R.id.buttonValider :
				///dataBase.addNote("Notes", "note", newName.getText() + ", body: " + newText.getText()) ;
				ContentValues values = new ContentValues();
				values.put(Notes.TITLE, newName.getText()+ "");
				values.put(Notes.BODY, newText.getText() + "");        
				Uri uri = getContentResolver().insert(Notes.CONTENT_URI, values);
				newText.setText("") ;
				newName.setText("") ;
				break ;
		}
	}


} 