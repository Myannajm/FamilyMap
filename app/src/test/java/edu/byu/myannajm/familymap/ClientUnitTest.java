package edu.byu.myannajm.familymap;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.IOException;

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
    public void testRegister() throws IOException {
        String serverInput = "localhost";
        String portInput = "8080";
        ServerProxy.clearDatabase(serverInput, portInput);
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
        registerRequest request = new registerRequest("username", "password", "email", "first", "last", "m");
        registerResponse response = ServerProxy.register(serverInput, portInput, request);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUsername());
    }
    @Test
    public void testLogin(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("username", "password");
        loginResponse response = ServerProxy.login(serverInput, portInput, request);
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getUsername());
    }
    @Test
    public void testLoginFail(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("username", "passw");
        loginResponse response = ServerProxy.login(serverInput, portInput, request);
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertNull(response.getUsername());
    }
    @Test
    public void testGetPeople(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("username", "password");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        familyResponse response = ServerProxy.getPeople(serverInput, portInput, loginResponse.getAuthToken());
        assertTrue(response.isSuccess());
        assertNotEquals(0, response.getAssociatedPeople().length);
    }
    @Test
    public void testGetPeopleFail(){
        String serverInput = "localhost";
        String portInput = "8080";
        loginRequest request = new loginRequest("username", "password");
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
        loginRequest request = new loginRequest("username", "password");
        loginResponse loginResponse = ServerProxy.login(serverInput, portInput, request);
        assert loginResponse != null;
        assertTrue(loginResponse.isSuccess());
        allEventResponse response = ServerProxy.getEvents(serverInput, portInput, loginResponse.getAuthToken());
        assertTrue(response.isSuccess());
        assertNotEquals(0, response.getEvents().length);
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

}