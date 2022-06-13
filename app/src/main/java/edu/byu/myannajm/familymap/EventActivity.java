package edu.byu.myannajm.familymap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import model.event;

public class EventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Iconify.with(new FontAwesomeModule());
        setContentView(R.layout.activity_event);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.eventActivity);
        String eventID = getIntent().getStringExtra("eventID");
        if(fragment == null) {
            fragment = new MapFragment();
            Bundle arguments = new Bundle();
            arguments.putString("eventID", eventID);
            fragment.setArguments(arguments);
            fragmentManager.beginTransaction()
                    .add(R.id.eventActivity, fragment)
                    .commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== android.R.id.home){
            Intent intent = new Intent(EventActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
}