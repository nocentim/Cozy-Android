package org.cozyAndroid;

import org.cozyAndroid.providers.TablesSQL.Dossiers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class RechercheNote extends MultiAutoCompleteTextView {

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
	
	//Creation de la string a afficher du la TextView a partir du cursor
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
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.darker_gray)), 0, textSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			textSpan.setSpan(new ForegroundColorSpan(getResources().getColor(android.R.color.black)), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
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
			String[] projection = {Dossiers.DOSSIER_ID,Dossiers.NAME};
			Cursor filtered = context.managedQuery(Dossiers.CONTENT_URI, projection, null, null, null);
			String selection = Dossiers.DOSSIER_ID + " IN (";
			if (filtered.moveToFirst()) {
				Boolean first = true;
				do {
					Dossier temp = Dossier.getDossierParId(filtered.getInt(0));
					String path = temp.getCheminComplet();
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
	
	/**
	 * Initialise l'adapter, les listeners et tout ce qu'il faut pour les suggestions
	 * @param context L'activite
	 */
	private void init(Activity context) {
		this.context = context;
		setThreshold(1);
		searchAdapter = new SimpleCursorAdapter(
				context, android.R.layout.simple_dropdown_item_1line,
				searchCursor, new String [] {Dossiers.DOSSIER_ID},
				new int [] {android.R.id.text1});
		setAdapter(searchAdapter);
		searchAdapter.setFilterQueryProvider(filterQuery);
		searchAdapter.setCursorToStringConverter(converter);
		searchAdapter.setViewBinder(viewBinder);
		Tokenizer space = new SpaceTokenizer();
		setTokenizer(space);
	}
	
	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER &&
				getListSelection() == ListView.INVALID_POSITION) {
			setListSelection(0);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	
	/**
     * Ce Tokenizer est utilisé pour les listes avec des
     * elements séparés par des espaces
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
