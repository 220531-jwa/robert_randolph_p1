package dev.randolph.service;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.randolph.model.Employee;
import dev.randolph.repo.EmployeeDAO;
import dev.randolph.util.ActiveEmployeeSessions;
import dev.randolph.util.DataSetup;
import kotlin.Pair;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    // Init
    private static EmployeeService empService;
    private static DataSetup dataSetup = DataSetup.getDataSetup();
    
    // Mock DAO
    private static EmployeeDAO mockEmpDAO;

    // Mock database data
    private static List<Employee> mockEmps;
    
    @BeforeAll
    private static void setup() {
        // Init
        mockEmpDAO = mock(EmployeeDAO.class);
        empService = new EmployeeService(mockEmpDAO);
        refreshMockData();
    }
    
    @AfterEach
    private void cleanup() {
        ActiveEmployeeSessions.clearAllActiveSessions();
        refreshMockData();
    }
    
    private static void refreshMockData() {
        // Getting mock database data
        mockEmps = dataSetup.getEmployeeTestSet();

        // Mocking database behavior
        for (Employee emp: mockEmps) {
            when(mockEmpDAO.getEmployeeByUsername(emp.getUsername())).thenReturn(emp);
        }
    }
    
    // === TESTING loginWithCredentials ===
    
    @ParameterizedTest
    @MethodSource("lwc_gebu_invalidInputs")
    public void lwc_invalidInputs_400null(String username, String password) {
        Pair<Employee, Integer> result = empService.loginWithCredentials(username, password);
        Object[] expected = {null, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> lwc_gebu_invalidInputs() {
        return Stream.of(
                Arguments.of("", ""),
                Arguments.of(null, null),
                Arguments.of("a", ""),
                Arguments.of("a", null),
                Arguments.of("", "a"),
                Arguments.of(null, "a"));
    }
    
    @Test
    public void lwc_employeeDoesNotExist_404null() {
        Pair<Employee, Integer> result = empService.loginWithCredentials("a", "pass");
        Object[] expected = {null, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void lwc_credentialsDoNotMatch_401null() {
        Pair<Employee, Integer> result = empService.loginWithCredentials("user1", "pass");
        Object[] expected = {null, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @MethodSource("lwc_validInputs")
    public void lwc_credentialsMatch_200EmployeeWithTokenPassword(int it, String username, String password) {
        // Init
        String oldEmployeePassword = mockEmps.get(it).getPassword();
        Pair<Employee, Integer> result = empService.loginWithCredentials(username, password);
        Object[] expected = {mockEmps.get(it), 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        assertArrayEquals(expected, actual);
        assertNotEquals(oldEmployeePassword, result.getFirst().getPassword());
    }
    private static Stream<Arguments> lwc_validInputs() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        int iteration = 0;
        for (Employee emp: mockEmps) {
            arguments.add(Arguments.of(iteration++, emp.getUsername(), emp.getPassword()));
        }
        return arguments.stream();
    }

    // === TESTING logout ===
    
    @ParameterizedTest
    @NullAndEmptySource
    public void l_invalidInputs_400(String token) {
        int result = empService.logout(token);
        assertEquals(400, result);
    }
    
    @Test
    public void l_employeeIsNotActive_404() {
        int result = empService.logout("MyNotActiveToken");
        assertEquals(404, result);
    }
    
    @Test
    public void l_employeeisActive_200() {
        String token = ActiveEmployeeSessions.addActiveEmployee("username");
        int result = empService.logout(token);
        assertEquals(200, result);
    }
    
    // === TESTING getEmployeeByUsername ===
    
    @ParameterizedTest
    @MethodSource("lwc_gebu_invalidInputs")
    public void gebu_invalidInputs_400null(String username, String token) {
        Pair<Employee, Integer> result = empService.getEmployeeByUsername(username, token);
        Object[] expected = {null, 400};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gebu_userNotActive_401null() {
        Pair<Employee, Integer> result = empService.getEmployeeByUsername("user1", "myNotActiveToken");
        Object[] expected = {null, 401};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"user", "user2", "admin1"})
    public void gebu_userNotAuthorized_403null(String username) {
        String token = ActiveEmployeeSessions.addActiveEmployee(username);
        Pair<Employee, Integer> result = empService.getEmployeeByUsername("user1", token);
        Object[] expected = {null, 403};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @Test
    public void gebu_userIsAuthorized_butEmployeeDoesNotExist_404null() {
        String token = ActiveEmployeeSessions.addActiveEmployee("user");
        Pair<Employee, Integer> result = empService.getEmployeeByUsername("user", token);
        Object[] expected = {null, 404};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    
    @ParameterizedTest
    @MethodSource("gebu_validInputs")
    public void gebu_userIsAuthorized_200Employee(int it, String username) {
        String token = ActiveEmployeeSessions.addActiveEmployee(username);
        Pair<Employee, Integer> result = empService.getEmployeeByUsername(username, token);
        Object[] expected = {mockEmps.get(it), 200};
        Object[] actual = {result.getFirst(), result.getSecond()};
        
        assertArrayEquals(expected, actual);
    }
    private static Stream<Arguments> gebu_validInputs() {
        List<Arguments> arguments = new ArrayList<Arguments>();
        int iteration = 0;
        for (Employee emp: mockEmps) {
            arguments.add(Arguments.of(iteration++, emp.getUsername()));
        }
        
        return arguments.stream();
    }
}
