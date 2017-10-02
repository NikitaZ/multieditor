package org.multieditor.data.services.multieditor;

import org.multieditor.data.exceptions.multieditor.DocumentNotFoundException;
import org.multieditor.data.exceptions.multieditor.UserNotFoundException;
import org.multieditor.data.info.multieditor.DocumentSummary;
import org.multieditor.data.info.multieditor.UpdateSummary;

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

public interface DocumentService {
    @POST
//    @Consumes(APPLICATION_JSON) // in case we will want to switch to passing DocumentSummary or some other parameter as JSON
    @Produces(APPLICATION_JSON)
    @Path("createOrUpdate")
    UpdateSummary createOrUpdate(@FormParam("name") String name, @FormParam("description") String description,
                                 @FormParam("contents") String contents, @FormParam("user") String userName,
                                 @FormParam("previousVersion") String previousVersion,
                                 @FormParam("cursorPosition") int cursorPosition) throws UserNotFoundException;

    @DELETE
    @Produces(APPLICATION_JSON)
    @Path("delete")
    void delete(@QueryParam("name") String documentName) throws DocumentNotFoundException;

    @GET
    @Path("name/{name}")
    @Produces(APPLICATION_JSON)
    DocumentSummary findByName(@PathParam("name") String documentName);


    // we may introduce ShortDocumentSummary to pass only names and descriptions but for now it would only complicate more
    @GET
    @Path("listAll")
    @Produces(APPLICATION_JSON)
    List<DocumentSummary> findAll();

    @GET
    @Path("namesByUser")
    @Produces(APPLICATION_JSON)
    List<String> findNamesChangedByUser(@QueryParam("user") String userName) throws UserNotFoundException;
}