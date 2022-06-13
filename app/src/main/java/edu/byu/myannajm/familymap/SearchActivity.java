package edu.byu.myannajm.familymap;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import model.*;
import edu.byu.myannajm.familymap.databinding.ActivitySearchBinding;

public class SearchActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySearchBinding binding;
    private static final int EVENT_VIEW_TYPE = 0;
    private static final int PERSON_VIEW_TYPE = 1;
    List<event> allEvents = DataCache.getInstance().getFilteredEvents();
    List<person> allPeople = DataCache.allFamilyMembers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchView searchView = findViewById(R.id.searchView);
        RecyclerView recyclerView = findViewById(R.id.RecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                List<person> searchPeople = searchPeople(query);
                List<event> searchEvent = searchEvents(query);
                SearchAdapter adapter = new SearchAdapter(searchEvent, searchPeople);
                recyclerView.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<person> searchPeople = searchPeople(newText);
                List<event> searchEvent = searchEvents(newText);
                SearchAdapter adapter = new SearchAdapter(searchEvent, searchPeople);
                recyclerView.setAdapter(adapter);
                return true;
            }
        });


    }
    private List<person> searchPeople(String query){
        query = query.toLowerCase();
        List<person> peopleSearch = new ArrayList<>();
        for(person person : allPeople){
            if(person.getFirstName().toLowerCase().contains(query) || person.getLastName().toLowerCase().contains(query)){
                peopleSearch.add(person);
            }
        }
        return peopleSearch;
    }
    private List<event> searchEvents(String query){
        query = query.toLowerCase();
        List<event> eventSearch = new ArrayList<>();
        for(event event : allEvents){
            if(event.getEventType().toLowerCase().contains(query) || event.getCountry().toLowerCase().contains(query) || event.getCity().toLowerCase().contains(query) || event.getYear().toString().contains(query)){
                eventSearch.add(event);
            }
        }
        return eventSearch;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()== android.R.id.home){
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return true;
    }
    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private final List<event> allEvents;
        private final List<person> allPeople;

        SearchAdapter(List<event> allEvents, List<person> allPeople) {
            this.allEvents = allEvents;
            this.allPeople = allPeople;
        }

        @Override
        public int getItemViewType(int position) {
            return position < allEvents.size() ? EVENT_VIEW_TYPE : PERSON_VIEW_TYPE;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.people_event_layout, parent, false);;
            return new SearchViewHolder(view, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            if(position < allEvents.size()) {
                holder.bind(allEvents.get(position));
            } else {
                holder.bind(allPeople.get(position - allEvents.size()));
            }
        }

        @Override
        public int getItemCount() {
            return allEvents.size() + allPeople.size();
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ImageView icon;
        private final TextView details;
        private final TextView personName;

        private final int viewType;
        private event event;
        private person person;

        SearchViewHolder(View view, int viewType) {
            super(view);
            this.viewType = viewType;

            itemView.setOnClickListener(this);

            if(viewType == EVENT_VIEW_TYPE) {
                icon = itemView.findViewById(R.id.markerOrGender);
                details = itemView.findViewById(R.id.nameOf);
                personName = itemView.findViewById(R.id.categoryOf);
            } else {
                icon = itemView.findViewById(R.id.markerOrGender);
                details = itemView.findViewById(R.id.nameOf);
                personName = null;
            }
        }

        private void bind(event event) {
            Drawable marker = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_map_marker).
                    colorRes(R.color.black).sizeDp(10);
            this.event = event;
            icon.setImageDrawable(marker);
            String eventDetails = event.getEventType() + ": " + event.getCity() + ", " + event.getCountry()
                    + ", (" + event.getYear().toString() + ")";
            details.setText(eventDetails);
            person associatedPerson = DataCache.getInstance().findPerson(event.getPersonID());
            String name = associatedPerson.getFirstName() + " " + associatedPerson.getLastName();
            personName.setText(name);
        }

        private void bind(person person) {
            this.person = person;
            Drawable genderIcon;
            if(Objects.equals(person.getGender(), "f")){
                genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_female).
                        colorRes(R.color.female_icon).sizeDp(10);
            }else{
                genderIcon = new IconDrawable(SearchActivity.this, FontAwesomeIcons.fa_male).
                        colorRes(R.color.male_icon).sizeDp(10);
            }
            icon.setImageDrawable(genderIcon);
            String name = person.getFirstName() + " " + person.getLastName();
            details.setText(name);
        }

        @Override
        public void onClick(View view) {
            if(viewType == EVENT_VIEW_TYPE) {
                Intent i = new Intent(SearchActivity.this, EventActivity.class);
                i.putExtra("eventID",event.getEventID());
                startActivity(i);
            } else {
                Intent i = new Intent(SearchActivity.this, PersonActivity.class);
                i.putExtra("personID", person.getPersonID());
                i.putExtra("fatherID", person.getFatherID());
                i.putExtra("motherID", person.getMotherID());
                startActivity(i);
            }
        }
    }
}
