package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by christoffer.gunning on 2015-01-28.
 */
public class Cookie {

    private String variable;
    private String value;
    private GregorianCalendar expirationDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH);

    public Cookie(String variable, String value, GregorianCalendar expirationDate) {
        this.variable = variable;
        this.value = value;
        this.expirationDate = expirationDate;
    }

    public String getCookieString() {
        return "Set-Cookie: " + variable + "=" + value + "; Expires=" + dateFormat.format(expirationDate.getTime());
    }

    public String getVariable() {
        return variable;
    }

    public String getValue() {
        return value;
    }
}
