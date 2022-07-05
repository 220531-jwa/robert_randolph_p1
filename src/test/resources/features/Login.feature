Feature: Login & Logout

  Scenario Outline: A user can login with their credentials
    Given The user is on the login page
    When The user types in their "<username>" and "<password>" and click the signin button
    Then The user will be on the homepage

    Examples: 
      | username | password |
      | admin1   | secret1  |
      | user1    | pass1    |

  Scenario: A use can logout if they are logged in
    Given The user is logged in and on the homepage
    When The user clicks the logout button
    Then The user will be on the logout page
