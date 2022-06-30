package dev.randolph.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.randolph.service.MetaService;
import io.javalin.http.Context;

public class MetaController {
    
    private MetaService metaService = new MetaService();
    private Logger log = LogManager.getLogger(MetaController.class);
    
    public void getMetaData(Context c) {
        log.debug("Http get request recieved at endpoint /meta");
        c.json(metaService.getMetaData());
        c.status(200);
    }
}
