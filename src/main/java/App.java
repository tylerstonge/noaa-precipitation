
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
 
public class App {
    
    public static void main(String[] args) {
        NOAA n = new NOAA();
        n.getTemperatureData();
    }
}
