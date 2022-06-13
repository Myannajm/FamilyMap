package edu.byu.myannajm.familymap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import model.*;


public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {
    private GoogleMap map;
    private event centerEvent = null;
    List<Polyline> polylines = new ArrayList<>();
    private event lastEvent = null;
    Float[] colors = {BitmapDescriptorFactory.HUE_RED, BitmapDescriptorFactory.HUE_AZURE,BitmapDescriptorFactory.HUE_BLUE, BitmapDescriptorFactory.HUE_CYAN, BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_MAGENTA, BitmapDescriptorFactory.HUE_ORANGE, BitmapDescriptorFactory.HUE_ROSE, BitmapDescriptorFactory.HUE_VIOLET, BitmapDescriptorFactory.HUE_YELLOW};
    Map<String, Float> colorTypeMap = new HashMap<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Iconify.with(new FontAwesomeModule());
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem settings = menu.findItem(R.id.settings_button);
        settings.setIcon(new IconDrawable(requireActivity(),
                FontAwesomeIcons.fa_gear)
                .colorRes(R.color.white)
                .actionBarSize());
        settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
        });
        MenuItem search = menu.findItem(R.id.search_button);
        search.setIcon(new IconDrawable(requireActivity(),
                FontAwesomeIcons.fa_search)
                .colorRes(R.color.white)
                .actionBarSize());
        search.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
            }
        });
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(layoutInflater, container, savedInstanceState);
        View view = layoutInflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
        if(getArguments() != null){
            setHasOptionsMenu(false);
            String eventID = getArguments().getString("eventID");
            centerEvent = DataCache.getEventById(eventID);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                colorRes(R.color.black).sizeDp(10);
        ImageView imageView = requireView().findViewById(R.id.genderView);
        imageView.setImageDrawable(genderIcon);
        map = googleMap;
        map.setOnMapLoadedCallback(this);
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                lastEvent = (event) marker.getTag();
                deletePolylines();
                populateDataAndLines(lastEvent, DataCache.getInstance().spouseChecked, DataCache.getInstance().lifeStoryChecked, DataCache.getInstance().familyTreeChecked);
                return true;
            }
        });
        setMarkers(centerEvent);
    }

    public void setMarkers(event currEvent) {
        ArrayList<Integer> usedBefore = new ArrayList<>();
        for(event event : DataCache.getInstance().getFilteredEvents()) {
            Random rndm = new Random();
            int index = rndm.nextInt(colors.length);
            LatLng temp = new LatLng(event.getLatitude(), event.getLongitude());
            Marker newMarker;
            if (colorTypeMap.containsKey(event.getEventType().toUpperCase())) {
                newMarker = map.addMarker(new MarkerOptions().position(temp).title(event.getEventType()).icon(BitmapDescriptorFactory.defaultMarker(colorTypeMap.get(event.getEventType().toUpperCase()))));
            } else {
                if (usedBefore.contains(index)) {
                    index = rndm.nextInt(colors.length);
                }
                colorTypeMap.put(event.getEventType().toUpperCase(), colors[index]);
                newMarker = map.addMarker(new MarkerOptions().position(temp).title(event.getEventType()).icon(BitmapDescriptorFactory.defaultMarker(colors[index])));
                usedBefore.add(index);
            }
            assert newMarker != null;
            newMarker.setTag(event);
        }
        if(currEvent != null && DataCache.getInstance().getFilteredEvents().contains(currEvent)){
            populateDataAndLines(currEvent, DataCache.getInstance().spouseChecked, DataCache.getInstance().lifeStoryChecked, DataCache.getInstance().familyTreeChecked);
            LatLng centerCoords = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(centerCoords, 5));
        }
    }
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_button:
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                return true;
            case R.id.search_button:
                Intent intent2 = new Intent(getActivity(), SettingsActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    void drawMarriage(String personID, event event1){
        person curr = DataCache.findPerson(personID);
        person spouse = DataCache.findPerson(curr.getSpouseID());
        if(spouse != null) {
            event event2 = DataCache.getPersonBirthOrFirst(spouse.getPersonID());
            if(DataCache.getInstance().getFilteredEvents().contains(event2)){
                LatLng l1 = new LatLng(event1.getLatitude(), event1.getLongitude());
                LatLng l2 = new LatLng(event2.getLatitude(), event2.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(l1,l2)
                        .color(Color.RED)
                        .width(5);
                Polyline line = map.addPolyline(options);
                polylines.add(line);
                DataCache.spouseLines.add(line);
            }
        }
    }
    void drawLifeLines(String personID){
        event[] lifeEvents = DataCache.getEventByPersonID(personID);
        List<event> lifeEvs = Arrays.asList(lifeEvents);
        lifeEvs.sort(new DataCache.sortByYear());
        for(int i = 0; i < lifeEvs.size()-1; i++){
            event curr = lifeEvs.get(i);
            event next = lifeEvs.get(i+1);
            LatLng l1 = new LatLng(curr.getLatitude(), curr.getLongitude());
            LatLng l2 = new LatLng(next.getLatitude(), next.getLongitude());
            PolylineOptions options = new PolylineOptions()
                    .add(l1,l2)
                    .color(Color.GREEN)
                    .width(5);
            Polyline line = map.addPolyline(options);
            polylines.add(line);
            DataCache.lifeLines.add(line);
        }
    }
    void drawFamilyLines(String personID, float width){
        event[] lifeEvents = DataCache.getEventByPersonID(personID);
        List<event> lifeEvs = Arrays.asList(lifeEvents);
        lifeEvs.sort(new DataCache.sortByYear());
        person current = DataCache.findPerson(personID);
        person father = DataCache.findPerson(current.getFatherID());
        person mother = DataCache.findPerson(current.getMotherID());
        event curr = DataCache.getPersonBirthOrFirst(current.getPersonID());
        if(father != null){
            event next = DataCache.getPersonBirthOrFirst(mother.getPersonID());
            if(DataCache.getInstance().getFilteredEvents().contains(next)){
                LatLng l1 = new LatLng(curr.getLatitude(), curr.getLongitude());
                LatLng l2 = new LatLng(next.getLatitude(), next.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(l1,l2)
                        .color(Color.BLUE)
                        .width(width);
                Polyline line = map.addPolyline(options);
                polylines.add(line);
                DataCache.familyTreeLines.add(line);
                drawFamilyLines(father.getPersonID(),width-5);
            }
        }
        if(mother != null){
            event next = DataCache.getPersonBirthOrFirst(mother.getPersonID());
            if(DataCache.getInstance().getFilteredEvents().contains(next)){
                LatLng l1 = new LatLng(curr.getLatitude(), curr.getLongitude());
                LatLng l2 = new LatLng(next.getLatitude(), next.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(l1,l2)
                        .color(Color.BLUE)
                        .width(width);
                Polyline line = map.addPolyline(options);
                polylines.add(line);
                DataCache.familyTreeLines.add(line);
                drawFamilyLines(mother.getPersonID(),width-5);
            }
        }
    }
    public void populateDataAndLines(event currEvent, boolean setMarriage, boolean setLifeLines, boolean setFamilyTree){
        person eventPerson = DataCache.findPerson(currEvent.getPersonID());
        event[] lifeEvents = DataCache.getEventByPersonID(eventPerson.getPersonID());
        List<event> lifeEvs = Arrays.asList(lifeEvents);
        lifeEvs.sort(new DataCache.sortByYear());
        LatLng coordinates = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        if(setLifeLines){
            drawLifeLines(currEvent.getPersonID());
        }
        if(setMarriage){
            drawMarriage(currEvent.getPersonID(), currEvent);
        }
        if(setFamilyTree){
            drawFamilyTree(currEvent);
        }
        if(Objects.equals(DataCache.findPerson(currEvent.getPersonID()).getGender(), "m")){
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                    colorRes(R.color.male_icon).sizeDp(10);
            ImageView view = requireView().findViewById(R.id.genderView);
            view.setImageDrawable(genderIcon);
        }
        else if(Objects.equals(DataCache.findPerson(currEvent.getPersonID()).getGender(), "f")){
            Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_female).
                    colorRes(R.color.female_icon).sizeDp(10);
            ImageView view = requireView().findViewById(R.id.genderView);
            view.setImageDrawable(genderIcon);
        }
        TextView text = requireView().findViewById(R.id.mapDetails);
        String eventData = eventPerson.getFirstName() + " " + eventPerson.getLastName() + "\n" + currEvent.getEventType().toUpperCase() + ": " + currEvent.getCity() + ", " + currEvent.getCountry()
                + ", (" + currEvent.getYear().toString() + ")";
        text.setText(eventData);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PersonActivity.class);
                i.putExtra("personID", eventPerson.getPersonID());
                i.putExtra("fatherID", eventPerson.getFatherID());
                i.putExtra("motherID", eventPerson.getMotherID());
                startActivity(i);
            }
        });
    }
    public void drawFamilyTree(event currEvent){
        person current = DataCache.findPerson(currEvent.getPersonID());
        person father = DataCache.findPerson(current.getFatherID());
        person mother = DataCache.findPerson(current.getMotherID());
        if(father != null){
            event next = DataCache.getPersonBirthOrFirst(father.getPersonID());
            if(DataCache.getInstance().getFilteredEvents().contains(next)) {
                LatLng l1 = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
                LatLng l2 = new LatLng(next.getLatitude(), next.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(l1, l2)
                        .color(Color.BLUE)
                        .width(20);
                Polyline line = map.addPolyline(options);
                polylines.add(line);
                DataCache.familyTreeLines.add(line);
                drawFamilyLines(father.getPersonID(), 10);
            }
        }
        if(mother != null){
            event next = DataCache.getPersonBirthOrFirst(mother.getPersonID());
            if(DataCache.getInstance().getFilteredEvents().contains(next)) {
                LatLng l1 = new LatLng(currEvent.getLatitude(), currEvent.getLongitude());
                LatLng l2 = new LatLng(next.getLatitude(), next.getLongitude());
                PolylineOptions options = new PolylineOptions()
                        .add(l1, l2)
                        .color(Color.BLUE)
                        .width(20);
                Polyline line = map.addPolyline(options);
                polylines.add(line);
                DataCache.familyTreeLines.add(line);
                drawFamilyLines(mother.getPersonID(), 10);
            }
        }
    }

    @Override
    public void onMapLoaded() {
        // You probably don't need this callback. It occurs after onMapReady and I have seen
        // cases where you get an error when adding markers or otherwise interacting with the map in
        // onMapReady(...) because the map isn't really all the way ready. If you see that, just
        // move all code where you interact with the map (everything after
        // map.setOnMapLoadedCallback(...) above) to here.
    }
    @Override
    public void onResume(){
        super.onResume();
        if (map != null) {
            map.clear();
            if(lastEvent != null && DataCache.getInstance().getFilteredEvents().contains(lastEvent)){
                    deletePolylines();
                    populateDataAndLines(lastEvent, DataCache.getInstance().spouseChecked, DataCache.getInstance().lifeStoryChecked, DataCache.getInstance().familyTreeChecked);
            }
            else{
                Drawable genderIcon = new IconDrawable(getActivity(), FontAwesomeIcons.fa_male).
                        colorRes(R.color.black).sizeDp(10);
                ImageView imageView = requireView().findViewById(R.id.genderView);
                imageView.setImageDrawable(genderIcon);
                TextView text = requireView().findViewById(R.id.mapDetails);
                String details = "Click on a marker to see information about the event!";
                text.setText(details);
            }
                setMarkers(centerEvent);
            }
    }
    private void deletePolylines() {
        for(Polyline line : polylines){
            line.remove();
        }
        polylines.clear();
    }

}
