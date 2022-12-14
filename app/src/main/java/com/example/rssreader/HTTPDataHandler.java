package com.example.rssreader;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class HTTPDataHandler {

    static String stream = "";

    private final String TAG = "HTTP_DATA_HANDLER_TAG";

    public HTTPDataHandler() {}

    public String GetHttpDataHandler(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_3) " +
                            "AppleWebKit/537.36 (KHTML, like Gecko) " +
                            "Chrome/44.0.2403.155 Safari/537.36");

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                ByteBuffer byteBuffer;
                while ((line = bufferedReader.readLine()) != null) {
                    byteBuffer = StandardCharsets.UTF_8.encode(line);
                    stringBuilder.append(StandardCharsets.UTF_8.decode(byteBuffer).toString());
                }
                stream = stringBuilder.toString();
            }
            urlConnection.disconnect();
        } catch (IOException e){
            e.printStackTrace();
        }
        return stream;
    }
}
