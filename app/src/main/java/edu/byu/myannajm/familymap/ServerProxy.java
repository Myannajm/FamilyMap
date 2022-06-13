package edu.byu.myannajm.familymap;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.*;
import request.*;
import response.*;

public class ServerProxy {
    protected static familyResponse getPeople(String serverHost, String serverPort, String authToken) {
        // This method shows how to send a GET request to a server
        Gson gson = new Gson();
        String errorMsg = null;
        try {
            // Create a URL indicating where the server is running, and which
            // web API operation we want to call
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/person");
            // Start constructing our HTTP request
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            // Specify that we are sending an HTTP GET request
            http.setRequestMethod("GET");
            // Indicate that this request will not contain an HTTP request body
            http.setDoOutput(false);
            // Add an auth token to the request in the HTTP "Authorization" header
            http.addRequestProperty("Authorization", authToken); //set to be the proper auth token
            // Specify that we would like to receive the server's response in JSON
            // format by putting an HTTP "Accept" header on the request (this is not
            // necessary because our server only returns JSON responses, but it
            // provides one more example of how to add a header to an HTTP request).
            http.addRequestProperty("Accept", "application/json");
            // Connect to the server and send the HTTP request
            http.connect();
            // By the time we get here, the HTTP response has been received from the server.
            // Check to make sure that the HTTP response from the server contains a 200
            // status code, which means "success".  Treat anything else as a failure.
            if(http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream containing the HTTP response body
                Reader respBody= new InputStreamReader(http.getInputStream());
                Map<String, ArrayList<Map<String, String>>> map = new HashMap<>();
                map = gson.fromJson(respBody, Map.class);
                ArrayList<Map<String, String>> toPeople = map.get("data");
                assert toPeople != null;
                person[] family = new person[toPeople.size()];
                int i = 0;
                for(Map<String, String> pers1: toPeople){
                    family[i] = new person(pers1.get("personID"), pers1.get("associatedUsername"), pers1.get("firstName"),pers1.get("lastName"), pers1.get("gender"), pers1.get("fatherID"), pers1.get("motherID"), pers1.get("spouseID"));
                    i++;
                }
                return new familyResponse(true, family);
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                errorMsg = http.getResponseMessage();
                // Get the error stream containing the HTTP response body (if any)

                // Extract data from the HTTP response body
                Reader respBody= new InputStreamReader(http.getErrorStream());

                // Display the data returned from the server
                System.out.println(respBody);
                return new familyResponse(false, http.getResponseMessage());
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            if(errorMsg == null){
                errorMsg = e.toString();
            }
            return new familyResponse(false, errorMsg);
        }
    }
    static allEventResponse getEvents(String serverHost, String serverPort, String authToken) {
        Gson gson = new Gson();
        String errorMsg = null;
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/event");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.addRequestProperty("Authorization", authToken);
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Reader respBody= new InputStreamReader(http.getInputStream());
                Map<String, ArrayList<Map<String, Object>>> map = new HashMap<>();
                map = gson.fromJson(respBody, Map.class);
                ArrayList<Map<String, Object>> toEvents = map.get("data");
                assert toEvents != null;
                event[] events = new event[toEvents.size()];
                int i = 0;
                for(Map<String, Object> event: toEvents){
                    String id = String.valueOf(event.get("eventID"));
                    String username = String.valueOf(event.get("associatedUsername"));
                    String pid = String.valueOf(event.get("personID"));
                    Float lat = Float.valueOf(String.valueOf(event.get("latitude")));
                    Float lon = Float.valueOf(String.valueOf(event.get("longitude")));
                    String country = String.valueOf(event.get("country"));
                    String city = String.valueOf(event.get("city"));
                    Integer year = Float.valueOf(String.valueOf(event.get("year"))).intValue();
                    String type = String.valueOf(event.get("eventType"));
                    events[i] = new event(id, username, pid, lat, lon, country, city, type, year); // add event properties
                    i++;
                }
                return new allEventResponse(true, events);
            }
            else {
                // The HTTP response status code indicates an error
                // occurred, so print out the message from the HTTP response
                System.out.println("ERROR: " + http.getResponseMessage());
                errorMsg = http.getResponseMessage();
                // Get the error stream containing the HTTP response body (if any)

                // Extract data from the HTTP response body
                Reader respBody= new InputStreamReader(http.getErrorStream());

                // Display the data returned from the server
                System.out.println(respBody);
                return new allEventResponse(false, http.getResponseMessage());
            }
        }
        catch (IOException e) {
            // An exception was thrown, so display the exception's stack trace
            e.printStackTrace();
            if(errorMsg == null){
                errorMsg = e.toString();
            }
            return new allEventResponse(false, errorMsg);
        }
    }
    static registerResponse register(String serverHost, String serverPort, registerRequest request) {
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/register");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            Writer reqBody = new OutputStreamWriter(http.getOutputStream());
            gson.toJson(request, reqBody);
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println(http.getResponseMessage());
                Reader respBody= new InputStreamReader(http.getInputStream());
                System.out.println("User successfully registered");
                return (registerResponse) gson.fromJson(respBody, registerResponse.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                Reader respBody= new InputStreamReader(http.getErrorStream());
                return (registerResponse) gson.fromJson(respBody, registerResponse.class);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static loginResponse login(String serverHost, String serverPort, loginRequest request) {
        String respData = null;
        Gson gson = new Gson();
        try {
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/user/login");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.addRequestProperty("Accept", "application/json");
            http.connect();
            Writer reqBody = new OutputStreamWriter(http.getOutputStream());
            gson.toJson(request, reqBody);
            reqBody.close();
            if (http.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println(http.getResponseMessage());
                Reader respBody= new InputStreamReader(http.getInputStream());
                System.out.println("User successfully logged in");
                return (loginResponse)gson.fromJson(respBody, loginResponse.class);
            }
            else {
                System.out.println("ERROR: " + http.getResponseMessage());
                Reader respBody= new InputStreamReader(http.getErrorStream());
                return (loginResponse)gson.fromJson(respBody, loginResponse.class);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void clearDatabase(String serverHost, String serverPort) throws IOException {
        try{
            URL url = new URL("http://" + serverHost + ":" + serverPort + "/clear");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            http.setDoOutput(false);
            http.connect();
            if (http.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("clear did not work LOSER!");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
