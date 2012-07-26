package org.cozyAndroid;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.codehaus.jackson.JsonNode;
import org.ektorp.UpdateConflictException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.touchdb.TDDatabase;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.TDViewMapBlock;
import com.couchbase.touchdb.TDViewMapEmitBlock;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;
import android.content.* ;
import android.os.* ;
import android.util.Log;
import android.view.* ;
import android.webkit.* ;
import android.widget.* ;




public class TabPlus extends Activity implements View.OnClickListener{

	private EditText newName = null ;
	private Button clear   = null ;
	private Button valider = null ;
	private Button bold = null;
	private Button italic = null;
	private Button underline = null;
	private static boolean modif = false;
	private WebView webView;
	
	// attributs coconut
	public static final String TAG = "TabPlus";
    static Handler myHandler;
    // setup clock
    Calendar cal = null;
    Date starttime = null;
    long long_starttime = 0;
    
	/*
	 * TODO voir avec benjamin pour les fonctions javascript qui permettent de mettre en gras, en italique et 
	 * de souligner. Il y a aussi la fonction pour remettre a zero la note
	 */

	{
	    TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}
	
	
	public EditText getNewName() {
		return newName;
	}
	
	public void setNewName(String name) {
		newName.setText(name);
	}
	
	public static void setModif() {
		if (modif) {
			modif = false;
		} else {
			modif = true;
		}
	}

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;

		setContentView(R.layout.plus );	 
		findViewById(R.id.nameNewNote).setOnClickListener(this)    ;
		findViewById(R.id.buttonClear).setOnClickListener(this)    ; 
		findViewById(R.id.buttonValider).setOnClickListener(this)  ;
		findViewById(R.id.indent).setOnClickListener(this)     ; 
		findViewById(R.id.unindent).setOnClickListener(this)   ; 
		findViewById(R.id.buttonUnderline).setOnClickListener(this); 

		webView = (WebView) findViewById(R.id.webView) ;
		webView.getSettings().setJavaScriptEnabled(true) ;  //elle est pas inutile mais eclipse ne le voit pas
		webView.setWebChromeClient (new chromeclient()) ; 
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		webView.loadUrl("file:///android_asset/www/index.html");
		
		/*newName   = (EditText)findViewById(R.id.nameNewNote)    ;
		clear     = (Button)  findViewById(R.id.buttonClear)    ; 
		valider   = (Button)  findViewById(R.id.buttonValider)  ;
		//bold      = (Button)  findViewById(R.id.buttonBold)     ; 
		//italic    = (Button)  findViewById(R.id.buttonItalic)   ; 
		underline = (Button)  findViewById(R.id.buttonUnderline); 

		clear.setOnClickListener(this)    ;
		valider.setOnClickListener(this)  ;
		bold.setOnClickListener(this)     ;
		italic.setOnClickListener(this)   ;
		underline.setOnClickListener(this);*/
	}
	
	
	public void onResume() {
		super.onResume();
		if (modif) {
			setNewName(TabListe.getTitleModif());
		}
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
			//TODO il faut remettre le titre et le corps à zero
			String inputTitle = newName.getText().toString();
			String inputBody = "prout prout tagada";
			if (modif) {
				createOrUpdateItem(inputTitle, inputBody, TabListe.getRev(), TabListe.getId());
				setModif();
			//if(!inputTitle.equals("")) {
			} else {
				createOrUpdateItem(inputTitle, inputBody, null, null);
			}
			newName.setText("");
			Toast.makeText (TabPlus.this, "Note saved", Toast.LENGTH_LONG).show();
			CozyAndroidActivity.gettabHost().setCurrentTab(0);
			break ;

		case R.id.indent :
			//Toast.makeText (TabPlus.this, "appui sur le bouton bold, pas implémenté", Toast.LENGTH_LONG).show();
			webView.loadUrl("javascript:indentation()") ;
			break ; 

		case R.id.unindent :
			//Toast.makeText (TabPlus.this, "appui sur le bouton Italic, pas implémenté", Toast.LENGTH_LONG).show();
			webView.loadUrl("javascript:unindentation()") ;
			break ;

		case R.id.buttonUnderline :
			//Toast.makeText (TabPlus.this, "appui sur le bouton Underline, pas implémenté", Toast.LENGTH_LONG).show();
			webView.loadUrl("javascript:underline()") ;
			break ;
		}
	} 

	/**
	 * utilisee pour ecouter les alertes js
	 * @author bissou
	 */
	private class chromeclient extends WebChromeClient {
		/**
		 * permet de reagir lors de l'envoi d'une alerte par les fonctions JS
		 */
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			// cette methode vient du livre android  mais elle n'avait pas exactement cette forme,
			// il peut etre interessant de retourner la voir
			Toast.makeText (TabPlus.this, "Interception d'une alertejs:\n" + message, Toast.LENGTH_LONG).show();
			result.confirm();
			return true;
		}
	} ;

	/**
	 * contient les methodes qui peuvent etre appelée par le javascript
	 * @author bissou
	 *
	 */
	public class JavaScriptInterface {
		Context mContext;

		/** Instantiate the interface and set the context */
		JavaScriptInterface(Context c) {
			mContext = c;
		}

		/** Show a toast from the web page */
		public void showToast(String toast) {
			Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
		}
	}

	
	 public void createOrUpdateItem(String title, String body, final String rev, String id) {
	        final JsonNode item = CozyItemUtils.createOrUpdate(title, body, rev, id);
	        CozySyncEktorpAsyncTask createItemTask = new CozySyncEktorpAsyncTask() {

				@Override
				protected void doInBackground() {
					if (rev == null) {
						Replication.couchDbConnector.create(item);
					} else {
						Replication.couchDbConnector.update(item);
					}
					
				}

				@Override
				protected void onSuccess() {
					Log.d(TAG, "Document created successfully");
				}

				@Override
				protected void onUpdateConflict(
						UpdateConflictException updateConflictException) {
					Log.d(TAG, "Got an update conflict for: " + item.toString());
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


