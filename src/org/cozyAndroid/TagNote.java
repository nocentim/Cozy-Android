package org.cozyAndroid;

import java.util.ArrayList;

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
import android.widget.EditText;
import android.widget.ListView;

import com.couchbase.touchdb.ektorp.TouchDBHttpClient;


public class TagNote extends Activity implements View.OnClickListener{
	private ListView listeTags;
	public static final String TAG = "TagNote";
	private CozySyncEtiqAdapter adapter;
	private EditText tag;
	private Bundle param;
	
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_note);
		listeTags = (ListView) findViewById(R.id.listTags);
		findViewById(R.id.oktag).setOnClickListener(this);
		tag = (EditText) findViewById(R.id.TagEdition);
		param = this.getIntent().getExtras();
		//Replication.TagView(this);
		//startEktorp();
		
	}
	
	@Override
	public void onClick(View v) {
		//ArrayList<String> l = new ArrayList<String>();
		//l.add(tag.getText().toString());
		//TabPlus.createOrUpdateItem(param.getString("title"), param.getString("body"), param.getString("rev"), param.getString("id"), l);
		TabPlus.addTag(tag.getText().toString());
		Properties.tag = tag.getText().toString();
		tag.setText(" ");
		finish();
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
				ViewQuery viewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.byTagsViewName).descending(true);
				adapter = new CozySyncEtiqAdapter(TagNote.this, Replication.couchDbConnector, viewQuery, TagNote.this);
				listeTags.setAdapter(adapter);
				//listeNotes.setOnItemClickListener(TabListe.this);
				listeTags.setOnItemLongClickListener(deleteItem);

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
		

		AlertDialog.Builder builder = new AlertDialog.Builder(TagNote.this);
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