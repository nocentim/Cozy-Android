package org.cozyAndroid;

<<<<<<< HEAD


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.coconut.CoconutUtils;
=======
>>>>>>> 2ff8b6450838afd1841b67c39247650cfd8f8950
import org.codehaus.jackson.JsonNode;
import org.cozyAndroid.providers.TablesSQL.Notes;
import org.ektorp.UpdateConflictException;
import org.coconut.UnZip;

<<<<<<< HEAD
import android.annotation.SuppressLint;
=======
import org.w3c.dom.*; 

>>>>>>> 2ff8b6450838afd1841b67c39247650cfd8f8950
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
<<<<<<< HEAD
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.javascript.TDJavaScriptViewCompiler;
import com.couchbase.touchdb.listener.TDListener;
=======
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
>>>>>>> 2ff8b6450838afd1841b67c39247650cfd8f8950

public class TabPlus extends Activity implements View.OnClickListener {

	private EditText newName = null ;

	private Button clear   = null ;
	private Button valider = null ;
<<<<<<< HEAD
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

	
	@SuppressLint({ "ParserError", "ParserError" })
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState) ;
		/*String filesDir = getFilesDir().getAbsolutePath();  //ok
		
	    Properties properties = new Properties();

    	try {
    		InputStream rawResource = getResources().openRawResource(R.raw.cozyandroid);
    		properties.load(rawResource);
    		System.out.println("The properties are now loaded");
    		System.out.println("properties: " + properties);
    	} catch (Resources.NotFoundException e) {
    		System.err.println("Did not find raw resource: " + e);
    	} catch (IOException e) {
    		System.err.println("Failed to open microlog property file");
    	}
    	
        String ipAddress = "0.0.0.0";
        Log.d(TAG, ipAddress);
		String host = ipAddress;
		int port = 8888;
		String url = "http://" + host + ":" + Integer.toString(port) + "/";
		this.setCouchAppUrl(url + properties.getProperty("couchAppInstanceUrl"));
		
        TDServer server;
        try {
            server = new TDServer(filesDir);
            
            //String ipAddress = IPUtils.getLocalIpAddress();
            //listener = new TDListener(server, 8888, ipAddress);
            listener = new TDListener(server, 8888);
            listener.start();
    		//String host = "localhost";

    		//couchAppUrl = url + "coconut-emas/_design/coconut/index.html";
    		//couchAppUrl = url + "coconut-emas/doc1/";
    		
    		//TDDatabase db = server.getExistingDatabaseNamed("coconut-emas");
    		//if(db == null) {
             //   String couchAppDoc = createTestDatabase(server);
             //   couchAppUrl = url + couchAppDoc;
    		}
            
            TDView.setCompiler(new TDJavaScriptViewCompiler());

        }catch (IOException e) {
            Log.e(TAG, "Unable to create TDServer", e);
        }
        
        final Activity activity = this;
        webView = new WebView(TabPlus.this);
        uiHandler = new Handler();
		coconutRef = this;
        
        progressDialog = new ProgressDialog(TabPlus.this);
		//progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("TouchDB");
		progressDialog.setMessage("Loading. Please wait...");
		progressDialog.setCancelable(false);
	    progressDialog.setOwnerActivity(coconutRef);
	    progressDialog.setIndeterminate(true);
	    progressDialog.setProgress(0);
	    progressDialog.show();
	    
		//webView.setWebChromeClient(new WebChromeClient());
		webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress)
            {
            	// Activities and WebViews measure progress with different scales.
				// The progress meter will automatically disappear when we reach 100%
				//activity.setProgress(progress * 1000);
				progressDialog.show();
				activity.setProgress(progress * 1000);
				coconutRef.setProgress(progress * 1000);
				progressDialog.setProgress(progress * 1000);
				progressDialog.incrementProgressBy(progress);
				Log.d(TAG, "Progress: " + progress);

				if(progress == 100 && progressDialog.isShowing()) {
					Log.d(TAG, "Progress: DONE! " + progress);
					 // Stop clock and calculate time elapsed
			        //stopwatchFinish();
					progressDialog.dismiss();
				}
            }

			public void stopwatchFinish() {
				Calendar cal2 = new GregorianCalendar();
				Date endtime = cal2.getTime();
				long long_endtime = endtime.getTime();
				long difference = (long_endtime - long_starttime);
				float diffSecs = difference / 1000;
				Log.v(TAG,"********  Time to open app: " + difference + " or " + diffSecs + " seconds ******");
			}
        });
		webView.setWebViewClient(new CustomWebViewClient());		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		webView.getSettings().setDomStorageEnabled(true);

		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		webView.requestFocus(View.FOCUS_DOWN);
	    webView.setOnTouchListener(new View.OnTouchListener() {
	     
	        public boolean onTouch(View v, MotionEvent event) {
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                case MotionEvent.ACTION_UP:
	                    if (!v.hasFocus()) {
	                        v.requestFocus();
	                    }
	                    break;
	            }
	            return false;
	        }
	    });*/
		setContentView(R.layout.plus );
		/*setContentView(webView);
    	String appDb = properties.getProperty("app_db");
	    File destination = new File(filesDir + File.separator + appDb + ".touchdb");
	    Log.d(TAG, "Checking for touchdb at " + filesDir + File.separator + appDb + ".touchdb");
	    Log.d(TAG, "Checking for touchdb at " + filesDir + File.separator + appDb + ".touchdb");
	    if (!destination.exists()) {
	    	Log.d(TAG, "Touchdb does not exist. Unzipping files.");
	    	// must be in the assets directory
	    	//installProgress =  ProgressDialog.show(CoconutActivity.this, "", "Installing database. Please wait...", true);
	    	try {
	    		// This is the touchdb
	    		//CoconutUtils.unZipFromAssets(this.getApplicationContext(), appDb + ".touchdb.zip", filesDir);
	        	String destinationFilename = CoconutUtils.extractFromAssets(this.getApplicationContext(), appDb + ".touchdb.zip", filesDir);	
	        	File destFile = new File(destinationFilename);
	    		unzipFile(destFile);
	    		// These are the attachments
	    		//CoconutUtils.unZipFromAssets(this.getApplicationContext(), appDb + ".zip", filesDir);
	    		destinationFilename = CoconutUtils.extractFromAssets(this.getApplicationContext(), appDb + ".zip", filesDir);	
	        	destFile = new File(destinationFilename);
	    		unzipFile(destFile);
                loadWebview();
			} catch (Exception e) {
				e.printStackTrace();
				String errorMessage = "There was an error extracting the database.";
				displayLargeMessage(errorMessage, "big");
				Log.d(TAG, errorMessage);
				//installProgress.setMessage("There was an error - unable to find database zip.");
				progressDialog.setMessage(errorMessage);
				this.setCouchAppUrl("/");
			    //installProgress.dismiss();
			}
	    } else {
	    	Log.d(TAG, "Touchdb exists. Loading WebView.");
	    	loadWebview();
	    }*/
	    
	    
	    
	    
		newText = (EditText)findViewById(R.id.bodyNewNote)  ;
		newName = (EditText)findViewById(R.id.nameNewNote)  ;
		clear   = (Button) findViewById(R.id.buttonClear)   ;
		valider = (Button) findViewById(R.id.buttonValider) ;
		

		clear.setOnClickListener(this);
		valider.setOnClickListener(this);

		// Pour mettre en gras
		bold = (Button) findViewById(R.id.buttonBold) ;
		bold.setOnClickListener(BIUListener) ;
		// Pour mettre en italic
		italic = (Button) findViewById(R.id.buttonItalic);
		italic.setOnClickListener(BIUListener);
		// Pour souligner
		underline = (Button) findViewById(R.id.buttonUnderline);
		underline.setOnClickListener(BIUListener);

		//		newText.setMovementMethod (new ScrollingMovementMethod()) ;
		// On ajouter un Listener sur l'appui de touches
		newText.setOnKeyListener(EnterListener);
		// On ajoute un autre Listener sur le changement dans le texte cette fois
		newText.addTextChangedListener(TextListener);
	}

	private class CustomWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith("tel:")) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
				startActivity(intent);
			} else if (url.startsWith("http:") || url.startsWith("https:")) {
				view.loadUrl(url);
			}
			return true;
		}
	}
	
	// coconut
	public void displayLargeMessage( String message, String size ) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = null;
		if (size.equals("big")) {
			layout = inflater.inflate(R.layout.toast_layout_large,(ViewGroup) findViewById(R.id.toast_layout_large));
		} else {
			layout = inflater.inflate(R.layout.toast_layout_medium,(ViewGroup) findViewById(R.id.toast_layout_large));
		}
		
		ImageView image = (ImageView) layout.findViewById(R.id.image);
		image.setImageResource(R.drawable.android);
		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText(message);
		//uiHandler.post( new ToastMessage( this, message ) );
		//Toast toast = new Toast(getApplicationContext());
		//toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		//toast.setDuration(Toast.LENGTH_LONG);
		//toast.setView(layout);
		//uiHandler.post( new ToastMessageBig( this, message, layout ) );
		//toast.show();
	}
	
	// coconut
	public void unzipFile(File zipfile) {
		//installProgress = ProgressDialog.show(CoconutActivity.this, "Extract Zip","Extracting Files...", false, false);
		File zipFile = zipfile;
		displayLargeMessage("Extracting: " + zipfile, "medium");
		String directory = null;
		directory = zipFile.getParent();
		directory = directory + "/";
		myHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// process incoming messages here
				switch (msg.what) {
				case 0:
					// update progress bar
					//installProgress.setMessage("" + (String) msg.obj);
					Log.d(TAG,  (String) msg.obj);
					break;
				case 1:
					//installProgress.cancel();
					//Toast toast = Toast.makeText(getApplicationContext(), "Zip extracted successfully", Toast.LENGTH_SHORT);
					displayLargeMessage(msg.obj + ": Complete.", "medium");
					//toast.show();
					//provider.refresh();
					Log.d(TAG, msg.obj + ":Zip extracted successfully");
					break;
				case 2:
					//installProgress.cancel();
					break;
				}
				super.handleMessage(msg);
			}

		};
		//Thread workthread = new Thread(new UnZip(myHandler, zipFile, directory));
	    //workthread.start();
		UnZip unzip = new UnZip(myHandler, zipFile, directory);
		unzip.run();
		Log.d(TAG, "Completed extraction.");
	}

	public String getCouchAppUrl() {
		return couchAppUrl;
	}
	
	public void setCouchAppUrl(String couchAppUrl) {
		this.couchAppUrl = couchAppUrl;
	}
	// coconut
	private void loadWebview() {
		String status = listener.getStatus();
		Log.d(TAG, "Server status:" + status);
		Log.d(TAG, "webView.loadUrl: " + couchAppUrl);
		webView.loadUrl(this.getCouchAppUrl());

	}
	
	// coconut
	/*class ToastMessageBig implements Runnable {
		View layout;
		Context ctx;
		String msg;
		
		public ToastMessageBig( Context ctx, String msg, View layout ) {
			this.ctx = ctx;
			this.msg = msg;
			this.layout = layout;
		}
		
		public void run() {
			//Toast.makeText( ctx, msg, Toast.LENGTH_SHORT).show();
			Toast toast = new Toast(ctx);
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();
		}
	}*/
	
=======
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

>>>>>>> 2ff8b6450838afd1841b67c39247650cfd8f8950
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
