package adams.im;

public class HelperContent {
    String msg;
    HelperAttachment[] attachments;

    public String toString(){
        StringBuilder s = new StringBuilder();
        if(attachments != null) {
            for (HelperAttachment attachment : attachments) {
                s.append(attachment.toString());
            }
        }
        s.append(msg);
        return s.toString();
    }
}

