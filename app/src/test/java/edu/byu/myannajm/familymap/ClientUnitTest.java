package edu.byu.myannajm.familymap;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import model.event;
import model.person;
import request.*;
import response.*;
import response.registerResponse;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ClientUnitTest {
    @Test
    public void testLogin(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "parker");
        loginResponse response = ServerProxy.login(serverInput, portInput, request);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getUsername());
    }
    @Test
    public void testLoginFail(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "park");
        loginResponse response = ServerProxy.login(serverInput, portInput, request);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUsername());
    }
    @Test
    public void testGetPeople(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "parker");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        familyResponse response = ServerProxy.getPeople(serverInput, portInput, loginResponse.getAuthToken());
        assertTrue(response.isSuccess());
        assertNotEquals(0, response.getAssociatedPeople().length);
        person[] familyMembers = response.getAssociatedPeople();
        DataCache.addToFamilyMembers(familyMembers);
        for(person person : familyMembers){
            DataCache.allFamilyMembers.add(person);
            if(Objects.equals(person.getPersonID(), loginResponse.getPersonID())){
                DataCache.user = person;
            }
        }
    }
    @Test
    public void testGetPeopleFail(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "parker");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        familyResponse response = ServerProxy.getPeople(serverInput, portInput, "ayo_fake_token!");
        assertFalse(response.isSuccess());
        assertNull(response.getAssociatedPeople());
    }
    @Test
    public void testGetEvents(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "parker");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        allEventResponse response = ServerProxy.getEvents(serverInput, portInput, loginResponse.getAuthToken());
        assertTrue(response.isSuccess());
        assertNotEquals(0, response.getEvents().length);
        event[] familyEvents = response.getEvents();
        DataCache.addToEvents(familyEvents);
    }
    @Test
    public void testGetEventFail(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("username", "password");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        allEventResponse response = ServerProxy.getEvents(serverInput, portInput, "ayo_fake_token!");
        assertFalse(response.isSuccess());
        assertNull(response.getEvents());
    }
    @Test
    public void testImmediateFamily(){
        DataCache.getInstance().deleteCache();
        setupCache();
        person user = edu.byu.myannajm.familymap.DataCache.user;
        person mother = edu.byu.myannajm.familymap.DataCache.findPerson(user.getMotherID());
        assertNotNull(mother);
        assertEquals(mother.getPersonID(), user.getMotherID());
        person father = edu.byu.myannajm.familymap.DataCache.findPerson(user.getFatherID());
        assertNotNull(father);
        assertEquals(father.getPersonID(), user.getFatherID());
        List<person> immediateFamily = edu.byu.myannajm.familymap.DataCache.returnImmediateFamily(user.getPersonID(), user.getFatherID(), user.getMotherID());
        assertTrue(immediateFamily.contains(mother));
        assertTrue(immediateFamily.contains(father));
    }
    @Test
    public void testsNoImmediateFamily(){
        DataCache.getInstance().deleteCache();
        setupCache();
        person spouse = edu.byu.myannajm.familymap.DataCache.findPerson(edu.byu.myannajm.familymap.DataCache.user.getSpouseID());
        List<person> immediateFamily = new ArrayList<>();
        immediateFamily = edu.byu.myannajm.familymap.DataCache.returnImmediateFamily(spouse.getPersonID(), spouse.getFatherID(), spouse.getMotherID());
        assertEquals(1, immediateFamily.size()); //should be one bc only spouse is related
        assertNull(edu.byu.myannajm.familymap.DataCache.findPerson(spouse.getFatherID()));
        assertNull(edu.byu.myannajm.familymap.DataCache.findPerson(spouse.getMotherID()));
    }
    @Test
    public void filteredEventsPass(){
        DataCache.getInstance().deleteCache();
        setupCache();
        DataCache.getInstance().maleChecked = false;
        List<event> allEvents = edu.byu.myannajm.familymap.DataCache.getInstance().getFilteredEvents();
        for(event event : allEvents){
            person eventPerson = DataCache.findPerson(event.getPersonID());
            assertNotEquals("m", eventPerson.getGender());
        }
        DataCache.getInstance().maleChecked = true;
        DataCache.getInstance().motherChecked = false;
        List<event> allEvents2 = edu.byu.myannajm.familymap.DataCache.getInstance().getFilteredEvents();
        for(event event : allEvents2){
            person eventPerson = DataCache.findPerson(event.getPersonID());
            assertFalse(DataCache.maternalAncestors.contains(eventPerson));
        }
    }
    @Test
    public void filteredEventsAllUnique(){
        DataCache.getInstance().deleteCache();
        setupCache();
        DataCache.getInstance().femaleChecked = false;
        DataCache.getInstance().fatherChecked = false;
        List<event> allEvents = edu.byu.myannajm.familymap.DataCache.getInstance().getFilteredEvents();
        for(event event : allEvents){
            person eventPerson = DataCache.findPerson(event.getPersonID());
            assertNotEquals("f", eventPerson.getGender());
            assertFalse(DataCache.paternalAncestors.contains(eventPerson));
        }
    }
    @Test
    public void testEventOrdering(){
        DataCache.getInstance().deleteCache();
        setupCache();
        person user = edu.byu.myannajm.familymap.DataCache.user;
        event[] lifeEvents = edu.byu.myannajm.familymap.DataCache.getEventByPersonID(user.getPersonID());
        List<event> lifeEvs = Arrays.asList(lifeEvents);
        lifeEvs.sort(new edu.byu.myannajm.familymap.DataCache.sortByYear());
        for(int i = 0; i < lifeEvents.length-1; ++i){
            assertTrue(lifeEvs.get(i).getYear() <= lifeEvs.get(i+1).getYear());
        }
    }
    @Test
    public void testWeirdEventOrder(){
        DataCache.getInstance().deleteCache();
        setupCache();
        person frank_jones = edu.byu.myannajm.familymap.DataCache.findPerson("Frank_Jones");
        event[] lifeEvents = edu.byu.myannajm.familymap.DataCache.getEventByPersonID(frank_jones.getPersonID());
        List<event> lifeEvs = Arrays.asList(lifeEvents);
        lifeEvs.sort(new edu.byu.myannajm.familymap.DataCache.sortByYear());
        for(int i = 0; i < lifeEvents.length-1; ++i){
            assertTrue(lifeEvs.get(i).getYear() <= lifeEvs.get(i+1).getYear());
        }
    }
    @Test
    public void correctSearch(){
        DataCache.getInstance().deleteCache();
        setupCache();
        DataCache.getInstance().maleChecked = false;
        List<person> allPeople = DataCache.allFamilyMembers;
        List<event> allEvents = edu.byu.myannajm.familymap.DataCache.getInstance().getFilteredEvents();
        assertNotEquals(0, searchEvents("birth", allEvents).size());
        assertNotEquals(0, searchEvents("java", allEvents).size());
        assertEquals(0, searchEvents("frog", allEvents).size());
        assertNotEquals(0, searchPeople("rod", allPeople).size());
        assertNotEquals(0, searchPeople("frank", allPeople).size());
    }
    @Test
    public void searchFail(){
        DataCache.getInstance().deleteCache();
        setupCache();
        DataCache.getInstance().femaleChecked = false;
        DataCache.getInstance().maleChecked = false;
        List<person> allPeople = DataCache.allFamilyMembers;
        List<event> allEvents = edu.byu.myannajm.familymap.DataCache.getInstance().getFilteredEvents();
        assertEquals(0, allEvents.size());
        assertEquals(0, searchEvents("birth", allEvents).size());
        assertEquals(0, searchEvents("frog", allEvents).size());
        assertNotEquals(0, searchPeople("jon", allPeople).size());
        assertNotEquals(0, searchPeople("b", allPeople).size());
    }
    @Test
    public void testRegister() throws IOException {
        DataCache.getInstance().deleteCache();
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest loginRequest = new loginRequest("username", "password");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, loginRequest);
        assert loginResponse != null;
        assertFalse(loginResponse.isSuccess());
        registerRequest request = new registerRequest("username", "password", "email", "first", "last", "m");
        registerResponse response = ServerProxy.register(serverInput, portInput, request);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getUsername());
    }
    @Test
    public void registerFail() throws IOException {
        String serverInput = "localhost";
        String portInput = "8080";
        registerRequest request = new registerRequest("username", "password", "email", "first", "last", "m/f");
        registerResponse response = ServerProxy.register(serverInput, portInput, request);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUsername());
    }

    private void setupCache(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("sheila", "parker");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        familyResponse response = ServerProxy.getPeople(serverInput, portInput, loginResponse.getAuthToken());
        person[] familyMembers = response.getAssociatedPeople();
        edu.byu.myannajm.familymap.DataCache.addToFamilyMembers(familyMembers);
        for(person person : familyMembers){
            edu.byu.myannajm.familymap.DataCache.allFamilyMembers.add(person);
            if(Objects.equals(person.getPersonID(), loginResponse.getPersonID())){
                edu.byu.myannajm.familymap.DataCache.user = person;
            }
        }
        allEventResponse eventResponse = ServerProxy.getEvents(serverInput, portInput, loginResponse.getAuthToken());
        event[] familyEvents = eventResponse.getEvents();
        edu.byu.myannajm.familymap.DataCache.addToEvents(familyEvents);
    }
    private List<person> searchPeople(String query, List<person> allPeople){
        query = query.toLowerCase();
        List<person> peopleSearch = new ArrayList<>();
        for(person person : allPeople){
            if(person.getFirstName().toLowerCase().contains(query) || person.getLastName().toLowerCase().contains(query)){
                peopleSearch.add(person);
            }
        }
        return peopleSearch;
    }
    private List<event> searchEvents(String query, List<event> allEvents){
        query = query.toLowerCase();
        List<event> eventSearch = new ArrayList<>();
        for(event event : allEvents){
            if(event.getEventType().toLowerCase().contains(query) || event.getCountry().toLowerCase().contains(query) || event.getCity().toLowerCase().contains(query) || event.getYear().toString().contains(query)){
                eventSearch.add(event);
            }
        }
        return eventSearch;
    }

}