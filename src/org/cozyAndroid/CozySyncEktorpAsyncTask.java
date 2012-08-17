package org.cozyAndroid;

import org.cozyAndroid.ListeNotes.TabListe;
import org.ektorp.DbAccessException;
import org.ektorp.android.util.EktorpAsyncTask;

import android.util.Log;

public abstract class CozySyncEktorpAsyncTask extends EktorpAsyncTask {

	@Override
	protected void onDbAccessException(DbAccessException dbAccessException) {
		Log.e(TabListe.TAG, "DbAccessException in background", dbAccessException);
	}

}
