package com.stock.screener.collector.adapter.in.web;

import com.stock.screener.collector.application.port.in.ManualCollectionJobUseCase;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Path("/api/collector/jobs")
@RequiredArgsConstructor
class CollectionJobController {

    private final ManualCollectionJobUseCase manualCollectionJobUseCase;

    @GET
    @Path("/{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJobStatus(@PathParam("jobId") String jobIdRaw) {
        try {
            var jobId = UUID.fromString(jobIdRaw);
            return manualCollectionJobUseCase.getJobStatus(jobId)
                    .map(CollectionJobStatusResponse::from)
                    .map(statusResponse -> Response.ok(statusResponse).build())
                    .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid jobId format").build();
        }
    }
}
