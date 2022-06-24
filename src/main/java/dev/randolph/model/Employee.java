package dev.randolph.model;

import dev.randolph.model.enums.EmployeeType;

public class Employee {
    
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private EmployeeType type;
    private double reimFunds;
    private double funds;
    private String token;   // For when employee is logged in
    
    public Employee() {}

    public Employee(int id, String username, String password, String firstName, String lastName, EmployeeType type,
            double reimFunds, double funds, String token) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.reimFunds = reimFunds;
        this.funds = funds;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getReimFunds() {
        return reimFunds;
    }

    public void setReimFunds(double reimFunds) {
        this.reimFunds = reimFunds;
    }

    public double getFunds() {
        return funds;
    }

    public void setFunds(double funds) {
        this.funds = funds;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Employee [id=" + id + ", username=" + username + ", password=" + password + ", firstName=" + firstName
                + ", lastName=" + lastName + ", type=" + type + ", reimFunds=" + reimFunds + ", funds=" + funds
                + ", token=" + token + "]";
    };
}
