Feature: View Requests

  Scenario: An employee can see all of their own requests
    Given The employee is logged in and on the home page
    When The user clicks the Your Requests button
    Then The user will see all their requests in a table

  Scenario: A manager can see all requests
    Given The manager is logged in and on the home page
    When The user clicks the Manage Requests button
    Then The user will see all the requests

  Scenario Outline: A user can filter the requests
    Given The user is logged in and on the home page
    When The user clicks on the filter dropdown
    And Clicks a status filter option "<option>"
    Then The user will see only requests of that option

    Examples: 
      | option   |
      | PENDING  |
      | FINISHED |

  Scenario: A user can see a specific request
    Given The user is logged in and on the home page
    When The user clicks on a request in the table
    Then The user will be on the request page
