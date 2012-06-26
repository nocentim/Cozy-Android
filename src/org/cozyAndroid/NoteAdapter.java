package org.cozyAndroid;


import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<Spanned> liste;
	TabListe context;
	
	public NoteAdapter (TabListe context) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		
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
		Spanned noteSpanned = (Spanned)getItem(position);
		String note = noteSpanned.toString();
		//String note = (String)getItem(position);
		String contenu [] = note.split(", body: ");
		if (contenu[0].length() > 35) {
			titre.setText(contenu[0].substring(0, 34));
		} else {
			titre.setText(contenu[0]);
		}
		if (contenu.length >= 2) {
			String aux;
			if (contenu[1].length() > 50) {
				aux = contenu[1].substring(0,49);
			} else {
				aux = contenu[1];
			}
			chemin.setText(aux.replace("\n", " "));
			convertView.setOnClickListener(new EditListener(contenu[0], contenu[1]));
		} else {
			chemin.setText("");
			convertView.setOnClickListener(new EditListener(contenu[0], ""));
		}
		
		return convertView;	
	}
	
	private class EditListener implements OnClickListener {

		String titre;
		String texte;
		
		public EditListener(String titre, String texte) {
			this.titre = titre;
			this.texte = texte;
		}
		
		public void onClick(View v) {
			/*Intent editer = new Intent(context, Edition.class);
			editer.putExtra("name", titre);
			editer.putExtra("body", texte);
	    	context.startActivity(editer);*/
			context.modeEdition(titre,texte);
		}
		
	}

}
