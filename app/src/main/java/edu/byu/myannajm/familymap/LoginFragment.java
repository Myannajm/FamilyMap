package edu.byu.myannajm.familymap;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.*;
import request.*;
import response.*;

public class LoginFragment extends Fragment {
    private static final String AUTH_TOKEN = "authToken";
    private static final String PERSON_ID = "personID";
    private static final String USERNAME = "username";
    private static final String FIRST_NAME = "firstname";
    private static final String LAST_NAME = "lastname";
    private EditText editUsername;
    private EditText editPassword;
    private EditText editServerHost;
    private EditText editPortNum;
    private Button loginButton;
    private EditText editEmail;
    private EditText editFirstName;
    private EditText editLastName;
    private RadioGroup gender;
    private Button registerButton;
    private String usernameInput;
    private String passwordInput;
    private String serverInput;
    private String portInput;
    private String firstNameInput;
    private String lastNameInput;
    private String emailInput;
    private String username;
    private String personID;
    private String authToken;
    private Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        editUsername = view.findViewById(R.id.username);
        editPassword = view.findViewById(R.id.password);
        editServerHost = view.findViewById(R.id.serverHost);
        editPortNum = view.findViewById(R.id.portnum);
        loginButton = view.findViewById(R.id.button1);

        editUsername.addTextChangedListener(loginTextWatch);
        editPassword.addTextChangedListener(loginTextWatch);
        editServerHost.addTextChangedListener(loginTextWatch);
        editPortNum.addTextChangedListener(loginTextWatch);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("HandlerLeak") Handler msgHandler = new Handler() {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        authToken = bundle.getString(AUTH_TOKEN);
                        personID = bundle.getString(PERSON_ID);
                        username = bundle.getString(USERNAME);
                        if(username == null){
                            Toast.makeText(getActivity(), R.string.login_failed, Toast.LENGTH_LONG).show();
                        }
                        else{
                            @SuppressLint("HandlerLeak") Handler peopleHandler = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    Bundle bundle = message.getData();
                                    Toast.makeText(getActivity(), "Welcome " + bundle.getString(FIRST_NAME) + " " + bundle.getString(LAST_NAME), Toast.LENGTH_LONG).show();
                                    if(listener != null) {
                                        listener.notifyDone();
                                    }
                                }
                            };
                            getPeopleTask task = new getPeopleTask(serverInput, portInput, authToken, personID, peopleHandler);
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.submit(task);
                            getEventsTask task2 = new getEventsTask(serverInput, portInput, authToken, personID);
                            ExecutorService executorService2 = Executors.newSingleThreadExecutor();
                            executorService2.submit(task2);
                        }
                    }
                };
                loginRequest request = new loginRequest(usernameInput, passwordInput);
                LoginTask login = new LoginTask(serverInput, portInput, request, msgHandler);
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.submit(login);
            }
        });
        editEmail = view.findViewById(R.id.email);
        editFirstName = view.findViewById(R.id.firstName);
        editLastName = view.findViewById(R.id.lastName);
        registerButton = view.findViewById(R.id.button2);
        gender = view.findViewById(R.id.gender);
        editEmail.addTextChangedListener(registerTextWatch);
        editFirstName.addTextChangedListener(registerTextWatch);
        editLastName.addTextChangedListener(registerTextWatch);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(getActivity(), "Please select Gender", Toast.LENGTH_SHORT).show();
                } else {
                    // get selected radio button from radioGroup
                    int selectedId = gender.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    RadioButton selectedButton = (RadioButton) view.findViewById(selectedId);
                    char genderResult = selectedButton.getText().charAt(0);
                    @SuppressLint("HandlerLeak") Handler msgHandler = new Handler() {
                        @Override
                        public void handleMessage(Message message) {
                            Bundle bundle = message.getData();
                            authToken = bundle.getString(AUTH_TOKEN);
                            personID = bundle.getString(PERSON_ID);
                            username = bundle.getString(USERNAME);
                            if(username == null){
                                Toast.makeText(getActivity(), R.string.register_failed, Toast.LENGTH_LONG).show();
                            }
                            @SuppressLint("HandlerLeak") Handler peopleHandler = new Handler() {
                                @Override
                                public void handleMessage(Message message) {
                                    Bundle bundle = message.getData();
                                    Toast.makeText(getActivity(), "Welcome " + bundle.getString(FIRST_NAME) + " " + bundle.getString(LAST_NAME), Toast.LENGTH_LONG).show();
                                    if(listener != null) {
                                        listener.notifyDone();
                                    }
                                }
                            };
                            getPeopleTask task = new getPeopleTask(serverInput, portInput, authToken, personID, peopleHandler);
                            ExecutorService executorService = Executors.newSingleThreadExecutor();
                            executorService.submit(task);
                            getEventsTask task2 = new getEventsTask(serverInput, portInput, authToken, personID);
                            ExecutorService executorService2 = Executors.newSingleThreadExecutor();
                            executorService2.submit(task2);
                            }
                        };
                    registerRequest request = new registerRequest(usernameInput, passwordInput, emailInput, firstNameInput, lastNameInput, String.valueOf(genderResult).toLowerCase());
                    RegisterTask register = new RegisterTask(serverInput, portInput, request, msgHandler);
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.submit(register);
                }
                }
            });
        return view;
    }
    private TextWatcher loginTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            usernameInput = editUsername.getText().toString().trim();
            passwordInput = editPassword.getText().toString().trim();
            serverInput = editServerHost.getText().toString().trim();
            portInput = editPortNum.getText().toString().trim();
            loginButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() && !serverInput.isEmpty() && !portInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private TextWatcher registerTextWatch = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            firstNameInput = editFirstName.getText().toString().trim();
            lastNameInput = editLastName.getText().toString().trim();
            emailInput = editEmail.getText().toString().trim();
            registerButton.setEnabled(loginButton.isEnabled() && !firstNameInput.isEmpty() && !lastNameInput.isEmpty() && !emailInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private static class LoginTask implements Runnable{
        private final String serverInput;
        private final String portInput;
        private final loginRequest request;
        private final Handler messageHandler;
        public LoginTask(String serverInput, String portInput, loginRequest request, Handler messageHandler){
            this.serverInput = serverInput;
            this.portInput = portInput;
            this.request = request;
            this.messageHandler = messageHandler;
        }
        public void run() {
            loginResponse response = ServerProxy.login(serverInput, portInput, request);
            sendMessage(response);
            System.out.println(response.getUsername());
        }
        private void sendMessage(loginResponse response){
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(AUTH_TOKEN, response.getAuthToken());
            messageBundle.putString(PERSON_ID, response.getPersonID());
            messageBundle.putString(USERNAME, response.getUsername());
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }
    }
    private static class RegisterTask implements Runnable{
        private final String serverInput;
        private final String portInput;
        private final registerRequest request;
        private final Handler messageHandler;
        public RegisterTask(String serverInput, String portInput, registerRequest request, Handler messageHandler){
            this.serverInput = serverInput;
            this.portInput = portInput;
            this.request = request;
            this.messageHandler = messageHandler;
        }
        public void run() {
            registerResponse response = ServerProxy.register(serverInput, portInput, request);
            assert response != null;
            sendMessage(response);
            System.out.println(response.getUsername());
        }
        private void sendMessage(registerResponse response){
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(AUTH_TOKEN, response.getAuthToken());
            messageBundle.putString(PERSON_ID, response.getPersonID());
            messageBundle.putString(USERNAME, response.getUsername());
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }
    }
    private static class getPeopleTask implements Runnable{
        private final String serverInput;
        private final String portInput;
        private final String authToken;
        private final String personID;
        private final Handler messageHandler;
        public getPeopleTask(String serverInput, String portInput, String authToken, String personID, Handler messageHandler){
            this.serverInput = serverInput;
            this.portInput = portInput;
            this.authToken = authToken;
            this.personID = personID;
            this.messageHandler = messageHandler;
        }
        public void run() {
            familyResponse family = ServerProxy.getPeople(serverInput, portInput, authToken);
            assert family != null;
            person[] familyMembers = family.getAssociatedPeople();
            DataCache.addToFamilyMembers(familyMembers);
            for(person person : familyMembers){
                DataCache.allFamilyMembers.add(person);
                if(Objects.equals(person.getPersonID(), personID)){
                    DataCache.user = person;
                    sendMessage(person);
                }
            }
            DataCache.splitSides();
        }
        private void sendMessage(person person){
            Message message = Message.obtain();
            Bundle messageBundle = new Bundle();
            messageBundle.putString(FIRST_NAME, person.getFirstName());
            messageBundle.putString(LAST_NAME, person.getLastName());
            message.setData(messageBundle);
            messageHandler.sendMessage(message);
        }
    }

    private static class getEventsTask implements Runnable{
        private final String serverInput;
        private final String portInput;
        private final String authToken;
        private final String personID;
        public getEventsTask(String serverInput, String portInput, String authToken, String personID){
            this.serverInput = serverInput;
            this.portInput = portInput;
            this.authToken = authToken;
            this.personID = personID;
        }
        public void run() {
            allEventResponse response = ServerProxy.getEvents(serverInput, portInput, authToken);
            assert response != null;
            event[] familyEvents = response.getEvents();
            DataCache.addToEvents(familyEvents);
        }
    }

}