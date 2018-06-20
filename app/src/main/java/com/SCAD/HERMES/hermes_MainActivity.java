package com.SCAD.HERMES;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import android.net.Uri;

public class hermes_MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,CompoundButton.OnCheckedChangeListener {





    private NavigationDrawerFragment mNavigationDrawerFragment;


    private CharSequence mTitle;
    SharedPreferences sharedPreferences;
    SettingsActivity settingsActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hermes_main);

        Button btn = (Button) findViewById(R.id.WhatsAppButton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myWebLink = new Intent(android.content.Intent.ACTION_VIEW);
                        myWebLink.setData(Uri.parse("https://scad.shinyapps.io/HERMES/"));
                        startActivity(myWebLink);


                    }
        });





        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        settingsActivity = new SettingsActivity(hermes_MainActivity.this);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        Switch securityOnOff = (Switch) findViewById(R.id.security_on_off);
        Boolean security = settingsActivity.checkSecurity();
        String securityState = (security) ? "ON" : "OFF";
        Toast.makeText(this,"Security is " + securityState, Toast.LENGTH_LONG).show();
        securityOnOff.setChecked(security);



        if (securityOnOff != null) {
            securityOnOff.setOnCheckedChangeListener(this);
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked){
        Context context = getApplicationContext();
        sharedPreferences = context.getSharedPreferences(settingsActivity.MyPreferences, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(isChecked){
            editor.putBoolean(settingsActivity.securityOnOffs, true);
            Toast.makeText(this, "Security is ON", Toast.LENGTH_LONG).show();
        } else {
            editor.putBoolean(settingsActivity.securityOnOffs, false);
            Toast.makeText(this, "Security is OFF", Toast.LENGTH_LONG).show();
        }
        editor.commit();
    }
//

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "HERMES";
                break;
            case 2:
                mTitle = "HERMES";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {

            getMenuInflater().inflate(R.menu.hermes_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void phedo(View view) {


    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_hermes_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((hermes_MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }




    public void sendMessage (View view){
        Context context = getApplicationContext();
        GPSTracker gpsTracker = new GPSTracker(context);

        sharedPreferences = context.getSharedPreferences(settingsActivity.MyPreferences,
                0);
        SmsManager smsManager = SmsManager.getDefault();
        Boolean security = sharedPreferences.getBoolean(settingsActivity.securityOnOffs, false);
        String message = sharedPreferences.getString(settingsActivity.Messages, null);
        String pContact = sharedPreferences.getString(settingsActivity.pContacts, null);
        String sContact = sharedPreferences.getString(settingsActivity.sContacts, null);
        if(security) {
            if (sharedPreferences.contains(settingsActivity.pContacts) || sharedPreferences.contains(settingsActivity.sContacts)) {
                if (sharedPreferences.contains(settingsActivity.Messages)) {
                    if (sharedPreferences.contains(settingsActivity.pContacts)) {
                        String urlWithPrefix = "";
                        if(gpsTracker.isGPSEnabled) {
                            String stringLatitude = String.valueOf(gpsTracker.latitude);
                            String stringLongitude = String.valueOf(gpsTracker.longitude);
                            urlWithPrefix = " and he/she is at https://www.google.co.in/maps/@" + stringLatitude + "," + stringLongitude + ",19z";
                        }else{
                            Toast.makeText(context,"Your GPS is OFF", Toast.LENGTH_LONG).show();
                        }

                        if (pContact != null && !pContact.isEmpty()) {
                            message = message + urlWithPrefix;
                            smsManager.sendTextMessage(pContact, null, message, null, null);
                            Toast.makeText(context, "Message sent : " + pContact, Toast.LENGTH_LONG).show();

                            if(sContact != null && !sContact.isEmpty()){
                                String url = (pContact != null && !pContact.isEmpty()) ? "" : urlWithPrefix;
                                message = message + url;
                                smsManager.sendTextMessage(sContact, null, message, null, null);
                                Toast.makeText(context, "Message sent : " + sContact, Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(context,
                                    "Please setup Primary Contact in HERMES App",
                                    Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context,
                                "Please setup Primary Contact in HERMES App",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context,
                            "You haven't setup any Emergency Message in HERMES App",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context,
                        "Please Configure contact details in HERMES App",
                        Toast.LENGTH_LONG).show();

            }
        } else {
            Toast.makeText(context,
                    "Your Security is OFF in HERMES App",
                    Toast.LENGTH_LONG).show();
        }


    }



}
