package ru.sbertech.atlas.jira.cupintegration.out;

import com.atlassian.jira.component.ComponentAccessor;
import ru.sbertech.atlas.jira.cupintegration.exception.RequiredComponentNotFoundException;
import ru.sbertech.atlas.jira.userenrich.manager.UserInfoManager;

import java.io.File;

public class TimesheetGeneratorChecker {

    public TimesheetGeneratorChecker checkUserInfoManager() throws RequiredComponentNotFoundException {
        if (ComponentAccessor.getOSGiComponentInstanceOfType(UserInfoManager.class) == null) {
            throw new RequiredComponentNotFoundException(UserInfoManager.class);
        }
        return this;
    }

    public TimesheetGeneratorChecker checkLocalDirectory(String fileName) throws RequiredComponentNotFoundException {
        File file = new File(fileName);
        if (!(file.isDirectory() && file.canWrite())) {
            throw new RequiredComponentNotFoundException("Local directory [" + fileName + "]");
        }
        return this;
    }

}
