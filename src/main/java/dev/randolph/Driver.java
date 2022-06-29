package dev.randolph;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import dev.randolph.controller.EmployeeController;
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
        
        // Starting server
        app.start(8080);

        // Handling end-points
        app.routes(() -> {
            path("/login", () -> {
                post(employeeC::loginWithCredentials);
            });
            path("logout", () -> {
                post(employeeC::logout);
            });
            path("/employee", () -> {
                get(employeeC::getEmployeeByUsername);
            });
            path("/request", () -> {
//                post(requestC::createNewRequest);
                get(requestC::getAllRequests);
                path("/{username}", () -> {
                    get(requestC::getAllEmployeeRequests);
                });
            });
        });
        
        // End of end-points
    }
}
