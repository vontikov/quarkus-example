package com.github.vontikov.quarkus.jdbc.example;

import io.micrometer.core.annotation.Counted;
import java.sql.SQLException;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import lombok.val;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    Fruit fr;

    @GET
    @Counted
    public List<Fruit> get() throws SQLException {
        return fr.findAll();
    }

    @GET
    @Path("/{id}")
    @Counted
    public Response getSingle(@PathParam Long id) throws SQLException {
        val o = fr.findById(id);
        if (!o.isPresent()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok().entity(o).build();
    }

    @POST
    @Counted
    public Response create(Fruit fruit) throws SQLException {
        val id = fr.create(fruit);
        return Response.status(Status.CREATED).entity(id).build();
    }

    @DELETE
    @Path("{id}")
    @Counted
    public Response delete(@PathParam Long id) throws SQLException {
        if (!fr.delete(id)) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }
}
