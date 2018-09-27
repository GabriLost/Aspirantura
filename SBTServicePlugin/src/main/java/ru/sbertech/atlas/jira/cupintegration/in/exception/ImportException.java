package ru.sbertech.atlas.jira.cupintegration.in.exception;

/**
 * @author Dmitriev Vladimir
 */
public class ImportException extends Exception {
    //ToDo: это что и зачем?
    private static final long serialVersionUID = 6470601495485963732L;

    public ImportException(String message) {
        super(message);
    }
}
