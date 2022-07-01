package dev.randolph.model;

import dev.randolph.model.enums.EmployeeType;

public class Employee {

    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private EmployeeType type;
    private Double reimFunds;
    private Double funds;

    public Employee() {}

    public Employee(String username, String password, String firstName, String lastName, EmployeeType type,
            Double reimFunds, Double funds) {
        super();
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.reimFunds = reimFunds;
        this.funds = funds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public EmployeeType getType() {
        return type;
    }

    public void setType(EmployeeType type) {
        this.type = type;
    }

    public Double getReimFunds() {
        return reimFunds;
    }

    public void setReimFunds(Double reimFunds) {
        this.reimFunds = reimFunds;
    }

    public Double getFunds() {
        return funds;
    }

    public void setFunds(Double funds) {
        this.funds = funds;
    }

    @Override
    public String toString() {
        return "Employee [username=" + username + ", password=" + password + ", firstName=" + firstName + ", lastName="
                + lastName + ", type=" + type + ", reimFunds=" + reimFunds + ", funds=" + funds + "]";
    }
}
