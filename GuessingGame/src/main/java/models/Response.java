package models;

import java.io.File;
import java.util.HashMap;

/**
 * Created by christoffer.gunning on 2015-01-28.
 */
public class Response {
    public enum Status {
        NOT_FOUND(404, "Not Found"), OK(200, "Ok");

        private int statusCode;
        private String statusText;

        Status(int statusCode, String statusText) {
            this.statusCode = statusCode;
            this.statusText = statusText;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getStatusText() {
            return statusText;
        }
    }
    private Status status;
    private String contentType;
    private Cookie cookie;
    File pageToShow;
    HashMap<String, String> parameters = new HashMap<String, String>();

    public Response(Status status, String contentType, Cookie cookie, File pageToShow) {
        this.status = status;
        this.contentType = contentType;
        this.cookie = cookie;
        this.pageToShow = pageToShow;
        this.parameters = parameters;
    }

    public Status getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public File getPageToShow() {
        return pageToShow;
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }

    public void setParameter(String key, String value) {
        parameters.put(key, value);
    }
}
