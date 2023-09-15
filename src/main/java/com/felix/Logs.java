package com.felix;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/logs")
public class Logs {
    private static final int DEFAULT_NUM_OF_ENTRIES = 500;
    private static final String DIRECTORY_PATH = "/var/log";

    @Path("{filename}")
    @GET
    public Response searchLogs(
        @PathParam("filename") String filename,
        @QueryParam("maxNumOfEntries") int maxNumOfEntries,
        @QueryParam("keyword") List<String> keywordList) {

        final java.nio.file.Path directory = java.nio.file.Paths.get(DIRECTORY_PATH);
        final java.nio.file.Path directoryFilePath = directory.resolve(filename);

        if (!java.nio.file.Files.exists(directoryFilePath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        final int resultSize = maxNumOfEntries > 0 ? maxNumOfEntries : DEFAULT_NUM_OF_ENTRIES;

        final List<String> result = FileSearchUtils.searchFile(
            DIRECTORY_PATH + "/" + filename,
            keywordList,
            resultSize
        );

        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }
}