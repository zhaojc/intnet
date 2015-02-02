package handlers;

import annotations.Default;
import annotations.GET;
import annotations.POST;
import annotations.Path;
import models.Cookie;
import models.GuessData;
import models.Request;
import models.Response;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by christoffer.gunning on 2015-01-28.
 */
public class HttpClient {

    static int currentClientID = 0;
    private static HashMap<Integer, GuessData> guessDataHashMap = new HashMap<Integer, GuessData>();

    @Path("/guess.html")
    @GET
    @SuppressWarnings("unused")
    public static Response handleGet(Request request) {
        URL localPageUrl = HttpClient.class.getResource("../guess.html");
        File pageToShow = null;

        try {
            pageToShow = new File(localPageUrl.toURI());
        } catch (URISyntaxException e) {
            System.out.println("Error with requested file: " + request.getPageRequested());
            System.exit(1);
        }

        Cookie setCookie = null;

        if(request.getCookie() == null) {
            GregorianCalendar g = new GregorianCalendar();
            g.setTimeZone(TimeZone.getTimeZone("Europe/Stockholm"));
            g.add(GregorianCalendar.YEAR, 1);
            setCookie = new Cookie("clientID", currentClientID + "", g);
            guessDataHashMap.put(currentClientID, new GuessData((int)( Math.random()*100) + 1));
            currentClientID++;
        }

        Response response = new Response(Response.Status.OK, "text/html", setCookie, pageToShow);

        response.setParameter("%MESSAGE%", "Make a guess!");
        response.setParameter("%LAST_GUESS%", 0 + "");

        return response;
    }

    @Path("/guess.html")
    @POST
    @SuppressWarnings("unused")
    public static Response handlePost(Request request) {
        URL localPageUrl = HttpClient.class.getResource("../guess.html");
        File pageToShow = null;
        try {
            pageToShow = new File(localPageUrl.toURI());
        } catch (URISyntaxException e) {
            System.out.println("Error with requested file: " + request.getPageRequested());
            System.exit(1);
        }

        Response response = new Response(Response.Status.OK, "text/html", null, pageToShow);

        int clientID = Integer.parseInt(request.getCookie().getValue());
        GuessData guessData = guessDataHashMap.get(clientID);
        int guess = Integer.parseInt(request.getParameter("guess"));
        int guessStatus = guessData.guess(guess);
        guessDataHashMap.put(clientID, guessData);

        if (guessStatus < 0)
            response.setParameter("%MESSAGE%", "Too low! You've made " + guessData.getNrOfGuesses() + " guesses.");
        else if (guessStatus > 0)
            response.setParameter("%MESSAGE%", "Too high! You've made " + guessData.getNrOfGuesses() + " guesses.");
        else
            response.setParameter("%MESSAGE%", "Correct! You've made " + guessData.getNrOfGuesses() + " guesses.");


        response.setParameter("%LAST_GUESS%", guess + "");
        return response;
    }

    @Default
    @SuppressWarnings("unused")
    public static Response fail(Request request) {
        Response response;
        File pageToShow = null;
        Response.Status status = Response.Status.OK;
        Cookie setCookie = null;
        try {
            pageToShow = new File(HttpClient.class.getResource("../404.html").toURI());
            status = Response.Status.NOT_FOUND;
        } catch (URISyntaxException e) {
            System.out.println("Error with requested file: " + request.getPageRequested());
            System.exit(1);
        }

        response = new Response(status, "text/html", setCookie, pageToShow);

        response.setParameter("%MESSAGE%", "Sidan du sökte finns tyvärr inte");
        response.setParameter("%LAST_GUESS%", 0 + "");

        return response;
    }
}
