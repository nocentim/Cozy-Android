package org.cozyAndroid;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private ArrayList<String> liste;
	
	public NoteAdapter (Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public void setListe (ArrayList<String> l) {
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
		titre.setText((String)getItem(position));
		return convertView;			 
	}

}
