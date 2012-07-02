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
	 * Le chainage entre les dossiers
	 * @param nom le nom du sous dossier
	 * @return le dossier créé
	 */
	public Dossier addDossier(String nom) {
		Dossier res = new Dossier(nom, this);
		sousDossiers.add(res);
		return res;
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
}
