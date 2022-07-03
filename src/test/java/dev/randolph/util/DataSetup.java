package dev.randolph.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import dev.randolph.model.Employee;
import dev.randolph.model.Event;
import dev.randolph.model.GradeFormat;
import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.EmployeeType;
import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;

public class DataSetup {
    
    private static DataSetup dataSetup;
    
    private List<Employee> employeeTestSet;
    private List<Event> eventTestSet;
    private List<GradeFormat> gradeFormatTestSet;
    private List<Request> requestTestSet;
    private List<RequestDTO> requestDTOTestSet;
    
    private DataSetup() {
        setupEmployeeTestSet();
        setupEventTestSet();
        setupGradeFormatTestSet();
        setupRequestDTOTestSet();
    }
    
    public static DataSetup getDataSetup() {
        if (dataSetup == null) {
            dataSetup = new DataSetup();
        }
        
        return dataSetup;
    }
    
    // === SETUP ===
    private void setupEmployeeTestSet() {
        employeeTestSet = new ArrayList<Employee>();
        employeeTestSet.add(new Employee("admin1", "secret1", "Wolf", "Flow", EmployeeType.MANAGER, 1000.00, 100.00));
        employeeTestSet.add(new Employee("admin2", "secret2", "Wolf2", "Flow2", EmployeeType.MANAGER, 0.00, 0.00));
        employeeTestSet.add(new Employee("user1", "pass1", "Alice", "Apple", EmployeeType.EMPLOYEE, 1000.00, 100.00));
        employeeTestSet.add(new Employee("user2", "pass2", "Bob", "Bacon", EmployeeType.EMPLOYEE, 0.00, 0.00));
        employeeTestSet.add(new Employee("user3", "pass3", "Carl", "Cake", EmployeeType.EMPLOYEE, 500.50, 50.50));
    }
    
    private void setupEventTestSet() {
        eventTestSet = new ArrayList<Event>();
        eventTestSet.add(new Event(EventType.UNIVERSITY, 0.80));
        eventTestSet.add(new Event(EventType.SEMINAR, 0.60));
        eventTestSet.add(new Event(EventType.CERTIFICATION_PREP, 0.75));
        eventTestSet.add(new Event(EventType.CERTIFICATION, 1.00));
        eventTestSet.add(new Event(EventType.TRAINING, 0.90));
        eventTestSet.add(new Event(EventType.OTHER, 0.30));
    }
    
    private void setupGradeFormatTestSet() {
        gradeFormatTestSet = new ArrayList<GradeFormat>();
        gradeFormatTestSet.add(new GradeFormat(GradeFormatType.LETTER, "C", false));
        gradeFormatTestSet.add(new GradeFormat(GradeFormatType.PASSFAIL, "P", true));
    }
    
    private void setupRequestTestSet() {
        requestTestSet = new ArrayList<Request>();
        requestTestSet.add(new Request(1, "admin1", EventType.UNIVERSITY,   RequestStatus.PENDING_APPROVAL,     500.00, 100.00,  GradeFormatType.LETTER,   "B",   "C", "Because I can",   getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", true,  false, "my reason"));
        requestTestSet.add(new Request(2, "admin1", EventType.OTHER,        RequestStatus.APPROVED,             20.00, 100.00,   GradeFormatType.LETTER,   "B",   "C", "Because I can",   getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", true,  false, "my reason"));
        requestTestSet.add(new Request(3, "user1", EventType.CERTIFICATION, RequestStatus.PENDING_REVIEW,       100.00, 100.00,  GradeFormatType.PASSFAIL, null,  "P", "just",            getTS("2022-06-09 12:13:14"), getTS("2022-06-01 12:13:14"), "loc", "desc", false, false, null));
        requestTestSet.add(new Request(4, "user1", EventType.UNIVERSITY,    RequestStatus.PENDING_APPROVAL,     500.00, 100.00,  GradeFormatType.LETTER,   "B",   "C", "Because I can",   getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", true,  false, "my reason"));
        requestTestSet.add(new Request(5, "user1", EventType.SEMINAR,       RequestStatus.APPROVED,             20.99, 10.00,    GradeFormatType.PASSFAIL,  null, "P", "Why Not",         getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", false, false,  null));
        requestTestSet.add(new Request(6, "user2", EventType.CERTIFICATION, RequestStatus.PENDING_GRADE,        120.50, 60.00,   GradeFormatType.PASSFAIL, "P",   "P", "Woot",            getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", false, true,   null));
        requestTestSet.add(new Request(7, "user2", EventType.TRAINING,      RequestStatus.REJECTED,             1500.00, 750.00, GradeFormatType.PASSFAIL, "F",   "P", "Gimmi Teh Cash",  getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", true,  true,   null));
        requestTestSet.add(new Request(8, "user3", EventType.OTHER,         RequestStatus.CANCELLED,            15.00, 15.00,    GradeFormatType.LETTER,    null, "B", "Testing",         getTS("2022-06-30 12:13:14"), getTS("2022-06-30 12:13:14"), "loc", "desc", false, false,  "my reason"));
    }
    
    private void setupRequestDTOTestSet() {
        setupRequestTestSet();
        requestDTOTestSet = new ArrayList<RequestDTO>();
        Employee emp;
        int employeeIndex = 0;
        
        // Going through requests, and getting associated employees
        // Assumes employee and request usernames are in the same 'order'
        for (Request req: requestTestSet) {
            // Checking if request
            emp = employeeTestSet.get(employeeIndex);
            // Going through employees for match
            while (!req.getEmployeeUsername().equals(emp.getUsername())) {
                // didn't find match
                employeeIndex++;    // Next employee
                emp = employeeTestSet.get(employeeIndex);
            }
            // Found match
            requestDTOTestSet.add(new RequestDTO(emp.getFirstName(), emp.getLastName(), emp.getReimFunds(), req));
        }
    }
    
    // === GETTERS ===

    public List<Employee> getEmployeeTestSet() {
        return new ArrayList<Employee>(employeeTestSet);
    }

    public List<Event> getEventTestSet() {
        return new ArrayList<Event>(eventTestSet);
    }

    public List<GradeFormat> getGradeFormatTestSet() {
        return new ArrayList<GradeFormat>(gradeFormatTestSet);
    }

    public List<Request> getRequestTestSet() {
        return new ArrayList<Request>(requestTestSet);
    }
    
    public List<RequestDTO> getRequestDTOTestSet() {
        return new ArrayList<RequestDTO>(requestDTOTestSet);
    }
    
    // === UTILITY ===
    
    private Timestamp getTS(String timestamp) {
        return Timestamp.valueOf(timestamp);
    }
}
