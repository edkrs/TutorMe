package com.accipio.tutorme;

import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

/**
 * Created by thise_000 on 2017-04-09.
 */

public class JSONParser {

    public JSONParser(){

    }




    public void request(String urlString, List<Pair<String, String>> args, String method) {
        // TODO Auto-generated method stub

        System.out.println("Eric stopped 0");

        JSONObject jsonObject = new JSONObject();
        System.out.println("Eric stopped sd");

        for (Pair<String, String> pair : args){
            try {
                jsonObject.put(pair.first, pair.second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        System.out.println("Eric stopped 1");
        String message = jsonObject.toString();
        System.out.println(message);
        StringBuffer chaine = new StringBuffer("");

        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            //System.out.println(connection.getResponseCode());
            System.out.println("Eric stopped 2");

            connection.setRequestProperty("User-Agent", "");
            if (method.equals("POST")) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
            }
            else if (method.equals("GET")) {
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

            }
            System.out.println("Eric stopped 3");

            //connection.setFixedLengthStreamingMode(message.getBytes().length);

            connection.connect();
            System.out.println("Eric stopped 4");

            OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
            outputStream.write(message.getBytes());
            //clean up
            outputStream.flush();
            System.out.println("Eric stopped 5");

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println(chaine);
                System.out.println("Eric stopped 4=5");
                chaine.append(line);
            }



            /*
            InputStream inputStream = connection.getInputStream();

            */
            outputStream.close();
            inputStream.close();
            connection.disconnect();
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }finally {

        }

        //return chaine;
    }
}
