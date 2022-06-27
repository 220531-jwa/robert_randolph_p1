package dev.randolph.model.DTO;

import dev.randolph.model.Request;

public class RequestDTO {
    
    private String firstName;
    private String lastName;
    private Request request;
    
    public RequestDTO() {}

    public RequestDTO(String firstName, String lastName, Request request) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.request = request;
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

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "EmployeeRequest [firstName=" + firstName + ", lastName=" + lastName + ", request=" + request + "]";
    }
}
