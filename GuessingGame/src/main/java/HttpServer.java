import handlers.RequestHandler;
import models.Cookie;
import models.Request;
import models.Response;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpServer{



    public static void main(String[] args) throws IOException, URISyntaxException {
        System.out.println("Skapar Serversocket");
        ServerSocket ss = new ServerSocket(8080);
        while(true){
            System.out.println("Väntar på klient...");
            Socket s = ss.accept();
            System.out.println("Klient är ansluten");
            BufferedReader requestReader =
                    new BufferedReader(new InputStreamReader(s.getInputStream()));

            Request request = readRequest(requestReader);
            Response response = null;
            try {
                response = RequestHandler.handleRequest(request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Förfrågan klar.");
            s.shutdownInput();

            PrintStream responseWriter =
                    new PrintStream(s.getOutputStream());

            responseWriter.println("HTTP/1.1 " + response.getStatus().getStatusCode() + " " + response.getStatus().getStatusText());
            responseWriter.println("Server : Teh Guessing Game");
            responseWriter.println("Content-Type: " + response.getContentType() + "; charset=utf-8");

            if (response.getCookie() != null)
                responseWriter.println(response.getCookie().getCookieString());

            responseWriter.println();

            viewPage(responseWriter, response);

            s.shutdownOutput();
            s.close();
        }
    }

    private static void viewPage(PrintStream responseWriter, Response response) throws URISyntaxException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(response.getPageToShow()));
        String line = br.readLine();
        while(line != null) {
            Pattern pat = Pattern.compile("%.*%");
            Matcher mat = pat.matcher(line);

            while (mat.find()) {
                String s = mat.group();
                line = line.replaceAll(s, response.getParameter(s));
            }

            responseWriter.println(line);

            line = br.readLine();
        }
    }

    private static Request readRequest(BufferedReader requestReader) throws IOException {
        String str;
        Request.Type requestType = Request.Type.GET;
        String requestedPage = "";
        Cookie cookie = null;
        HashMap<String, String> parameters = new HashMap<String, String>();

        int contentLength = 0;
        while( (str = requestReader.readLine()) != null && str.length() > 0) {
            String[] parts = str.split(" ");
            if(parts[0].equals("GET")) {
                requestedPage = parts[1];
                requestType = Request.Type.GET;
            } else if(parts[0].equals("POST")) {
                requestedPage = parts[1];
                requestType = Request.Type.POST;
            } else if(parts[0].equals("Cookie:")) {
                String[] cookieParts = parts[1].split("=");
                cookie = new Cookie(cookieParts[0], cookieParts[1], null);
            } else if(parts[0].equals("Content-Length:")) {
                contentLength = Integer.parseInt(parts[1]);
            }
        }

        // Läs post-skit
        String postData = "";
        if(requestType == Request.Type.POST) {
            for (int i = 0; i < contentLength; i++) {
                postData += (char) requestReader.read();
            }
            if (postData != "") {
                String[] params = postData.split("&");

                for (String param : params) {
                    String[] parts = param.split("=");
                    parameters.put(parts[0], parts[1]);
                }
            }
        }

        return new Request(requestType, requestedPage, cookie, parameters);
    }
}