package com.mahennig.postgres;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/postgres/user")
public class Resource {

    @Inject
    Service service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response get(@QueryParam("id") String id) {
        return Response.ok(service.getUser(id)).build();
    }
}
