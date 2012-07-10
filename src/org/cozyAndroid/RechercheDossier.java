package org.cozyAndroid;

import org.cozyAndroid.providers.TablesSQL.Dossiers;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class RechercheDossier extends AutoCompleteTextView {

	private TabDossier context;
	
	private Cursor searchCursor = null;
	
	private SimpleCursorAdapter searchAdapter;
	
	private String filterPattern = "";
	
	//Action a effectuer quand on clique sur un element de la liste
	private OnItemClickListener itemClick = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Cursor c = (Cursor)searchAdapter.getItem(position);
			context.ouvreDossier(Dossier.getDossierParId(c.getInt(0)));
		}
	};
	
	//Creation de la string a afficher du la TextView a partir du cursor
	private CursorToStringConverter converter = new CursorToStringConverter() {
		
		public CharSequence convertToString(Cursor cursor) {
			Dossier temp = Dossier.getDossierParId(cursor.getInt(0));
			return temp.getPathComplet();
		}
	};
	
	//
	private ViewBinder viewBinder = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			Dossier temp = Dossier.getDossierParId(cursor.getInt(columnIndex));
			if (temp == null) {
				return false;
			}
			String text = temp.getPathComplet();
			int start = text.toLowerCase().indexOf(filterPattern);
			if (start == -1) {
				((TextView) view).setText(text);
				return true;	
			}
			int end = start + filterPattern.length();
			Spannable textSpan = new SpannableString(text);
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.darker_gray)), 0, textSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((TextView) view).setText(textSpan);
			return true;
		}
	};
	
	//Filtrage des resultats suivant la string rentree par l'utilisateur
	private FilterQueryProvider filterQuery = new FilterQueryProvider() {
		//TODO : essayer de faire ca avec un seul acces a la BD
		public Cursor runQuery(CharSequence constraint) {
			String pattern = constraint.toString().toLowerCase();
			filterPattern = pattern;
			String[] projection = {Dossiers.DOSSIER_ID,Dossiers.NAME};
			Cursor filtered = context.managedQuery(Dossiers.CONTENT_URI, projection, null, null, null);
			String selection = Dossiers.DOSSIER_ID + " IN (";
			if (filtered.moveToFirst()) {
				Boolean first = true;
				do {
					Dossier temp = Dossier.getDossierParId(filtered.getInt(0));
					String path = temp.getPathComplet();
					path = path.toLowerCase();
					if (path.startsWith(pattern)
						|| path.matches(".*/" + pattern + ".*")) {
						if (!first) {
							selection += ",";
						}
						selection += temp.getId();
						first = false;
					}
				} while (filtered.moveToNext());
				selection += ")";
				filtered = context.managedQuery(Dossiers.CONTENT_URI, projection, selection, null, null);
			}
			return filtered;
		}
	};
	
	public RechercheDossier(Context context) {
		super(context);
	}
	
	public RechercheDossier(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public RechercheDossier(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Initialise l'adapter, les listeners et tout ce qu'il faut pour les suggestions
	 * @param context L'onglet de gestion des dossiers
	 */
	public void init(TabDossier context) {
		this.context = context;
		searchAdapter = new SimpleCursorAdapter(
				context, android.R.layout.simple_dropdown_item_1line,
				searchCursor, new String [] {Dossiers.DOSSIER_ID},
				new int [] {android.R.id.text1});
		setAdapter(searchAdapter);
		setOnItemClickListener(itemClick);
		searchAdapter.setFilterQueryProvider(filterQuery);
		searchAdapter.setCursorToStringConverter(converter);
		searchAdapter.setViewBinder(viewBinder);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER &&
				getListSelection() == ListView.INVALID_POSITION) {
			setListSelection(0);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}
