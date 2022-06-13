package edu.byu.myannajm.familymap;

import android.content.Intent;
import android.os.Bundle;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;


import com.google.android.material.internal.TextWatcherAdapter;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.byu.myannajm.familymap.databinding.ActivityMainBinding;
import kotlinx.coroutines.ObsoleteCoroutinesApi;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoginFragment.Listener {

    private MapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Iconify.with(new FontAwesomeModule());
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.frameLayout);

        if(fragment == null) {
            fragment = createFirstFragment();

            fragmentManager.beginTransaction()
                    .add(R.id.frameLayout, fragment)
                    .commit();
        } else {
            if(fragment instanceof LoginFragment) {
                ((LoginFragment) fragment).registerListener(this);
            }
        }
    }
    private Fragment createFirstFragment() {
        LoginFragment fragment = new LoginFragment();
        fragment.registerListener(this);
        return fragment;
    }

    @Override
    public void notifyDone() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        Fragment fragment = new MapFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

    }
    @Override
    public void onResume() {
        super.onResume();
    }
}