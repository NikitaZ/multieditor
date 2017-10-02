package org.multieditor.data.services.multieditor;

import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.UserAccountSummary;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

public interface UserService {
    @POST
    @Produces(APPLICATION_JSON)
    @Path("createOrUpdate")
    UserAccountSummary createOrUpdate(@FormParam("name") String name, @FormParam("fullName") String fullName,
                                      @FormParam("email") String email, @FormParam("colour") String colour);

    @DELETE
    @Produces(APPLICATION_JSON)
    @Path("delete")
    void delete(@QueryParam("name") String userName) throws UserNotFoundException;

    @GET
    @Produces(APPLICATION_JSON)
    @Path("name/{name}")
    UserAccountSummary findByName(@PathParam("name") String name);

    @GET
    @Produces(APPLICATION_JSON)
    @Path("listAll")
    List<UserAccountSummary> findAll();

}
