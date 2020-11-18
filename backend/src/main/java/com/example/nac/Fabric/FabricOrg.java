package main.java.com.example.nac.Fabric;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import main.java.com.example.nac.Fabric.Client.CAClient;
import main.java.com.example.nac.Fabric.User.UserContext;
import org.hyperledger.fabric.sdk.User;

public class FabricOrg {
    private final String name;
    private final String mspid;
    private CAClient caClient;

    public FabricOrg(String name, String mspid) {
        this.name = name;
        this.mspid = mspid;
    }

    private Map<String, User> userMap = new HashMap<>();
    private Map<String, String> peerLocations = new HashMap<>();
    private Map<String, String> eventHubLocations = new HashMap<>();
    private UserContext admin;
    private String caLocation;
    private Properties caProperties = null;

    private String domainName;

    private String caName;

    public String getCAName() {
        return caName;
    }

    public UserContext getAdmin() {
        return admin;
    }

    public void setAdmin(UserContext admin) {
        this.admin = admin;
    }

    public String getMSPID() {
        return mspid;
    }

    public String getCALocation() {
        return this.caLocation;
    }

    public void setCALocation(String caLocation) {
        this.caLocation = caLocation;
    }

    public void addPeerLocation(String name, String location) {

        peerLocations.put(name, location);
    }


    public void addEventHubLocation(String name, String location) {

        eventHubLocations.put(name, location);
    }

    public String getPeerLocation(String name) {
        return peerLocations.get(name);

    }

    public String getEventHubLocation(String name) {
        return eventHubLocations.get(name);

    }

    public Set<String> getPeerNames() {

        return Collections.unmodifiableSet(peerLocations.keySet());
    }


    public Set<String> getEventHubNames() {

        return Collections.unmodifiableSet(eventHubLocations.keySet());
    }

    public CAClient getCaClient() {
        return caClient;
    }

    public void setCaClient(CAClient caClient) {
        this.caClient = caClient;
    }

    public String getName() {
        return name;
    }

    public User getUser(String name) {
        return userMap.get(name);
    }

    public Collection<String> getEventHubLocations() {
        return Collections.unmodifiableCollection(eventHubLocations.values());
    }

    public void setCAProperties(Properties caProperties) {
        this.caProperties = caProperties;
    }

    public Properties getCAProperties() {
        return caProperties;
    }


    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setCAName(String caName) {
        this.caName = caName;
    }
}
