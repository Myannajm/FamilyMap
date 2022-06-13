package edu.byu.myannajm.familymap;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;

import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SwitchCompat lifeStoryLines = findViewById(R.id.switch1);
        lifeStoryLines.setChecked(DataCache.getInstance().lifeStoryChecked);
        lifeStoryLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lifeStoryLines.setChecked(isChecked);
                DataCache.getInstance().lifeStoryChecked = isChecked;
            }
        });
        SwitchCompat familyTreeLines = findViewById(R.id.switch2);
        familyTreeLines.setChecked(DataCache.getInstance().familyTreeChecked);
        familyTreeLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                familyTreeLines.setChecked(isChecked);
                DataCache.getInstance().familyTreeChecked = isChecked;
                if(!isChecked){

                }
            }
        });
        SwitchCompat spouseLines = findViewById(R.id.switch3);
        spouseLines.setChecked(DataCache.getInstance().spouseChecked);
        spouseLines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                spouseLines.setChecked(isChecked);
                DataCache.getInstance().spouseChecked = isChecked;
            }
        });
        SwitchCompat fatherSide = findViewById(R.id.switch4);
        fatherSide.setChecked(DataCache.getInstance().fatherChecked);
        fatherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                fatherSide.setChecked(isChecked);
                DataCache.getInstance().fatherChecked= isChecked;
            }
        });
        SwitchCompat motherSide = findViewById(R.id.switch5);
        motherSide.setChecked(DataCache.getInstance().motherChecked);
        motherSide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                motherSide.setChecked(isChecked);
                DataCache.getInstance().motherChecked = isChecked;
            }
        });
        SwitchCompat maleCheck = findViewById(R.id.switch6);
        maleCheck.setChecked(DataCache.getInstance().maleChecked);
        maleCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                maleCheck.setChecked(isChecked);
                DataCache.getInstance().maleChecked = isChecked;
            }
        });
        SwitchCompat femaleCheck = findViewById(R.id.switch7);
        femaleCheck.setChecked(DataCache.getInstance().femaleChecked);
        femaleCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                femaleCheck.setChecked(isChecked);
                DataCache.getInstance().femaleChecked = isChecked;
            }
        });
        Button logOut = findViewById(R.id.button2);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                DataCache.getInstance().deleteCache();
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== android.R.id.home){
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }

}