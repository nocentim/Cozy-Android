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
		String essai = "bonjour";
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
	
	public void addPref(String table, String key, String name) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(key,name);
		db.insert(table,null,value);
		db.close();
	}
	
	public ArrayList<String> getAllPref(String table, String key) {
		ArrayList<String> list = new ArrayList<String>();
		String query = "SELECT DISTINCT " + key + " FROM " + table;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query,null);
		
		if (cursor.moveToFirst()) {
			do {
				String name = cursor.getString(0);
				list.add(name);
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