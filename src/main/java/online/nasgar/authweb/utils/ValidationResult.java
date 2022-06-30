package online.nasgar.authweb.utils;

public final class ValidationResult {
    private final String messagePath;
    private final String[] args;

    /**
     * Constructor for a successful validation.
     */
    public ValidationResult() {
        this.messagePath = null;
        this.args = null;
    }

    /**
     * Constructor for a failed validation.
     *
     * @param messagePath message key of the validation error
     * @param args       arguments for the message key
     */
    public ValidationResult(String messagePath, String... args) {
        this.messagePath = messagePath;
        this.args = args;
    }

    /**
     * Returns whether an error was found during the validation, i.e. whether the validation failed.
     *
     * @return true if there is an error, false if the validation was successful
     */
    public boolean hasError() {
        return messagePath != null;
    }

    public String getMessagePath() {
        return messagePath;
    }

    public String[] getArgs() {
        return args;
    }
}