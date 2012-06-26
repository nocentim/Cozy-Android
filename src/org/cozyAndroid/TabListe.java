package org.cozyAndroid;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class TabListe extends Activity {
	
	NoteAdapter adapter;
	ListView listeNotes;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		}
	
	public void onResume() {
		super.onResume();
		setContentView(R.layout.liste_notes);
		listeNotes = (ListView) findViewById(R.id.listNotes);
		adapter = new NoteAdapter(this);
		listeNotes.setAdapter(adapter);
		DataBase db = DataBase.getInstance();
		ArrayList<Spanned> note = db.getAllNotes("notes","note");
		adapter.setListe(note);
		adapter.notifyDataSetChanged();
	}
	
	public void modeEdition (String oldName, String oldBody) {
		setContentView(R.layout.edition);
		EditText name = (EditText) findViewById(R.id.nameEdition);
		EditText body = (EditText) findViewById(R.id.bodyEdition);
		name.setText(oldName);
		body.setText(oldBody);
		Button annuler = (Button) findViewById(R.id.buttonAnnuler);
		annuler.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Retour à la liste
				onResume();
			}
		});
	}
}