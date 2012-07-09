package org.cozyAndroid;

import android.database.Cursor;
import android.text.Html;
import android.text.Spanned;

public class Note {

	public int id;
	public String titre;
	public String body;
	//TODO tags
	
	public Note(Cursor c) {
		id = c.getInt(0);
		titre = c.getString(1);
		body = c.getString(2);
	}
	
	public Note(int id, String titre, String body) {
		this.id = id;
		this.titre = titre;
		this.body = body;
	}
	
	public Spanned getSpannedBody() {
		//TODO a faire mieux
		return Html.fromHtml(body);
	}
	
}
