public class Main {


    public static void main(String[] args) {
        String accessToken;
        String accessSecret;
        String consumerKey;
        String consumerSecret;
        String owner;
        String searchKeyword;

        if (args.length > 5) {
            accessToken = args[0];
            accessSecret = args[1];
            consumerKey = args[2];
            consumerSecret = args[3];
            owner = args[4];
            searchKeyword = args[5];
        }
        else {
            System.err.println("Not enough parameters");
            return;
        }

        Token t = new Token(accessToken, accessSecret);
        SingleUserCredential c = new SingleUserCredential(owner, consumerKey, consumerSecret, t);
        System.out.println("Credential:" + c);

        Authentication auth = new Authentication(c);
        boolean isOK = auth.verify();
        if (isOK) {
            String result = auth.search(searchKeyword);
            System.out.println("Search result: " + result);
        }

    }
}
