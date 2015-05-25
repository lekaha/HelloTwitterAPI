import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class Authentication {
    private final static String REQUEST_METHOD = "GET";
    private final static String URL_TWITTER_API_BASE = "https://api.twitter.com/1.1/";
    private final static String URL_TWITTER_API_CREDENTIAL = URL_TWITTER_API_BASE + "account/verify_credentials.json";
    private final static String URL_TWITTER_API_SEARCH = URL_TWITTER_API_BASE + "search/tweets.json";
    private final static String HEADER = "Authorization";

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private Credential mCredential;
    private User mUser;

    public Authentication(Credential credential) {
        mCredential = credential;
        mUser = null;
    }

    private String getHeader(HttpURLConnection conn) {
        Map<String, List<String>> headerFields = conn.getHeaderFields();
        Set<String> headerFieldsSet = headerFields.keySet();
        Iterator<String> hearerFieldsIter = headerFieldsSet.iterator();

        StringBuffer header = new StringBuffer();
        while (hearerFieldsIter.hasNext()) {
            String headerFieldKey = hearerFieldsIter.next();
            List<String> headerFieldValue = headerFields.get(headerFieldKey);
            StringBuilder sb = new StringBuilder();
            for (String value : headerFieldValue) {
                sb.append(value);
                sb.append("");
            }
            header.append("\n" + headerFieldKey + "=" + sb.toString());
        }

        return header.toString();
    }

    private String getResponse(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + con.getURL());
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = null;
        StringBuffer response = new StringBuffer();
        try {
            in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    private HttpsURLConnection send(String apiUrl, String params, OAuthParameter auth) {

        try {
            String p = params.equals("")? "": "?" + params;
            final URL url = new URL(apiUrl + p);
            final HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
            String cypherAlg = URLEncoder.encode(mCredential.getSecret(), "UTF-8");
            if (mCredential instanceof SingleUserCredential) {
                auth.put(OAuthParameter.TOKEN, (((SingleUserCredential) mCredential).getToken().getToken()));
                cypherAlg += ("&" + URLEncoder.encode((((SingleUserCredential) mCredential).getToken().getSecret()), "UTF-8"));
            }
            SecretKeySpec keySpec = new SecretKeySpec(
                    cypherAlg.getBytes(),
                    HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(keySpec);

            StringBuffer content = new StringBuffer();
            content.append(REQUEST_METHOD);
            content.append("&");
            content.append(URLEncoder.encode(apiUrl, "UTF-8"));
            content.append("&");
            content.append(URLEncoder.encode(auth.getParameterString(), "UTF-8"));
            byte[] result = mac.doFinal(content.toString().getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            String cypher = encoder.encode(result);

            auth.put(OAuthParameter.SIGNATURE, URLEncoder.encode(cypher, "UTF-8"));

            httpURLConnection.addRequestProperty(HEADER, auth.getHeaderValue());
            httpURLConnection.setRequestProperty(HEADER, auth.getHeaderValue());
            httpURLConnection.setRequestMethod(REQUEST_METHOD);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

//            System.out.println("Signature base string: " + content.toString());
//            System.out.println("Authorization header: " + HEADER + ": " + auth.getHeaderValue());

            return httpURLConnection;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean verify() {
        if (null != mUser && mUser.isVerified()) {
            return true;
        }

        try {
            final OAuthParameter auth = new OAuthParameter(mCredential.getKey());
            String response = getResponse(send(URL_TWITTER_API_CREDENTIAL, "", auth));

            mUser = new User();
            mUser.setAuthentication(response);

            return true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String search(String keyword) {
        if (verify()) {
            try {
                final OAuthParameter auth = new OAuthParameter(mCredential.getKey());
                auth.put("q", URLEncoder.encode(keyword, "UTF-8"));

                StringBuffer searchKey = new StringBuffer();
                searchKey.append("q=");
                searchKey.append(keyword);

                String response = getResponse(send(URL_TWITTER_API_SEARCH, searchKey.toString(), auth));
                return response;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
