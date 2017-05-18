package com.example.onur.firebaseexample.Activity;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.onur.firebaseexample.Fragment.OneFragment;
import com.example.onur.firebaseexample.Fragment.ThreeFragment;
import com.example.onur.firebaseexample.Fragment.TwoFragment;
import com.example.onur.firebaseexample.Gps.GPSTracker;
import com.example.onur.firebaseexample.Gps.StartReceiver;
import com.example.onur.firebaseexample.R;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class SimpleTabsActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    ConnectivityManager cm;
    GPSTracker gpsTracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_simple_tabs);
        gpsTracker = new GPSTracker(this);
        getLocation();
        BroadcastReceiver broadcastReceiver = new StartReceiver();
        IntentFilter filter =new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        getApplicationContext().registerReceiver(broadcastReceiver,filter);

        //Eğer bir user bağlandıysa onun var olup olmadığını kontrol ediyoruz.
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            startActivity(new Intent(SimpleTabsActivity.this, LoginActivity.class));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().isHideOnContentScrollEnabled();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if(!isOnline())
            Snackbar.make(viewPager, "Internet bağlantınızı kontrol edin", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Tamam", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    })
                    .setActionTextColor(Color.RED)
                    .show();
        }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "ONE");
        adapter.addFragment(new TwoFragment(), "TWO");
        adapter.addFragment(new ThreeFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            FirebaseAuth.getInstance().signOut();
            stopService(new Intent(SimpleTabsActivity.this,GPSTracker.class));
            startActivity(new Intent(SimpleTabsActivity.this, LoginActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private boolean isOnline() {
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void getLocation(){

        if(gpsTracker.canGetLocation()){
            gpsTracker.getLocation();
        }
        else {
            gpsTracker.showSettingsAlert();
        }

    }

    public void onDestroy(){

        Log.e("Servis","Servis yeniden başlatıldı.");
        Intent broadcastIntent = new Intent("RestartService");
        sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

}
