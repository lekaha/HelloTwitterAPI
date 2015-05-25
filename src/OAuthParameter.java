import java.util.*;

public class OAuthParameter {
    public static final String TIMESTAMP = "oauth_timestamp";
    public static final String SIGN_METHOD = "oauth_signature_method";
    public static final String VERSION = "oauth_version";
    public static final String NONCE = "oauth_nonce";
    public static final String CONSUMER_KEY = "oauth_consumer_key";
    public static final String SIGNATURE = "oauth_signature";
    public static final String TOKEN = "oauth_token";

    public HashMap params;


    public OAuthParameter(String key) {
        params = new HashMap();
        params.put(SIGN_METHOD, "HMAC-SHA1");
        params.put(VERSION, "1.0");
        params.put(CONSUMER_KEY, key);
        params.put(TIMESTAMP, getTimestampInSeconds());
        params.put(NONCE, getTimestampInSeconds());
    }

    private static String getTimestampInSeconds() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    public void put(String key, String value) {
        params.put(key, value);
    }

    public String get(String key) {
        return (String) params.get(key);
    }

    private String[] sortedKeys() {
        Set keySet = params.keySet();
        Iterator i = keySet.iterator();
        String[] sKeys = new String[params.size()];
        int j = 0;
        while (i.hasNext()) {
            sKeys[j++] = (String)i.next();
        }
        QSort qsort = new QSort();
        qsort.quicksort(sKeys, 0, sKeys.length -1);

        return sKeys;
    }

    private void updateTimestamp() {
        params.put(TIMESTAMP, getTimestampInSeconds());
        params.put(NONCE, getTimestampInSeconds());
    }

    public String getParameterString() {
        String[] sKeys = sortedKeys();
        StringBuffer parameterStr = new StringBuffer();

        for (int index = 0; index<sKeys.length; index++) {
            String value = get(sKeys[index]);
            parameterStr.append(sKeys[index]);
            parameterStr.append("=");
            parameterStr.append(value);

            if (index < (sKeys.length - 1)) {
                parameterStr.append("&");
            }
        }

        return parameterStr.toString();
    }

    public String getHeaderValue() {
        String[] sKeys = sortedKeys();
        StringBuffer buffer = new StringBuffer();
        buffer.append("OAuth ");

        for (int index = 0; index<sKeys.length; index++) {
            String value = get(sKeys[index]);
            if (sKeys[index].startsWith("oauth_")) {
                buffer.append(sKeys[index]);
                buffer.append("=");
                buffer.append('"');
                buffer.append(value);
                buffer.append("\", ");
            }
        }

        return buffer.toString().substring(0, buffer.length() - 2);
    }
}
