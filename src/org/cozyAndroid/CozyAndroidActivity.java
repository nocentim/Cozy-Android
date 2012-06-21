package org.cozyAndroid;


import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;

public class CozyAndroidActivity extends TabActivity {
    /** Called when the activity is first created. */
	private static TabHost tabHost;
	private int [] layoutTab;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tabHost = getTabHost();
		layoutTab = new int[5];
		layoutTab[0] = R.layout.tab_liste;
		layoutTab[1] = R.layout.tab_calendrier;
		layoutTab[2] = R.layout.tab_plus;
		layoutTab[3] = R.layout.tab_tags;
		layoutTab[4] = R.layout.tab_recherche;
		
		setupTab("TabListe", new Intent().setClass(this, TabListe.class),0);
        setupTab("TabCalendrier", new Intent().setClass(this, TabCalendrier.class),1);
        setupTab("TabPlus", new Intent().setClass(this, TabPlus.class),2);
		setupTab("TabTags", new Intent().setClass(this, TabTags.class),3);
		setupTab("TabRecherche", new Intent().setClass(this, TabRecherche.class),4);
    }
    
    private void setupTab(String tag, Intent intent, int layoutTabIndex) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator( createTabView(tabHost.getContext(), layoutTabIndex)).setContent(intent));
	}
	 
	// créé la vue associée à l'onglet considéré
	private View createTabView(final Context context, int layoutTabIndex) {
		View view = LayoutInflater.from(context).inflate(layoutTab[layoutTabIndex], null);
		view.refreshDrawableState();
		//view.setBackgroundResource(R.color.Ensimag);
		return view;
	}
}