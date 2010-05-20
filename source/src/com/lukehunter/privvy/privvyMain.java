package com.lukehunter.privvy;

import android.app.ExpandableListActivity;
import android.os.Bundle;

public class privvyMain extends ExpandableListActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}