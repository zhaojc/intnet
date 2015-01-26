import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by daniel on 2015-01-24.
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 953184295622776147L;

    public static final String COMMAND_PREFIX = "/";

    public enum Type {
        LOGIN(""),
        LOGOUT("logout"),
        MESSAGE(""),
        USERNAME("username"),
        USERNAME_CHANGED(""),
        MEMBERS("members"),
        TIME("time"),
        ERROR("");

        private String command;

        Type(String command) {
            this.command = COMMAND_PREFIX + command;
        }

        public static Type fromString(String command) {
            if(command != null && !command.isEmpty()) {
                for(Type t : Type.values()) {
                    if(command.equals(t.command)) {
                        return t;
                    }
                }
            }

            throw new IllegalArgumentException("No such command.");
        }

        @Override
        public String toString() {
            return command;
        }
    }

    private Type type;
    private Date date;
    private Map<String, Object> data;

    public Message(Type type) {
        this.type = type;
        date = new Date();

        data = new HashMap<String, Object>();
    }

    public Type getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public String getSender() {
        return (String) data.get("sender");
    }

    public void setSender(String sender) {
        data.put("sender", sender);
    }

    public boolean hasDataProperty(String key) {
        return data.containsKey(key);
    }

    public void addData(String key, Object value) {
        data.put(key, value);
    }

    public <T> T getObject(String key, Class<T> type) {
        return type.cast(data.get(key));
    }

    public String serialize() {
        Gson gson = new Gson();

        return gson.toJson(this);
    }

    public static Message deserialize(String json) {
        Gson gson = new Gson();

        return gson.fromJson(json, Message.class);
    }
}
