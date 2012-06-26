package org.cozyAndroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

public class DataBase extends SQLiteOpenHelper  {	
	private static String TABLE_NOTES = "notes";
	
	private DataBase() {
		super(CozyAndroidActivity.getContext(), "CozyAndroid_DB", null, 2);
	}
	
	private static class DataBaseHolder { 
		public static final DataBase instance = new DataBase() {
		};
	}
		 
	public static DataBase getInstance() {
		return DataBaseHolder.instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE "+TABLE_NOTES+
				" (id0 INTEGER PRIMARY KEY AUTOINCREMENT, note VARCHAR NOT NULL);");
		/*File fichier = new File("src/exemple.txt");
		Log.d("ok","Chemin absolu du fichier : " + fichier.getAbsolutePath());
		Log.d("ok","Nom du fichier : " + fichier.getName());
		Log.d("ok","Est-ce qu'il existe ? " + fichier.exists());
		Log.d("ok","Est-ce un répertoire ? " + fichier.isDirectory());
		Log.d("ok","Est-ce un fichier ? " + fichier.isFile());*/
		//String essai = loadFile(fichier);
		String essai = "Note exemple, body: <div id=\"CNID_1\" class=\"Th-1\"><span>Un premier titre</span><br></div><div id=\"CNID_2\" class=\"Tu-2\"><span>Un paragraphe avec juste un titre</span><br></div><div id=\"CNID_3\" class=\"Tu-2\"><span>Un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long !</span><br></div><div id=\"CNID_4\" class=\"Tu-2\"><span>Un troisième paragraphe avec une liste en dessous :</span><br></div><div id=\"CNID_5\" class=\"Tu-3\"> <span>Premier paragraphe (1 titre seul)</span><br></div><div id=\"CNID_6\" class=\"Tu-3\"><span>Second paragraphe (1 titre &amp; une ligne)</span><br></div><div id=\"CNID_7\" class=\"l-3\"><span>Ligne du Second paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_8\" class=\"Tu-3\"><span>3ième paragraphe (1 titre &amp; 2 lignes)</span><br></div><div id=\"CNID_9\" class=\"l-3\"><span>Ligne 1 du 3ième paragraphe, pas longue.</span><br></div><div id=\"CNID_10\" class=\"l-3\"><span>Ligne 2 du 3ième paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span> <br></div><div id=\"CNID_11\" class=\"Tu-3\"><span>Quatrième paragraphe avec une sous liste :</span><br></div><div id=\"CNID_12\" class=\"Tu-4\"><span>Premier paragraphe</span><br></div><div id=\"CNID_13\" class=\"Tu-4\"><span>Second paragraphe</span><br></div><div id=\"CNID_14\" class=\"l-4\"><span>Ligne 1 du 2nd paragraphe, pas longue.</span><br></div><div id=\"CNID_15\" class=\"Tu-4\"><span>troisième paragraphe</span><br></div><div id=\"CNID_16\" class=\"Tu-4\"><span>Quatrième paragraphe</span><br></div><div id=\"CNID_17\" class=\"Th-1\"><span>Un titre de niveau 1</span><br></div><div id=\"CNID_18\" class=\"Th-2\"><span>Un titre de niveau 2</span><br></div><div id=\"CNID_19\" class=\"Tu-3\"><span>Un paragraphe un titre et deux lignes</span><br></div><div id=\"CNID_20\" class=\"l-3\"><span>Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_21\" class=\"l-3\"><span>Seconde ligne, pas très longue.</span><br></div><div id=\"CNID_22\" class=\"Th-2\"><span>Un titre de niveau 2</span><br></div><div id=\"CNID_23\" class=\"Tu-3\"><span>Un paragraphe avec juste un titre</span><br></div><div id=\"CNID_24\" class=\"Tu-3\"><span>Un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long !</span><br></div><div id=\"CNID_25\" class=\"Tu-3\"><span>Un troisième paragraphe avec une liste en dessous :</span><br></div><div id=\"CNID_26\" class=\"Tu-4\"><span>Premier paragraphe (1 titre seul)</span><br></div><div id=\"CNID_27\" class=\"Tu-4\"><span>Second paragraphe (1 titre &amp; une ligne)</span><br></div><div id=\"CNID_28\" class=\"l-4\"><span>Ligne du Second paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_29\" class=\"Tu-4\"><span></span><br></div><div id=\"CNID_30\" class=\"Tu-4\"><span>c'était un paragraphe vide :-)</span><br></div>";
	ContentValues valueNote = new ContentValues();
		valueNote.put("note",essai);
		db.insert(TABLE_NOTES,null,valueNote);
	}		
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		onCreate(db);
	}
	
	public void addNote(String table, String key, String note) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(key,note);
		db.insert(table,null,value);
		db.close();
	}
	
	public void addListNote(String table, String key, ArrayList<String> listNote) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		Iterator<String> it = listNote.iterator();
		while (it.hasNext()) {
			value.put(key,it.next());
			db.insert(table,null,value);
		}
		db.close();
	}
	
	public void deletePref(String table, String key, String name) {
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "DELETE FROM " + table + " WHERE " + key +"='"+name+"';";
		db.execSQL(query);
		db.close(); 
	}
	
	
	public ArrayList<Spanned> getAllNotes(String table, String key) {
		ArrayList<Spanned> list = new ArrayList<Spanned>();
		String query = "SELECT DISTINCT " + key + " FROM " + table;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(0);
				Spanned markedUp = Html.fromHtml(cursor.getString(0));
				list.add(markedUp);
			} while (cursor.moveToNext());
		}
		return list;
	}
	
	/*public static String loadFile(File f) {
		try { 
			String sortie="";
	        BufferedReader aLire= new BufferedReader(new FileReader(f));
	        String uneLigne = aLire.readLine();
	        do {
	        	sortie += uneLigne;
	        	
	        } while ((uneLigne = aLire.readLine())!=null);
	        aLire.close();
	        return sortie;
	     }
	      catch (IOException e) {  
	         System.out.println("Une operation sur les fichiers a leve l'exception "+e);
	         return("erreur pendant la lecture du fichier");
	     }
	   }*/
}