import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.time.LocalDate;

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
    public static final String PRECIP_URL = BASE_URL + "data?datasetid=GHCND&limit=1000&startdate=*STARTDATE*&enddate=*ENDDATE*";

    private OkHttpClient client;
    private String startDate;
    private String endDate;

    public NOAA() {
        // Create a client with a lengthy timeout in hopes of connecting and getting data
        client = new OkHttpClient.Builder().connectTimeout(1600L, TimeUnit.SECONDS).build();
        startDate = LocalDate.now().minusDays(1).toString();
        endDate = LocalDate.now().plusDays(1).toString();
    }

    /**
    *  Call the NOAA API and get precipitation data for 1000 random stations
    */
    public HashMap<String,Weather> getPrecipData() {
        HashMap<String, Weather> result = new HashMap<String, Weather>();
        String url = NOAA.PRECIP_URL.replace("*STARTDATE*", startDate).replace("*ENDDATE*", endDate);
        try {
            Response res = makeRequest(url);
            JsonParser parser = new JsonParser();
            JsonArray results = parser.parse(res.body().string()).getAsJsonObject().getAsJsonArray("results");

            Iterator<JsonElement> iterator = results.iterator();
            while (iterator.hasNext()) {
                JsonObject e = iterator.next().getAsJsonObject();
                String type = e.get("datatype").getAsString();
                String station = e.get("station").getAsString();
                String attributes = e.get("attributes").getAsString();
                int value = e.get("value").getAsInt();

                result.put(station, new Weather(type, station, attributes, value));
            }
            return result;
        } catch (IOException e) {

        } catch (IllegalStateException e) {

        }
        return null;
    }

    /**
    * Make a request to the NOAA API to the proper url
    */
    private Response makeRequest(String url) throws IOException, IllegalStateException {
        // Make the request
        Request req = new Request.Builder()
                .url(url)
                .addHeader("token", NOAA.KEY)
                .build();
        Response res = client.newCall(req).execute();
        return res;
    }
}
