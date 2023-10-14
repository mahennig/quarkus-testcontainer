package com.mahennig.mongodb;

import com.mahennig.common.UserRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/mongodb/users")
@Produces(MediaType.APPLICATION_JSON)
public class Resource {

    @Inject
    Service service;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") String id) {
        return Response.ok(service.getUser(id)).build();
    }

    @GET
    public Response getAll() {
        return Response.ok(service.getAll()).build();
    }

    @POST
    public Response create(UserRequest request) {
        service.create(request);
        return Response.status(Response.Status.CREATED)
                .entity("User created successfully.")
                .build();
    }
}
