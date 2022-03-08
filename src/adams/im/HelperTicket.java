package adams.im;

public class HelperTicket {
    String channel_name;
    String submitted_by;
    String created_at;
    String author_id;
    String guild_id;
    String confirmed_by;
    String context;
    HelperUser users;
    HelperMessage[] messages;

    public String toString(){
        StringBuilder s = new StringBuilder();
        s.append("Ticket Name: " + channel_name + "\n");
        s.append("Ticket Context: " + context + "\n");
        for(HelperMessage message : messages){
            s.append(message.toString());
        }
        return s.toString();
    }

}
