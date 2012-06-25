package org.cozyAndroid;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	LayoutInflater inflater;
	 
	public NoteAdapter (Context context) {
		inflater = LayoutInflater.from(context);
	}
	
	public int getCount() {
		//return getList().size();
		return 2;
	}

	public Object getItem(int position) {
		//return getList().get(position);
		return "Note numero " + position;
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
