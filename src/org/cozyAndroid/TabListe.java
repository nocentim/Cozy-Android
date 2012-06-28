package org.cozyAndroid;

import java.util.ArrayList;

import org.cozyAndroid.providers.NoteSQL.Notes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.widget.ListView;

public class TabListe extends Activity {
	
	NoteAdapter adapter;
	ListView listeNotes;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		listeNotes = (ListView) findViewById(R.id.listNotes);
		adapter = new NoteAdapter(this);
		listeNotes.setAdapter(adapter) ;
	}
	
	public void onResume() {
		super.onResume();
		ArrayList<Note> note = new ArrayList<Note>();
		Cursor cursor = managedQuery(Notes.CONTENT_URI,null, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				note.add(new Note(cursor));
			} while (cursor.moveToNext());
		}

		adapter.setListe(note);
		adapter.notifyDataSetChanged();
	}
}