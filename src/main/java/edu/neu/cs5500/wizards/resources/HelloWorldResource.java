//package edu.neu.cs5500.wizards.resources;
//
//import com.codahale.metrics.annotation.Timed;
//import com.google.common.base.Optional;
//import edu.neu.cs5500.wizards.api.Saying;
//import edu.neu.cs5500.wizards.core.Template;
//import io.dropwizard.jersey.caching.CacheControl;
//import io.dropwizard.jersey.params.DateTimeParam;
//import io.swagger.annotations.*;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.ws.rs.GET;
//import javax.ws.rs.Path;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.core.MediaType;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicLong;
//
//@Path("/hello-world")
//@Produces(MediaType.APPLICATION_JSON)
//@Api(value = "/hello-world", description = "Hello world!")
//public class HelloWorldResource {
//    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldResource.class);
//
//    private final Template template;
//    private final AtomicLong counter;
//
//    public HelloWorldResource(Template template) {
//        this.template = template;
//        this.counter = new AtomicLong();
//    }
//
//    @GET
//    @Timed(name = "get-requests")
//    @CacheControl(maxAge = 1, maxAgeUnit = TimeUnit.DAYS)
//    @ApiOperation(
//            value = "Say hello!",
//            notes = "Returns a saying",
//            response = Saying.class)
//    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid name supplied"),
//            @ApiResponse(code = 404, message = "Name not found") })
//    public Saying sayHello(
//            @ApiParam(value = "Name of person to say hello to", required = false) @QueryParam("name") Optional<String> name) {
//        return new Saying(counter.incrementAndGet(), template.render(name));
//    }
//
//    @GET
//    @Path("/date")
//    @Produces(MediaType.TEXT_PLAIN)
//    public String receiveDate(@QueryParam("date") Optional<DateTimeParam> dateTimeParam) {
//        if (dateTimeParam.isPresent()) {
//            final DateTimeParam actualDateTimeParam = dateTimeParam.get();
//            LOGGER.info("Received a date: {}", actualDateTimeParam);
//            return actualDateTimeParam.get().toString();
//        } else {
//            LOGGER.warn("No received date");
//            return null;
//        }
//    }
//}
