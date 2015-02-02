package handlers;

import annotations.RequestParser;
import models.Request;
import models.Response;

/**
 * Created by christoffer.gunning on 2015-01-29.
 */
public class RequestHandler {

    private static RequestParser requestParser = new RequestParser();

    public static Response handleRequest(Request request) throws Exception {
        System.out.println(request.getType());
        return requestParser.parse(HttpClient.class, request);
    }
}
