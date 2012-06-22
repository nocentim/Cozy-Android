package org.cozyAndroid;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {	
	private static String TABLE_NOTES = "notes";
	
	DataBase(Context context) {		
		super(context, "GCM_DB", null, 2);
	}
	
	/*private static class DataBaseHolder { 
		public static final DataBase instance = new DataBase() {
		};
	}
		 
	public static DataBase getInstance() {
		return DataBaseHolder.instance;
	}*/

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("CREATE TABLE "+TABLE_NOTES+
				" (id0 INTEGER PRIMARY KEY AUTOINCREMENT, note VARCHAR NOT NULL);");
		File fichier = new File("exemple_note");
		String essai = loadFile(fichier);
		ContentValues valueNote = new ContentValues();
		valueNote.put("note",essai);
		db.insert(TABLE_NOTES,null,valueNote);
	}		
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
		onCreate(db);
		
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
	
	public static String loadFile(File f) {
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
	   }
}