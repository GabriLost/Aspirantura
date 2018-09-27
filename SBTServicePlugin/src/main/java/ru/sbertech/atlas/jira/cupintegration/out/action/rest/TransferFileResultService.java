package ru.sbertech.atlas.jira.cupintegration.out.action.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

@Path("/fileTransfer")
public class TransferFileResultService {

    private static final Logger log = LoggerFactory.getLogger(TransferFileResultService.class);

    @Path("sendResult")
    @AnonymousAllowed
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    public void sendResult(@FormParam("fileName") String fileName, @FormParam("error") String error) {
        if (StringUtils.isEmpty(error)) {
            log.info("File with path: " + fileName + " was transferred successful");
            return;
        }
        log.error("File with path: " + fileName + " was transferred with error: " + error);
    }
}
