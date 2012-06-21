package org.canvas.rest.hello;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Path("/helloworld")
public class HelloWorldResource {

    @GET
    @Produces("application/json")
    public HelloWorld getClichedMessage(@Context HttpServletRequest request) {

        HelloWorld helloWorld = new HelloWorld();
        request.getServerName();
        String greeting = String.format("Hola from %s it's now %s", request.getServerName(), getTime());
        helloWorld.setGreeting(greeting);
        helloWorld.setLocale("es");
        return helloWorld;
    }

    private String getTime() {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(new Date());
    }
}

