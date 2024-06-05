package com.olivar.springbootmongodbdocker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SwaggerController {

    @ApiIgnore
    @RequestMapping(value = "/")
    public void  redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
