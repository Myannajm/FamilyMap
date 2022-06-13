package edu.byu.myannajm.familymap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import edu.byu.myannajm.familymap.databinding.ActivityPersonBinding;
import model.*;

public class PersonActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPersonBinding binding;
    private String personID;
    private String motherID;
    private String fatherID;
    private person currPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        ExpandableListView personListView = findViewById(R.id.expandableListView);
        personID = getIntent().getStringExtra("personID");
        fatherID = getIntent().getStringExtra("fatherID");
        motherID = getIntent().getStringExtra("motherID");
        currPerson = DataCache.findPerson(personID);
        List<event> lifeEvs;
        if((Objects.equals(currPerson.getGender(), "m") && !DataCache.getInstance().maleChecked) || (Objects.equals(currPerson.getGender(), "f") && !DataCache.getInstance().femaleChecked)){
             lifeEvs = new ArrayList<>();
        }
        else if((!DataCache.getInstance().motherChecked && DataCache.maternalAncestors.contains(currPerson)) || (!DataCache.getInstance().fatherChecked && DataCache.paternalAncestors.contains(currPerson))){
            lifeEvs = new ArrayList<>();
        }
        else{
            event[] lifeEvents = DataCache.getEventByPersonID(personID);
            lifeEvs = Arrays.asList(lifeEvents);
            lifeEvs.sort(new DataCache.sortByYear());
        }
        List<person> immediateFamily = DataCache.returnImmediateFamily(personID, fatherID, motherID);
        TextView personFirstName = findViewById(R.id.personFirstName);
        personFirstName.setText(currPerson.getFirstName());
        TextView personLastName = findViewById(R.id.personLastName);
        personLastName.setText(currPerson.getLastName());
        TextView gender = findViewById(R.id.personGender);
        if(Objects.equals(currPerson.getGender(), "m")){
            String g = "Male";
            gender.setText(g);
        }else{
            String g = "Female";
            gender.setText(g);
        }
        personListView.setAdapter(new ExpandableListAdapter(lifeEvs, immediateFamily));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== android.R.id.home){
            Intent intent = new Intent(PersonActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
    private class ExpandableListAdapter extends BaseExpandableListAdapter {

        private static final int EVENT_POSITION = 0;
        private static final int PEOPLE_POSITION = 1;

        private final List<event> lifeEvents;
        private final List<person> immediateFamily;

        ExpandableListAdapter(List<event> lifeEvents, List<person> immediateFamily) {
            this.lifeEvents = lifeEvents;
            this.immediateFamily = immediateFamily;
        }

        @Override
        public int getGroupCount() {
            return 2;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case EVENT_POSITION:
                    return lifeEvents.size();
                case PEOPLE_POSITION:
                    return immediateFamily.size();
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.list_item_group, parent, false);
            }

            TextView titleView = convertView.findViewById(R.id.listTitle);

            switch (groupPosition) {
                case EVENT_POSITION:
                    titleView.setText(R.string.eventTitle);
                    break;
                case PEOPLE_POSITION:
                    titleView.setText(R.string.familyTitle);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View itemView;

            switch(groupPosition) {
                case EVENT_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.people_event_layout, parent, false);
                    initializeEventView(itemView, childPosition);
                    break;
                case PEOPLE_POSITION:
                    itemView = getLayoutInflater().inflate(R.layout.people_event_layout, parent, false);
                    initializePeopleView(itemView, childPosition);
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized group position: " + groupPosition);
            }

            return itemView;
        }

        private void initializeEventView(View eventView, final int childPosition) {
            ImageView imageView = eventView.findViewById(R.id.markerOrGender);
            Drawable marker = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.black).sizeDp(10);
            imageView.setImageDrawable(marker);
            TextView nameView = eventView.findViewById(R.id.nameOf);
            String eventDetails = lifeEvents.get(childPosition).getEventType() + ": " + lifeEvents.get(childPosition).getCity() + ", " + lifeEvents.get(childPosition).getCountry()
                    + ", (" + lifeEvents.get(childPosition).getYear().toString() + ")";
            nameView.setText(eventDetails);

            TextView relationshipTo = eventView.findViewById(R.id.categoryOf);
            String name = currPerson.getFirstName() + " " + currPerson.getLastName();
            relationshipTo.setText(name);
            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PersonActivity.this, EventActivity.class);
                    i.putExtra("eventID",lifeEvents.get(childPosition).getEventID());
                    startActivity(i);
                }
            });
        }

        private void initializePeopleView(View personView, final int childPosition) {
            ImageView imageView = personView.findViewById(R.id.markerOrGender);
            String gender = immediateFamily.get(childPosition).getGender();
            Drawable genderIcon;
            if(Objects.equals(gender, "f")){
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(10);
            }else{
                genderIcon = new IconDrawable(PersonActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(10);
            }
            imageView.setImageDrawable(genderIcon);
            TextView nameView = personView.findViewById(R.id.nameOf);
            String personName = immediateFamily.get(childPosition).getFirstName() + " " + immediateFamily.get(childPosition).getLastName();
            nameView.setText(personName);

            TextView relationshipTo = personView.findViewById(R.id.categoryOf);
            String relationship = null;
            if(Objects.equals(immediateFamily.get(childPosition).getFatherID(), personID) || Objects.equals(immediateFamily.get(childPosition).getMotherID(), personID)){
                relationship = "CHILD";
            }else if(Objects.equals(immediateFamily.get(childPosition).getSpouseID(), personID)){
                relationship = "SPOUSE";
            }else if(Objects.equals(immediateFamily.get(childPosition).getPersonID(), fatherID)){
                relationship = "FATHER";
            }else if(Objects.equals(immediateFamily.get(childPosition).getPersonID(), motherID)){
                relationship = "MOTHER";
            }
            relationshipTo.setText(relationship);

            personView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(PersonActivity.this, PersonActivity.class);
                    i.putExtra("personID", immediateFamily.get(childPosition).getPersonID());
                    i.putExtra("fatherID", immediateFamily.get(childPosition).getFatherID());
                    i.putExtra("motherID", immediateFamily.get(childPosition).getMotherID());
                    startActivity(i);
                }
            });
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}