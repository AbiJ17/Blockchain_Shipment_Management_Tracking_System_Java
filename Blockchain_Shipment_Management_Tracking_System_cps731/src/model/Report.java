package model;

public class Report {

    private final String title;
    private final String body;

    public Report(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return title + "\n\n" + body;
    }
}
