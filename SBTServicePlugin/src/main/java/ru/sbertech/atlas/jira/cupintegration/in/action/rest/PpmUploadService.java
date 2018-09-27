package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartFormParam;
import ru.sbertech.atlas.jira.cupintegration.in.model.ImportResult;
import ru.sbertech.atlas.jira.cupintegration.in.service.ObjectsUploadService;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Sedelnikov FM on 24/12/2015.
 */
@Path("/ppmobjects")
public class PpmUploadService {

    private final ObjectsUploadService uploadService;

    public PpmUploadService(ObjectsUploadService uploadService) {
        this.uploadService = uploadService;
    }

    @POST
    @Path("/upload")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public Response uploadFile(@MultipartFormParam("file") FilePart filePart) throws Exception {
        if (filePart == null) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("File wasn't uploaded").type(MediaType.APPLICATION_XML).build();
        }
        try {
            InputStream is = filePart.getInputStream();
            List<ImportResult> result = uploadService.upload(is);
            return Response.status(200).entity(result).build();
        } catch (IOException io) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity("File is not provided properly").type(MediaType.APPLICATION_XML).build();
        }
    }

}
