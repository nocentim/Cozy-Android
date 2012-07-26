package org.cozyAndroid;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.util.Log;

/**
 * Classe des dossiers
 * Fonctionnement particulier : Il n'y a pas de constructeur public.
 * On crée une arborescence (ensemble de dossiers chainés) avec newArborescence.
 * On peut aussi ajouter un dossier à un dossier existant avec addDossier.
 * On accède au dossier racine avec Dossier.racine .
 */
public class Dossier {
	
	public String nom;
	public Dossier parent = null;
	private int id;
	private ArrayList<Dossier> sousDossiers = new ArrayList<Dossier>();
	private ArrayList<Note> notes = new ArrayList<Note>();
	
	private static HashMap <Integer,Dossier> idToDossier = new HashMap<Integer, Dossier>();
	
	public static final Dossier racine = new Dossier(0,"Navigateur",null);
	
	
	
	// CONSTRUCTEURS
	
	private Dossier (int id, String nom, Dossier parent) {
		this.nom = nom;
		this.parent = parent;
		this.id = id;
		idToDossier.put(id, this);
	}
	
	/**
	 * Constructeur incomplet :
	 * Construit un dossier en ne connaissant que son id 
	 * Attention, il faut imperativement mettre a jour les
	 * autres champs avant de l'utiliser
	 */
	private Dossier (int id) {
		this.id = id;
		idToDossier.put(id, this);
	}
	
	/**
	 * Constructeur d'arborescence a partir d'un cursor
	 * Le principe : on construit les dossiers dans l'ordre du cursor
	 * Pour chaque dossier, si son parent n'existe pas, on le construit
	 * sans renseigner tous les champs (on n'a que l'id a ce moment la)
	 * et on le mettra a jour plus tard dans la boucle.
	 * Voir newDossier et newDossierIncomplet.
	 */
	public static void newArborescence(Cursor c) {
		idToDossier.clear();
		racine.sousDossiers.clear();
		racine.notes.clear();
		idToDossier.put(0,racine);
		if (c.moveToFirst()) {
			do {
				
				newDossier(c.getInt(0),c.getString(1),c.getInt(2));
			} while (c.moveToNext());
		}
		
	}
	
	/**
	 * Si un dossier avec cet id existe deja, on va le chercher.
	 * Sinon, on construit un nouveau dossier.
	 * Pour assurer le chainage, on construit aussi son parent s'il n'existait pas.
	 * (Voir newDossierIncomplet)
	 * @return le nouveau dossier
	 */
	private static Dossier newDossier (int id, String nom, int idParent) {
		Dossier res = idToDossier.get(id);
		if (res == null) {
			Dossier parent = newDossierIncomplet(idParent);
			res = new Dossier(id,nom, parent);
			parent.sousDossiers.add(res);
		} else {
			res.nom = nom;
			res.parent = newDossierIncomplet(idParent);
			res.parent.sousDossiers.add(res);
		}
		return res;
	}
	
	/**
	 * Si un dossier avec cet id existe deja, on va le chercher.
	 * Sinon, on construit un nouveau dossier, sans renseigner tout ses champs
	 * Il devra etre mis a jour avant d'etre utilisable.
	 * @return le dossier avec cet id
	 */
	private static Dossier newDossierIncomplet (int id) {
		Dossier res = idToDossier.get(id);
		if (res == null) {
			return new Dossier(id);
		}
		return res;
	}
	
	/**
	 * Crée et ajoute des note aux bons dossiers
	 * @param c Un cursor representant plusieurs notes avec,
	 * dans l'ordre : id de la note, titre, body, id du dossier 
	 * @return la note créée
	 */
	public static void addNotes(Cursor c) {
		if (c.moveToFirst()) {
			do {
				if (c.getColumnCount() >= 4) {
					Dossier d = idToDossier.get(c.getInt(3));
					Note n = new Note(c);
					d.addNote(n);
				}
			} while (c.moveToNext());
		}
	}
	
	/**
	 * Ajoute une note à ce dossier
	 * @param n la note a ajouter
	 */
	public void addNote(Note n) {
		notes.add(n);
	}
	
	/**
	 * Crée un dossier au bon endroit dans l'arborescence
	 * @param c Dans l'ordre : id du dossier, nom, id du parent
	 * @return le dossier créé
	 */
	public static Dossier addDossier(Cursor c) {
		Dossier parent = idToDossier.get(c.getInt(2));
		return parent.addDossier(c.getInt(0), c.getString(1));
	}
	
	/**
	 * Crée un nouveau sous-dossier
	 * @param id l'id du sous-dossier
	 * @param name son nom
	 * @return le dossier créé
	 */
	public Dossier addDossier(int id, String name) {
		Dossier res = new Dossier(id, name, this);
		sousDossiers.add(res);
		return res;
	}
	
	//GETTERS ET SETTERS
	
	public int getId() {
		return id;
	}
	
	public ArrayList<Dossier> getSousDossiers () {
		return sousDossiers;
	}
	
	/**
	 * Retourne un la liste des id des tous les fils
	 * (directs ou pas) de ca dossier
	 * @return
	 */
	public ArrayList<Integer> getTousLesFils () {
		ArrayList<Integer> res = new ArrayList<Integer>();
		res.add(getId());
		for (int i = 0; i < sousDossiers.size(); i++) {
			res.addAll(sousDossiers.get(i).getTousLesFils());
		}
		return res;
	}
	
	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	/**
	 * Retourne le dossier associé a l'id passée en parametre
	 * ou null s'il n'existe pas
	 */
	public static Dossier getDossierParId (int id) {
		return idToDossier.get(id);
	}
	
	/**
	 * Retourne le dossier associé au chemin en paramètre.
	 * Il vaut mieux utiliser getDossierParId si l'id est connue, c'est beaucoup plus performant
	 * @param chemin le chemin du dossier recherché
	 * @return le dossier associé au chemin, ou null
	 */
	public static Dossier getDossierParChemin (String chemin) {
		return racine.suisChemin(chemin);
	}
	
	private Dossier suisChemin (String chemin) {
		if (chemin.equals("")) {
			return this;
		}
		String newChemin;
		int end = chemin.indexOf('/');
		if (end == -1) {
			end = chemin.length();
			newChemin = "";
		} else {
			newChemin = chemin.substring(end + 1);
		}
		String next = chemin.substring(0, end);
		for (int i=0; i < sousDossiers.size(); i++) {
			Dossier d = sousDossiers.get(i);
			if (d.nom.equals(next)) {
				return d.suisChemin(newChemin);
			}
		}
		return null;
	}
	
	/**
	 * Verifie si un sous-dossier possède deja ce nom
	 */
	public boolean contient(String nom) {
		for (int i = 0; i < sousDossiers.size();i++) {
			if (nom.equals(sousDossiers.get(i).nom)) {
				return true;
			}
		}
		return false;
	}
	
	public void supprimerDossier(Dossier d) {
		idToDossier.remove(d.id);
		sousDossiers.remove(d);
	}
	
	public int nbDossiers() {
		return sousDossiers.size();
	}
	
	public int nbNotes() {
		return notes.size();
	}
	
	public int size() {
		return sousDossiers.size() + notes.size();
	}
	
	/**
	 * @return un tableau avec le nom de ses parents ainsi que le sien.
	 * Le tableau possède au moins 1 element (son propre nom)
	 */
	public ArrayList<Dossier> getParents () {
		ArrayList<Dossier> res;
		if (parent == null) {
			res = new ArrayList<Dossier>();
		} else {
			res = parent.getParents();			
		}
		res.add(this);
		return res;
	}
	
	/**
	 * @return une string indiquant le nombre de notes et de sous-dossiers
	 */
	public String getInfos() {
		int n = notes.size();
		int d = sousDossiers.size();
		if (n == 0 && d == 0) {
			return "Vide";
		}
		String res = "";
		if (n == 1) {
			res = "1 note";
		} else if (n > 1) {
			res = n + " notes";
		}
		if (n != 0 && d != 0) {
			res += ", ";
		}
		if (d == 1) {
			res += "1 sous-dossier";
		} else if (d > 1) {
			res += d + " sous-dossiers";
		}
		return res;
	}
	
	/**
	 * Retourne le chemin du dossier
	 * (sans son propre nom)
	 */
	public String getChemin() {
		if (parent == null || parent == racine) {
			return "";
		}
		return parent.getChemin() + parent.nom + "/";
	}
	
	public String getCheminComplet() {
		return getChemin() + nom + "/";
	}

	public static ArrayList<Dossier> getSuggestions(String[] patternStrings) {
		ArrayList<Dossier> res = new ArrayList<Dossier>();
		ArrayList<String> patterns = new ArrayList<String>();
		for (int i = 0; i < patternStrings.length; i++) {
			patterns.add(patternStrings[i]);
		}
		for (int fils = 0; fils < racine.sousDossiers.size(); fils ++) {
			racine.sousDossiers.get(fils).getSuggestions(patterns,res);
		}
		return res;
	}

	private void getSuggestions(ArrayList<String> patterns, ArrayList<Dossier> res) {
		ArrayList<String> match = new ArrayList<String>();
		if (patterns.isEmpty()) {
			//le parent match tout les criteres, on se rajoute (basse priorite)
			res.add(this);
		} else {
			String nomLower = nom.toLowerCase();
			for (int i = 0; i < patterns.size();) {
				String mot = patterns.get(i).toLowerCase();
				if (nomLower.matches(mot +".*") || nomLower.matches(".* " + mot + ".*")) {
					match.add(mot);
					patterns.remove(i);
				} else {
					i++;
				}
			}
			if (patterns.isEmpty()) {
				//on match tout les criteres, on se rajoute en haute priorit�
				res.add(0,this);
			}
		}
		//on regarde si les fils correspondent aux crit�res
		for (int fils = 0; fils < sousDossiers.size(); fils ++) {
			sousDossiers.get(fils).getSuggestions(patterns,res);
		}
		//On remet la liste de criteres dans son etat initial
		patterns.addAll(match);
	}
	
}
