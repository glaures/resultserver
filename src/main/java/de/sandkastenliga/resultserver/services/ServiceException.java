package de.sandkastenliga.resultserver.services;

public class ServiceException extends Exception {

    private String[] args = new String[]{};
    private String code;

    public ServiceException() {
    }

    public ServiceException(String code, String... args) {
        super(code);
        init(code, args);
    }

    public ServiceException(String code, Throwable rootCause, String... args) {
        super(code, rootCause);
        init(code, args);
    }

    private void init(String code, String... args) {
        this.code = code;
        if (args != null && args.length > 0)
            this.args = args;

    }

    public String[] getArgs() {
        return args;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
