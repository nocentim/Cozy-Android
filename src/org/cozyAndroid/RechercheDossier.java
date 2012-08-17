package org.cozyAndroid;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class RechercheDossier extends AutoCompleteTextView {

	
	private Activity context;
	
	private Cursor searchCursor = null;
	
	private SimpleCursorAdapter searchAdapter;
	
	private String filterPattern = "";
	
	public RechercheDossier(Context context) {
		super(context);
		//init((Activity) context);
	}
	
	public RechercheDossier(Context context, AttributeSet attrs) {
		super(context, attrs);
		//init((Activity) context);
	}
	
	public RechercheDossier(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		//init((Activity) context);
	}
	
	//Creation de la string a afficher de la TextView a partir du cursor
	private CursorToStringConverter converter = new CursorToStringConverter() {
		
		public CharSequence convertToString(Cursor cursor) {
			Dossier temp = Dossier.getDossierParId(cursor.getInt(0));
			return temp.getCheminComplet();
		}
	};
	
	//
	private ViewBinder viewBinder = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			Dossier temp = Dossier.getDossierParId(cursor.getInt(columnIndex));
			if (temp == null) {
				return false;
			}
			String text = temp.getCheminComplet();
			int start;
			if (text.toLowerCase().indexOf(filterPattern) == 0) {
				start = 0;
			} else
				start = text.toLowerCase().indexOf("/" + filterPattern) + 1;
			if (start == -1) {
				((TextView) view).setText(text);
				return true;	
			}
			int end = start + filterPattern.length();
			Spannable textSpan = new SpannableString(text);
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((TextView) view).setText(textSpan);
			return true;
		}
	};
	
	//Filtrage des resultats suivant la string rentree par l'utilisateur
	private FilterQueryProvider filterQuery = new FilterQueryProvider() {
		//TODO : essayer de faire ca avec un seul acces a la BD
		public Cursor runQuery(CharSequence constraint) {
			if (constraint == null || constraint.equals("")) {
				return null;
			}
			/*String pattern = constraint.toString().toLowerCase();
			filterPattern = pattern;
			String[] projection = {Dossiers._ID};
			Cursor all = context.managedQuery(Dossiers.CONTENT_URI, projection, null, null, null);
			MatrixCursor filtered = new MatrixCursor(projection);
			if (all.moveToFirst()) {
				do {
					Dossier temp = Dossier.getDossierParId(all.getInt(0));
					String path = temp.getCheminComplet();
					path = path.toLowerCase();
					if (path.startsWith(pattern)
						|| path.matches(".*//*" + pattern + ".*")) {
						Object[] id = {all.getInt(0)};
						filtered.addRow(id);
					}
				} while (all.moveToNext());
			}
			return filtered;*/
			return null;
		}
	};
	
	/**
	 * Initialise l'adapter, les listeners et tout ce qu'il faut pour les suggestions
	 * Il faut impérativement l'appeler avec d'utiliser la recherche
	 * @param context L'onglet de gestion des dossiers
	 */
	/*private void init(Activity context) {
		this.context = context;
		setThreshold(1);
		searchAdapter = new SimpleCursorAdapter(
				context, R.layout.suggestion,
				searchCursor, new String [] {Dossiers._ID},
				new int [] {R.id.textSuggestion});
		setAdapter(searchAdapter);
		searchAdapter.setFilterQueryProvider(filterQuery);
		searchAdapter.setCursorToStringConverter(converter);
		searchAdapter.setViewBinder(viewBinder);
		setValidator(new DossierValidator());
		
	}*/
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER &&
				getListSelection() == ListView.INVALID_POSITION) {
			setListSelection(0);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private class DossierValidator implements Validator {

		public CharSequence fixText(CharSequence invalidText) {
			// TODO Auto-generated method stub
			Log.w("DossierValidator","Appel a fixText (ne fait rien)");
			return invalidText;
		}

		public boolean isValid(CharSequence text) {
			Dossier d = Dossier.getDossierParChemin(text.toString().trim());
			return d != null;
		}
		
	}
	
}
