package org.cozyAndroid;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.codehaus.jackson.JsonNode;
import org.ektorp.UpdateConflictException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;


public class TabPlus extends Activity implements View.OnClickListener{

	private EditText newName = null ;
	private String title;
	static boolean modif = false ;
	static boolean retour=false ;
	private WebView webView;
	private static ArrayList<String> tags = new ArrayList<String>();
	private static String formerActivity;
	
	public static final String TAG = "TabPlus";
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
		findViewById(R.id.clear).setOnClickListener(this); 
		findViewById(R.id.save).setOnClickListener(this);
		findViewById(R.id.indent).setOnClickListener(this)     ; 
		findViewById(R.id.unindent).setOnClickListener(this)   ; 
		findViewById(R.id.listBullets).setOnClickListener(this)   ; 
		findViewById(R.id.listNum).setOnClickListener(this)   ; 
		findViewById(R.id.properties).setOnClickListener(this);

		webView = (WebView) findViewById(R.id.webView) ;
		webView.getSettings().setJavaScriptEnabled(true) ;  //elle est pas inutile mais eclipse ne le voit pas
		webView.setWebChromeClient (new chromeclient()) ; 
		webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
		webView.loadUrl("file:///android_asset/www/index.html");
		}

	//TODO pour ouvrir une note existante il faudra charger son body grace à la fonction js setEditorContent

	public void onResume() {
		super.onResume();
		if (modif) {
			setNewName(CozyItemUtils.getTitleModif());
			tags = CozyItemUtils.getListTags();
		}
		if ((formerActivity=="properties") && (retour)) {
			setNewName(title);
			retour=false;
		}
		
	}

	public void onClick(View v) {
		int id = v.getId() ;

		switch (id) {
		case R.id.clear : 
			webView.loadUrl("javascript:deleteContentAndroid()") ;
			break ;
			
		case R.id.properties :
			Intent properties = new Intent(TabPlus.this, Properties.class);
			
			if (modif) {
				properties.putExtra("id", CozyItemUtils.getId());
				properties.putExtra("rev", CozyItemUtils.getRev());
				properties.putExtra("title", newName.getText().toString());
				properties.putExtra("body", "prout prout tagada");
			}
			title = newName.getText().toString();
			TabPlus.this.startActivity(properties);
			break;
			
		case R.id.save :
			//TODO il faut remettre le titre et le corps a zero, on peut inverser l'ordre des deux premiers case et
			//pas mettre de break entre les deux pour qu'après la sauvegarde il y ai directement la remise à zero
			String inputTitle = newName.getText().toString();
			//TODO utiliser la fonction js pour récupérer le corps du text
			String inputBody = "prout prout tagada";
			Log.d("tag.size", " "+ tags.size());
			if (tags.size()<1) {
				if (modif) {
					createOrUpdateItem(inputTitle, inputBody, CozyItemUtils.getRev(), CozyItemUtils.getId(), null);
					modif = false;
				//if(!inputTitle.equals("")) {
				} else {
					createOrUpdateItem(inputTitle, inputBody, null, null, null);
				}
			} else {
				if (modif) {
					createOrUpdateItem(inputTitle, inputBody, CozyItemUtils.getRev(), CozyItemUtils.getId(), tags);
					modif = false;
				//if(!inputTitle.equals("")) {
				} else {
					createOrUpdateItem(inputTitle, inputBody, null, null, tags);
				}
			}
			newName.setText("");
			Toast.makeText (TabPlus.this, "Note saved", Toast.LENGTH_LONG).show();
			int size = tags.size();
			int i = size-1;
			while (i>=0) {
				tags.remove(i);
				i--;
			}
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

	public static void createOrUpdateItem(String title, String body, final String rev, String id, ArrayList<String> tags) {
		final JsonNode item = CozyItemUtils.createOrUpdateNote(title, body, rev, id,tags);
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

	public void setNewName(String name) {
		newName.setText(name);
	}

	public static void addTag(String s) {
		tags.add(s);
	}
	
	public static void formerActivity(String a) {
		formerActivity = a;
	}
	
	public static String formerActivity() {
		return formerActivity;
	}
	
	public EditText getNewName() {
		return newName;
	}


}

