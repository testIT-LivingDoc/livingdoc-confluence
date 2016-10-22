package info.novatec.testit.livingdoc.confluence.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;


@Path("/command")
public class LivingDocRestServiceImpl implements LivingDocRestService {

    @GET
    public String testRestInterface() {

        return "Hello RestUser";
    }
}
