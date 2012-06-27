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

	private EditText newText = null ;
	private EditText newName = null ;

	private Button clear   = null ;
	private Button valider = null ;
	private Button bold = null;
	private Button italic = null;
	private Button underline = null;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		setContentView(R.layout.plus );

		newText = (EditText)findViewById(R.id.bodyNewNote)  ;
		newName = (EditText)findViewById(R.id.nameNewNote)  ;
		clear   = (Button) findViewById(R.id.buttonClear)   ;
		valider = (Button) findViewById(R.id.buttonValider) ;

		clear.setOnClickListener(this)  ;
		valider.setOnClickListener(this);

		// Pour mettre en gras
		bold = (Button) findViewById(R.id.buttonBold) ;
		bold.setOnClickListener(new BIUListener()) ;

		// Pour mettre en italic
		italic = (Button) findViewById(R.id.buttonItalic);
		italic.setOnClickListener(new BIUListener());

		// Pour souligner
		underline = (Button) findViewById(R.id.buttonUnderline);
		underline.setOnClickListener(new BIUListener());
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

	// BIU pour: bold italic underligne
	private class BIUListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			// On récupère la sélection
			int selectionStart = newText.getSelectionStart();
			int selectionEnd = newText.getSelectionEnd();

			Editable editable = newText.getText();

			switch (v.getId()) {

			case R.id.buttonBold :
				if(selectionStart == selectionEnd)
					editable.insert(selectionStart, "<b></b>");
				else {
					editable.insert(selectionStart, "<b>");      // On met la balise avant la sélection
					editable.insert(selectionEnd + 3, "</b>");   // On rajoute la balise après la sélection (et les 3 caractères de la balise <b>)
				}
				break ;

			case R.id.buttonItalic :
				if(selectionStart == selectionEnd)
					editable.insert(selectionStart, "<i></i>");
				else
				{
					editable.insert(selectionStart, "<i>");
					editable.insert(selectionEnd + 3, "</i>");
				}
				break ;

			case R.id.buttonUnderline :
				if(selectionStart == selectionEnd)
					editable.insert(selectionStart, "<u></u>");
				else
				{
					editable.insert(selectionStart, "<u>");
					editable.insert(selectionEnd + 3, "</u>");
				}
				break ;
			}
		}
	}
}
