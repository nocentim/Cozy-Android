package org.cozyAndroid;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DossierAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private Dossier courant;
	TabDossier context;
	
	public DossierAdapter (TabDossier context, Dossier courant) {
		inflater = LayoutInflater.from(context);
		this.courant = courant;
		this.context = context;
		
	}
	
	public void setDossier (Dossier courant) {
		this.courant = courant;
	}
	
	public int getCount() {
		return courant.size();
	}

	public Object getItem(int position) {
		int nbDossiers = courant.nbDossiers();
		if (position < nbDossiers) {
			return courant.getSousDossiers().get(position);
		} else {
			return courant.getNotes().get(position - nbDossiers);
		}
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
		ImageView icone = (ImageView)convertView.findViewById(R.id.icone);
		if (position < courant.nbDossiers()) {
			// C'est un dossier :
			// on affiche son nom et ce qu'il y a dedans
			Dossier d = (Dossier) getItem(position);
			titreView.setText(d.nom);
			bodyView.setText(d.getInfos());
			icone.setImageResource(R.drawable.folder);
			convertView.setOnClickListener(new DossierListener(d));
		} else {
			// C'est une note :
			// on affiche son titre et ses premiers mots
			Note n = (Note) getItem(position);
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
			icone.setImageResource(R.drawable.note);
			convertView.setOnClickListener(new EditListener(n));
		}
		
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
	
	private class DossierListener implements OnClickListener {

		Dossier d;
		
		public DossierListener(Dossier d) {
			this.d = d;
		}
		
		public void onClick(View v) {
			context.ouvreDossier(d);
		}
		
	}

}
