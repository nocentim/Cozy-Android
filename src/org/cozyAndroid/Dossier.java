package org.cozyAndroid;

import java.util.ArrayList;

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
	
	public String getPath () {
		if (parent == null) {
			return nom;
		}
		return parent.getPath() + " > " + nom;
	}
}
