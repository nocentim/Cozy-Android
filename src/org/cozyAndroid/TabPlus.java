package org.cozyAndroid;

import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.ektorp.UpdateConflictException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;
import android.content.* ;
import android.webkit.* ;
import android.widget.* ;




public class TabPlus extends Activity implements View.OnClickListener{

	private EditText newName = null ;
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
	 * TODO voir avec benjamin pour remettre a zero la note
	 */
	{
		TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}

	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;

		setContentView(R.layout.plus );	    
		newName = (EditText) findViewById(R.id.nameNewNote) ;
		findViewById(R.id.clear).setOnClickListener(this)    ; 
		findViewById(R.id.save).setOnClickListener(this)  ;
		findViewById(R.id.indent).setOnClickListener(this)     ; 
		findViewById(R.id.unindent).setOnClickListener(this)   ; 
		findViewById(R.id.listBullets).setOnClickListener(this)   ; 
		findViewById(R.id.listNum).setOnClickListener(this)   ; 

		webView = (WebView) findViewById(R.id.webView) ;
		webView.getSettings().setJavaScriptEnabled(true) ;  //elle est pas inutile mais eclipse ne le voit pas
		webView.setWebChromeClient (new chromeclient()) ; 
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		webView.loadUrl("file:///android_asset/www/index.html");
	}

	public EditText getNewName() {
		return newName;
	}

	//TODO pour ouvrir une note existante il faudra charger son body grace à la fonction js setEditorContent

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

	public void onResume() {
		super.onResume();
		if (modif) {
			setNewName(TabListe.getTitleModif());
		}
	}

	//TODO verifier le bon fonctionnement de cette methode suite a toutes les modifications

	public void onClick(View v) {
		int id = v.getId() ;

		switch (id) {
		case R.id.clear : 
			webView.loadUrl("javascript:deleteContentAndroid()") ;
			break ;

		case R.id.save :
			//TODO il faut remettre le titre et le corps a zero, on peut inverser l'ordre des deux premiers case et
			//pas mettre de break entre les deux pour qu'après la sauvegarde il y ai directement la remise à zero
			String inputTitle = newName.getText().toString();
			//TODO utiliser la fonction js pour récupérer le corps du text
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
			webView.loadUrl("javascript:indentation()") ;
			break ; 
		case R.id.unindent :
			webView.loadUrl("javascript:unindentation()") ;
			break ;
		case R.id.listBullets :
			webView.loadUrl("javascript:markerListAndroid()") ;
			break ;
		case R.id.listNum :
			webView.loadUrl("javascript:titleListAndroid()") ;
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

