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
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class FruitResource {

    @Inject
    Fruit fr;

    @GET
    @Counted
    public List<Fruit> get() throws SQLException {
        log.info("get: thread: {}", Thread.currentThread().getName());
        return fr.findAll();
    }

    @GET
    @Path("/{id}")
    @Counted
    public Response getSingle(@PathParam Long id) throws SQLException {
        log.info("getSingle: thread: {}", Thread.currentThread().getName());
        val o = fr.findById(id);
        if (!o.isPresent()) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.ok().entity(o).build();
    }

    @POST
    @Counted
    public Response create(Fruit fruit) throws SQLException {
        log.info("create: thread: {}", Thread.currentThread().getName());
        val id = fr.create(fruit);
        return Response.status(Status.CREATED).entity(id).build();
    }

    @DELETE
    @Path("{id}")
    @Counted
    public Response delete(@PathParam Long id) throws SQLException {
        log.info("delete: thread: {}", Thread.currentThread().getName());
        if (!fr.delete(id)) {
            return Response.status(Status.NOT_FOUND).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }
}