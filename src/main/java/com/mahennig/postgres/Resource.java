package com.mahennig.postgres;

import com.mahennig.common.UserRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.hibernate.exception.ConstraintViolationException;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.List;

@Path("/postgres/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Resource {

    @Inject
    Service service;

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        return service.getUser(id)
                .map(user -> Response.ok(user).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    public List<User> getAll() {
        return service.getAll();
    }

    @POST
    public Response create(UserRequest request, UriInfo uriInfo) {
        if (request == null || isBlank(request.name) || isBlank(request.email)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User user = service.create(request);
        return Response.created(uriInfo.getAbsolutePathBuilder().path(String.valueOf(user.id)).build())
                .entity(user)
                .build();
    }

    /** The UNIQUE constraint on users.email is enforced by PostgreSQL. */
    @ServerExceptionMapper
    public Response mapConstraintViolation(ConstraintViolationException e) {
        return Response.status(Response.Status.CONFLICT).build();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
