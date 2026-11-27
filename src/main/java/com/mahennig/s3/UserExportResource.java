package com.mahennig.s3;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.util.List;
import java.util.Map;

@Path("/exports/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserExportResource {

    @Inject
    UserExportService exportService;

    /** Exports all PostgreSQL users to S3. */
    @POST
    public Response export(UriInfo uriInfo) {
        String key = exportService.exportUsers();
        return Response.created(uriInfo.getAbsolutePathBuilder().path(key).build())
                .entity(Map.of("key", key))
                .build();
    }

    /** Loads an export back from S3. */
    @GET
    @Path("/{key}")
    public List<ExportedUser> load(@PathParam("key") String key) {
        return exportService.loadExport(key);
    }

    @ServerExceptionMapper
    public Response mapMissingKey(NoSuchKeyException e) {
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
