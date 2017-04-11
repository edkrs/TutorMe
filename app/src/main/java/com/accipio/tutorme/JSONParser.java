package com.accipio.tutorme;

import android.util.Pair;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thise_000 on 2017-04-09.
 */

public class JSONParser {

    public JSONParser(){

    }




    public ArrayList<ArrayList<String>> request(String urlString, List<Pair<String, String>> args, String method) {
        // TODO Auto-generated method stub

        System.out.println("Eric stopped 0");
        ArrayList<ArrayList<String>> returns = new ArrayList<ArrayList<String>>();
        String result = "";
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
        StringBuilder chaine = new StringBuilder();

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

            if (method.equals("POST")) {
                OutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                outputStream.write(message.getBytes());
                //clean up
                outputStream.flush();
                outputStream.close();

            }
            System.out.println("Eric stopped 5");

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                System.out.println("Eric stopped 4=5");
                chaine.append(line + "\n");
            }
            result = chaine.toString();


            JSONArray jsonArray = new JSONArray(result);
            for (int i =0; i < jsonArray.length(); i++){
                //JSONObject tempobj = new JSONObject();
                JSONObject tempobj = (JSONObject)jsonArray.get(i);
                //System.out.println(tempobj.getString("tutor_id"));
                ArrayList<String> tempValues = new ArrayList<String>();
                tempValues.add(tempobj.getString("tutor_id"));
                tempValues.add((tempobj.getString("user_fname") + " " + (tempobj.getString("user_lname"))));
                tempValues.add(tempobj.getString("tutor_bio"));
                tempValues.add(tempobj.getString("course_name"));
                tempValues.add(tempobj.getString("tutor_rating"));
                tempValues.add(tempobj.getString("tutor_status"));
                tempValues.add(tempobj.getString("tutor_price"));
                returns.add(tempValues);
            }
            System.out.println(result);





            /*
            InputStream inputStream = connection.getInputStream();

            */
            inputStream.close();
            connection.disconnect();
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }

        return returns;
    }

}
