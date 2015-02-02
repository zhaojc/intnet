package guesser;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * Created by christoffer.gunning on 2015-02-02.
 */
public class Guesser {
    public static void main(String[] args) throws ProtocolException {

        HttpURLConnection con = getConnection("http","localhost", 8080, "/guess.html");
        int clientID = getClientID(con);
        con = getConnection("http","localhost", 8080, "/guess.html");
        int nrOfGuesses = 0;
        int lowerBound = 1;
        int higherBound = 100;
        int guess = 50;

        int guessStatus = guess(con, guess, clientID);
        nrOfGuesses++;

        while (guessStatus != 0) {
            switch (guessStatus) {
                case -1:
                    lowerBound = guess + 1;
                    break;
                case 1:
                    higherBound = guess - 1;
                    break;
                case 0:
                    break;
            }
            guess = lowerBound + (higherBound - lowerBound) / 2;
            con = getConnection("http","localhost", 8080, "/guess.html");
            guessStatus = guess(con, guess, clientID);
            nrOfGuesses++;
            System.out.println("Guess = " + guess);
            System.out.println("nrOfGuesses = " + nrOfGuesses);
        }


    }

    private static HttpURLConnection getConnection(String protocol, String address, int port, String page) {
        URL url = null;
        try{
            url = new URL(protocol, address, port, page);
        }
        catch(MalformedURLException e){
            System.out.println(e.getMessage());
        }
        HttpURLConnection con = null;
        try{
            con = (HttpURLConnection)url.openConnection();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        return con;
    }

    private static int guess(HttpURLConnection con, int guess, int clientID) {
        con.setDoOutput(true);
        String urlParameter = "guess=" + guess;
        byte[] postData = urlParameter.getBytes(Charset.forName("utf-8"));

        con.setRequestProperty("User-Agent","Mozilla");
        con.setRequestProperty("Cookie","clientID=" + clientID);
        con.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty( "charset", "utf-8");
        con.setRequestProperty( "Content-Length", Integer.toString( postData.length ));
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            DataOutputStream wr = new DataOutputStream( con.getOutputStream());
            wr.write( postData );
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            con.connect();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        BufferedReader infil = null;
        try{
            infil = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        String rad = null;
        try{
            while( (rad=infil.readLine()) != null){
                if(rad.contains("Too high!")) {
                    con.disconnect();
                    return 1;
                }
                if(rad.contains("Too low!")) {
                    con.disconnect();
                    return -1;
                }
                if(rad.contains("Correct!")) {
                    con.disconnect();
                    return 0;
                }
            }
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }
        con.disconnect();
        return -2;
    }

    private static int getClientID(HttpURLConnection con) {
        int clientID = -1;

        con.setRequestProperty("User-Agent","Mozilla");
        try{
            con.connect();
        }
        catch(IOException e){
            System.out.println(e.getMessage());
        }

        Map<String, List<String>> headerFields = con.getHeaderFields();
        for(String key : headerFields.keySet()) {
            if (key != null && key.equals("Set-Cookie")) {
                clientID = Integer.parseInt(headerFields.get(key).get(0).split("=")[1].split(";")[0]);
            }
        }
        con.disconnect();
        return clientID;
    }


}
