package dev.randolph;

import static io.javalin.apibuilder.ApiBuilder.*;

import dev.randolph.controller.EmployeeController;
import dev.randolph.controller.RequestController;
import io.javalin.Javalin;

public class Driver {

    public static void main(String[] args) {
        // Init server
        Javalin app = Javalin.create();
        
        // Creating controllers
        EmployeeController ec = new EmployeeController();
        RequestController rc = new RequestController();
        
        // Starting server
        app.start(8080);

        // Handling end-points
        app.routes(() -> {
            path("/login", () -> {
                post(ec::loginWithUsername);
            });
            path("logout", () -> {
                // No clue
            });
            path("/employee", () -> {
                get(ec::getEmployeeById);
            });
            path("/request", () -> {
                get(rc::getAllEmployeeRequests);
            });
        });
        
        // End of end-points
    }
}
