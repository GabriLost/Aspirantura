package ru.sbertech.atlas.jira.cupintegration.out.model;

import com.atlassian.jira.user.ApplicationUser;

import java.util.Objects;

/**
 * Created by Yaroslav Astafiev on 25/11/2015.
 * Department of analytical solutions and system services improvement.
 */
public class UserContainer {
    private ApplicationUser user;
    private String employeeCode;

    public UserContainer(ApplicationUser user, String employeeCode) {
        this.user = user;
        this.employeeCode = employeeCode;
    }

    public ApplicationUser getUser() {
        return user;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        UserContainer that = (UserContainer) o;

        return Objects.equals(this.employeeCode, that.employeeCode) && Objects.equals(this.user, that.user);

    }

    @Override
    public int hashCode() {
        return Objects.hash(user, employeeCode);
    }
}
