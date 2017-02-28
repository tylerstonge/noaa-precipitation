import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.Iterator;
import java.time.LocalDate;

import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import okhttp3.OkHttpClient.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NOAA {
    public static final String KEY = "cYlTIFWOAubKxvzCcdtciMFcdZMkXCTc";
    public static final String BASE_URL = "https://www.ncdc.noaa.gov/cdo-web/api/v2/";
    //public static final String PRECIP_URL = BASE_URL + "data?datasetid=PRECIP_15&limit=500&includemetadata=false&units=metric&startdate=*STARTDATE*&enddate=*ENDDATE*";
    public static final String PRECIP_URL = BASE_URL + "data?datasetid=PRECIP_15&limit=1000&includemetadata=false&units=metric&startdate=2010-05-01&enddate=2010-05-31";
    public static final String LOCATION_URL = BASE_URL + "/stations/";

    private OkHttpClient client;
    private String startDate;
    private String endDate;

    public NOAA() {
        // Create a client with a lengthy timeout in hopes of connecting and getting data
        client = new OkHttpClient.Builder().connectTimeout(1600L, TimeUnit.SECONDS).build();
        startDate = LocalDate.now().minusDays(120).toString();
        endDate = LocalDate.now().minusDays(100).toString();
    }

    /**
    *  Call the NOAA API and get precipitation data for 1000 random stations
    */
    public HashMap<String,Weather> getPrecipData() {
        HashMap<String, Weather> result = new HashMap<String, Weather>();
        //String url = NOAA.PRECIP_URL.replace("*STARTDATE*", startDate).replace("*ENDDATE*", endDate);
        try {
            // Get and parse the JSON
            Response res = makeRequest(NOAA.PRECIP_URL);
            JsonParser parser = new JsonParser();
            JsonArray results = parser.parse(res.body().string())
                    .getAsJsonObject()
                    .getAsJsonArray("results");

            // Iterate over results, storing the values un the hashmap,
            // the key is the StationID, the value is a weather object of the
            // conditions and information about the station.
            Iterator<JsonElement> iterator = results.iterator();
            while (iterator.hasNext()) {
                JsonObject e = iterator.next().getAsJsonObject();
                String type = e.get("datatype").getAsString();
                String station = e.get("station").getAsString();
                String attributes = e.get("attributes").getAsString();
                String date = e.get("date").getAsString();
                int value = e.get("value").getAsInt();

                result.put(station, new Weather(type, station, attributes, date, value));
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLocationFromStationId(String stationId) {
        try {
            Response res = makeRequest(LOCATION_URL + stationId);
            JsonParser parser = new JsonParser();
            JsonObject results = parser.parse(res.body().string()).getAsJsonObject();
            return results.get("name").getAsString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
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
