package org.cozyAndroid;

import java.util.ArrayList;

import org.cozyAndroid.providers.TablesSQL.Notes;
import org.cozyAndroid.providers.TablesSQL.Suggestions;
import org.ektorp.ViewQuery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

public class RechercheNote extends MultiAutoCompleteTextView {
	
	private static final String _ID = "_id";
	
	private static final String SUGGESTION = "suggestion";
	
	private static final String SOURCE = "source";
	
	private static final String TYPE = "type";
	
	private static final int TYPE_MOT = 0;
	
	private static final int TYPE_NOTE = 1;
	
	private static final int TYPE_DOSSIER = 2;
	
	private Activity context;
	
	private CozySyncListAdapter searchAdapter;
	
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
	
	/*private ViewBinder viewBinder = new ViewBinder() {
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == 1) {
				//On met le texte dans la textview
				String text = cursor.getString(columnIndex);
				Spannable textSpan = new SpannableString(text);
				String [] pattern;
				switch (cursor.getInt(3)) {
				case TYPE_NOTE : 
				case TYPE_DOSSIER :
					// tout le texte de recherche nous interesse
					pattern = getText().toString().split(" +");
					break;
				default :
					//Seulement le dernier mot nous interesse
					pattern = new String [] {filterPattern};
				}
				for (int i = 0; i < pattern.length; i++) {
					int start = text.toLowerCase().indexOf(pattern[i].toLowerCase());
					if (start != -1) {
						ForegroundColorSpan black = new ForegroundColorSpan(getResources().getColor(android.R.color.black));
						textSpan.setSpan(black, start, start + pattern[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
				}
				((TextView) view).setText(textSpan);
				return true;
			} else if (columnIndex == 2) {
				//On affiche ou pas le bouton d'acces rapide
				switch (cursor.getInt(3)) {
				case TYPE_MOT :
					view.setVisibility(GONE);
					break;
				case TYPE_NOTE :
					view.setVisibility(VISIBLE);
					((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.note));
					final int noteId = cursor.getInt(columnIndex);
					final String titre = cursor.getString(1);
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d("rechercheNote", "bouton note cliqué");
							String [] projection = {Notes.BODY};
							Cursor c = context.managedQuery(Notes.CONTENT_URI, projection, Notes._ID + " = " + noteId, null, null);
							if (c.moveToFirst()) {
								Intent editer = new Intent(context, Edition.class);
								editer.putExtra("id", noteId);
								editer.putExtra("titre", titre);
								editer.putExtra("body", c.getString(0));
						    	context.startActivity(editer);
							}
						}
					});
					break;
				case TYPE_DOSSIER :
					view.setVisibility(VISIBLE);
					((ImageView)view).setImageDrawable(getResources().getDrawable(R.drawable.folder));
					final int dossierId = cursor.getInt(columnIndex);
					view.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Log.d("rechercheNote", "bouton dossier cliqué");
							Intent ouvreDossier = new Intent(context, CozyAndroidActivity.class);
							ouvreDossier.addCategory("android.intent.category.LAUNCHER");
							ouvreDossier.putExtra("ouvreDossier",dossierId);
							context.startActivity(ouvreDossier);
						}
					});
					break;
				default:
					return false;
				}
				return true;
			}
			return false;	
		}
	};*/
	
	//Filtrage des resultats suivant la string rentree par l'utilisateur
	/*private FilterQueryProvider filterQuery = new FilterQueryProvider() {
		//TODO : essayer de faire ca avec un seul acces a la BD
		public Cursor runQuery(CharSequence constraint) {
			if (constraint == null || constraint.equals("")) {
				return null;
			}
			// Suggestions de mots (contenus dans les titres et corps de notes)
			String pattern = constraint.toString();
			filterPattern = pattern;
			String[] projection = {Suggestions._ID,Suggestions.WORD};
			String selection = Suggestions.WORD + " LIKE \'" + pattern + "%\'";
			Cursor cursor = context.managedQuery(Suggestions.CONTENT_URI, projection, selection, null, Suggestions.OCCURENCES + " DESC");
			String [] proj = {_ID,SUGGESTION,SOURCE,TYPE};
			MatrixCursor filtered = new MatrixCursor(proj);
			int id =0;
			if (cursor.moveToFirst()) {
				do {
					id++;
					Object[] row = {id,cursor.getString(1),cursor.getString(0),TYPE_MOT};
					filtered.addRow(row);
				} while (cursor.moveToNext());
			}
			//Suggestions de titre de notes
			String[] fullPattern = getText().toString().split(" +");
			selection = "";
			for (int i = 0; i + 1 < fullPattern.length; i++) {
				selection += "(" + Notes.TITLE + " LIKE \'" + fullPattern[i] + "%\' OR " + Notes.TITLE + " LIKE \'% " + fullPattern[i] +"%\') AND ";
			}
			if (fullPattern.length > 0) {
				int last = fullPattern.length - 1;
				selection += "(" + Notes.TITLE + " LIKE \'" + fullPattern[last] + "%\' OR " + Notes.TITLE + " LIKE \'% " + fullPattern[last] +"%\')";
			}
			projection = new String [] {Notes._ID,Notes.TITLE};
			cursor = context.managedQuery(Notes.CONTENT_URI, projection, selection, null, null);
			int count = 0;
			if (cursor.moveToFirst()) {
				do {
					id++;
					Object[] row = {id,cursor.getString(1),cursor.getString(0),TYPE_NOTE};
					filtered.addRow(row);
					count++;
				} while (cursor.moveToNext() && count < 2);
			}
			// Suggestions de dossiers
			ArrayList <Dossier> dossiers = Dossier.getSuggestions(fullPattern);
			count = 0;
			for (int i = 0; i < dossiers.size() && count < 2; i++, count ++) {
				id++;
				Dossier d = dossiers.get(i);
				Object[] row = {id, d.nom,d.getId(),TYPE_DOSSIER};
				filtered.addRow(row);
			}
			return filtered;
		}
	};*/
	
	/**
	 * Initialise l'adapter, les listeners et tout ce qu'il faut pour les suggestions
	 * @param context L'activite
	 */
	private void init(Activity context) {
		this.context = context;
		setThreshold(1);
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
        	if (text == null) {
        		return "";
        	}
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
