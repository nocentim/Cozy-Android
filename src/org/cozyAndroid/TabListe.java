package org.cozyAndroid;

import java.util.ArrayList;

import org.cozyAndroid.providers.TablesSQL.Dossiers;
import org.cozyAndroid.providers.TablesSQL.Notes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TabListe extends Activity {
	
	private int TRI_PERTINENCE = 0;
	private int TRI_CHEMIN = 1;
	private int TRI_DATE = 2;
	
	private NoteAdapter adapter;
	private ListView listeNotes;
	
	//Recherche
	private RechercheNote rechercheNote;
	private RechercheDossier dansDossier;
	private int methodeTri = TRI_DATE;
	
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		listeNotes = (ListView) findViewById(R.id.listNotes);
		listeNotes.setOnItemClickListener(new EditListener());
		adapter = new NoteAdapter(this);
		listeNotes.setAdapter(adapter);
		rechercheNote = (RechercheNote) findViewById(R.id.recherche_note);
		dansDossier = (RechercheDossier) findViewById(R.id.dans_dossier);
		//Recupperation des dossiers pour les suggestions
		String projection[] = {Dossiers.DOSSIER_ID,Dossiers.NAME,Dossiers.PARENT};
		Cursor cursor = managedQuery(Dossiers.CONTENT_URI, projection, null, null, Dossiers.NAME + " COLLATE NOCASE");
		Dossier.newArborescence(cursor);
		//Tri :
		TextView textTri= (TextView) findViewById(R.id.textTri);
		String[] tris = getResources().getStringArray(R.array.sort_array);
		for (int i = 0; i < tris.length; i++) {
			final int ii = i;
			LinkSpan.linkify(textTri, tris[i],new LinkSpan.OnClickListener() {
				public void onClick() {
					setTri(ii);
					lanceRecherche();
				}
			});	
		}
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
	
	public void setTri (int tri) {
		methodeTri = tri;
	}
	
	public void lanceRecherche() {
		String constraint = rechercheNote.getText().toString();
		String chemin = dansDossier.getText().toString();
		int idDossier;
		ArrayList<Integer> dossiersAChercher; 
		if (chemin.equals("")) {
			idDossier = 0;
			dossiersAChercher = null;
		} else {
			Dossier d = Dossier.getDossierParChemin(chemin);
			if (d == null) {
				Toast t = Toast.makeText(this, "Dossier " + chemin + " introuvable.", Toast.LENGTH_SHORT);
				t.show();
				return;
			}
			dossiersAChercher = d.getTousLesFils();
		}
		String selection = "";
		if (dossiersAChercher != null && !dossiersAChercher.isEmpty()) {
			selection = Notes.DOSSIER + " IN (" + dossiersAChercher.get(0);
			for (int i = 1; i < dossiersAChercher.size(); i++) {
				selection += "," + dossiersAChercher.get(i);
			}
			selection += ")";
			if (!constraint.matches(" *")) {
				selection += " AND (";
			}
		}
		if (!constraint.matches(" *")) {
			String [] mots = constraint.split(" +");
			for (int i = 0; i < mots.length - 1; i++) {
				selection += Notes.TITLE +" LIKE \'%" + mots[i] + "%\' OR " + Notes.BODY +" LIKE \'%" + mots[i] + "%\' OR";
			}
			selection += Notes.TITLE +" LIKE \'%" + mots[mots.length-1] + "%\' OR " + Notes.BODY +" LIKE \'%" + mots[mots.length-1] + "%\'";
			if (dossiersAChercher != null && !dossiersAChercher.isEmpty()) {
				selection += ")";
			}
		}
		ArrayList<Note> note = new ArrayList<Note>();
		String projection[] = {Notes.NOTE_ID,Notes.TITLE,Notes.BODY,Notes.DOSSIER};
		Cursor cursor = managedQuery(Notes.CONTENT_URI, projection, selection, null, null);
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
	    	TabListe.this.startActivity(editer);
		}
		
	}
}