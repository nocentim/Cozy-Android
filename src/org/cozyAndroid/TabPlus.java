package org.cozyAndroid;

import java.io.IOException;
import java.io.StringWriter;

import org.cozyAndroid.providers.TablesSQL.Notes;

import javax.xml.parsers.* ; 
import javax.xml.transform.* ;
import javax.xml.transform.dom.* ;
import javax.xml.transform.stream.* ;

import org.w3c.dom.*; 
import org.xml.sax.*; 

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.* ;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TabPlus extends Activity implements View.OnClickListener {

	private Document bodyDoc ;

	private EditText newText = null ;
	private EditText newName = null ;
	private String currentColor = "#000000";  // Couleur actuelle du texte

	private Button clear   = null ;
	private Button valider = null ;
	private Button bold = null;
	private Button italic = null;
	private Button underline = null ;

	/**
	 * Ecoute le bouton enter pour permettre a l'utilisateur de revenir
	 * a la ligne dans ca note
	 */
	private View.OnKeyListener EnterListener = new View.OnKeyListener() {
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			int cursorIndex = newText.getSelectionStart(); // On récupère la position du début de la sélection dans le texte
			if(event.getAction() == 0)                     // Ne réagir qu'à l'appui sur une touche (et pas le relâchement)
				if(keyCode == 66)                          // Pour la touche « entrée »
					//						((Editable) body).insert(cursorIndex, "<br />"); // On insère une balise de retour à la ligne
					afficheText() ;
			return true;
		}
	} ;

	/** 
	 * Ecoute les changements du texte
	 */
	private TextWatcher TextListener = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			newText.removeTextChangedListener(TextListener) ;
			//				((Editable) body).replace (start, start + before, s, start, start + count) ;
			newText.addTextChangedListener(TextListener) ;	
		}
		public void afterTextChanged(Editable s) {	
			afficheText() ;
		}
	} ;

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		setContentView(R.layout.plus ) ;

		newText = (EditText)findViewById(R.id.bodyNewNote)  ;
		newName = (EditText)findViewById(R.id.nameNewNote)  ;
		clear   = (Button) findViewById(R.id.buttonClear)   ;
		valider = (Button) findViewById(R.id.buttonValider) ;

		clear.setOnClickListener(this)  ;
		valider.setOnClickListener(this);

		// Pour mettre en gras
		bold = (Button) findViewById(R.id.buttonBold) ;
		bold.setOnClickListener(this) ;
		// Pour mettre en italic
		italic = (Button) findViewById(R.id.buttonItalic);
		italic.setOnClickListener(this);
		// Pour souligner
		underline = (Button) findViewById(R.id.buttonUnderline);
		underline.setOnClickListener(this);

		//		newText.setMovementMethod (new ScrollingMovementMethod()) ;
		// On ajouter un Listener sur l'appui de touches
		newText.setOnKeyListener(EnterListener);
		// On ajoute un autre Listener sur le changement dans le texte cette fois
		newText.addTextChangedListener(TextListener);

		bodyDoc = Dom.creationDOM() ;
	}

	/**
	 *  BIU pour: bold italic underligne, ecoute les boutons correspondants
	 */
	//TODO verifier le bon fonctionnement de cette methode suite a toutes les modifications
	//TODO cette methode doit modifier l'arbre, mais avant il faut savoir ou l'on est dans l'arbre
	public void onClick(View v) {
		int id = v.getId() ;
		CharSequence baliseOuvrante = "", baliseFermante = "" ;

		switch (id) {
		case R.id.buttonClear : 
			newText.setText("") ;
			break ;
		case R.id.buttonValider :
			///dataBase.addNote("Notes", "note", newName.getText() + ", body: " + newText.getText()) ;
			ContentValues values = new ContentValues();
			values.put(Notes.TITLE, newName.getText()+ "");
			values.put(Notes.BODY, newText.getText() + "");        
			Uri uri = getContentResolver().insert(Notes.CONTENT_URI, values);
			newText.setText("") ; // Pour ces deux lignes il faudra surement faire plus
			newName.setText("") ;
			break ;
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

		if (id == R.id.buttonBold || id == R.id.buttonItalic || id == R.id.buttonUnderline) {
			int selectionStart = newText.getSelectionStart();  // On récupère la sélection
			int selectionEnd   = newText.getSelectionEnd();
			if (selectionStart == -1) selectionStart = 0 ;
			if (selectionEnd == -1) selectionEnd = 0 ;
			//TODO verifier les effets de bord de la selection

			//		((Editable) body).insert(selectionStart, baliseOuvrante) ;    // On met la balise avant la sélection
			//		((Editable) body).insert(selectionEnd + 3 , baliseFermante) ; // On rajoute la balise après la sélection (et les 3 caractères de la balise <b>)
			afficheText() ;
		}
	}


	/**
	 * transform le DOM en html pour qu'il soit interprete par la classe Html puis affiche dans l'edit text(plus maintenant)
	 */
	public void afficheText() {
		//TODO voir la methode notify pour savoir s'il est possible de se passer de cette methode (pour factoriser le code)
		newText.removeTextChangedListener(TextListener) ;
		newText.setText(Dom.afficheDom (bodyDoc)) ;
		newText.addTextChangedListener(TextListener) ;	
	}

}

// Pour mettre en couleur un texte en html:
//		newText.setText(Html.fromHtml("<font color=\"" + currentColor + "\">" + newText.getText().toString() + "</font>", null , null));
