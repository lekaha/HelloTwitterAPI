public class Token {
    private String mToken;
    private String mSecret;

    public Token() {
    }

    public Token(String token, String secret) {
        mToken = token;
        mSecret = secret;
    }

    public String getToken() {
        return mToken;
    }

    String getSecret() {
        return mSecret;
    }

    @Override
    public String toString() {
        return "Token: " + getToken() + " Secret: " + getSecret().substring(0, 3) + "...";
    }
}
