package org.cozyAndroid;

import java.io.IOException;
import java.util.List;
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
import com.couchbase.touchdb.TDViewReduceBlock;
import com.couchbase.touchdb.router.TDURLStreamHandlerFactory;

public class Replication {
	
	public static final String TAG = "Replication";
	
	//constants
	public static final String DATABASE_NOTES = "grocery-sync";
	public static final String dDocName = "grocery-local";
	public static final String dDocId = "_design/" + dDocName;
	public static final String byDateViewName = "byDate";
	public static final String byPathViewName = "byPath";
	public static final String byTitleViewName = "byTitle";
	public static final String byTagsViewName = "ByTags";
	public static final String suggestionsViewName =  "suggestions";
	public static final String byDayViewName = "ByDay";
	public static final String byParentViewName = "ByParent";
	public static final String FolderbyNameViewName = "ByFolder";
	public static final String byWordAndDateViewName = "ByWordAndDate";
	public static final String byWordAndPathViewName = "ByWordAndPath";
	
	//couch internals
	protected static TDServer server;
	protected static HttpClient httpClient;
	protected static TDDatabase db;
	
	//ektorp impl
	public static CouchDbInstance dbInstance;
	public static CouchDbConnector couchDbConnector;
	protected static ReplicationCommand pushReplicationCommand;
	protected static ReplicationCommand pullReplicationCommand;

    
	protected static void NotesView(Context context) {
	    String filesDir = context.getFilesDir().getAbsolutePath();
	    try {
            server = new TDServer(filesDir);
        } catch (IOException e) {
            Log.e(TAG, "Error starting TDServer", e);
        }

	    //install a view definition needed by the application
	    db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byDateViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {

	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object modifiedAt = document.get("modified_at");
                Object type = document.get("type");
                if( (type == null || type.toString().equals("note")) && modifiedAt != null) {
                    emitter.emit(modifiedAt.toString(), document);
                }
            }
	    	
        }, null, "1.0");
	    
	    //View by path
	    TDView viewByPath = db.getViewNamed(String.format("%s/%s", dDocName, byPathViewName));
	    viewByPath.setMapReduceBlocks(new TDViewMapBlock() {

	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object path = document.get("parent");
                Object type = document.get("type");
                if( (type == null || type.toString().equals("note")) && path != null) {
                    emitter.emit(path.toString(), document);
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
	    
	    // Calendar View
	    TDView viewByDay = db.getViewNamed(String.format("%s/%s", dDocName, byDayViewName));
	    viewByDay.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
	            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	                Object createdAt = document.get("created_at");
	                Object type = document.get("type");
	                if(type != null && type.toString().equals("note") && createdAt != null) {     
	                	//if (createdAt.toString().equals(TabCalendrier.getDay().substring(1,11))) {	
	                		emitter.emit(createdAt.toString(), document);
	                	//}
	                }
	    	}
	    }, null, "1.0");
		    
		//Tags View
		TDView viewByTags = db.getViewNamed(String.format("%s/%s", dDocName, byTagsViewName));
	    viewByTags.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
	        public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	            Object tagged = document.get("tags");
	            if(tagged != null) {	
	            	if (!tagged.toString().equals("aucun")) {
	            		emitter.emit(tagged.toString(), document);
	            	}
	            }
	    	}
	    }, null, "1.0");
	    
	    //Search Views : by modification date or by path
	    TDView viewByWordAndDate = db.getViewNamed(String.format("%s/%s", dDocName, byWordAndDateViewName));
	    viewByWordAndDate.setMapReduceBlocks(new TDViewMapBlock() {
			
			@Override
			public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
				Object modified = document.get("modified_at");
				Object title = document.get("title");
				Object body = document.get("body");
				if (title != null && body != null && modified != null) {
					String [] titleWords = title.toString().split(" +");
					String [] bodyWords = body.toString().split(" +");
					for (int i = 0; i < titleWords.length; i++) {
						emitter.emit(new String [] {titleWords[i], modified.toString()}, document);
					}
					for (int i = 0; i < bodyWords.length; i++) {
						emitter.emit(new String [] {bodyWords[i], modified.toString()}, document);
					}
				}
			}
		}, null, "1.0");
	    
	    TDView viewByWordAndPath = db.getViewNamed(String.format("%s/%s", dDocName, byWordAndPathViewName));
	    viewByWordAndPath.setMapReduceBlocks(new TDViewMapBlock() {
			
			@Override
			public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
				Object path = document.get("parent");
				Object title = document.get("title");
				Object body = document.get("body");
				if (title != null && body != null && path != null) {
					String [] titleWords = title.toString().split(" +");
					String [] bodyWords = body.toString().split(" +");
					for (int i = 0; i < titleWords.length; i++) {
						emitter.emit(new String [] {titleWords[i], path.toString()}, document);
					}
					for (int i = 0; i < bodyWords.length; i++) {
						emitter.emit(new String [] {bodyWords[i], path.toString()}, document);
					}
				}
			}
		}, null, "1.0");
	}
	
	protected static void suggestionView(Context context) {
	    
	    //install a view definition needed by the application
		db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, suggestionsViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
	    		Object title = document.get("title");
	    		if (title != null) {
                	String [] mots = title.toString().split(" +");
                	for (int i = 0; i < mots.length; i++) {
                        if (mots[i].length() > 3) {
                        	emitter.emit(mots[i], 1);
                        }
                    }
                }
	    	}
	    }, new TDViewReduceBlock() {
			
			@Override
			public Object reduce(List<Object> keys, List<Object> values,
					boolean rereduce) {
				return TDView.totalValues(values);
			}
		}, "1.0");
	}
	
	/*protected static void TagView(Context context) {
	    
	    //install a view definition needed by the application
	    TDDatabase db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view = db.getViewNamed(String.format("%s/%s", dDocName, byTagsViewName));
	    view.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object tagged = document.get("tags");
                if(tagged != null) {	
                	if (!tagged.toString().equals("aucun")) {
                		emitter.emit(tagged.toString(), document);
                	}
                }
	    	}
	    }, null, "1.0");

	}*/
	
	protected static void ViewByFolder(Context context) {
	    db = server.getDatabaseNamed(DATABASE_NOTES);
	    TDView view1 = db.getViewNamed(String.format("%s/%s", dDocName, byParentViewName));
	    view1.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object parent = document.get("parent");
                if( parent != null) {
            		emitter.emit(parent.toString(), document);
                }
	        }
	    }, null, "1.0");
	    
	    TDView view2 = db.getViewNamed(String.format("%s/%s", dDocName, FolderbyNameViewName));
	    view2.setMapReduceBlocks(new TDViewMapBlock() {
	    	
	    	@Override
            public void map(Map<String, Object> document, TDViewMapEmitBlock emitter) {
                Object name = document.get("name");
                if( name != null) {
            		emitter.emit(name.toString(), document);
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