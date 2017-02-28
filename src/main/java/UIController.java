import java.net.URL;
import java.util.ResourceBundle;
import java.util.Random;
import java.util.Collection;
import java.util.Arrays;

import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

public class UIController implements Initializable {
    @FXML private TextField input;
    @FXML private Text entry1;
    @FXML private Text entry3;
    @FXML private Text entry2;

    private NOAA noaa;
    private HashMap<String, Weather> data;
    private Random random;

    public void initialize(URL location, ResourceBundle resources) {
        noaa = new NOAA();
        this.data = noaa.getPrecipData();
        this.random = new Random();
    }

    @FXML
    public void findSimilarPrecipitationData(ActionEvent e) {
        try {
            Weather selected = data.get(input.getText());
            Collection<Weather> values = data.values();
            Weather similar = null;
            for (Weather w : values) {
                // If the selected value is the current value, skip
                if (w == selected) { continue; }
                if (similar == null) {
                    similar = w;
                } else if (Math.abs(w.getValue() - selected.getValue()) < Math.abs(similar.getValue() - selected.getValue())) {
                    similar = w;
                }
            }
            displayEntry(selected, entry1);
            entry2.setText("is most similar to");
            displayEntry(similar, entry3);
        } catch (Exception x) {
            
        }
    }

    @FXML
    public void findRandomPrecipitationData(ActionEvent e) {
        Weather randomWeather = data.getAtIndex(random.nextInt(data.size()));
        displayEntry(randomWeather, entry1);
        randomWeather = data.getAtIndex(random.nextInt(data.size()));
        displayEntry(randomWeather, entry2);
        randomWeather = data.getAtIndex(random.nextInt(data.size()));
        displayEntry(randomWeather, entry3);
    }

    private void displayEntry(Weather w, Text t) {
        t.setText("Station ID: " + w.getStation() + "\nLocation: " + noaa.getLocationFromStationId(w.getStation()) + "\nValue: " + w.getValue() + " mm");
    }

}
