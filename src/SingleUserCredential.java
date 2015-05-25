public class SingleUserCredential extends Credential {
    protected Token pToken;

    public SingleUserCredential(String userName, String key, String secret, Token token) {
        super(userName, key, secret);
        pToken = token;
    }

    public Token getToken() {
        return pToken;
    }

    @Override
    public String toString() {
        String str = super.toString();
        return str + " Token " + pToken;
    }
}
