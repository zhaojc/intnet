package models;

import java.util.HashMap;

/**
 * Created by christoffer.gunning on 2015-01-28.
 */
public class Request {
    public enum Type {
        GET("GET"), POST("POST");

        private String type;

        Type(String type) {
            this.type = type;
        }

        public static Type fromString(String type) throws IllegalArgumentException {
            if(type != null && !type.isEmpty()) {
                for(Type t : Type.values()) {
                    if(type == t.type) {
                        return t;
                    }
                }
            }

            throw new IllegalArgumentException("No such request type");
        }
    }

    Type type;
    String pageRequested;
    Cookie cookie;
    HashMap<String, String> parameters = new HashMap<String, String>();

    public Request(Type type, String resource, Cookie cookie, HashMap<String, String> parameters) {
        this.type = type;
        this.pageRequested = resource;
        this.cookie = cookie;
        this.parameters = parameters;
    }

    public Type getType() {
        return type;
    }

    public String getPageRequested() {
        return pageRequested;
    }

    public Cookie getCookie() {
        return cookie;
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }
}
