package org.cozyAndroid;


import java.util.ArrayList;

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
	private ArrayList<Note> liste;
	TabListe context;
	
	public NoteAdapter (TabListe context) {
		inflater = LayoutInflater.from(context);
		this.context = context;
		
	}
	
	public void setListe (ArrayList<Note> l) {
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
		TextView titreView = (TextView)convertView.findViewById(R.id.titre_note);
		TextView bodyView = (TextView)convertView.findViewById(R.id.body_note);
		Note n = (Note)getItem(position);
		String titre = n.titre;
		String body = n.getSpannedBody().toString().replace("\n", " ");
		if (titre.length() > 35) {
			titreView.setText(titre.substring(0, 34));
		} else {
			titreView.setText(titre);
		}
		if (body.length() > 50) {
			bodyView.setText(body.substring(0, 49));
		} else {
			bodyView.setText(body);
		}
		
		convertView.setOnClickListener(new EditListener(n));
		
		return convertView;	
	}
	
	private class EditListener implements OnClickListener {

		Note note;
		
		public EditListener(Note note) {
			this.note = note;
		}
		
		public void onClick(View v) {
			Intent editer = new Intent(context, Edition.class);
			editer.putExtra("id", note.id);
			editer.putExtra("titre", note.titre);
			editer.putExtra("body", note.body);
	    	context.startActivity(editer);
		}
		
	}

}
