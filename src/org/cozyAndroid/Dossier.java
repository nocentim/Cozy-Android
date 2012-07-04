package org.cozyAndroid;

import java.util.ArrayList;

import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;

/**
 * Classe des dossiers
 * Fonctionnement particulier :
 * Il n'y a pas de constructeur public. Pour créer un dossier,
 * il faut appeler la methode addDossier depuis un dossier existant.
 * Cela garantis que tous les dossiers ont un dossier parent
 * et qu'ils sont accessibles depuis la racine.
 * On accède au dossier racine avec Dossier.racine .
 */
public class Dossier {
	
	public String nom;
	public Dossier parent = null;
	private ArrayList<Dossier> sousDossiers = new ArrayList<Dossier>();
	private ArrayList<Note> notes = new ArrayList<Note>();
	
	public static final Dossier racine = new Dossier("Navigateur",null);
	
	
	private Dossier (String nom, Dossier parent) {
		this.nom = nom;
		this.parent = parent;
	}
	
	public ArrayList<Dossier> getDossiers () {
		return sousDossiers;
	}
	
	public ArrayList<Note> getNotes() {
		return notes;
	}
	
	/**
	 * Cree et ajoute un sous-dossier.
	 * Assure le bon chainage entre les dossiers.
	 * @param nom Le nom du sous dossier
	 * @return Le dossier créé, et null si un dossier de ce nom existait deja
	 */
	public Dossier addDossier(String nom) {
		if (existe(nom)) {
			return null;
		}
		Dossier res = new Dossier(nom, this);
		sousDossiers.add(res);
		return res;
	}
	
	/**
	 * Verifie si un sous-dossier possède deja ce nom
	 */
	private boolean existe(String nom) {
		for (int i = 0; i < sousDossiers.size();i++) {
			if (nom.equals(sousDossiers.get(i).nom)) {
				return true;
			}
		}
		return false;
	}
	
	public void supprimerDossier(Dossier d) {
		sousDossiers.remove(d);
	}
	
	public void addNote(Note n) {
		notes.add(n);
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
	private String getPath() {
		if (parent == null || parent == racine) {
			return "";
		}
		return parent.getPath() + parent.nom + "/";
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
