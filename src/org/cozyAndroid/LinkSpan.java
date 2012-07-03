package org.cozyAndroid;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Permet de mettre des liens dans une TextView
 * qui sont gérés en interne dans l'application
 * (les liens sont normalement vers une page web)
 */
public class LinkSpan extends ClickableSpan {

	private OnClickListener mListener;

    public LinkSpan(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View widget) {
       if (mListener != null) mListener.onClick();
    }

    public interface OnClickListener {
        void onClick();
    }

    /**
     * Ajoute un lien dans une textview
     * @param view la TextVFiew a modifier
     * @param clickableText la partie du texte a transformer en lien
     * @param listener l'action a effectuer quand on clique sur le lien
     */
    public static void linkify(TextView view, final String clickableText, 
    	    final OnClickListener listener) {

    	    CharSequence text = view.getText();
    	    String string = text.toString();
    	    LinkSpan span = new LinkSpan(listener);

    	    int start = string.indexOf(clickableText);
    	    int end = start + clickableText.length();
    	    if (start == -1) return;

    	    if (text instanceof Spannable) {
    	        ((Spannable)text).setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	    } else {
    	        SpannableString s = SpannableString.valueOf(text);
    	        s.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    	        view.setText(s);
    	    }

    	    MovementMethod m = view.getMovementMethod();
    	    if ((m == null) || !(m instanceof LinkMovementMethod)) {
    	        view.setMovementMethod(LinkMovementMethod.getInstance());
    	    }
    	}

}
