Feature: Edit Request

  Scenario: An employee can provide a grade
    Given The employee is logged in and on the request to edit
    When The user enters a grade
    And The user clicks save
    Then The user is on the homepage

  Scenario: An employee can cancel the request
    Given The employee is logged in and on the request to edit
    When The user sets the status to cancelled
    And The user clicks save
    Then The user is on the homepage

  Scenario: A Manager can change the reimbursement amount
    Given The manager is logged in and on the request to edit
    When The manager sets the reimbursement amount
    And The manager provides a reason for the change
    And The user clicks save
    Then The user is on the homepage

  Scenario: A Manager can approve a status
    Given The manager is logged in and on the request to edit
    When The manager sets the status to approved
    And The user clicks save
    Then The user is on the homepage

  Scenario: A user can go back to the home page
    Given The employee is logged in and on the request page
    When The user clicks the back button
    Then The user is on the homepage
