package org.cozyAndroid;


import java.util.ArrayList;

import org.cozyAndroid.providers.TablesSQL.Dossiers;
import org.cozyAndroid.providers.TablesSQL.Notes;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TabDossier extends ListActivity implements View.OnClickListener {
	
	/**
	 * Historique des dossiers parcourus (par id)
	 * Le dossier courant est accessible avec getDossierCourant()
	 * On peut acceder a d'autres dossiers de l'historique avec
	 * getHistorique(int pos)
	 */
	private ArrayList<Integer> historique;
	private int position;
	
	//Widget de l'interface par ordre de lecture
	private AutoCompleteTextView search;
	
	private ImageButton precedent;
	private ImageButton suivant;
	private TextView path;
	
	private ListView navigateur;
	private DossierAdapter dossierAdapter;
	
	private Button supprimer;
	
	private Button creer_note;
	private Button creer_dossier;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dossier);
		
		//Initialisation des champs
		historique = new ArrayList<Integer>();
		historique.add(Dossier.racine.getId());
		position = 0;
		navigateur = (ListView) findViewById(android.R.id.list);
		dossierAdapter = new DossierAdapter(this,Dossier.racine);
		navigateur.setAdapter(dossierAdapter);
		navigateur.setOnItemClickListener(dossierClick);
		
		path = (TextView) findViewById(R.id.navigateur_path);
		setPathWithLinks(Dossier.racine);
	    
		//Initialisation de la barre de recherche de dossiers
		search = (RechercheDossier) findViewById(R.id.search_dossier);
		search.setOnItemClickListener(suggestionClick);
		
		//Boutons
		precedent = (ImageButton) findViewById(R.id.precedent);
		suivant = (ImageButton) findViewById(R.id.suivant);
		precedent.setOnClickListener(this);
		suivant.setOnClickListener(this);
		precedent.setEnabled(false);
		suivant.setEnabled(false);
		supprimer = (Button) findViewById(R.id.suppr_button);
		creer_dossier = (Button) findViewById(R.id.add_button);
		creer_note = (Button) findViewById(R.id.add_note_button);
		supprimer.setOnClickListener(this);
		creer_dossier.setOnClickListener(this);
		creer_note.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String projection[] = {Dossiers.DOSSIER_ID,Dossiers.NAME,Dossiers.PARENT};
		Cursor cursor = managedQuery(Dossiers.CONTENT_URI, projection, null, null, Dossiers.NAME + " COLLATE NOCASE");
		Dossier.newArborescence(cursor);
		String notesProjection []= {Notes.NOTE_ID,Notes.TITLE,Notes.BODY,Notes.DOSSIER};
		Cursor notesCursor = managedQuery(Notes.CONTENT_URI, notesProjection, null, null, Notes.TITLE + " COLLATE NOCASE");
		Dossier.addNotes(notesCursor);
		dossierAdapter.setDossier(getDossierCourant());
		dossierAdapter.notifyDataSetChanged();
		enableButtons();
	}
	
	public Dossier getDossierCourant() {
		return Dossier.getDossierParId(historique.get(position));
	}
	
	public Dossier getHistorique(int pos) {
		return Dossier.getDossierParId(historique.get(pos));
	}
	
	public void onClick(View v) {
		if (!v.isEnabled()) {
			return;
		}
		switch(v.getId()) {
		case R.id.precedent :
			ouvrePrecedent();
			break;
		case R.id.suivant :
			ouvreSuivant();
			break;
		case R.id.suppr_button :
			supprimerCourant();
			break;
		case R.id.add_button :
			fenetreCreer();
			break;
		case R.id.add_note_button :
			creerNote();
		default :
			break;
		}
	}
	
	public void editer (Note n) {
		Intent editer = new Intent(TabDossier.this, Edition.class);
		editer.putExtra("id", n.id);
		editer.putExtra("titre", n.titre);
		editer.putExtra("body", n.body);
		editer.putExtra("idDossier", n.idDossier);
		startActivity(editer);
	}
	
	public void ouvreDossier (Dossier d) {
		for (int i = position + 1; i < historique.size();) {
			historique.remove(i);
		}
		
		historique.add(d.getId());
		position++;
		
		majInterface();
	}
	
	//Ouvre le dossier precedent dans l'historique
	public void ouvrePrecedent() {
		position--;
		majInterface();
	}
	
	//Ouvre le dossier suivant dans l'historique
	public void ouvreSuivant() {
		position++;
		majInterface();
	}
	
	/**
	 * Supprime le dossier courant et met à jour
	 * l'historique ainsi que l'interface
	 */
	private void supprimerCourant() {
		Dossier supprimeMoi = getDossierCourant();
		int id = supprimeMoi.getId();
		int count = getContentResolver().delete(Dossiers.CONTENT_URI, Dossiers.DOSSIER_ID + "= " + id, null);
		if (count < 1) {
			Toast t = Toast.makeText(TabDossier.this, "Echec de la suppression", Toast.LENGTH_SHORT);
			t.show();
			return;
		}
		Toast t = Toast.makeText(TabDossier.this, "Dossier " +supprimeMoi.nom + " supprimé", Toast.LENGTH_SHORT);
		t.show();
		//position != 0 car la racine ne peut pas etre supprimee
		position--; 
		//Mise a jour de l'historique : on enleve toutes les occurences de courant.
		//Ca marche car courant n'a pas de sous-dossiers :
		//on ne supprime que des dossiers vides (TODO : est-ce vrai?)
		for (int i = 1; i < historique.size();) {
			if (getHistorique(i).equals(supprimeMoi)) {
				historique.remove(i);
				if (i <= position) {
					position--;
				}
			} else {
				i++;
			}
		}
		//Il se peut maintenant qu'un dossier soit present 2 fois de suite dans l'historique
		//On enleve donc les doublons
		for (int i = 0; i < historique.size() - 1;) {
			Dossier courant = getHistorique(i);
			Dossier suivant = getHistorique(i+1);
			if (courant.equals(suivant)) {
				historique.remove(i+1);
				if (i < position) {
					position--;
				}
			} else {
				i++;
			}
		}
		supprimeMoi.parent.supprimerDossier(supprimeMoi);
		majInterface();
	}
	
	/**
	 * Fait apparaitre une fenetre demandant le nom du dossier a creer
	 */
	private void fenetreCreer() {
		final Dialog dialog =  new Dialog(this);
		dialog.setContentView(R.layout.creer_dossier);
		dialog.setTitle("Creer un dossier");
		dialog.setCanceledOnTouchOutside(true);
		final Button confirm = (Button) dialog.findViewById(R.id.button_confirm);
		final TextView text = (TextView) dialog.findViewById(R.id.text_creer);
		text.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					confirm.performClick();
					return true; 
				}
				return false;
			}
		});
		
		OnClickListener confirmer = new OnClickListener() {
			
			public void onClick(View v) {
				String nom = text.getText().toString().trim();
				text.setText(nom);
				if (!nom.equals("")) {
					Dossier courant = getDossierCourant();
					if (courant.contient(nom)) {
						Toast t = Toast.makeText(TabDossier.this,
								"Un dossier de ce nom existe déjà", Toast.LENGTH_SHORT);
						t.show();
					} else {
						ContentValues values = new ContentValues();
						values.put(Dossiers.NAME, nom);
						values.put(Dossiers.PARENT, courant.getId());        
						Uri uri = getContentResolver().insert(Dossiers.CONTENT_URI, values);
						int id = Integer.parseInt(uri.getLastPathSegment());
						courant.addDossier(id, nom);
						dialog.cancel();
						majInterface();
					}
				} else {
					Toast t = Toast.makeText(TabDossier.this, "Entrez un nom", Toast.LENGTH_SHORT);
					t.show();
				}
			}
		};
		confirm.setOnClickListener(confirmer);
		
		dialog.show();
	}
	
	//TODO faire appel a l'editeur une fois qu'il sera fait
	private void creerNote() {
		Dossier courant = getDossierCourant();
		ContentValues values = new ContentValues();
		values.put(Notes.TITLE, "Nouvelle Note");
		values.put(Notes.BODY, "");
		values.put(Notes.DOSSIER, courant.getId());  
		Uri uri = getContentResolver().insert(Notes.CONTENT_URI, values);
		int id = Integer.parseInt(uri.getLastPathSegment());
		Note n = new Note(id, "Nouvelle Note", "", courant.getId());
		courant.addNote(n);
		majInterface();
	}
	
	/**
	 * A appeler quand le dossier courant change.
	 * Met a jour l'interface avec toutes les informations
	 * sur le nouveau dossier.
	 */
	private void majInterface() {
		Dossier courant = getDossierCourant();
		
		enableButtons();
		setPathWithLinks(courant);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		dossierAdapter.setDossier(courant);
		dossierAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Gère l'activation et la visibilité des boutons
	 */
	private void enableButtons () {
		if (position == 0) {
			precedent.setEnabled(false);
		} else {
			precedent.setEnabled(true);
		}
		if (position == historique.size() - 1) {
			suivant.setEnabled(false);
		} else {
			suivant.setEnabled(true);
		}
		Dossier courant = getDossierCourant();
		if (courant.size() == 0 && !courant.equals(Dossier.racine)) {
			supprimer.setVisibility(View.VISIBLE);
		} else {
			supprimer.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setPathWithLinks (Dossier d) {
		final ArrayList<Dossier> parents = d.getParents();
		String pathString = parents.get(0).nom;
		for (int i = 1; i < parents.size(); i++) {
			pathString += " / " + parents.get(i).nom;
		}
		path.setText(pathString);
		for (int i = 0; i < parents.size() - 1; i++) {
			final int iBis = i;
			LinkSpan.linkify(path, parents.get(iBis).nom, new LinkSpan.OnClickListener() {
				
				public void onClick() {
					ouvreDossier(parents.get(iBis));
				}
			});
		}
	}
	
	private OnItemClickListener dossierClick = new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (dossierAdapter.isDossier(position)) {
				//C'est un dossier
				Dossier d = (Dossier) dossierAdapter.getItem(position);
				ouvreDossier(d);
			} else {
				//C'est une note
				Note n = (Note) dossierAdapter.getItem(position);
				editer(n);
			}
		}
	};
	
	private OnItemClickListener suggestionClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Cursor c = (Cursor)search.getAdapter().getItem(position);
			ouvreDossier(Dossier.getDossierParId(c.getInt(0)));
		}
	};
}