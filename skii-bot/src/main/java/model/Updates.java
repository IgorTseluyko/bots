package model;

import java.util.List;

/**
 * Created by mariiarudenko on 11/12/16.
 */
public class Updates {

    public String ok;
    public List<Result> result;

    public class Result {
        public long update_id;
        public Message message;

        @Override
        public String toString() {
            return "Result{" +
                    "update_id='" + update_id + '\'' +
                    ", message=" + message +
                    '}';
        }
    }

    public class Message {
        public long message_id;
        public From from;
        public Chat chat;
        public long date;
        public String text;

        @Override
        public String toString() {
            return "Message{" +
                    "message_id=" + message_id +
                    ", from=" + from +
                    ", chat=" + chat +
                    ", date=" + date +
                    ", text='" + text + '\'' +
                    '}';
        }
    }

    public class From {
        public long id;
        public String first_name;
        public String last_name;

        @Override
        public String toString() {
            return "From{" +
                    "id=" + id +
                    ", first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    '}';
        }
    }

    public class Chat {
        public long id;
        public String first_name;
        public String last_name;
        public String type;

        @Override
        public String toString() {
            return "Chat{" +
                    "id=" + id +
                    ", first_name='" + first_name + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Updates{" +
                "ok='" + ok + '\'' +
                ", result=" + result +
                '}';
    }
}
