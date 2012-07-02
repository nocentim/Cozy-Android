package org.cozyAndroid;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TabDossier extends Activity implements View.OnClickListener {
	
	private final Dossier racine = Dossier.racine;
	private ArrayList<Dossier> historique;
	private int position;
	
	private ListView navigateur;
	private DossierAdapter adapter;
	private TextView path;
	
	private ImageButton precedent;
	private ImageButton suivant;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dossier);
		
		//a enlever
		AjouteDossiers();
		
		historique = new ArrayList<Dossier>();
		historique.add(racine);
		position = 0;
		navigateur = (ListView) findViewById(R.id.liste_navigateur);
		adapter = new DossierAdapter(this,racine);
		navigateur.setAdapter(adapter);
		navigateur.setEmptyView(findViewById(R.layout.dossier_vide));
		
		path = (TextView) findViewById(R.id.navigateur_path);
		setPathWithLinks(racine);
	    
		precedent = (ImageButton) findViewById(R.id.precedent);
		suivant = (ImageButton) findViewById(R.id.suivant);
		precedent.setOnClickListener(this);
		suivant.setOnClickListener(this);
		precedent.setEnabled(false);
		suivant.setEnabled(false);
		
	}
	
	//TODO : a enlever, c'est juste un test
	private void AjouteDossiers() {
		
		Dossier photos = racine.addDossier("Photos");
		Dossier info = racine.addDossier("Informatique");
		Dossier divers = racine.addDossier("Divers");
		
		racine.addNote(new Note("0","TODO","   -Sortir le chien\n   -Appeler Bob\n   -Conquerir le monde"));
		photos.addNote(new Note("0","Vacances", "Super vacances trop ouf, lol\n [photo 1] \n [photo 2] \n ..."));
		divers.addNote(new Note("0","Mots de passes","Compte bancaire : 9681681616468418694523\n Mot de passe: 426831\n\nCompte amazon: Lalala@gmail.com\nmot de passe : hunter2"));
		Dossier _2012 = photos.addDossier("2012");
		Dossier vacs = _2012.addDossier("Montagne");
		vacs.addDossier("Mont Blanc");
	}
	
	public void ouvreDossier (Dossier d) {
		for (int i = position + 1; i < historique.size();) {
			historique.remove(i);
		}
		
		historique.add(d);
		position++;
		
		enableButtons();
		setPathWithLinks(d);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		adapter.setDossier(d);
		adapter.notifyDataSetChanged();
	}
	
	//Ouvre le dossier precedent dans l'historique
	public void ouvrePrecedent() {
		position--;
		Dossier courant = historique.get(position);
		
		enableButtons();
		setPathWithLinks(courant);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		adapter.setDossier(courant);
		adapter.notifyDataSetChanged();
	}
	
	//Ouvre le dossier suivant dans l'historique
	public void ouvreSuivant() {
		position++;
		Dossier courant = historique.get(position);
		
		enableButtons();
		setPathWithLinks(courant);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		adapter.setDossier(courant);
		adapter.notifyDataSetChanged();
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
		default :
			break;
		}
	}
	
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
	}
	
	private void setPathWithLinks (Dossier d) {
		final ArrayList<Dossier> parents = d.getParents();
		String pathString = parents.get(0).nom;
		for (int i = 1; i < parents.size(); i++) {
			pathString += " > " + parents.get(i).nom;
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