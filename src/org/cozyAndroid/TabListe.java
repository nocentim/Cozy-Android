package org.cozyAndroid;

import java.util.ArrayList;

import org.cozyAndroid.providers.NoteSQL.Notes;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

public class TabListe extends Activity {
	
	NoteAdapter adapter;
	ListView listeNotes;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		listeNotes = (ListView) findViewById(R.id.listNotes);
		adapter = new NoteAdapter(this);
		listeNotes.setAdapter(adapter);
	}
	
	public void onResume() {
		super.onResume();
		ArrayList<Note> note = new ArrayList<Note>();
		String projection[] = {Notes.NOTE_ID,Notes.TITLE,Notes.BODY};
		Cursor cursor = managedQuery(Notes.CONTENT_URI, projection, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				note.add(new Note(cursor));
			} while (cursor.moveToNext());
		}

		adapter.setListe(note);
		adapter.notifyDataSetChanged();
	}
}