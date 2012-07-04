package org.cozyAndroid;


import java.util.ArrayList;

import android.app.Dialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TabDossier extends ListActivity implements View.OnClickListener {
	
	//Historique des dossiers parcourus
	//Le dossier courant est accessible avec historique.get(position)
	private ArrayList<Dossier> historique;
	private int position;
	
	//Widget de l'interface par ordre de lecture
	private AutoCompleteTextView search;
	private ArrayAdapter<Dossier> searchAdapter;
	
	private ImageButton precedent;
	private ImageButton suivant;
	private TextView path;
	
	private ListView navigateur;
	private DossierAdapter dossierAdapter;
	
	private Button supprimer;
	private Button creer;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dossier);
		
		//a enlever
		if (Dossier.racine.size() == 0) {
			AjouteDossiers();
		}
		
		//Initialisation des champs
		historique = new ArrayList<Dossier>();
		historique.add(Dossier.racine);
		position = 0;
		navigateur = (ListView) findViewById(android.R.id.list);
		dossierAdapter = new DossierAdapter(this,Dossier.racine);
		navigateur.setAdapter(dossierAdapter);
		
		path = (TextView) findViewById(R.id.navigateur_path);
		setPathWithLinks(Dossier.racine);
	    
		search = (AutoCompleteTextView) findViewById(R.id.search_dossier);
		initSearch();
		
		//Boutons
		precedent = (ImageButton) findViewById(R.id.precedent);
		suivant = (ImageButton) findViewById(R.id.suivant);
		precedent.setOnClickListener(this);
		suivant.setOnClickListener(this);
		precedent.setEnabled(false);
		suivant.setEnabled(false);
		
		supprimer = (Button) findViewById(R.id.suppr_button);
		creer = (Button) findViewById(R.id.add_button);
		supprimer.setOnClickListener(this);
		creer.setOnClickListener(this);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		dossierAdapter.notifyDataSetChanged();
	}
	
	/**
	 * Initialise le champ de recherche
	 */
	private void initSearch() {
		//Creation de l'adapteur
		ArrayList<Dossier> dossiers = Dossier.getTous();
		for (int i = 0; i < dossiers.size(); i++) {
			Log.d("TabDossier",""+dossiers.get(i));
		}
		searchAdapter = new ArrayAdapter<Dossier>(
				this, android.R.layout.simple_dropdown_item_1line, dossiers);
		search.setAdapter(searchAdapter);
		//Action a effectuer quand on clique sur un element de la liste
		search.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ouvreDossier(searchAdapter.getItem(position));
				search.setText("");
			}
		});
		
	}
	
	//TODO : a enlever, c'est juste un test
	private void AjouteDossiers() {
		
		Dossier photos = Dossier.racine.addDossier("Photos");
		Dossier info = Dossier.racine.addDossier("Informatique");
		Dossier divers = Dossier.racine.addDossier("Divers");
		
		Dossier.racine.addNote(new Note("0","TODO","   -Sortir le chien\n   -Appeler Bob\n   -Conquerir le monde"));
		photos.addNote(new Note("0","Vacances", "Super vacances trop ouf, lol\n [photo 1] \n [photo 2] \n ..."));
		divers.addNote(new Note("0","Mots de passes","Compte bancaire : 9681681616468418694523\n Mot de passe: 426831\n\nCompte amazon: Lalala@gmail.com\nmot de passe : hunter2"));
		Dossier _2012 = photos.addDossier("2012");
		Dossier vacs = _2012.addDossier("Montagne");
		vacs.addDossier("Mont Blanc");
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
		default :
			break;
		}
	}
	
	public void ouvreDossier (Dossier d) {
		for (int i = position + 1; i < historique.size();) {
			historique.remove(i);
		}
		
		historique.add(d);
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
		Dossier supprimeMoi = historique.get(position);
		//position != 0 car la racine ne peut pas etre supprimee
		position--; 
		//Mise a jour de l'historique : on enleve toutes les occurences de courant.
		//Ca marche car courant n'a pas de sous-dossiers :
		//on ne supprime que des dossiers vides (TODO : est-ce vrai?)
		for (int i = 1; i < historique.size();) {
			if (historique.get(i).equals(supprimeMoi)) {
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
			Dossier courant = historique.get(i);
			Dossier suivant = historique.get(i+1);
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
		confirm.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				String nom = text.getText().toString();
				if (!nom.equals("")) {
					Dossier courant = historique.get(position);
					Dossier nouveau = courant.addDossier(nom);
					if (nouveau == null) {
						Toast t = Toast.makeText(TabDossier.this,
								"Un dossier de ce nom existe déjà", Toast.LENGTH_SHORT);
						t.show();
					} else {
						dialog.cancel();
						majInterface();
					}
				} else {
					Toast t = Toast.makeText(TabDossier.this, "Entrez un nom", Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
		
		dialog.show();
	}
	
	/**
	 * A appeler quand le dossier courant change.
	 * Met a jour l'interface avec toutes les informations
	 * sur le nouveau dossier.
	 */
	private void majInterface() {
		Dossier courant = historique.get(position);
		
		enableButtons();
		setPathWithLinks(courant);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		dossierAdapter.setDossier(courant);
		dossierAdapter.notifyDataSetChanged();
		
		searchAdapter.clear();
		ArrayList<Dossier> dossiers = courant.getTousAvecPriorite();
		for (int i = 0; i < dossiers.size(); i++) {
			searchAdapter.add(dossiers.get(i));
		}
		
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
		Dossier courant = historique.get(position);
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
}