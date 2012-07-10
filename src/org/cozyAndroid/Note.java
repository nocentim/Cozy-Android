package org.cozyAndroid;

import android.database.Cursor;
import android.text.Html;
import android.text.Spanned;

public class Note {

	public int id;
	public String titre;
	public String body;
	public int idDossier;
	//TODO tags
	
	public Note(Cursor c) {
		id = c.getInt(0);
		titre = c.getString(1);
		body = c.getString(2);
		if (c.getColumnCount() >= 4) {
			idDossier = c.getInt(3);
		}
	}
	
	public Note(int id, String titre, String body, int idDossier) {
		this.id = id;
		this.titre = titre;
		this.body = body;
		this.idDossier = idDossier;
	}
	
	public Spanned getSpannedBody() {
		//TODO a faire mieux
		return Html.fromHtml(body);
	}
	
}
