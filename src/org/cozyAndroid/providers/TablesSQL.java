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
	    	
	    	public static final String NAME = "name";
	    	
	    	public static final String PARENT = "parent";
	    	
	    }
	    
	    public static final class Suggestions implements BaseColumns {
	    	private Suggestions() {
	    	}
	    	
	    	public static final Uri CONTENT_URI = Uri.parse("content://"
	                + NotesProvider.AUTHORITY + "/suggestions");
	    	
	    	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.Cozy-Android.suggestions";
	    	
	    	public static final String WORD = "word";
	    	
	    	public static final String OCCURENCES = "occurences";
	    	
	    	public static final int TYPE_MOT = 0;
	    	
	    	public static final int TYPE_NOTE = 1;
	    	
	    	public static final int TYPE_DOSSIER = 2;
	    	
	    	
	    	
	    	
	    }
	}