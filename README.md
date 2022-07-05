# Employee Reimbursement System

## Project Description

The Employee Reimbursement System (ERS) manages the process of reimbursing employees for expenses related to professional development, such as University Courses, Certifications, and Technical Trainings. All employees in the company can login and submit requests for reimbursement, as well as view their past tickets, and pending requests. Finance managers can log in and view all reimbursement requests for all employees in the company. Finance managers are authorized to approve and deny requests for expense reimbursement.

## Technologies Used

* Eclipse IDE
* Maven
* Agile-Scrum

- Java
- Javalin
- JDBC
- JUnit
- Mockito
- Selenium
- WebDriver

* HTML
* Bootstrap - CSS

- PostgreSQL
- AWS RDS

## Features

Implemented Features:
* Employee/Manager is able to login/logout
* Employee/Manager is able to create a new request
* Employee/Manager is able to see their own requests
* Employee is able to cancel a request
* Manager is able to see all requests
* Manager is able to approve requests
  * Other then their own
* Manager is able to reject requests
* Manager is able to change reimbursement amount with provided reason.
  * Provided request isn't in a finished status. (I.e. already approved)

To-do & Improvements list:
* Add admin role with no restrictions on requests
* Add ability to register new employees
* Add ability to add new event types
* Add ability to add new grade formats
* Add ability to upload presentation/supporting documents to request

- Update client-server authentication/security
- Update server to initialize enum/type data from database. (i.e. event types)
- Update server to Minimize/Optimize database calls
- Update better tracking of available reimbursement funds and whether current pending reimbursements exceed the amount
- Improve DTOs to be more efficent

## Getting Started

Project setup:
- Clone project onto machine.
- Add project to eclispe as a maven project.
- Add connection.properties file into src/main/resources
  - Values: {postgresql driver, database (db) url, db username, db password}
  - Database used was AWS
  
Database setup:
- Conenct to database via DeBeaver
- Run db setup sql file (For tables and initial data set)

Start Program:
- Run driver from eclipse

## Usage

- Open you're prefered web browser.
  - Chrome was used
- Login using one of the credentials stored in the database.
- Home page:
  - Displays you're requests in a table.
    - Able to filter requests based on status. Found on the left above the request table
    - To see a specific request click the blue event type name of the request inside the table
  - Can click "New Request" on the right above the request table to make a new request
  - If you logged in as a manager you can click "Mange Requests" to see all employee requests
  - Can click "logout" on the top of the page to logout
- New request:
  - On the home page you can click the "New Request" button on the right above the request table.
  - Can enter initial data for a new request.
  - All fields are required
  - Can go back by clicking the 'Back' button on the bottom left of the page.
  - Can sumbit the new request by clicking the 'submit' button on the bottom right of the page.
- View/Update Exisitng request:
  - On the home page you can click the event type name of a specific request inside the table to see and edit it.
  - Grade can be updated if you are viewing your own request.
    - Can also cancel the request
  - If you are a manager you can change the status, and the reimbursement amount.
    - If the reimbursement amount is changed the reason for that change must be provided.
  - Note: If the request is in a finished state (i.e. Approved) the request cannot be edited and can only be read.
  - Can go back by clicking the 'Back' button on the bottom left of the page.
  - Can save any changes to the request by clicking the 'save' button on the bottom right of the page.
