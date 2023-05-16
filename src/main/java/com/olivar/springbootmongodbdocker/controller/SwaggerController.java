package com.olivar.springbootmongodbdocker.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@PreAuthorize("hasAuthority('SCOPE_{frabicantes}')")
public class SwaggerController {

    @ApiIgnore
    @RequestMapping(value = "/")
    public void  redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui.html");
    }
}
