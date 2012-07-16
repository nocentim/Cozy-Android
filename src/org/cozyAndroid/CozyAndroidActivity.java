package org.cozyAndroid;

import java.io.IOException;
import java.util.Map;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.ViewQuery;
import org.ektorp.http.HttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.TabHost;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.couchbase.touchdb.TDDatabase;
import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.TDViewMapBlock ;
import com.couchbase.touchdb.TDViewMapEmitBlock;
import com.couchbase.touchdb.ektorp.TouchDBHttpClient;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;



public class CozyAndroidActivity extends TabActivity  implements OnItemClickListener, OnItemLongClickListener, OnKeyListener{
    /** Called when the activity is first created. */
	private static TabHost tabHost;
	private int [] layoutTab;
	
	private static CozyAndroidActivity instance;
	
	//couch internals
	protected static TDServer server;
	protected static HttpClient httpClient;
	
	
	public static String TAG = "CozyAndroid";
	
	//constants
	public static final String DATABASE_NAME = "cozy-sync";
	public static final String dDocName = "cozy-local";
	public static final String dDocId = "_design/" + dDocName;
	public static final String byDateViewName = "byDate";
	
	protected CouchDbInstance dbInstance;
	protected static CouchDbConnector couchDbConnector;
	protected ReplicationCommand pushReplicationCommand;
	protected ReplicationCommand pullReplicationCommand;
	
	{
		TDURLStreamHandlerFactory.registerSelfIgnoreError();
	}

    public CozyAndroidActivity() {
        instance = this;
    }

    public static Context getContext() {
        return instance;
    }
    
    public static CouchDbConnector returnCouchDbConnector() {
    	return couchDbConnector;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tabHost = getTabHost();
		layoutTab = new int[4];
		layoutTab[0] = R.layout.tab_notes;
		layoutTab[3] = R.layout.tab_calendrier;
		layoutTab[2] = R.layout.tab_plus;
		layoutTab[1] = R.layout.tab_dossier;

		setupTab("TabListe", new Intent().setClass(this, TabListe.class),0);
		setupTab("TabTags", new Intent().setClass(this, TabDossier.class),1);
		setupTab("TabPlus", new Intent().setClass(this, TabPlus.class),2);
        setupTab("TabCalendrier", new Intent().setClass(this, TabCalendrier.class),3);
    }
    
    //  A VOIR!!!!
   /* protected void startTouchDB() {
    	server = null;
	    String filesDir = getFilesDir().getAbsolutePath();    // getContext().getFilesDir().getAbsolutePath(); ??
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }

	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NAME);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byDateViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {

	    	// overide?? devrait marcher
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object createdAt = document.get("created_at");
                if(createdAt != null) {
                    emitter.emit(createdAt.toString(), document);
                }

            }
        }, null, "1.0");
	}*/
    
    protected void startEktorp() {
		Log.v(TAG, "starting ektorp");

		if(httpClient != null) {
			httpClient.shutdown();
		}

		httpClient = new TouchDBHttpClient(server);
		dbInstance = new StdCouchDbInstance(httpClient);

		CozySyncEktorpAsyncTask startupTask = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				couchDbConnector = dbInstance.createConnector(DATABASE_NAME, true);
			}

			@Override
			protected void onSuccess() {
				//attach list adapter to the list and handle clicks
				ViewQuery viewQuery = new ViewQuery().designDocId(dDocId).viewName(byDateViewName).descending(true);
				/*itemListViewAdapter = new GrocerySyncListAdapter(AndroidGrocerySyncActivity.this, couchDbConnector, viewQuery);
				itemListView.setAdapter(itemListViewAdapter);
				itemListView.setOnItemClickListener(AndroidGrocerySyncActivity.this);
				itemListView.setOnItemLongClickListener(AndroidGrocerySyncActivity.this);*/

				startReplications();
			}
		};
		startupTask.execute();
	}
    
    public void startReplications() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

		pushReplicationCommand = new ReplicationCommand.Builder()
			.source(DATABASE_NAME)
			.target(prefs.getString("sync_url", "http://mschoch.iriscouch.com/grocery-test"))
			.continuous(true)
			.build();

		CozySyncEktorpAsyncTask pushReplication = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				dbInstance.replicate(pushReplicationCommand);
			}
		};

		pushReplication.execute();

		pullReplicationCommand = new ReplicationCommand.Builder()
			.source(prefs.getString("sync_url", "http://mschoch.iriscouch.com/grocery-test"))
			.target(DATABASE_NAME)
			.continuous(true)
			.build();

		CozySyncEktorpAsyncTask pullReplication = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				dbInstance.replicate(pullReplicationCommand);
			}
		};

		pullReplication.execute();
	}

	public void stopEktorp() {
	}
    

	private void setupTab(String tag, Intent intent, int layoutTabIndex) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator( createTabView(tabHost.getContext(), layoutTabIndex)).setContent(intent));
	}
	 
	// créé la vue associée à l'onglet considéré
	private View createTabView(final Context context, int layoutTabIndex) {
		View view = LayoutInflater.from(context).inflate(layoutTab[layoutTabIndex], null);
		view.refreshDrawableState();
		//view.setBackgroundResource(R.color.Ensimag);
		return view;
	}
	
	public void onResume(){
		super.onResume();
		
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}	
	
	
}