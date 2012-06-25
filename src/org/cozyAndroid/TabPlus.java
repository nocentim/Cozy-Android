package org.cozyAndroid;

import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
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
			DataBase.getInstance().addNote("Notes", "note", getString(R.id.nameNewNote) + ", body: " + getString(R.id.bodyNewNote)) ;
			newText.setText("") ;
			newName.setText("") ;
			break ;
		}
	}


} 