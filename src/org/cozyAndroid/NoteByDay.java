package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NoteByDay extends Activity implements View.OnClickListener{
	
	private ListView listeNotesByDay;
	static CozyListByDateAdapter adapter;
	public static String TAG = "NoteByDay";
	public static ViewResult vResult;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_by_day);
		listeNotesByDay = (ListView) findViewById(R.id.listNotesByDay);
		vResult = TabCalendrier.getHashQuery().get(TabCalendrier.dayclicked.substring(1,11));
		listeNotesByDay.setAdapter(adapter);
		adapter.updateListItems();
		listeNotesByDay.setOnItemClickListener(new clicknote());
		listeNotesByDay.setOnItemLongClickListener(deleteItem);
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
			if (item.get("_rev").getTextValue()!=null) {
				CozyItemUtils.setRev(item.get("_rev").getTextValue());
			}
			if (item.get("_id").getTextValue()!=null) {
				CozyItemUtils.setId(item.get("_id").getTextValue());
			}
			if (item.get("tags").getTextValue()!=null) {
				CozyItemUtils.setListTags(item.get("tags").getTextValue());   // Pour l'instant on ne teste qu'un tag
			}
			CozyItemUtils.setDateCreation(item.get("created_at").getTextValue());
			CozyItemUtils.setDateModification(item.get("modified_at").getTextValue());
		    CozyItemUtils.setTitleModif(itemText.getTextValue());
		    TabPlus.formerActivity("tabliste");
		    finish();
		    CozyAndroidActivity.gettabHost().setCurrentTab(2);
				
		}
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