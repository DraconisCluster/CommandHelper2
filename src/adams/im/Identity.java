package adams.im;

public class Identity
{
    private String DisplayName;
    private String IdentityId;
    private String balance;

    public Identity(String displayName, String identityId){
        this.DisplayName = displayName;
        this.IdentityId = identityId;
    }


    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getIdentityId() {
        return IdentityId;
    }

    public void setIdentityId(String identityId) {
        IdentityId = identityId;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }

    public String toString(){
        return "Id:" + IdentityId + "Bal:" + balance + "Name:" + DisplayName;
    }
}
