package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.cozyAndroid.providers.TablesSQL.Notes;
import org.ektorp.UpdateConflictException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.* ;
import android.os.* ;
import android.util.Log;
import android.view.* ;
import android.webkit.* ;
import android.widget.* ;


public class TabPlus extends Activity implements View.OnClickListener {

	private EditText newName = null ;
	
	// attributs coconut
	public static final String TAG = "CoconutActivity";
	private WebView webView;
    static Handler myHandler;
    long long_starttime = 0;
    
	/*
	 * TODO voir avec benjamin pour les fonctions javascript qui permettent de mettre en gras, en italique et 
	 * de souligner. Il y a aussi la fonction pour remettre a zero la note
	 */

	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		
		setContentView(R.layout.plus );	    
		findViewById(R.id.nameNewNote).setOnClickListener(this)    ;
		findViewById(R.id.buttonClear).setOnClickListener(this)    ; 
		findViewById(R.id.buttonValider).setOnClickListener(this)  ;
		findViewById(R.id.buttonBold).setOnClickListener(this)     ; 
		findViewById(R.id.buttonItalic).setOnClickListener(this)   ; 
		findViewById(R.id.buttonUnderline).setOnClickListener(this); 

		webView = (WebView) findViewById(R.id.webView) ;
		webView.getSettings().setJavaScriptEnabled(true) ;  //elle est pas inutile mais eclipse ne le voit pas
		webView.setWebChromeClient (new chromeclient()) ; 
		webView.loadUrl("file:///android_asset/www/index.html");
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
			//TODO il faut remettre le titre et le corps a zero
			Toast.makeText (TabPlus.this, "Note saved ", Toast.LENGTH_SHORT).show() ;
			startActivity(new Intent(TabPlus.this, CozyAndroidActivity.class)) ; // on retourne à la vue liste
			break ;

		case R.id.buttonBold :
			//Toast.makeText (TabPlus.this, "appui sur le bouton bold, pas implémenté", Toast.LENGTH_LONG).show();
			webView.loadUrl("javascript:bold()") ;
			break ; 

		case R.id.buttonItalic :
			//Toast.makeText (TabPlus.this, "appui sur le bouton Italic, pas implémenté", Toast.LENGTH_LONG).show();
			webView.loadUrl("javascript:italic()") ;
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
