package com.example.helloworld.exception;

/**
 * Created by amala on 14/06/16.
 */
import com.example.helloworld.logging.Log;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class ResponseException {

    public static void formatAndThrow(Response.Status status, String message) {
        Log.log(message);
        throw new WebApplicationException(Response.status(status).entity("{\"error\":\"" + message + "\"}").build());
    }
}