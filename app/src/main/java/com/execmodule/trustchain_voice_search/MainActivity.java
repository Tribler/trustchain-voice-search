package com.execmodule.trustchain_voice_search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout llcont = (LinearLayout) findViewById(R.id.llcontainer);

        LinearLayout ll = new LinearLayout(getApplicationContext());
        int tmpid = 1;
        ll.setId(tmpid);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        Fragment frag = new MainFragment();

        transaction.add(ll.getId(), frag, "fragment0");
        transaction.commit();

        llcont.addView(ll);
    }
}
