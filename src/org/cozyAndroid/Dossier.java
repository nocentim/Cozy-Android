package org.cozyAndroid;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;

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
	public String getPath() {
		if (parent == null || parent == racine) {
			return "";
		}
		return parent.getPath() + parent.nom + "/";
	}
	
	public String getPathComplet() {
		return getPath() + nom + "/";
	}
	
	@Override
	/**
	 * Utilisée pour le filtrage des suggestions de recherche
	 */
	public String toString() {
		if (parent == null || parent == racine) {
			return nom;
		}
		return nom + " (" + getPath() +")";
	}
	
	
	/**
	 * Retourne tous les dossiers de l'arborescence
	 */
	public static ArrayList<Dossier> getTous() {
		ArrayList<Dossier> tous = racine.getTousLesFils();
		tous.add(racine);
		return tous;
	}
	
	/**
	 * Retourne tous les dossiers de l'arborescence.
	 * Les sous-dossiers de this sont placés en tete de liste.
	 */
	public ArrayList<Dossier> getTousAvecPriorite() {
		if (parent == null) {
			//this est la racine
			return getTous();
		}
		//On ajoute les sous-dossiers
		ArrayList<Dossier> tous = getTousLesFils();
		tous.add(this);
		//Puis on ajoute le reste
		tous.addAll(racine.getTousSauf(this));
		tous.add(racine);
		return tous;
	}
	
	protected ArrayList<Dossier> getTousLesFils() {
		ArrayList<Dossier> tous = new ArrayList<Dossier>();
		for (int i = 0; i < sousDossiers.size();i++) {
			Dossier fils = sousDossiers.get(i); 
			tous.add(fils);
			tous.addAll(fils.getTousLesFils());
		}
		return tous;
	}
	
	protected ArrayList<Dossier> getTousSauf(Dossier lui) {
		ArrayList<Dossier> tous = new ArrayList<Dossier>();
		for (int i = 0; i < sousDossiers.size();i++) {
			Dossier fils = sousDossiers.get(i);
			if(!fils.equals(lui)) {
				tous.add(fils);
				tous.addAll(fils.getTousSauf(lui));
			}
		}
		return tous;
	}
	
}
