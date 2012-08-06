package org.cozyAndroid;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.ReplicationCommand;
import org.ektorp.UpdateConflictException;
import org.ektorp.http.HttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.couchbase.touchdb.TDDatabase;
import com.couchbase.touchdb.TDServer;
import com.couchbase.touchdb.TDView;
import com.couchbase.touchdb.TDViewMapBlock;
import com.couchbase.touchdb.TDViewMapEmitBlock;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

public class Replication {
	
	public static final String TAG = "Replication";
	
	//constants
	public static final String DATABASE_NOTES = "grocery-sync";
	public static final String dDocName = "grocery-local";
	public static final String dDocId = "_design/" + dDocName;
	public static final String byDateViewName = "byDate";
	public static final String byTitleViewName = "byTitle";
	public static final String byTagsViewName = "ByTags";
	
	//couch internals
	protected static TDServer server;
	protected static HttpClient httpClient;
	
	//ektorp impl
	public static CouchDbInstance dbInstance;
	public static CouchDbConnector couchDbConnector;
	protected static ReplicationCommand pushReplicationCommand;
	protected static ReplicationCommand pullReplicationCommand;

	 //static inializer to ensure that touchdb:// URLs are handled properly
    {
        TDURLStreamHandlerFactory.registerSelfIgnoreError();
    }
	
    
	protected static void NotesView(Context context) {
	    String filesDir = context.getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }

	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byDateViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {

	    	 @Override
	            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	                Object modifiedAt = document.get("modified_at");
	                if(modifiedAt != null) {
	                    emitter.emit(modifiedAt.toString(), document);
	                }

	            }
        }, null, "1.0");
	    //Test pour les suggestions
	    TDView viewByTitle = db.getViewNamed(String.format("%s/%s", dDocName, byTitleViewName));
	    viewByTitle.setMapReduceBlocks(new TDViewMapBlock() {

            @Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object title = document.get("title");
                if(title != null) {
                    emitter.emit(title.toString(), document);
                }

            }
        }, null, "1.0");
	}
	
	protected static void TagView(Context context) {
		/*String filesDir = context.getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }*/
	    
	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byTagsViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
	            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	                Object tagged = document.get("tags");
	                if(tagged != null) {
	                	if (tagged !="aucun") {
	                		emitter.emit(tagged.toString(), document);
	                	}
	                }
        }
    }, null, "1.0");

	}
	
	protected static void ViewByDay(Context context) {
		/*String filesDir = context.getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }*/
	    
	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byTagsViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
	            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	                Object tagged = document.get("tags");
	                if(tagged != null) {
	                	if (tagged !="aucun") {
	                		emitter.emit(tagged.toString(), document);
	                	}
	                }
        }
    }, null, "1.0");

	}
	
	
	public static void startReplications(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		pushReplicationCommand = new ReplicationCommand.Builder()
			.source(DATABASE_NOTES)
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
			.target(DATABASE_NOTES)
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
	
	 public static void deleteGroceryItem(final JsonNode item) {
	        CozySyncEktorpAsyncTask deleteTask = new CozySyncEktorpAsyncTask() {

				@Override
				protected void doInBackground() {
					couchDbConnector.delete(item);
				}

				@Override
				protected void onSuccess() {
					Log.d(TAG, "Document deleted successfully");
				}

				@Override
				protected void onUpdateConflict(
						UpdateConflictException updateConflictException) {
					Log.d(TAG, "Got an update conflict for: " + item.toString());
				}
			};
		    deleteTask.execute();
	    }
}