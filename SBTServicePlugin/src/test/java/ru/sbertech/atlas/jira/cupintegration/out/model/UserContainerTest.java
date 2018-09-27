package ru.sbertech.atlas.jira.cupintegration.out.model;

import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UserContainerTest {

    @Test
    public void testGetUser_Null() {
        final ApplicationUser user = null;
        UserContainer container = new UserContainer(user, "0");

        assertEquals(null, container.getUser());
    }

    @Test
    public void testGetUser_MockUser() {
        final ApplicationUser mockedUser = new MockApplicationUser("Emma");
        UserContainer container = new UserContainer(mockedUser, "1");
        assertEquals(mockedUser, container.getUser());
        assertEquals("Emma", container.getUser().getUsername());
    }

    @Test
    public void testGetEmployeeCode_Null() {
        final ApplicationUser mockedUser = new MockApplicationUser("Jasmine");
        UserContainer container = new UserContainer(mockedUser, null);
        //verify null'able Employee Code
        assertEquals(null, container.getEmployeeCode());
        //Verify User Name
        assertEquals(mockedUser, container.getUser());
        assertEquals("Jasmine", container.getUser().getUsername());
    }

    @Test
    public void testConstructorDoubleNull() {
        UserContainer container = new UserContainer(null, null);

        assertEquals(null, container.getEmployeeCode());
        assertEquals(null, container.getUser());
    }

    @Test
    public void testEquals_DoubleNull() {
        UserContainer container = new UserContainer(null, null);
        UserContainer container2 = new UserContainer(null, null);
        assertEquals(container, container2);
    }

    @Test
    public void testHashCode_DoubleNull() {
        UserContainer container = new UserContainer(null, null);
        UserContainer container2 = new UserContainer(null, null);

        assertEquals(container.hashCode(), container2.hashCode());
    }

    @Test
    public void testNotEquals_Null() {
        final ApplicationUser mockedUser = new MockApplicationUser("Jasmine");
        final ApplicationUser mockedUser2 = new MockApplicationUser("Jasmine", "Jasmine");
        UserContainer container = new UserContainer(mockedUser, null);
        UserContainer container2 = new UserContainer(mockedUser2, null);

        assertNotEquals(container, container2);
    }

    @Test
    public void testEquals_MockUser() {
        final ApplicationUser mockedUser = new MockApplicationUser("Jasmine");
        final ApplicationUser mockedUser2 = new MockApplicationUser("Jasmine");
        UserContainer container = new UserContainer(mockedUser, "-1");
        UserContainer container2 = new UserContainer(mockedUser2, "-1");

        assertEquals(container, container2);
    }

    @Test
    public void testEquals_SameHashUserName(){
        final ApplicationUser mockedUser = new MockApplicationUser("AaAa");
        final ApplicationUser mockedUser2 = new MockApplicationUser("BBBB");
        UserContainer container = new UserContainer(mockedUser, "-1");
        UserContainer container2 = new UserContainer(mockedUser2, "-1");

        assertNotEquals(container, container2);
    }

    @Test
    public void testEquals_SameHashEmployeeCode(){
        final ApplicationUser mockedUser = new MockApplicationUser("Jasmine");
        UserContainer container = new UserContainer(mockedUser, "AaAa");
        UserContainer container2 = new UserContainer(mockedUser, "BBBB");
        System.out.println(container.equals(container2));
        assertNotEquals(container, container2);

    }
}
