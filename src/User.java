public class User {
    private boolean mIsVerified;
    private String mAuthentication;

    public User() {
    }

    public boolean isVerified() {
        return mIsVerified;
    }

    public void setAuthentication(String auth) {
        mAuthentication = auth;
        mIsVerified = true;
    }

    public String getAuthentication() {
        return mAuthentication;
    }

}
