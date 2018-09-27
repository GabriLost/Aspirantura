package ru.sbertech.atlas.jira.cupintegration.in.action.rest;

import ru.sbertech.atlas.jira.cupintegration.in.model.IMapping;
import ru.sbertech.atlas.jira.cupintegration.in.model.JsonMapping;
import ru.sbertech.atlas.jira.cupintegration.in.repository.IMappingRepository;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitriy Klabukov
 */
@Path("/mapping")
public class MappingService {

    private IMappingRepository mappingRepository;

    public MappingService(IMappingRepository mappingRepository) {
        this.mappingRepository = mappingRepository;
    }

    @GET
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonMapping[] getAll() {
        List<JsonMapping> result = new ArrayList<>();
        for (IMapping o : mappingRepository.getAll()) {
            result.add(new JsonMapping(o));
        }

        return result.toArray(new JsonMapping[result.size() > 0 ? result.size() - 1 : 0]);
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public JsonMapping[] update(JsonMapping[] holders) {
        mappingRepository.deleteAll();
        List<JsonMapping> result = new ArrayList<>();
        for (IMapping o : mappingRepository.createAll(holders)) {
            result.add(new JsonMapping(o));
        }

        return result.toArray(new JsonMapping[result.size() > 0 ? result.size() - 1 : 0]);
    }

}
