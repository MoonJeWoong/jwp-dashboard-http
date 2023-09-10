package nextstep.org.apache.coyote.http11.response;

import nextstep.org.apache.coyote.http11.Status;

public class StatusLine {

    private final String httpVersion;
    private final Status status;

    public StatusLine(String httpVersion, Status status) {
        this.httpVersion = httpVersion;
        this.status = status;
    }

    public String toString() {
        return httpVersion + " " + status.getCodeMessage() + " ";
    }
}
