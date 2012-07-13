package org.cozyAndroid;

import java.util.ArrayList;

import org.cozyAndroid.providers.TablesSQL.Notes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TabListe extends Activity {
	
	NoteAdapter adapter;
	ListView listeNotes;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		listeNotes = (ListView) findViewById(R.id.listNotes);
		listeNotes.setOnItemClickListener(new EditListener());
		adapter = new NoteAdapter(this);
		listeNotes.setAdapter(adapter) ;
	}
	
	public void onResume() {
		super.onResume();
		ArrayList<Note> note = new ArrayList<Note>();
		String projection[] = {Notes.NOTE_ID,Notes.TITLE,Notes.BODY,Notes.DOSSIER};
		Cursor cursor = managedQuery(Notes.CONTENT_URI, projection, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				note.add(new Note(cursor));
			} while (cursor.moveToNext());
		}

		adapter.setListe(note);
		adapter.notifyDataSetChanged();
	}
	
	private class EditListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Note note = (Note) adapter.getItem(position);
			Intent editer = new Intent(TabListe.this, Edition.class);
			editer.putExtra("id", note.id);
			editer.putExtra("titre", note.titre);
			editer.putExtra("body", note.body);
	    	startActivity(editer);
		}
		
	}
}