package dev.randolph;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import dev.randolph.controller.EmployeeController;
import dev.randolph.controller.MetaController;
import dev.randolph.controller.RequestController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Driver {
    
    public static void main(String[] args) {
        // Init server
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/public", Location.CLASSPATH);
        });
        
        // Creating controllers
        EmployeeController employeeC = new EmployeeController();
        RequestController requestC = new RequestController();
        MetaController metaC = new MetaController();
        
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
            path("/meta", () -> {
                get(metaC::getMetaData);
            });
            path("/employee/{username}", () -> {
                get(employeeC::getEmployeeByUsername);
            });
            path("/request", () -> {
                get(requestC::getAllRequests);
                path("/{username}", () -> {
                    post(requestC::createNewRequest);
                    get(requestC::getAllEmployeeRequests);
                    path("/{rid}", () -> {
                        get(requestC::getEmployeeRequestById);
                        patch(requestC::updateEmployeeRequestById);
                    });
                });
            });
        });
        
        // End of end-points
    }
}
