package org.cozyAndroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.cozyAndroid.providers.TablesSQL.Dossiers;
import org.cozyAndroid.providers.TablesSQL.Notes;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FilterQueryProvider;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class RechercheNote extends MultiAutoCompleteTextView {

	public static final String SUGGESTION_ID = "_id";
	
	public static final String SUGGESTION = "suggestion";
	
	private Activity context;
	
	private Cursor searchCursor = null;
	
	private SimpleCursorAdapter searchAdapter;
	
	private String filterPattern = "";
	
	public RechercheNote(Context context) {
		super(context);
		init((Activity) context);
	}
	
	public RechercheNote(Context context, AttributeSet attrs) {
		super(context, attrs);
		init((Activity) context);
	}
	
	public RechercheNote(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init((Activity) context);
	}
	
	//Creation de la string a afficher de la TextView a partir du cursor
	private CursorToStringConverter converter = new CursorToStringConverter() {
		
		public CharSequence convertToString(Cursor cursor) {
			return cursor.getString(1);
		}
	};
	
	//
	private ViewBinder viewBinder = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			String mot = cursor.getString(columnIndex);
			int start = filterPattern.length();
			Spannable textSpan = new SpannableString(mot);
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.darker_gray)), start, textSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			((TextView) view).setText(textSpan);
			((TextView) view).setEllipsize(TruncateAt.MIDDLE);
			return true;
		}
	};
	
	//Filtrage des resultats suivant la string rentree par l'utilisateur
	private FilterQueryProvider filterQuery = new FilterQueryProvider() {
		//TODO : essayer de faire ca avec un seul acces a la BD
		public Cursor runQuery(CharSequence constraint) {
			String pattern = constraint.toString().toLowerCase();
			filterPattern = pattern;
			String[] projection = {Notes.NOTE_ID,Notes.TITLE};
			Cursor all = context.managedQuery(Notes.CONTENT_URI, projection, null, null, null);
			String[] proj = {SUGGESTION_ID, SUGGESTION};
			//TODO tout ca est tres laid : faire mieux
			HashMap <String,Integer> motsConnus = new HashMap<String, Integer>();
			if (all.moveToFirst()) {
				do {
					String title = all.getString(1).toLowerCase();
					int start, end;
					while (!title.equals("")) {
						if (title.startsWith(pattern)) {
							start = 0;
						} else if (title.matches(".* " + pattern + ".*")) {
							start = title.indexOf(" " + pattern);
						} else {
							continue;
						}
						for (end = start; end < title.length() && title.charAt(end) != ' '; end ++);
						String mot = title.substring(start, end);
						Integer nombreOccurences = motsConnus.put(mot, 1);
						if (nombreOccurences != null) {
							//Si le mot était déja dans la map
							motsConnus.put(mot,nombreOccurences + 1);
						}
						title = title.substring(end);
					}
				} while (all.moveToNext());
			}
			List<Entry<String, Integer>> entries = new ArrayList<Entry<String, Integer>>(motsConnus.entrySet());
			 
			// Tri de la liste selon le nombre d'occurences
			Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
				public int compare(final Entry<String, Integer> e1, final Entry<String, Integer> e2) {
					return e1.getValue().compareTo(e2.getValue());
				}
			});
			MatrixCursor filtered = new MatrixCursor(proj);
			for (int i = 0; i < entries.size(); i++) {
				Object[] mot = {i,entries.get(i).getKey()};
				filtered.addRow(mot);
			}
			return filtered;
		}
	};
	
	/**
	 * Initialise l'adapter, les listeners et tout ce qu'il faut pour les suggestions
	 * @param context L'activite
	 */
	private void init(Activity context) {
		this.context = context;
		setThreshold(1);
		searchAdapter = new SimpleCursorAdapter(
				context, android.R.layout.simple_dropdown_item_1line,
				searchCursor, new String [] {SUGGESTION},
				new int [] {android.R.id.text1});
		setAdapter(searchAdapter);
		searchAdapter.setFilterQueryProvider(filterQuery);
		searchAdapter.setCursorToStringConverter(converter);
		searchAdapter.setViewBinder(viewBinder);
		Tokenizer space = new SpaceTokenizer();
		setTokenizer(space);
	}
	
	/**
     * Ce Tokenizer est utilise pour les listes avec des
     * elements separes par des espaces
     */
    public static class SpaceTokenizer implements Tokenizer {
        public int findTokenStart(CharSequence text, int cursor) {
            int i = cursor;

            while (i > 0 && text.charAt(i - 1) != ' ') {
                i--;
            }

            return i;
        }

        public int findTokenEnd(CharSequence text, int cursor) {
            int i = cursor;
            int len = text.length();

            while (i < len) {
                if (text.charAt(i) == ' ') {
                    return i;
                } else {
                    i++;
                }
            }

            return len;
        }

        public CharSequence terminateToken(CharSequence text) {
            int i = text.length();

            if (i > 0 && text.charAt(i - 1) == ' ') {
                return text;
            } else {
                if (text instanceof Spanned) {
                    SpannableString sp = new SpannableString(text + " ");
                    TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
                                            Object.class, sp, 0);
                    return sp;
                } else {
                    return text + " ";
                }
            }
        }
    }
}
