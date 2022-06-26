package dev.randolph;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import dev.randolph.controller.EmployeeController;
import dev.randolph.controller.EventController;
import dev.randolph.controller.GradeFormatController;
import dev.randolph.controller.RequestController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Driver {
    
    public static void main(String[] args) {
        // Init server
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);       // Needed so html pages find scripts
        });
        
        // Creating controllers
        EmployeeController employeeC = new EmployeeController();
        RequestController requestC = new RequestController();
        EventController eventC = new EventController();
        GradeFormatController gradeFC = new GradeFormatController();
        
        // Starting server
        app.start(8080);

        // Handling end-points
        app.routes(() -> {
            path("/login", () -> {
                post(employeeC::loginWithCredentials);
            });
            path("logout", () -> {
                // No clue
            });
            path("/employee", () -> {
                get(employeeC::getEmployeeByUsername);
            });
            path("/request", () -> {
                post(requestC::createNewRequest);
                get(requestC::getAllEmployeeRequests);
            });
            path("/event", () ->{
                get(eventC::getAllEvents);
            });
            path("/grade", () -> {
               get(gradeFC::getAllGradeFormats);
            });
        });
        
        // End of end-points
    }
}
