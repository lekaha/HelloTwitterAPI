public class Credential {
    protected String pUserName;
    protected String pConsumerKey;
    protected String pConsumerSecret;

    public Credential(String userName, String key, String secret) {
        pUserName = userName;
        pConsumerKey = key;
        pConsumerSecret = secret;
    }

    public String getKey() {
        return pConsumerKey;
    }

    public String getUserName() {
        return pUserName;
    }

    public String getSecret() {
        return pConsumerSecret;
    }

    @Override
    public String toString() {
        return "UserName: " + pUserName
                + " Key: " + pConsumerKey
                + " Secret: " + pConsumerSecret.substring(0, 3) + "...";
    }
}
