package org.cozyAndroid.providers;

import java.util.HashMap;

import org.cozyAndroid.providers.TablesSQL.Dossiers;
import org.cozyAndroid.providers.TablesSQL.Notes;
import org.cozyAndroid.providers.TablesSQL.Suggestions;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.sax.StartElementListener;
import android.util.Log;

public class NotesProvider extends ContentProvider {
	 
	private static final String TAG = "NotesProvider";
    
    private SQLiteDatabase notesDB;
 
    private static final String CONTENT_PROVIDER_DB_NAME = "CozyAndroid_DB";
	 
    private static final int CONTENT_PROVIDER_DB_VERSION = 1;
	    
	private static final String NOTES_TABLE_NAME = "notes";
	
	private static final String DOSSIERS_TABLE_NAME = "dossiers";
	
	private static final String SUGGESTIONS_TABLE_NAME = "suggestions";
	 
    public static final String AUTHORITY = "org.cozyAndroid.providers.NotesProvider";
	 
    private static HashMap<String, String> notesProjectionMap;
    
    private static HashMap<String, String> dossiersProjectionMap;
    
    private static HashMap<String, String> suggestionsProjectionMap;
    
    private static final UriMatcher sUriMatcher;
	 
    private static final int NOTES = 1;
    
    private static final int DOSSIERS = 2;
    
    private static final int SUGGESTIONS = 3;
    
	// classe implémentant la base de données
    public static class DataBase extends SQLiteOpenHelper  {
    	
    	// A VOIR SI ON LE GARDE
    	    	
		/*private DataBase() {
			super(CozyAndroidActivity.getContext(), CONTENT_PROVIDER_DB_NAME, null, CONTENT_PROVIDER_DB_VERSION);
		}
		
		private static class DataBaseHolder { 
			public static final DataBase instance = new DataBase() {
			};
		}
			 
		public static DataBase getInstance() {
			return DataBaseHolder.instance;
		}*/
    	private DataBase(Context context) {
    		super(context, CONTENT_PROVIDER_DB_NAME, null, CONTENT_PROVIDER_DB_VERSION);
    	}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL("CREATE TABLE "+NOTES_TABLE_NAME+
					" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ Notes.TITLE + " VARCHAR NOT NULL COLLATE NOCASE, " + Notes.BODY + " VARCHAR NOT NULL COLLATE NOCASE, " + Notes.DOSSIER +" VARCHAR);");
			db.execSQL("CREATE TABLE "+DOSSIERS_TABLE_NAME+
					" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ Dossiers.NAME + " VARCHAR NOT NULL COLLATE NOCASE, " + Dossiers.PARENT + " VARCHAR);");
			db.execSQL("CREATE TABLE "+SUGGESTIONS_TABLE_NAME+
					" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+ Suggestions.WORD  + " VARCHAR NOT NULL COLLATE NOCASE, " + Suggestions.OCCURENCES  + " INTEGER NOT NULL);");
			/*File fichier = new File("src/exemple.txt");
			Log.d("ok","Chemin absolu du fichier : " + fichier.getAbsolutePath());
			Log.d("ok","Nom du fichier : " + fichier.getName());
			Log.d("ok","Est-ce qu'il existe ? " + fichier.exists());
			Log.d("ok","Est-ce un répertoire ? " + fichier.isDirectory());
			Log.d("ok","Est-ce un fichier ? " + fichier.isFile());
			//String essai = loadFile(fichier);
			String essai = "Note exemple, body: <div id=\"CNID_1\" class=\"Th-1\"><span>Un premier titre</span><br></div><div id=\"CNID_2\" class=\"Tu-2\"><span>Un paragraphe avec juste un titre</span><br></div><div id=\"CNID_3\" class=\"Tu-2\"><span>Un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long !</span><br></div><div id=\"CNID_4\" class=\"Tu-2\"><span>Un troisième paragraphe avec une liste en dessous :</span><br></div><div id=\"CNID_5\" class=\"Tu-3\"> <span>Premier paragraphe (1 titre seul)</span><br></div><div id=\"CNID_6\" class=\"Tu-3\"><span>Second paragraphe (1 titre &amp; une ligne)</span><br></div><div id=\"CNID_7\" class=\"l-3\"><span>Ligne du Second paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_8\" class=\"Tu-3\"><span>3ième paragraphe (1 titre &amp; 2 lignes)</span><br></div><div id=\"CNID_9\" class=\"l-3\"><span>Ligne 1 du 3ième paragraphe, pas longue.</span><br></div><div id=\"CNID_10\" class=\"l-3\"><span>Ligne 2 du 3ième paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span> <br></div><div id=\"CNID_11\" class=\"Tu-3\"><span>Quatrième paragraphe avec une sous liste :</span><br></div><div id=\"CNID_12\" class=\"Tu-4\"><span>Premier paragraphe</span><br></div><div id=\"CNID_13\" class=\"Tu-4\"><span>Second paragraphe</span><br></div><div id=\"CNID_14\" class=\"l-4\"><span>Ligne 1 du 2nd paragraphe, pas longue.</span><br></div><div id=\"CNID_15\" class=\"Tu-4\"><span>troisième paragraphe</span><br></div><div id=\"CNID_16\" class=\"Tu-4\"><span>Quatrième paragraphe</span><br></div><div id=\"CNID_17\" class=\"Th-1\"><span>Un titre de niveau 1</span><br></div><div id=\"CNID_18\" class=\"Th-2\"><span>Un titre de niveau 2</span><br></div><div id=\"CNID_19\" class=\"Tu-3\"><span>Un paragraphe un titre et deux lignes</span><br></div><div id=\"CNID_20\" class=\"l-3\"><span>Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_21\" class=\"l-3\"><span>Seconde ligne, pas très longue.</span><br></div><div id=\"CNID_22\" class=\"Th-2\"><span>Un titre de niveau 2</span><br></div><div id=\"CNID_23\" class=\"Tu-3\"><span>Un paragraphe avec juste un titre</span><br></div><div id=\"CNID_24\" class=\"Tu-3\"><span>Un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long, un second paragraphe avec un titre long !</span><br></div><div id=\"CNID_25\" class=\"Tu-3\"><span>Un troisième paragraphe avec une liste en dessous :</span><br></div><div id=\"CNID_26\" class=\"Tu-4\"><span>Premier paragraphe (1 titre seul)</span><br></div><div id=\"CNID_27\" class=\"Tu-4\"><span>Second paragraphe (1 titre &amp; une ligne)</span><br></div><div id=\"CNID_28\" class=\"l-4\"><span>Ligne du Second paragraphe (1 titre &amp; une ligne), longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs, Très longue ligne mais avec des variations de longueurs.</span><br></div><div id=\"CNID_29\" class=\"Tu-4\"><span></span><br></div><div id=\"CNID_30\" class=\"Tu-4\"><span>c'était un paragraphe vide :-)</span><br></div>";
			ContentValues valueNote = new ContentValues();
			valueNote.put("note",essai);
			db.insert(CONTENT_PROVIDER_TABLE_NAME,null,valueNote);*/
		}		
		
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + DOSSIERS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + SUGGESTIONS_TABLE_NAME);
			onCreate(db);
		}
    }
	
	/*public void addNote(String table, String key, String note) {
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
	}*/
	
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
    
    private DataBase dbHelper;    

	@Override
	public boolean onCreate() {
		dbHelper = new DataBase(getContext());
		notesDB = dbHelper.getWritableDatabase();
		return (notesDB == null)? false:true;
	}
	
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		// SQLiteDatabase db = dbHelper.getWritableDatabase(); A remettre a la place de notesDB en dessous??
		int count = 0;
		switch (sUriMatcher.match(uri)) {
			case NOTES:
				deleteSuggestionsWhere(where, whereArgs);
				count = notesDB.delete(NOTES_TABLE_NAME, where, whereArgs);
		        break;
			case DOSSIERS:
				count = notesDB.delete(DOSSIERS_TABLE_NAME, where, whereArgs);
				break;
			case SUGGESTIONS:
				count = notesDB.delete(SUGGESTIONS_TABLE_NAME, where, whereArgs);
				break;
	        default:
	        	throw new IllegalArgumentException("Unknown URI " + uri);
		}
			 
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
			case NOTES:
				return Notes.CONTENT_TYPE;
			case DOSSIERS:
				return Dossiers.CONTENT_TYPE;
			case SUGGESTIONS:
				return Suggestions.CONTENT_TYPE;
		    default:
		    	throw new IllegalArgumentException("Unknown URI " + uri);
		}
	}	
		   

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		ContentValues values;
		if (initialValues != null) {
			values = new ContentValues(initialValues);
		} else {
			values = new ContentValues();
		}
		String table;
		String nullCollumn;
		Uri contentUri;
		switch (sUriMatcher.match(uri)) {
			case NOTES :
				table = NOTES_TABLE_NAME;
				nullCollumn = Notes.BODY;
				contentUri = Notes.CONTENT_URI;
				insertSuggestions (values.getAsString(Notes.TITLE),1);
				break;
			case DOSSIERS :
				table = DOSSIERS_TABLE_NAME;
				nullCollumn = Dossiers.PARENT;
				contentUri = Dossiers.CONTENT_URI;
				break;
			case SUGGESTIONS :
				table = SUGGESTIONS_TABLE_NAME;
				nullCollumn = Suggestions.OCCURENCES;
				contentUri = Suggestions.CONTENT_URI;
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		long rowId = notesDB.insert(table, nullCollumn, values);
		if (rowId > 0) {
			// SQLiteDatabase db = dbHelper.getWritableDatabase();  // A remettre à la place de notesDB en dessous??
			Uri noteUri = ContentUris.withAppendedId(contentUri, rowId);
		    getContext().getContentResolver().notifyChange(noteUri, null);
		    return noteUri;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		String limit = null;
		switch (sUriMatcher.match(uri)) {
			case NOTES:
				qb.setTables(NOTES_TABLE_NAME);
		        qb.setProjectionMap(notesProjectionMap);
		        break;
			case DOSSIERS:
				qb.setTables(DOSSIERS_TABLE_NAME);
				qb.setProjectionMap(dossiersProjectionMap);
				break;
			case SUGGESTIONS:
				qb.setTables(SUGGESTIONS_TABLE_NAME);
				qb.setProjectionMap(suggestionsProjectionMap);
				limit = "4";
				break;
			default:
				throw new IllegalArgumentException("Unknown URI " + uri);
		}
		//SQLiteDatabase db = dbHelper.getReadableDatabase();     A remettre à la place de notesDB?
		Cursor c = qb.query(notesDB, projection, selection, selectionArgs, null, null, sortOrder,limit);
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return c;
	}

	@Override
	public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
		// SQLiteDatabase db = dbHelper.getWritableDatabase();  // A remettre a la place de notesDB en dessous??
		int count=0;
		switch (sUriMatcher.match(uri)) {
			case NOTES:
				if (values.containsKey(Notes.TITLE)) {
					deleteSuggestionsWhere(where,whereArgs);
				}
				count = notesDB.update(NOTES_TABLE_NAME, values, where, whereArgs);
				if (values.containsKey(Notes.TITLE)) {
					insertSuggestions(values.getAsString(Notes.TITLE),count);
				}
				break;
			case DOSSIERS:
				count = notesDB.update(DOSSIERS_TABLE_NAME, values, where, whereArgs);
				break;
			case SUGGESTIONS:
				count = notesDB.update(SUGGESTIONS_TABLE_NAME, values, where, whereArgs);
				break;
		    default:
		    	throw new IllegalArgumentException("Unknown URI " + uri);
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	private void insertSuggestions (String title, int occurences) {
		String [] mots = title.split(" +");
		Cursor cursor;
		String table = SUGGESTIONS_TABLE_NAME;
		String [] columns = {Suggestions._ID,Suggestions.OCCURENCES};
		for (int i = 0; i < mots.length; i++) {
			String mot = mots[i].toLowerCase();
			if (mot.length() > 3 ) {
				ContentValues values = new ContentValues();
				cursor = notesDB.query(table, columns, Suggestions.WORD + " = \'" + mot + "\'", null, null, null, null);
				if (cursor.moveToFirst()) {
					//Le mot existe deja dans la DB, on augmente son nombre d'occurences
					values.put(Suggestions.OCCURENCES, cursor.getInt(1) + occurences);
					notesDB.update(table, values, Suggestions._ID + " = " + cursor.getString(0), null);
				} else {
					//Le mot n'existe pas dans la DB, on l'insert
					values.put(Suggestions.WORD, mot);
					values.put(Suggestions.OCCURENCES, occurences);
					notesDB.insert(table, Suggestions.OCCURENCES, values);
				}
				cursor.close();
			}
		}
	}
	
	private void deleteSuggestionsWhere (String where, String [] whereArgs) {
		String[] columns = {Notes._ID,Notes.TITLE};
		Cursor notesToDelete = notesDB.query(NOTES_TABLE_NAME, columns, where, whereArgs, null, null, null);
		
		if (notesToDelete.moveToFirst()) {
			do {
				deleteSuggestions(notesToDelete.getString(1));
			} while (notesToDelete.moveToNext());
		}
		notesToDelete.close();
	}
	
	
	private void deleteSuggestions (String title) {
		String [] mots = title.split(" +");
		Cursor cursor = null;
		String table = SUGGESTIONS_TABLE_NAME;
		String [] columns = {Suggestions._ID,Suggestions.OCCURENCES};
		for (int i = 0; i < mots.length; i++) {
			String mot = mots[i].toLowerCase();
			if (mot.length() > 3 ) {
				cursor = notesDB.query(table, columns, Suggestions.WORD + " = \'" + mot + "\'", null, null, null, null);
				if (cursor.moveToFirst()) {
					if (cursor.getColumnCount() >= 2 && cursor.getInt(1) > 1) {
						ContentValues values = new ContentValues();
						values.put(Suggestions.OCCURENCES, cursor.getInt(1) - 1);
						notesDB.update(table, values, Suggestions._ID + " = " + cursor.getString(0), null);
					} else if (cursor.getColumnCount() >= 2 && cursor.getInt(1) == 1) {
						notesDB.delete(table, Suggestions._ID + " = " + cursor.getString(0), null);
					}
				}
				cursor.close();
			}
		} 
	}
	
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME, NOTES);
		sUriMatcher.addURI(AUTHORITY, DOSSIERS_TABLE_NAME, DOSSIERS);
		sUriMatcher.addURI(AUTHORITY, SUGGESTIONS_TABLE_NAME, SUGGESTIONS);
			 
		dossiersProjectionMap = new HashMap<String, String>();
		dossiersProjectionMap.put(Dossiers._ID, Dossiers._ID);
		dossiersProjectionMap.put(Dossiers.NAME, Dossiers.NAME);
		dossiersProjectionMap.put(Dossiers.PARENT, Dossiers.PARENT);
			
		notesProjectionMap = new HashMap<String, String>();
		notesProjectionMap.put(Notes._ID, Notes._ID);
		notesProjectionMap.put(Notes.TITLE, Notes.TITLE);
		notesProjectionMap.put(Notes.BODY, Notes.BODY);
		notesProjectionMap.put(Notes.DOSSIER, Notes.DOSSIER);
		
		suggestionsProjectionMap = new HashMap<String, String>();
		suggestionsProjectionMap.put(Suggestions._ID, Suggestions._ID);
		suggestionsProjectionMap.put(Suggestions.WORD, Suggestions.WORD);
		suggestionsProjectionMap.put(Suggestions.OCCURENCES, Suggestions.OCCURENCES);
		Log.d("ok","ok");
	}
}