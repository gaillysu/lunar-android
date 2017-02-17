package com.medcorp.network.validic.model;

/**
 * Created by gaillysu on 16/3/9.
 */
public class ValidicOrganization {

    String _id;
    String name;
    int users;
    int users_provisioned;
    int activities;
    int connections;
    String[] organizations;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public int getUsers_provisioned() {
        return users_provisioned;
    }

    public void setUsers_provisioned(int users_provisioned) {
        this.users_provisioned = users_provisioned;
    }

    public int getActivities() {
        return activities;
    }

    public void setActivities(int activities) {
        this.activities = activities;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public String[] getOrganizations() {
        return organizations;
    }

    public void setOrganizations(String[] organizations) {
        this.organizations = organizations;
    }
}
