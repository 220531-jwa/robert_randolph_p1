package dev.randolph.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.randolph.model.Employee;
import dev.randolph.model.Event;
import dev.randolph.model.GradeFormat;
import dev.randolph.model.Request;
import dev.randolph.model.DTO.RequestDTO;
import dev.randolph.model.enums.EventType;
import dev.randolph.model.enums.GradeFormatType;
import dev.randolph.model.enums.RequestStatus;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.repo.MetaDAO;
import dev.randolph.repo.RequestDAO;
import dev.randolph.util.ActiveEmployeeSessions;
import dev.randolph.util.DataSetup;
import kotlin.Pair;

@ExtendWith(MockitoExtension.class)
public class RequestServiceTests {
    
    // Init
    private static RequestService reqService;
    private static DataSetup dataSetup = DataSetup.getDataSetup();
    
    // Mock DAOs
    private static EmployeeDAO mockEmpDAO;
    private static MetaDAO mockMetaDAO;
    private static RequestDAO mockRequestDAO;
    
    // Mock database data
    private static List<Employee> mockEmps;
    private static List<Event> mockEvents;
    private static List<GradeFormat> mockGradeFormats;
    private static List<RequestDTO> mockReqDTOs;
    private static Hashtable<String, List<RequestDTO>> filtersStorage;
    
    // Statuses
    private static RequestStatus[] allStatuses = RequestStatus.values();
    private static RequestStatus[] pendingStatuses = RequestStatus.getPending();
    private static RequestStatus[] finishedStatuses = RequestStatus.getFinished();
    
    @BeforeAll
    private static void setup() {
        // Init
        mockEmpDAO = mock(EmployeeDAO.class);
        mockMetaDAO = mock(MetaDAO.class);
        mockRequestDAO = mock(RequestDAO.class);
        reqService = new RequestService(mockEmpDAO, mockRequestDAO);
        refreshMockData();
        
        // Mock data
        mockEvents = dataSetup.getEventTestSet();
        mockGradeFormats = dataSetup.getGradeFormatTestSet();
        
        // Mocking database behavior
        when(mockMetaDAO.getMetaData()).thenReturn(new Pair<>(mockGradeFormats, mockEvents));
        when(mockMetaDAO.getEventsMetaData()).thenReturn(mockEvents);
        when(mockMetaDAO.getGradeFormatsMetaData()).thenReturn(mockGradeFormats);
    }
    
    @AfterEach
    void cleanup() {
        ActiveEmployeeSessions.clearAllActiveSessions();
        refreshMockData();
    }
    
    private static void refreshMockData() {
        // Getting mock database data
        mockEmps = dataSetup.getEmployeeTestSet();
        mockReqDTOs = dataSetup.getRequestDTOTestSet();
        filtersStorage = null;
        
        // Mocking database behavior
        // Employee
        for (Employee emp: mockEmps) {
            when(mockEmpDAO.getEmployeeByUsername(emp.getUsername())).thenReturn(emp);
        }
        
        // RequestDTO
        when(mockRequestDAO.getAllRequests(null)).thenReturn(getFilteredRequestDTOs(null, null));
        when(mockRequestDAO.getAllRequests(allStatuses)).thenReturn(getFilteredRequestDTOs(null, null));
        when(mockRequestDAO.getAllRequests(pendingStatuses)).thenReturn(getFilteredRequestDTOs(null, "PENDING"));
        when(mockRequestDAO.getAllRequests(finishedStatuses)).thenReturn(getFilteredRequestDTOs(null, "FINISHED"));
        for (Employee emp: mockEmps) {
            when(mockRequestDAO.getAllEmployeeRequests(emp.getUsername(), null)).thenReturn(getFilteredRequestDTOs(emp.getUsername(), null));
            when(mockRequestDAO.getAllEmployeeRequests(emp.getUsername(), allStatuses)).thenReturn(getFilteredRequestDTOs(emp.getUsername(), null));
            when(mockRequestDAO.getAllEmployeeRequests(emp.getUsername(), pendingStatuses)).thenReturn(getFilteredRequestDTOs(emp.getUsername(), "PENDING"));
            when(mockRequestDAO.getAllEmployeeRequests(emp.getUsername(), finishedStatuses)).thenReturn(getFilteredRequestDTOs(emp.getUsername(), "FINISHED"));
        }
        for (RequestDTO reqDTO: mockReqDTOs) {
            when(mockRequestDAO.getEmployeeRequestById(reqDTO.getRequest().getEmployeeUsername(), reqDTO.getRequest().getId())).thenReturn(reqDTO);
        }
    }
    
    private static List<RequestDTO> getFilteredRequestDTOs(String username, String statusFilter) {
        // Checking if making new ones
        if (filtersStorage == null) {
            filtersStorage = new Hashtable<String, List<RequestDTO>>();
        }
        
        // Returning already filtered items.
        if (statusFilter == null) {
            statusFilter = "";
        }
        if (filtersStorage.containsKey(username + statusFilter)) {
            List<RequestDTO> requests = filtersStorage.get(username + statusFilter);
            return requests.isEmpty() ? null : requests;
        }
        
        // Getting filters
        RequestStatus[] filters = RequestStatus.getFilters(statusFilter);
        
        // Init
        ArrayList<RequestStatus> filterList = new ArrayList<RequestStatus>(Arrays.asList(filters));
        List<RequestDTO> requests = new ArrayList<RequestDTO>();
        RequestStatus reqDTOStatus;
        
        // Going through requests to find ones associated with username.
        // Only keeps requests depending on filter
        for (RequestDTO reqDTO: mockReqDTOs) {
            reqDTOStatus = reqDTO.getRequest().getStatus();
            if ((username == null || reqDTO.getRequest().getEmployeeUsername().equals(username)) && filterList.contains(reqDTOStatus)) {
                requests.add(reqDTO);
            }
        }
        filtersStorage.put(username + statusFilter, requests);
        return requests.isEmpty() ? null : requests;
    }
    
    private Request getDefaultValidNewRequest() {
        return new Request(null, null, EventType.CERTIFICATION, null, 100.00,
                null, GradeFormatType.PASSFAIL, null, "P", "just",
                Timestamp.from(Instant.now().plus(8, ChronoUnit.DAYS)), null, "loc", "desc",
                null, null, null);
    }
    
    // === TESTING getAllRequests === 
    
    @ParameterizedTest
    @MethodSource("gar_invalidInputs")
    public void gar_invalidInputs_400null(String statusFilter, String token) {
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(statusFilter, token);
        Object[] expected = {null, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gar_invalidInputs() {
        return Stream.of(
                Arguments.of(null, ""),
                Arguments.of(null, null),
                Arguments.of("ALL", ""),
                Arguments.of("ALL", null));
    }
    
    @Test
    public void gar_userNotActive_401null() {
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(null, "myNotActiveToken");
        Object[] expected = {null, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gar_activeUserDoesNotExist_503null() {
        // Should never happen
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(null, token);
        Object[] expected = {null, 503};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gar_userNotAuthorized_403null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(null, token);
        Object[] expected = {null, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @CsvSource(value = {",admin1", "PENDING,admin1", "FINISHED,admin1"})
    public void gar_userIsAuthorized_200RequestDTOList(String statusFilter, String username) {
        String token = ActiveEmployeeSessions.addActiveEmployee(username);
        Pair<List<RequestDTO>, Integer> result = reqService.getAllRequests(statusFilter, token);
        Object[] expected = {getFilteredRequestDTOs(null, statusFilter), 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    // === TESTING getAllEmployeeRequests === 
    
    @ParameterizedTest
    @MethodSource("gaer_invalidInputs")
    public void gaer_invalidInputs_400null(String username, String token) {
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests(username, null, token);
        Object[] expected = {null, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gaer_invalidInputs() {
        return Stream.of(
                Arguments.of(null, "a"),
                Arguments.of("a", null),
                Arguments.of("", "a"),
                Arguments.of("a", ""));
    }
    
    @Test
    public void gaer_userNotActive_401null() {
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests("user", null, "myNotActiveToken");
        Object[] expected = {null, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gaer_activeUserDoesNotExist_503null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests("user1", null, token);
        Object[] expected = {null, 503};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gaer_userNotAuthorized_403null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests("user2", null, token);
        Object[] expected = {null, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gaer_userIsAuthorized_butEmployeeDoesNotExist_404null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests("user", null, token);
        Object[] expected = {null, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gaer_userIsAuthorized_butNoRequestFoundDueToFilter_404null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user3");
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests("user3", "PENDING", token);
        Object[] expected = {getFilteredRequestDTOs("user3", "PENDING"), 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @MethodSource("gaer_validInputsNoFilters")
    public void gaer_userIsAuthorized_noFilters_200RequestDTOList(String username, String userUsername) {
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests(username, null, token);
        Object[] expected = {getFilteredRequestDTOs(username, null), 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gaer_validInputsNoFilters() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        Request req;
        
        for (RequestDTO reqDTO: mockReqDTOs) {
            req = reqDTO.getRequest();
            arguments.add(Arguments.of(req.getEmployeeUsername(), req.getEmployeeUsername()));
            arguments.add(Arguments.of(req.getEmployeeUsername(), "admin1"));
        }
        
        return arguments.stream();
    }
    
    @ParameterizedTest
    @MethodSource("gaer_validInputsWithFilters")
    public void gaer_userIsAuthorized_withFilters_200RequestDTOList(String username, String statusFilter, String userUsername) {
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<List<RequestDTO>, Integer> result = reqService.getAllEmployeeRequests(username, statusFilter, token);
        Object[] expected = {getFilteredRequestDTOs(username, statusFilter), 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gaer_validInputsWithFilters() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        Request req;
        
        for (RequestDTO reqDTO: mockReqDTOs) {
            req = reqDTO.getRequest();
            // Null was tested with "noFilters"
            arguments.add(Arguments.of(req.getEmployeeUsername(), "ALL", req.getEmployeeUsername()));
            arguments.add(Arguments.of(req.getEmployeeUsername(), "FINISHED", req.getEmployeeUsername()));
            arguments.add(Arguments.of(req.getEmployeeUsername(), "ALL", "admin1"));
            arguments.add(Arguments.of(req.getEmployeeUsername(), "FINISHED", "admin1"));
            
            if (!req.getEmployeeUsername().equals("user3")) {
                // user 3 has NO pending requests -> skipped -> tested in gaer_userIsAuthorized_butNoRequestFoundDueToFilter_404null
                arguments.add(Arguments.of(req.getEmployeeUsername(), "PENDING", req.getEmployeeUsername()));
                arguments.add(Arguments.of(req.getEmployeeUsername(), "PENDING", "admin1"));
            }
        }
        
        return arguments.stream();
    }
    
    // === TESTING getEmployeeRequestById === 
    
    @ParameterizedTest
    @MethodSource("gerbi_invalidInputs")
    public void gerbi_invalidInputs_400null(String username, Integer rid, String token) {
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById(username, rid, token);
        Object[] expected = {null, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gerbi_invalidInputs() {
        return Stream.of(
                Arguments.of(null, 0, "a"),
                Arguments.of("a", null, "a"),
                Arguments.of("a", 0, null),
                Arguments.of("", 0, "a"),
                Arguments.of("a", -1, "a"),
                Arguments.of("a", 0, ""));
    }
    
    @Test
    public void gerbi_userNotActive_401null() {
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById("user", 0, "myNotActiveToken");
        Object[] expected = {null, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gerbi_activeUserDoesNotExist_503null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById("user1", 0, token);
        Object[] expected = {null, 503};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gerbi_userNotAuthorized_403null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById("user2", 0, token);
        Object[] expected = {null, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gerbi_userIsAuthorized_butRequestDoesNotExist_requestIDDoesNotExist_404null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById("user1", 9, token);
        Object[] expected = {null, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gerbi_userIsAuthorized_butRequestDoesNotExist_requestUsernameDoesNotExist_404null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById("user", 1, token);
        Object[] expected = {null, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @MethodSource("gerbi_validInputs")
    public void gerbi_userIsAuthorize_200Request(String username, Integer id, String userUsername) {
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<RequestDTO, Integer> result = reqService.getEmployeeRequestById(username, id, token);
        
        // Finding expected request
        RequestDTO expectedDTO = null;
        for (RequestDTO reqDTO: mockReqDTOs) {
            if (reqDTO.getRequest().getEmployeeUsername().equals(username) && reqDTO.getRequest().getId() == id) {
                // Found
                expectedDTO = reqDTO;
                break;
            }
        }
        
        Object[] expected = {expectedDTO, 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gerbi_validInputs() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        Request req;
        
        for (RequestDTO reqDTO: mockReqDTOs) {
            req = reqDTO.getRequest(); 
            arguments.add(Arguments.of(req.getEmployeeUsername(), req.getId(), req.getEmployeeUsername()));
            arguments.add(Arguments.of(req.getEmployeeUsername(), req.getId(), "admin1"));
        }
        return arguments.stream();
    }
    
    
    // === TESTING createRequest === 
    
    @ParameterizedTest
    @MethodSource("cr_invalidInputs_nullBlank")
    public void cr_invalidInputs_nullBlank_400false(String username, Request reqData, String token) {
        Pair<Boolean, Integer> result = reqService.createRequest(username, reqData, token);
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> cr_invalidInputs_nullBlank() {
        Request req = new Request();
        return Stream.of(
                Arguments.of(null, req, "a"),
                Arguments.of("a", null, "a"),
                Arguments.of("a", req, null),
                Arguments.of("", req, "a"),
                Arguments.of("a", req, ""));
    }
    
    @ParameterizedTest
    @MethodSource("cr_invalidInputs_reqDataNullBlank")
    public void cr_invalidInputs_reqDataNullBlank_400false(Request reqData) {
        Pair<Boolean, Integer> result = reqService.createRequest("a", reqData, "a");
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> cr_invalidInputs_reqDataNullBlank() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        
        for (int i = 0; i < 8; i++) {
            arguments.add(Arguments.of(new Request(1, "username", i==0?null:EventType.CERTIFICATION, RequestStatus.APPROVED, i==1?null:0.00,
                                                        0.00, i==2?null:GradeFormatType.LETTER, "grade", i==3?null:"cutoff", i==4?null:"just",
                                                        i==5?null:Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), i==6?null:"loc", i==7?null:"desc",
                                                        false, false, "reason")));
        }
        
        return arguments.stream();
    }
    
    @ParameterizedTest
    @MethodSource("cr_invalidInputs_reqDataInvalid")
    public void cr_invalidInputs_reqDataInvalid_400false(Request reqData) {
        Pair<Boolean, Integer> result = reqService.createRequest("a", reqData, "a");
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> cr_invalidInputs_reqDataInvalid() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        
        for (int i = 0; i < 7; i++) {
            arguments.add(Arguments.of(new Request(1, "username", EventType.CERTIFICATION, RequestStatus.APPROVED, i==0?-1.00:i==1?10000.00:0.00,
                                                        0.00, i==2?GradeFormatType.LETTER:GradeFormatType.PASSFAIL, "grade", i==2||i==3?"cutoff":"P", i==4?"":"just",
                                                        Timestamp.from(Instant.now()), Timestamp.from(Instant.now()), i==5?"":"loc", i==6?"":"desc",
                                                        false, false, "reason")));
        }
        
        return arguments.stream();
    }
    
    @Test
    public void cr_userNotActive_401false() {
        Pair<Boolean, Integer> result = reqService.createRequest("user1", getDefaultValidNewRequest(), "myNotActiveToken");
        Object[] expected = {false, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"user1", "admin1"})
    public void cr_userNotAuthorized_403false(String userUsername) {
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<Boolean, Integer> result = reqService.createRequest("user2", getDefaultValidNewRequest(), token);
        Object[] expected = {false, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void cr_userIsAuthorized_butEmployeeUsernameDoesNotExist_404false() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<Boolean, Integer> result = reqService.createRequest("user", getDefaultValidNewRequest(), token);
        Object[] expected = {false, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void cr_userIsAuthorized_userIsAuthorized_notUrgentNotExceedsFunds_true201() {
        // Setting up mock
        Request req = getDefaultValidNewRequest();
        when(mockRequestDAO.createRequest(req)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.createRequest("user1", req, token);
        
        // Finding expectedEventReimAmount
        Double expectedReimAmount = null;
        for (Event event: mockEvents) {
            if (event.getType() == req.getEventType()) {
                expectedReimAmount = req.getCost() * event.getReimPercent();
                expectedReimAmount = (double) (Math.round(expectedReimAmount * 100) / 100);
                break;
            }
        }
        
        // Finding expected submissionDate - within 1 min of NOW (after it was created)
        long diff = req.getSubmissionDate().getTime() - Timestamp.from(Instant.now()).getTime();
        diff = TimeUnit.MILLISECONDS.toMinutes(diff);
        boolean expectedSubmissionDate = diff < 1;
        
        Object[] expected = {
                // Service Return
                true, 201,
                // Calculated Request Details
                "user1", RequestStatus.PENDING_REVIEW,
                expectedReimAmount, expectedSubmissionDate,
                false, false
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // Calculated Request Details
                req.getEmployeeUsername(), req.getStatus(),
                req.getReimAmount(), true, // true if submission date within 1 minute
                req.getIsUrgent(), req.getExceedsFunds()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @ValueSource(ints = {1, 0, -1})
    public void cr_userIsAuthorized_userIsAuthorized_isUrgentNotExceedsFunds_true201(int dayOffset) {
        // Setting up mock
        Request req = getDefaultValidNewRequest();
        req.setStartDate(Timestamp.from(Instant.now().plus(dayOffset, ChronoUnit.DAYS)));
        when(mockRequestDAO.createRequest(req)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.createRequest("user1", req, token);
        
        // Finding expectedEventReimAmount
        Double expectedReimAmount = null;
        for (Event event: mockEvents) {
            if (event.getType() == req.getEventType()) {
                expectedReimAmount = req.getCost() * event.getReimPercent();
                expectedReimAmount = (double) (Math.round(expectedReimAmount * 100) / 100);
                break;
            }
        }
        
        // Finding expected submissionDate - within 1 min of NOW (after it was created)
        long diff = req.getSubmissionDate().getTime() - Timestamp.from(Instant.now()).getTime();
        diff = TimeUnit.MILLISECONDS.toMinutes(diff);
        boolean expectedSubmissionDate = diff < 1;
        
        Object[] expected = {
                // Service Return
                true, 201,
                // Calculated Request Details
                "user1", RequestStatus.PENDING_REVIEW,
                expectedReimAmount, expectedSubmissionDate,
                true, false
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // Calculated Request Details
                req.getEmployeeUsername(), req.getStatus(),
                req.getReimAmount(), true, // true if submission date within 1 minute
                req.getIsUrgent(), req.getExceedsFunds()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @CsvSource(value = {"user1,1000","user2,0","user3,500.50"})
    public void cr_userIsAuthorized_userIsAuthorized_NotUrgentIsExceedsFunds_true201(String userUsername, Double expecetedReimAmount) {
        // Setting up mock
        Request req = getDefaultValidNewRequest();
        req.setCost(9999.99);
        when(mockRequestDAO.createRequest(req)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<Boolean, Integer> result = reqService.createRequest(userUsername, req, token);
        
        // Finding expectedEventReimAmount
        Double expectedReimAmount = expecetedReimAmount;    // The rest of the available funds for them.
        
        // Finding expected submissionDate - within 1 min of NOW (after it was created)
        long diff = req.getSubmissionDate().getTime() - Timestamp.from(Instant.now()).getTime();
        diff = TimeUnit.MILLISECONDS.toMinutes(diff);
        boolean expectedSubmissionDate = diff < 1;
        
        Object[] expected = {
                // Service Return
                true, 201,
                // Calculated Request Details
                userUsername, RequestStatus.PENDING_REVIEW,
                expectedReimAmount, expectedSubmissionDate,
                false, true
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // Calculated Request Details
                req.getEmployeeUsername(), req.getStatus(),
                req.getReimAmount(), true, // true if submission date within 1 minute
                req.getIsUrgent(), req.getExceedsFunds()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    // === TESTING updateRequest === 
    
    @ParameterizedTest
    @MethodSource("ur_invalidInputs")
    public void ur_invalidInputs_nullBlank_400false(String username, Integer rid, Request regData, String token) {
        Pair<Boolean, Integer> result = reqService.updateRequest(username, rid, regData, token);
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> ur_invalidInputs() {
        Request req = new Request();
        return Stream.of(
                Arguments.of(null, 0, req, "a"),
                Arguments.of("a", null, req, "a"),
                Arguments.of("a", 0, null, "a"),
                Arguments.of("a", 0, req, null),
                Arguments.of("", 0, req, "a"),
                Arguments.of("a", -1, req, "a"),
                Arguments.of("a", 0, req, ""));
    }
    
    @Test
    public void ur_userNotActive_401false() {
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 1, new Request(), "myNotActiveToken");
        Object[] expected = {false, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_userIsActive_butUserEmployeeUsernameDoesNotExist_503false() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<Boolean, Integer> result = reqService.updateRequest("user", 1, new Request(), token);
        Object[] expected = {false, 503};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_userNotAuthorized_403false() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user2", 1, new Request(), token);
        Object[] expected = {false, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_userIsAuthorized_butRequestDoesNotExist_404false() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 8, new Request(), token);
        Object[] expected = {false, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_userIsAuthorized_butRequestNotAssociatedWithUsername_404false() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 1, new Request(), token);
        Object[] expected = {false, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"user1", "admin1"})
    public void ur_userIsAuthorized_butRequestIsFinished_403false(String userUsername) {
        String token = ActiveEmployeeSessions.addActiveEmployee(userUsername);
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 5, new Request(), token);
        Object[] expected = {false, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedGrade_invalidGrade_400false() {
        Request reqData = new Request();
        reqData.setGrade("A");
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 3, reqData, token);
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedGrade_validGrade_changedToP_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setGrade("P");
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getGrade()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getGrade()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedGrade_validGrade_statusWasPendingGrade_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setGrade("P");
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        request.setStatus(RequestStatus.PENDING_GRADE); // Setting initial request to pending_grade for test
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getGrade(), RequestStatus.PENDING_APPROVAL
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getGrade(), request.getStatus()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedGrade_validGrade_changedToNull_200true() {
        // Setting up mock
        Request reqData = new Request();
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 4) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getGrade()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getGrade()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @EnumSource(value = RequestStatus.class, names = {"PENDING_GRADE", "PENDING_APPROVAL", "APPROVED", "REJECTED"})
    public void ur_employeeChangedStatus_invalidStatus_403false(RequestStatus status) {
        Request reqData = new Request();
        reqData.setStatus(status);
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 3, reqData, token);
        Object[] expected = {false, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedStatus_nullStatus_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setStatus(null);
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {true, 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_employeeChangedStatus_validStatusIsCanceled_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setStatus(RequestStatus.CANCELLED);
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("user1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getStatus()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getStatus()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @ValueSource(doubles = {-1.00, 10000.00})
    public void ur_managerChangedRiemAmount_invalidRange_400false(Double reimAmount) {
        Request reqData = new Request();
        reqData.setReimAmount(reimAmount);
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 3, reqData, token);
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedRiemAmount_reasonNotProvided_400false() {
        Request reqData = new Request();
        reqData.setReimAmount(50.00);
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 3, reqData, token);
        Object[] expected = {false, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedRiemAmount_validReimAmountValidReason_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setReimAmount(50.00);
        reqData.setReason("Halved Because I can");
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getReimAmount(), reqData.getReason()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getReimAmount(), request.getReason()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedRiemAmount_validReimAmountNullValue_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setReimAmount(null);
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {true, 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedReason_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setReason("Random Reason");
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getReason()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getReason()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedStatus_invalidStatus_cannotApproveOwnRequest_400false() {
        Request reqData = new Request();
        reqData.setGrade("B");  // syncing with data set
        reqData.setStatus(RequestStatus.APPROVED);
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest("admin1", 1, reqData, token);
        Object[] expected = {false, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedStatus_nullStatus_200true() {
        Request reqData = new Request();
        reqData.setStatus(null);
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest("user1", 3, reqData, token);
        Object[] expected = {true, 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @EnumSource(value =  RequestStatus.class, names = {"PENDING_REVIEW", "PENDING_GRADE", "PENDING_APPROVAL", "REJECTED", "CANCELLED"})
    public void ur_managerChangedStatus_validStatusNotApproved_200true(RequestStatus status) {
        // Setting up mock
        Request reqData = new Request();
        reqData.setStatus(status);
        Request request = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            request = reqDTO.getRequest();
            if (request.getEmployeeUsername().equals("user1") && request.getId() == 3) {
                break;
            }
        }
        when(mockRequestDAO.updateRequest(request)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest(request.getEmployeeUsername(), request.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getStatus()
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                request.getStatus()
                };
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void ur_managerChangedStatus_toApproved_200true() {
        // Setting up mock
        Request reqData = new Request();
        reqData.setStatus(RequestStatus.APPROVED);
        Request changedRequest = null;
        Employee changedEmp = null;
        Double expectedFunds = null;
        Double expectedReimAmount = null;
        // Finding request that will change
        for (RequestDTO reqDTO: mockReqDTOs) {
            changedRequest = reqDTO.getRequest();
            if (changedRequest.getEmployeeUsername().equals("user1") && changedRequest.getId() == 3) {
                break;
            }
        }
        // Finding employee associated with request that will change
        for (Employee emp: mockEmps) {
            changedEmp = emp;
            if (changedEmp.getUsername().equals(changedRequest.getEmployeeUsername())) {
                expectedFunds = emp.getFunds() + changedRequest.getReimAmount();
                expectedReimAmount = emp.getReimFunds() - changedRequest.getReimAmount();
                if (expectedReimAmount < 0) {expectedReimAmount = 0.00;}
                break;
            }
        }
        when(mockEmpDAO.updateEmployeeFunds(changedEmp)).thenReturn(true);
        when(mockRequestDAO.updateRequest(changedRequest)).thenReturn(true);
        
        // Testing
        String token = ActiveEmployeeSessions.addActiveEmployee("admin1");
        Pair<Boolean, Integer> result = reqService.updateRequest(changedRequest.getEmployeeUsername(), changedRequest.getId(), reqData, token);
        Object[] expected = {
                // Service Return
                true, 200,
                // Changed values
                reqData.getStatus(),                // Request
                expectedFunds, expectedReimAmount   // Employee
                };
        Object[] actual = {
                // Service Return
                result.getFirst(), result.getSecond(),
                // changed values
                changedRequest.getStatus(),                                // Request
                changedEmp.getFunds(), changedEmp.getReimFunds()    // Employee
                };
        
        assertArrayEquals(expected, actual);
    }
}
