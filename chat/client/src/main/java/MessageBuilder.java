/**
 * Created by daniel on 2015-01-25.
 */
public class MessageBuilder {

    public static Message create(String input) throws IllegalArgumentException {
        String[] tokens = input.split(" ", 2);
        Message.Type type = Message.Type.MESSAGE;

        if(tokens[0].startsWith(Message.COMMAND_PREFIX)) {
            type = Message.Type.fromString(tokens[0]);
        }

        Message m = new Message(type);

        try {
            switch (m.getType()) {
                case MESSAGE:
                    m.addData("message", input);
                    break;
                case LOGIN:
                    break;
                case LOGOUT:
                    break;
                case USERNAME:
                    m.addData("username", tokens[1]);
                    break;
                case TIME:
                    break;
            }
        } catch(IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Missing arguments for command.");
        }

        return m;
    }
}
