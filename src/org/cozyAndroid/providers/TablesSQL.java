package org.cozyAndroid.providers;

import org.cozyAndroid.providers.NotesProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class TablesSQL {	 
	    public TablesSQL() {
	    }
	 
	    public static final class Notes implements BaseColumns {
	        private Notes() {
	        }
	 
	        public static final Uri CONTENT_URI = Uri.parse("content://"
	                + NotesProvider.AUTHORITY + "/notes");
	 
	        // que faut-il mettre ici???
	        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.Cozy-Android.notes";
	 
	        public static final String NOTE_ID = "_id";
	 
	        public static final String TITLE = "title";
	 
	        public static final String BODY = "text";
	        
	        public static final String DOSSIER = "dossier";
	    }
	    
	    public static final class Dossiers implements BaseColumns {
	    	private Dossiers() {
	    	}
	    	
	    	public static final Uri CONTENT_URI = Uri.parse("content://"
	                + NotesProvider.AUTHORITY + "/dossiers");
	    	
	    	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.Cozy-Android.dossiers";
	    	
	    	public static final String DOSSIER_ID = "_id";
	    	
	    	public static final String NAME = "name";
	    	
	    	public static final String PARENT = "parent";
	    	
	    }
	 
	}