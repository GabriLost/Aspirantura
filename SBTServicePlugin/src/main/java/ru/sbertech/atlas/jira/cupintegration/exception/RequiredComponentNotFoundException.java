package ru.sbertech.atlas.jira.cupintegration.exception;

public class RequiredComponentNotFoundException extends Exception {
    public RequiredComponentNotFoundException(Object userInfoManagerClass) {
        super("No " + userInfoManagerClass.toString());
    }
}
