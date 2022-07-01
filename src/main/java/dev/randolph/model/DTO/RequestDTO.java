package dev.randolph.model.DTO;

import dev.randolph.model.Request;

public class RequestDTO {
    
    // Employee - Associated with request
    private String username;
    private String firstName;
    private String lastName;
    private Double reimFunds;
    // Request
    private Request request;
    // Meta
    private MetaDTO meta = MetaDTO.getMetaDTO();
    
    public RequestDTO() {}

    public RequestDTO(String username, String firstName, String lastName, Double reimFunds, Request request) {
        super();
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.reimFunds = reimFunds;
        this.request = request;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getReimFunds() {
        return reimFunds;
    }

    public void setReimFunds(Double reimFunds) {
        this.reimFunds = reimFunds;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    public MetaDTO getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "RequestDTO [username=" + username + ", firstName=" + firstName + ", lastName=" + lastName
                + ", reimFunds=" + reimFunds + ", request=" + request + ", meta=" + meta + "]";
    }
}
