package org.cozyAndroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class TabListe extends Activity {
	
	NoteAdapter adapter;
	ListView listeNotes;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		
		listeNotes = (ListView) findViewById(R.id.listNotes);
		adapter = new NoteAdapter(getApplicationContext());
		listeNotes.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
}