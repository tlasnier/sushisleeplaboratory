package fr.wyniwyg.sushisleeplaboratory.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by t.lasnier on 05/08/13.
 */
public class InternetConnection
{
    public static boolean hasActiveInternetConnection(Context context) throws IOException
    {
        return testConnectionTo(new URL("http://www.google.com"), context);
    }

    public static boolean testConnectionTo(URL url, Context context) throws IOException
    {
        if (isNetworkAvailable(context))
        {
            HttpURLConnection urlc = (HttpURLConnection) (url.openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == HttpURLConnection.HTTP_OK);
        }
        return false;
    }

    public static boolean isNetworkAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    @Deprecated
    public static String getResponse(URL url) throws IOException {
        BufferedReader in;
        String readLine, result = "";
        in = new BufferedReader(new InputStreamReader(url.openStream()));

        while ((readLine = in.readLine()) != null)
            result+=readLine + "\n";

        return result;
    }
}
