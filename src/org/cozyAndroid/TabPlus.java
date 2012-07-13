package org.cozyAndroid;

import org.cozyAndroid.providers.TablesSQL.Notes;

import org.w3c.dom.*; 

import android.app.Activity;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.* ;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TabPlus extends Activity implements View.OnClickListener {

	private EditText newName = null ;

	private Button clear   = null ;
	private Button valider = null ;
	private Button bold    = null ;
	private Button italic  = null ;
	private Button underline = null ;

	private WebView webView ;


	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		setContentView(R.layout.plus ) ;

		newName   = (EditText)findViewById(R.id.nameNewNote)    ;
		clear     = (Button)  findViewById(R.id.buttonClear)    ; 
		valider   = (Button)  findViewById(R.id.buttonValider)  ;
		bold      = (Button)  findViewById(R.id.buttonBold)     ; 
		italic    = (Button)  findViewById(R.id.buttonItalic)   ; 
		underline = (Button)  findViewById(R.id.buttonUnderline); 

		clear.setOnClickListener(this)    ;
		valider.setOnClickListener(this)  ;
		bold.setOnClickListener(this)     ;
		italic.setOnClickListener(this)   ;
		underline.setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView) ;
		webView.getSettings().setJavaScriptEnabled(true) ;
//		webView.setWebChromeClient (chromeclient) ;
		webView.loadUrl("file:///android_asset/www/testWebView.html");
	}

	/**
	 *  BIU pour: bold italic underligne, ecoute les boutons correspondants
	 */
	//TODO verifier le bon fonctionnement de cette methode suite a toutes les modifications
	public void onClick(View v) {
		int id = v.getId() ;

		switch (id) {
		case R.id.buttonClear : 
			Toast.makeText (TabPlus.this, "appui sur le bouton clear, pas implémenté", Toast.LENGTH_LONG).show();
			break ;
		case R.id.buttonValider :
//			///dataBase.addNote("Notes", "note", newName.getText() + ", body: " + newText.getText()) ;
//			ContentValues values = new ContentValues();
//			values.put(Notes.TITLE, newName.getText()+ "");
//			values.put(Notes.BODY, newText.getText() + "");        
//			Uri uri = getContentResolver().insert(Notes.CONTENT_URI, values);
//			newText.setText("") ; // Pour ces deux lignes il faudra surement faire plus
//			newName.setText("") ;
			Toast.makeText (TabPlus.this, "appui sur le bouton, pas implémenté", Toast.LENGTH_LONG).show();

			break ;
		case R.id.buttonBold :
			Toast.makeText (TabPlus.this, "appui sur le bouton bold, pas implémenté", Toast.LENGTH_LONG).show();
			break ; 
		case R.id.buttonItalic :
			Toast.makeText (TabPlus.this, "appui sur le bouton Italic, pas implémenté", Toast.LENGTH_LONG).show();
			break ;
		case R.id.buttonUnderline :
			Toast.makeText (TabPlus.this, "appui sur le bouton Underline, pas implémenté", Toast.LENGTH_LONG).show();
			break ;
		}
	}
}


///**
//* Ecoute la touche enter pour permettre a l'utilisateur de revenir
//* a la ligne dans ca note
//*/
//private View.OnKeyListener EnterListener = new View.OnKeyListener() {
//	public boolean onKey(View v, int keyCode, KeyEvent event) {
//		int cursorIndex = newText.getSelectionStart(); // On récupère la position du début de la sélection dans le texte
//		if(event.getAction() == 0)                     // Ne réagir qu'à l'appui sur une touche (et pas le relâchement)
//			if(keyCode == 66)                          // Pour la touche « entrée »
//				//						((Editable) body).insert(cursorIndex, "<br />"); // On insère une balise de retour à la ligne
//				afficheText() ;
//		return true ;
//	}
//} ;
