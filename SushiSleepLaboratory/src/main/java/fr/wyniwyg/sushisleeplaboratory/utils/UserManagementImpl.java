package fr.wyniwyg.sushisleeplaboratory.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import fr.wyniwyg.sushisleeplaboratory.exception.BadAuthenticationException;
import fr.wyniwyg.sushisleeplaboratory.exception.RegisterException;

/**
 * Created by t.lasnier on 27/08/13.
 */
public class UserManagementImpl implements UserManagement{
    private URL url;

    public UserManagementImpl(URL myUrl) {
        url = myUrl;
    }

    public void setUrl(URL newUrl) {
        url = newUrl;
    }

    @Override
    public void login(String login, String password) throws BadAuthenticationException {
        HttpPost httppost;
        HttpClient httpclient;
        String response;

        //TODO : public key, check keys

        //creating request
        httppost = new HttpPost(url.toString() + "/auth");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("user",login));
        nameValuePairs.add(new BasicNameValuePair("password",password));
//        nameValuePairs.add(new BasicNameValuePair("public_key","myPublicKey"));

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //posting request
        httpclient = new DefaultHttpClient();
        try {
            response = getResponse(httpclient.execute(httppost));

            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject jo = reader.readObject();

            if(!jo.get("status").toString().toLowerCase().equals("success"))
                throw new BadAuthenticationException("Wrong credentials");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void register(String lastname, String firstname, String login, String password, String gender, long birth) throws RegisterException {
        HttpPost httppost;
        HttpClient httpclient;
        String response;

        //TODO : public key, check keys

        //creating request
        httppost = new HttpPost(url.toString() + "/user/create");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("lastname",lastname));
        nameValuePairs.add(new BasicNameValuePair("firstname",firstname));
        nameValuePairs.add(new BasicNameValuePair("user",login));
        nameValuePairs.add(new BasicNameValuePair("gender",gender));
        nameValuePairs.add(new BasicNameValuePair("birthdate",String.valueOf(birth)));
//        nameValuePairs.add(new BasicNameValuePair("public_key","myPublicKey"));


        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //posting request
        httpclient = new DefaultHttpClient();
        try {
            response = getResponse(httpclient.execute(httppost));

            JsonReader reader = Json.createReader(new StringReader(response));
            JsonObject jo = reader.readObject();

            if(!jo.get("status").toString().toLowerCase().equals("success"))
                throw new RegisterException("Registration was not possible");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getResponse(HttpResponse hrep) {
        String res = "", readLine;
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(hrep.getEntity().getContent()));

            while ((readLine = in.readLine()) != null)
                res+=readLine + "\n";

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }
}
