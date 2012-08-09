package org.cozyAndroid;


import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

public class TabDossier extends ListActivity implements View.OnClickListener {
	

	public static final String TAG = "TabDossier";
	
	/**
	 * Historique des dossiers parcourus
	 * Le dossier courant est accessible avec getDossierCourant()
	 * On peut acceder a d'autres dossiers de l'historique avec
	 * getHistorique(int pos)
	 */
	private ArrayList<String> historique;
	private int position;
	
	//Widget de l'interface par ordre de lecture
	private AutoCompleteTextView search;
	
	private ImageButton precedent;
	private ImageButton suivant;
	private TextView path;
	
	private ListView navigateur;
	private  CozySyncFolderAdapter folderAdapter = null;
	
	private Button supprimer;
	
	private Button creer_dossier;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dossier);

		//Initialisation des champs
		historique = new ArrayList<String>();
		historique.add("");
		position = 0;
		
		navigateur = (ListView) findViewById(android.R.id.list);
		navigateur.setOnItemClickListener(dossierClick);
		AsyncTask<Void, Void, Void> waitForEktorpTask = new AsyncTask<Void,Void,Void>() {

			@Override
			protected Void doInBackground(Void... arg0) {
				while (!CozyAndroidActivity.ektorpStarted()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
				return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				Log.d(TAG, "ektorp started");
				ViewQuery fViewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.byParentViewName).key("").descending(true);
				folderAdapter = new CozySyncFolderAdapter(Replication.couchDbConnector, fViewQuery, TabDossier.this);
				navigateur.setAdapter(folderAdapter);
				Log.d(TAG, "adapter de dossiers mis a jour");
		     }
		};
		waitForEktorpTask.execute();
		
		path = (TextView) findViewById(R.id.navigateur_path);
		setPathWithLinks("");
	    
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
		supprimer.setOnClickListener(this);
		creer_dossier.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (folderAdapter != null) {
			folderAdapter.notifyDataSetChanged();
		}
		enableButtons();
	}
	
	public String getDossierCourant() {
		return historique.get(position);
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
			if (folderAdapter.getCount() == 0) {
				supprimerCourant();
			}
			break;
		case R.id.add_button :
			fenetreCreer();
			break;
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
	
	public void ouvreDossier (String name) {
		for (int i = position + 1; i < historique.size();) {
			historique.remove(i);
		}
		
		historique.add(name);
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
					String courant = getDossierCourant();
					String nomComplet;
					if (courant.equals("")) {
						nomComplet = nom;
					} else {
						nomComplet = courant + "/" + nom;
					}
					if (folderAdapter.contient(nomComplet)) {
						Toast t = Toast.makeText(TabDossier.this,
								"Un dossier de ce nom existe déjà", Toast.LENGTH_SHORT);
						t.show();
					} else if (nomValide(nom)){
						createOrUpdateFolder(nomComplet, courant, null, null);
						dialog.dismiss();
					} else {
						Toast t = Toast.makeText(TabDossier.this,
								"Un nom de dossier ne peux pas contenir de \'/\'", Toast.LENGTH_SHORT);
						t.show();
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
	
	/**
	 * Verifie qu'un nom de dossier est valide
	 * Pour le moment seul le '/' pose problème
	 * TODO autres caracteres ?
	 * @param nom
	 * @return true si le nom est valide, false sinon
	 */
	private boolean nomValide(String nom) {
		return !nom.contains("/");
	}
	
	/**
	 * A appeler quand le dossier courant change.
	 * Met a jour l'interface avec toutes les informations
	 * sur le nouveau dossier.
	 */
	private void majInterface() {
		String courant = getDossierCourant();
		
		enableButtons();
		setPathWithLinks(courant);
		MovementMethod m = path.getMovementMethod();
	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
	        path.setMovementMethod(LinkMovementMethod.getInstance());
	    }
		folderAdapter.setDossier(courant);
	}
	
	/**
	 * Gere l'activation et la visibilité des boutons
	 */
	public void enableButtons () {
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
		String courant = getDossierCourant();
		if (folderAdapter != null && folderAdapter.getCount() == 0 && !courant.equals("")) {
			supprimer.setVisibility(View.VISIBLE);
		} else {
			supprimer.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setPathWithLinks (String dossier) {
		String pathString;
		if (dossier.equals("")) {
			pathString = "Navigateur";
		} else {
			pathString = "Navigateur / " + dossier.replace("/", " / ");
		}
		final String [] parents = pathString.split(" / ");
		final String [] parentsComplet = new String [parents.length-1];
		String nomComplet = "";
		if (parentsComplet.length > 0) {
			parentsComplet[0] = "";
		}
		for (int i = 1; i < parentsComplet.length; i++) {
			nomComplet += parents[i];
			parentsComplet[i] = nomComplet;
			nomComplet += "/";
		}
		
		path.setText(pathString);
		if (dossier.equals("")) {
			return;
		}
		LinkSpan.linkify(path, "Navigateur", new LinkSpan.OnClickListener() {
			
			public void onClick() {
				ouvreDossier("");
			}
		});
		for (int i = 1; i < parents.length - 1; i++) {
			final int iBis = i;
			LinkSpan.linkify(path, parents[iBis], new LinkSpan.OnClickListener() {
				
				public void onClick() {
					ouvreDossier(parentsComplet[iBis]);
				}
			});
		}
	}
	
	private OnItemClickListener dossierClick = new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (folderAdapter.isDossier(position)) {
				//C'est un dossier
				Row r = (Row) folderAdapter.getItem(position);
				String name = r.getValueAsNode().get("name").getTextValue();
				ouvreDossier(name);
			} else {
				//C'est une note
				TabPlus.modif = true;
				Row row = (Row)folderAdapter.getItem(position);
				JsonNode item = row.getValueAsNode();
				JsonNode itemText = item.get("title");
				Log.d("title", itemText.getTextValue());
				CozyItemUtils.setRev(item.get("_rev").getTextValue());
				CozyItemUtils.setId(item.get("_id").getTextValue());
				CozyItemUtils.setListTags(item.get("tags").getTextValue());   // Pour l'instant on ne teste qu'un tag
				CozyItemUtils.setDateCreation(item.get("created_at").getTextValue());
				CozyItemUtils.setDateModification(item.get("modified_at").getTextValue());
				Log.d("tags", item.get("tags").getTextValue());
		        CozyItemUtils.setTitleModif(itemText.getTextValue());
		        TabPlus.formerActivity("tabliste");
		        CozyAndroidActivity.gettabHost().setCurrentTab(2);
			}
		}
	};
	
	private OnItemClickListener suggestionClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d(TAG,"clic sur une suggestion : a implementer");
		}
	};
	
	
	/**
	 * Supprime le dossier courant et met Ã  jour
	 * l'historique ainsi que l'interface
	 */
	private void supprimerCourant() {
		final String courant = getDossierCourant();
		CozySyncEktorpAsyncTask deleteItemTask = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				ViewQuery viewQuery = new ViewQuery().designDocId(Replication.dDocId).viewName(Replication.FolderbyNameViewName).key(courant);
				ViewResult result = Replication.couchDbConnector.queryView(viewQuery);
				List<Row> rows = result.getRows();
				if (rows.size() == 0) {
					throw new UpdateConflictException();
				}
				Row r = rows.get(0);
				JsonNode node = r.getValueAsNode();
				Replication.couchDbConnector.delete(node.get("_id").getTextValue(),node.get("_rev").getTextValue());
			}

			@Override
			protected void onSuccess() {
				Log.d(TAG, "Document created successfully");
				Toast t = Toast.makeText(TabDossier.this, "Dossier " + courant + " supprimé", Toast.LENGTH_SHORT);
				t.show();
				//position != 0 car la racine ne peut pas etre supprimee
				position--; 
				//Mise a jour de l'historique : on enleve toutes les occurences de courant.
				//Ca marche car courant n'a pas de sous-dossiers :
				//on ne supprime que des dossiers vides (TODO : est-ce vrai?)
				for (int i = 1; i < historique.size();) {
					if (historique.get(i).equals(courant)) {
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
					String courant = historique.get(i);
					String suivant = historique.get(i+1);
					if (courant.equals(suivant)) {
						historique.remove(i+1);
						if (i < position) {
							position--;
						}
					} else {
						i++;
					}
				}
				majInterface();
			}

			@Override
			protected void onUpdateConflict(
					UpdateConflictException updateConflictException) {
				Log.d(TAG, "Got an update conflict for: " + courant);
				Toast t = Toast.makeText(TabDossier.this, "Echec de la suppression", Toast.LENGTH_SHORT);
				t.show();
			}
		};
		deleteItemTask.execute();
	}
	
	public void createOrUpdateFolder(String name, String parent, final String rev, String id) {
		final JsonNode item = CozyItemUtils.createOrUpdateFolder(name, parent, rev, id);
		CozySyncEktorpAsyncTask createItemTask = new CozySyncEktorpAsyncTask() {

			@Override
			protected void doInBackground() {
				if (rev == null) {
					Replication.couchDbConnector.create(item);
				} else {
					Replication.couchDbConnector.update(item);
				}

			}

			@Override
			protected void onSuccess() {
				Log.d(TAG, "Document created successfully");
				Toast t = Toast.makeText(TabDossier.this, "Dossier " + item.get("name") + " créé.", Toast.LENGTH_SHORT);
				t.show();
				folderAdapter.notifyDataSetChanged();
			}

			@Override
			protected void onUpdateConflict(
					UpdateConflictException updateConflictException) {
				Log.d(TAG, "Got an update conflict for: " + item.toString());
				Toast t = Toast.makeText(TabDossier.this, "Echec de la création de " + item.get("name") + ".", Toast.LENGTH_SHORT);
				t.show();
			}
		};
		createItemTask.execute();
	}
	
	
}