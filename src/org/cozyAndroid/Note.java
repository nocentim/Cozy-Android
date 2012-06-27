package org.cozyAndroid;

import android.database.Cursor;
import android.text.Html;
import android.text.Spanned;

public class Note {

	public String id;
	public String titre;
	public String body;
	//TODO tags
	
	public Note(Cursor c) {
		id = c.getString(0);
		titre = c.getString(1);
		body = c.getString(2);
	}
	
	public Note(String id, String titre, String body) {
		this.id = id;
		this.titre = titre;
		this.body = body;
	}
	
	public Spanned getSpannedBody() {
		return Html.fromHtml(body);
	}
	
}
