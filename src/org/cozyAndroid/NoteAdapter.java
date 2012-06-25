package org.cozyAndroid;


import java.util.ArrayList;

import android.content.Context;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<Spanned> liste;
	
	public NoteAdapter (Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public void setListe (ArrayList<Spanned> l) {
		liste = l;
	}
	
	public int getCount() {
		if (liste == null) {
			return 0;
		}
		return liste.size();
	}

	public Object getItem(int position) {
		return liste.get(position);
	}

	public long getItemId(int position) {
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
			 
		if(convertView == null) {
			convertView = inflater.inflate(R.layout.elem_list, null);
		}
		TextView titre = (TextView)convertView.findViewById(R.id.titre_note);
		TextView chemin = (TextView)convertView.findViewById(R.id.chemin_note);
		Spanned note = (Spanned)getItem(position);
		titre.setText(note.subSequence(0, 20));
		
		// ---- VERSION QUI FONCTIONNE -----
		/*String note = (String)getItem(position);
		String contenu [] = note.split(", body: ");
		titre.setText(contenu[0]);
		if (contenu.length >= 2) {
			if (contenu[1].length() > 20) {
				chemin.setText(contenu[1].substring(0,20));
			} else
				chemin.setText(contenu[1]);
		} else {
			chemin.setText("");
		}*/
		
		return convertView;	
	}

}
