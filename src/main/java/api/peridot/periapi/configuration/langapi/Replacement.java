package api.peridot.periapi.configuration.langapi;

public class Replacement {

    private String from;
    private String to;

    public Replacement(String from, Object to) {
        this.from = from;
        this.to = to.toString();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

}
