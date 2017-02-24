import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import okhttp3.OkHttpClient.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NOAA {
    public static final String KEY = "cYlTIFWOAubKxvzCcdtciMFcdZMkXCTc";
    public static final String BASE_URL = "https://www.ncdc.noaa.gov/cdo-web/api/v2/";
    public static final String PRECIP_URL = BASE_URL + "data?datasetid=GHCND&locationid=ZIP:13126&startdate=2016-01-01&enddate=2016-12-31";
    public static final String ZIPCODE_URL = BASE_URL + "locations?datasetid=GHCND&locationcategoryid=ZIP&sortfield=name&limit=500&startdate=2016-01-01&enddate=2016-12-31";
    
    public OkHttpClient client;
    
    public NOAA() {
        // Create a client with a lengthy timeout in hopes of connecting and getting data
        client = new OkHttpClient.Builder().connectTimeout(1600L, TimeUnit.SECONDS).build();
    }
    
    public ArrayList<String> getZipCodes() {
        try {
            // Make the request
            Request req = new Request.Builder()
                    .url(NOAA.ZIPCODE_URL)
                    .addHeader("token", NOAA.KEY)
                    .build();
            Response res = client.newCall(req).execute();
            
            // Parse results to a JsonArray
            JsonParser parser = new JsonParser();
            JsonArray results = parser.parse(res.body().string())
                    .getAsJsonObject().getAsJsonArray("results");
            
            // Loop over results and stuff into an java array
            Iterator<JsonElement> iterator = results.iterator();
            ArrayList<String> zipCodes = new ArrayList<String>();
            while (iterator.hasNext()) {
                zipCodes.add(iterator.next().getAsJsonObject().get("id").getAsString());
            }
            return zipCodes;
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HashMap<String,Integer> getTemperatureData() {
        ArrayList<String> zipCodes = getZipCodes();
        for (String z : zipCodes) {
            System.out.println(z);
        }
        return null;
    }
}