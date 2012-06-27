package org.cozyAndroid.providers;

import org.cozyAndroid.providers.NotesProvider;

import android.net.Uri;
import android.provider.BaseColumns;

public class NoteSQL {	 
	    public NoteSQL() {
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
	    }
	 
	}