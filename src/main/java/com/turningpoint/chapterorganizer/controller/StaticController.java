package com.turningpoint.chapterorganizer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to handle SPA routing by forwarding all non-API requests to index.html
 */
@Controller
public class StaticController {

    /**
     * Forward all requests (except API and static resources) to index.html for SPA routing
     */
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String forward() {
        return "forward:/index.html";
    }
}