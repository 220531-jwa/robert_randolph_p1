Feature: New Request

  Scenario: A user creates a new request
    Given The user is logged in and on the home page with your requests
    When The user clicks on the New Request button
    And The user enters the required fields
    And The user clicks the submit button
    Then The user will be on the home page
    And A new request will be in the table
