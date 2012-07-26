package org.cozyAndroid;

import org.codehaus.jackson.JsonNode;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult.Row;
import org.ektorp.impl.StdCouchDbInstance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.couchbase.touchdb.ektorp.TouchDBHttpClient;

public class TabListe extends Activity {
	
	private int TRI_PERTINENCE = 0;
	private int TRI_CHEMIN = 1;
	private int TRI_DATE = 2;
	
	private CozySyncListAdapter adapter;
	private ListView listeNotes;
	private static String titleModif;
	private static String rev;
	private static String id;
	
	public static String TAG = "TabListe";
	//Recherche
	private RechercheNote rechercheNote;
	private RechercheDossier dansDossier;
	private int methodeTri = TRI_DATE;
	
	//constants
	public static final String DATABASE_NAME = "grocery-sync";

	
	//splash screen
	protected SplashScreenDialog splashDialog;
	

    public Context returnBaseContext() {
    	return getBaseContext();
    }
    
    public static String getTitleModif() {
    	return titleModif;
    }
    
    public void setRev(String Rev){
    	rev=Rev;
    }
    
    public static String getRev() {
    	return rev;
    }
    
    public void setId(String Id){
    	id=Id;
    }
    
    public static String getId() {
    	return id;
    }
    
	public void onCreate(Bundle saveInstanceState) {
		super.onCreate(saveInstanceState);
		setContentView(R.layout.liste_notes);
		
		//connect items from layout
		
		listeNotes = (ListView) findViewById(R.id.listNotes);
		listeNotes.setOnItemClickListener(new clicknote());	
		//listeNotes.setOnItemClickListener(new EditListener());
		//adapter = new NoteAdapter(this);  // crée une vue
		//listeNotes.setAdapter(adapter);
		rechercheNote = (RechercheNote) findViewById(R.id.recherche_note);
		dansDossier = (RechercheDossier) findViewById(R.id.dans_dossier);
		
		showSplashScreen();
		removeSplashScreen();
		Replication.startTouchDB(returnBaseContext());
        startEktorp();
		//Recupperation des dossiers pour les suggestions
		/*String projection[] = {Dossiers.DOSSIER_ID,Dossiers.NAME,Dossiers.PARENT};
		Cursor cursor = managedQuery(Dossiers.CONTENT_URI, projection, null, null, Dossiers.NAME + " COLLATE NOCASE");
		Dossier.newArborescence(cursor);*/
		//Tri :
		TextView textTri= (TextView) findViewById(R.id.textTri);
		String[] tris = getResources().getStringArray(R.array.sort_array);
		for (int i = 0; i < tris.length; i++) {
			final int ii = i;
			LinkSpan.linkify(textTri, tris[i],new LinkSpan.OnClickListener() {
				public void onClick() {
					setTri(ii);
					//lanceRecherche();
				}
			});	
		}
	
	}
	
	/*public void onResume() {
		super.onResume();
		ArrayList<Note> note = new ArrayList<Note>();
		String projection[] = {Notes.NOTE_ID,Notes.TITLE,Notes.BODY,Notes.DOSSIER};
		Cursor cursor = managedQuery(Notes.CONTENT_URI, projection, null, null, null);
		if (cursor.moveToFirst()) {
			do {
				note.add(new Note(cursor));
			} while (cursor.moveToNext());
		}

		//adapter.setListe(note);
		//adapter.notifyDataSetChanged();
	}*/
	
	public void onResume() {
		super.onResume();
		titleModif = "";
	}
	
	public void setTri (int tri) {
		methodeTri = tri;
	}
	
	/*public void lanceRecherche() {
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
				note.add(new Note(cursor)) ;
			} while (cursor.moveToNext());
		}

		adapter.setListe(note);
		adapter.notifyDataSetChanged();
		
	}*/
	
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
	
	/**
	 * Handle click on item in list
	 */
	private class clicknote implements OnItemClickListener {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {		
			TabPlus.modif = true;
			Row row = (Row)parent.getItemAtPosition(position);
			JsonNode item = row.getValueAsNode();
			JsonNode itemText = item.get("title");
			setRev(item.get("_rev").getTextValue());
			setId(item.get("_id").getTextValue());
	        titleModif = itemText.getTextValue();
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
				Replication.couchDbConnector = Replication.dbInstance.createConnector(DATABASE_NAME, true);
			}

			@Override
			protected void onSuccess() {
				//attach list adapter to the list and handle clicks
				ViewQuery viewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.byDateViewName).descending(true);
				adapter = new CozySyncListAdapter(TabListe.this, Replication.couchDbConnector, viewQuery, TabListe.this);
				listeNotes.setAdapter(adapter);
				//listeNotes.setOnItemClickListener(TabListe.this);
				listeNotes.setOnItemLongClickListener(deleteItem);

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
		

		AlertDialog.Builder builder = new AlertDialog.Builder(TabListe.this);
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

	/**
	 * Removes the Dialog that displays the splash screen
	 */
	protected void removeSplashScreen() {
	    if (splashDialog != null) {
	        splashDialog.dismiss();
	        splashDialog = null;
	    }
	}

	/**
	 * Shows the splash screen over the full Activity
	 */
	protected void showSplashScreen() {
	    splashDialog = new SplashScreenDialog(this);
	    splashDialog.show();
	}
}