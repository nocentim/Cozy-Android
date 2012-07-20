package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.cozyAndroid.providers.TablesSQL.Notes;
import org.ektorp.UpdateConflictException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.touchdb.listener.TDListener;


public class TabPlus extends Activity implements View.OnClickListener {

	private EditText newName = null ;

	private Button clear   = null ;
	private Button valider = null ;
	private Button bold = null;
	private Button italic = null;
	private Button underline = null;
	
	// attributs coconut
	public static final String TAG = "CoconutActivity";
    private TDListener listener;
	private WebView webView;
    private static TabPlus coconutRef;
    private ProgressDialog progressDialog;
    private Handler uiHandler;
    static Handler myHandler;
    private String couchAppUrl;
    long long_starttime = 0;

    
	/*
	 * TODO voir avec benjamin pour les fonctions javascript qui permettent de mettre en gras, en italique et 
	 * de souligner. Il y a aussi la fonction pour remettre a zero la note
	 */

	

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		
		setContentView(R.layout.plus );	    
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
		webView.getSettings().setJavaScriptEnabled(true) ;  //elle est pas inutile mais eclipse ne le voit pas
		webView.setWebChromeClient (new chromeclient()) ; 
		webView.loadUrl("file:///android_asset/www/testWebView.html");
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		}

	/*
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
			ContentValues values = new ContentValues();
			values.put(Notes.TITLE, newName.getText()+ "");
			values.put(Notes.BODY, "texte de la nouvelle note");        
			getContentResolver().insert(Notes.CONTENT_URI, values);
			//TODO il faut remettre le titre et le corps à zero
			Toast.makeText (TabPlus.this, "Note saved", Toast.LENGTH_LONG).show();
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

	public void createCozyyItem(String name) {
		final JsonNode item = CozyItemUtils.createWithText(name);
		CozySyncEktorpAsyncTask createItemTask = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				CozyAndroidActivity.returnCouchDbConnector().create(item);
			}
			@Override
			protected void onSuccess() {
				Log.d(CozyAndroidActivity.TAG, "Document created successfully");
			}
			@Override
			protected void onUpdateConflict(
					UpdateConflictException updateConflictException) {
				Log.d(CozyAndroidActivity.TAG, "Got an update conflict for: " + item.toString());
			}
		};
		createItemTask.execute();
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
