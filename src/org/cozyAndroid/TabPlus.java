package org.cozyAndroid;

import org.cozyAndroid.providers.NoteSQL.Notes;

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TabPlus extends Activity implements View.OnClickListener {

	private EditText newText = null ;
	private EditText newName = null ;
	private String currentColor = "#000000";  // Couleur actuelle du texte

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

		newText.setMovementMethod (new ScrollingMovementMethod()) ;
		// On ajouter un Listener sur l'appui de touches
		newText.setOnKeyListener(new EnterListener());
		// On ajoute un autre Listener sur le changement dans le texte cette fois
		newText.addTextChangedListener(new TextListener());
	}

	public void onResume() {
		super.onResume();
		newText.setSelection(newText.getText().length(), newText.getText().length());
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

	/**
	 *  BIU pour: bold italic underligne
	 * @author bissou
	 * 
	 */
	private class BIUListener implements View.OnClickListener {

		public void onClick(View v) {
			int selectionStart = newText.getSelectionStart();  // On récupère la sélection
			int selectionEnd   = newText.getSelectionEnd();
			if (selectionStart == -1) selectionStart = 0 ;
			if (selectionEnd == -1) selectionEnd = 0 ;


			Editable editable = newText.getText() ;
			CharSequence baliseOuvrante = "", baliseFermante = "" ;

			switch (v.getId()) {

			case R.id.buttonBold :
				baliseOuvrante = "<b>" ;
				baliseFermante = "</b>" ;
				break ;

			case R.id.buttonItalic :
				baliseOuvrante = "<i>" ;
				baliseFermante = "</i>" ;
				break ;

			case R.id.buttonUnderline :
				baliseOuvrante = "<u>" ;
				baliseFermante = "</u>" ;
				break ;
			}

			editable.insert(selectionStart, baliseOuvrante) ;    // On met la balise avant la sélection
			editable.insert(selectionEnd + 3 , baliseFermante) ; // On rajoute la balise après la sélection (et les 3 caractères de la balise <b>)
			//TODO il faut revoir cette double insertion car cela modifie deux fois le texte donc appel deux fois le listener
		}
	}

	private class EnterListener implements View.OnKeyListener {

		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// On récupère la position du début de la sélection dans le texte
			Log.d("hello","je bug la");
			int cursorIndex = newText.getSelectionStart();
			// Ne réagir qu'à l'appui sur une touche (et pas le relâchement)
			if(event.getAction() == 0)
				// S'il s'agit d'un appui sur la touche « entrée »
				if(keyCode == 66)
					// On insère une balise de retour à la ligne
					newText.getText().insert(cursorIndex, "<br />");
			return true;
		}

	}

	private class TextListener implements TextWatcher {


		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			newText.removeTextChangedListener(this) ;
			newText.setSelection(newText.getText().length(), newText.getText().length());
			//			// Le Textview interprète le texte dans l'éditeur en une certaine couleur
			Editable edit = newText.getText() ;
			String sr = edit.toString() ;
			Spanned sp = Html.fromHtml(sr) ;
			//			newText.setText(Html.fromHtml("<font color=\"" + currentColor + "\">" + newText.getText().toString() + "</font>", null , null));
			newText.addTextChangedListener(this) ;
			
		}

		public void afterTextChanged(Editable s) {	
		}

	}
}
