package edu.byu.myannajm.familymap;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import model.*;
//class for anything you need to keep track of!
public class DataCache {
    private static DataCache instance = new DataCache();
    public static DataCache getInstance(){
        return instance;
    }
    private DataCache(){
    }
    static person user = new person();
    static Map<String, event> eventMap = new HashMap<>(); //string is eventID
    static Map<String, person> familyMembers = new HashMap<>();
    static ArrayList<event> allEvents = new ArrayList<>();
    static Map<String, ArrayList<event>> personEvents = new HashMap<>();
    static Map<person, Marker> personMarkerMap = new HashMap<>();
    static ArrayList<Polyline> spouseLines = new ArrayList<>();
    static ArrayList<Polyline> lifeLines = new ArrayList<>();
    static ArrayList<Polyline> familyTreeLines = new ArrayList<>();
    static ArrayList<person> allFamilyMembers = new ArrayList<>();
    static ArrayList<person> paternalAncestors = new ArrayList<>();
    static ArrayList<person> maternalAncestors = new ArrayList<>();
    public boolean spouseChecked = true;
    public boolean familyTreeChecked = true;
    public boolean lifeStoryChecked = true;
    public boolean maleChecked = true;
    public boolean femaleChecked = true;
    public boolean fatherChecked = true;
    public boolean motherChecked = true;
    protected static event getEventById(String eventID){
        return eventMap.get(eventID);
    }
    protected static event[] getEventByPersonID(String personID){
        ArrayList<event> temp = personEvents.get(personID);
        assert temp != null;
        event[] temp2 = new event[temp.size()];
        int i = 0;
        for(event event : temp){
            temp2[i] = event;
            i++;
        }
        return temp2;
    }
    protected static List<person> returnImmediateFamily(String personID, String fatherID, String motherID){
        List<person> immediateFamily = new ArrayList<>();
        for(person person : allFamilyMembers){
            if(Objects.equals(person.getSpouseID(), personID) || Objects.equals(person.getFatherID(), personID) || Objects.equals(person.getMotherID(), personID)){
                if(!immediateFamily.contains(person)){
                    immediateFamily.add(person);
                }
            }
            if((fatherID != null && Objects.equals(person.getPersonID(), fatherID)) || (motherID != null && Objects.equals(person.getPersonID(), motherID))){
                immediateFamily.add(person);
            }
        }
        return immediateFamily;
    }
    protected static event getPersonBirthOrFirst(String personID){
        event[] temp = getEventByPersonID(personID);
        int minYear = 0;
        event earliestEvent = null;
        for(event event: temp){
            if(Objects.equals(event.getEventType(), "birth")){return event;}
            else{
                int tempYear = event.getYear();
                if(minYear == 0){
                    minYear = tempYear;
                    earliestEvent = event;
                }
                else if(minYear > tempYear){
                    minYear = tempYear;
                    earliestEvent = event;
                }
            }
        }
        return earliestEvent;
    }
    protected static void addToEvents(event[] events){
        for(event event : events){
            allEvents.add(event);
            if(!personEvents.containsKey(event.getPersonID())){
                ArrayList<event> newEvent = new ArrayList<>();
                newEvent.add(event);
                personEvents.put(event.getPersonID(), newEvent);
            }
            else{
                personEvents.get(event.getPersonID()).add(event);
            }
            if(!eventMap.containsKey(event.getEventID())){
                eventMap.put(event.getEventID(), event);
            }
        }
    }

    protected static void addToFamilyMembers(person[] family){
        for(person person : family){
            if(!familyMembers.containsKey(person.getPersonID())){
                familyMembers.put(person.getPersonID(), person);
            }
        }
    }
    protected static person findPerson(String personID){
        return familyMembers.get(personID);
    }
    static class sortByYear implements Comparator<event> {
        public int compare(event a, event b){
            return a.getYear()-b.getYear();
        }
    }
    public void deleteCache(){
        eventMap.clear();
        familyMembers.clear();
        allEvents.clear();
        personEvents.clear();
        personMarkerMap.clear();
        spouseLines.clear();
        lifeLines.clear();
        familyTreeLines.clear();
        allFamilyMembers.clear();
        paternalAncestors.clear();
        maternalAncestors.clear();
        spouseChecked = true;
        lifeStoryChecked = true;
        familyTreeChecked =true;
        maleChecked = true;
        femaleChecked = true;
        fatherChecked = true;
        motherChecked = true;
    }
    public List<event> getFilteredEvents(){
        List<event> filteredEvents = new ArrayList<>();
        for(event event: allEvents){
            person eventPerson = findPerson(event.getPersonID());
            if(!maleChecked && Objects.equals(eventPerson.getGender(), "m")){
                continue;
            }
            if(!femaleChecked && Objects.equals(eventPerson.getGender(), "f")){
                continue;
            }
            if(!fatherChecked && paternalAncestors.contains(eventPerson)){
                continue;
            }
            if(!motherChecked && maternalAncestors.contains(eventPerson)){
                continue;
            }
            filteredEvents.add(event);
        }
        return filteredEvents;
    }
    protected static void splitSides(){
        person father = findPerson(user.getFatherID());
        person mother = findPerson(user.getMotherID());
        if(father != null){
            setFatherAncestors(father);
        }
        if(mother != null){
            setMotherAncestors(mother);
        }

    }
    private static void setFatherAncestors(person person){
        paternalAncestors.add(person);
        person father = findPerson(person.getFatherID());
        person mother = findPerson(person.getMotherID());
        if(father != null){
            setFatherAncestors(father);
        }
        if(mother != null){
            setFatherAncestors(mother);
        }
    }
    private static void setMotherAncestors(person person){
        maternalAncestors.add(person);
        person father = findPerson(person.getFatherID());
        person mother = findPerson(person.getMotherID());
        if(father != null){
            setMotherAncestors(father);
        }
        if(mother != null){
            setMotherAncestors(mother);
        }
    }

}
