package org.cozyAndroid;


import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
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
		String essai = readFile("./exemple_note");
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
	
	public static String readFile ( String theFileName ){
        InputStreamReader flog	= null;
	LineNumberReader llog	= null;
	String myLine		     = null;
        String myConcatLines     = "";
	try{ 
		flog = new InputStreamReader(new FileInputStream(theFileName) );
		llog = new LineNumberReader(flog);
		while ((myLine = llog.readLine()) != null) { 
                      // --- Ajout de la ligne au contenu
                      myConcatLines += myLine;
                }
        }catch (Exception e){
               // --- Gestion erreur lecture du fichier (fichier non existant, illisible, etc.)
               System.err.println("Error : "+e.getMessage());
               return null;
        }
        return myConcatLines;
}
}