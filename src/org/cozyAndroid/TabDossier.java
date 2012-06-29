package org.cozyAndroid;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class TabDossier extends Activity implements View.OnClickListener {
	
	Dossier racine = Dossier.racine;
	ArrayList<Dossier> historique;
	int position;
	
	ListView navigateur;
	DossierAdapter adapter;
	TextView path;
	
	ImageButton precedent;
	ImageButton suivant;
	
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
		
		path = (TextView) findViewById(R.id.navigateur_path);
		path.setText("Navigateur");
		
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
		photos.addDossier("2012");
	}
	
	public void ouvreDossier (Dossier d) {
		Log.d("TabDossier.ouvreDossier","taille de l'histo =" + historique.size());
		for (int i = position + 1; i < historique.size();) {
			Log.d("TabDossier.ouvreDossier","enlevage de " + historique.get(i).nom);
			historique.remove(i);
		}
		Log.d("TabDossier.ouvreDossier","apres suppr: taille de l'histo =" + historique.size());
		
		historique.add(d);
		position++;
		
		enableButtons();
		path.setText(d.getPath());
		adapter.setDossier(d);
		adapter.notifyDataSetChanged();
	}
	
	//Ouvre le dossier precedent dans l'historique
	public void ouvrePrecedent() {
		position--;
		Dossier courant = historique.get(position);
		
		enableButtons();
		path.setText(courant.getPath());
		adapter.setDossier(courant);
		adapter.notifyDataSetChanged();
	}
	
	//Ouvre le dossier suivant dans l'historique
	public void ouvreSuivant() {
		position++;
		Dossier courant = historique.get(position);
		
		enableButtons();
		path.setText(courant.getPath());
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
}