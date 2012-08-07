package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult.Row;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.couchbase.touchdb.ektorp.TouchDBHttpClient;

public class NoteByDay extends Activity implements View.OnClickListener{
	
	private ListView listeNotesByDay;
	private CozyListByDateAdapter adapter;
	public static String TAG = "NoteByDay";
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_by_day);
		listeNotesByDay = (ListView) findViewById(R.id.listNotesByDay);
		listeNotesByDay.setOnItemClickListener(new clicknote());
		Replication.ViewByDay(this);
		startEktorp();
		Replication.startReplications(this);
	}
	
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Handle click on item in list
	 */
	private class clicknote implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
			TabPlus.modif = true;
			Row row = (Row)parent.getItemAtPosition(position);
			JsonNode item = row.getValueAsNode();
			JsonNode itemText = item.get("title");
			CozyItemUtils.setRev(item.get("_rev").getTextValue());
			CozyItemUtils.setId(item.get("_id").getTextValue());
			CozyItemUtils.setListTags(item.get("tags").getTextValue());   // Pour l'instant on ne teste qu'un tag
			CozyItemUtils.setDateCreation(item.get("created_at").getTextValue());
			CozyItemUtils.setDateModification(item.get("modified_at").getTextValue());
		    CozyItemUtils.setTitleModif(itemText.getTextValue());
		    TabPlus.formerActivity("tabliste");
		    finish();
		    CozyAndroidActivity.gettabHost().setCurrentTab(2);
				
		}
	}

	
	protected void startEktorp() {
		Log.v(TAG, "starting ektorp");

		if(Replication.httpClient != null) {
			Replication.httpClient.shutdown();
		}

		Replication.httpClient = new TouchDBHttpClient(Replication.server);
		Replication.dbInstance = new StdCouchDbInstance(Replication.httpClient);

		CozySyncEktorpAsyncTask startupTask = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				Replication.couchDbConnector = Replication.dbInstance.createConnector(Replication.DATABASE_NOTES, true);
			}

			@Override
			protected void onSuccess() {
				//attach list adapter to the list and handle clicks
				ViewQuery viewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.byDayViewName).descending(true);
				adapter = new CozyListByDateAdapter(NoteByDay.this, Replication.couchDbConnector, viewQuery, NoteByDay.this);
				listeNotesByDay.setAdapter(adapter);
				listeNotesByDay.setOnItemLongClickListener(deleteItem);

				Replication.startReplications(getBaseContext());
			}
		};
		startupTask.execute();
	}
	
	/**
	 * Handle long-click on item in list
	 */
	private AdapterView.OnItemLongClickListener deleteItem = new AdapterView.OnItemLongClickListener() {
		public boolean onItemLongClick (AdapterView<?> parent, View view, int position, long id) {
	        Row row = (Row)parent.getItemAtPosition(position);
	        final JsonNode item = row.getValueAsNode();
			JsonNode textNode = item.get("text");
			String itemText = "";
			if(textNode != null) {
				itemText = textNode.getTextValue();
			}
		

			AlertDialog.Builder builder = new AlertDialog.Builder(NoteByDay.this);
			AlertDialog alert = builder.setTitle("Delete Item?")
				   .setMessage("Are you sure you want to delete \"" + itemText + "\"?")
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			        	   Replication.deleteGroceryItem(item);
			           }
			       })
			       .setNegativeButton("No", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			               // Handle Cancel
			           }
			       })
			       .create();
	
			alert.show();
	
			return true;
		}
	};
	
}