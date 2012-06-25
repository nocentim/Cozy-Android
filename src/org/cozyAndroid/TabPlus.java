package org.cozyAndroid;

import android.view.View;
import android.view.View.OnClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class TabPlus extends Activity implements View.OnClickListener {
	private EditText newText ;
	Button clear   = null ;
	Button valider = null ;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		setContentView(R.layout.plus );

		newText = (EditText)findViewById(R.id.bodyNewNote)  ;
		clear   = (Button) findViewById(R.id.buttonClear)   ;
		valider = (Button) findViewById(R.id.buttonValider) ;
		
		clear.setOnClickListener(this)   ;
		valider.setOnClickListener(this) ;
	}


	public void onClick(View v) {
	    switch(v.getId()) {

	    case R.id.buttonClear : 
	    	newText = (EditText)findViewById(R.id.bodyNewNote) ;
			newText.setText("") ;
	    	break ;
	    
	    case R.id.buttonValider :
	    	//enregistrer la note dans la bd locale
	    	break ;
	    }
	}


} 