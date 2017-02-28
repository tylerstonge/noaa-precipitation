public class Weather {
    String type;
    String station;
    String attributes;
    int value;
    String date;

    public Weather(String type, String station, String attributes, String date, int value) {
        this.type = type;
        this.station = station;
        this.attributes = attributes;
        this.date = date;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getStation() {
        return station;
    }

    public String getAttributes() {
        return attributes;
    }

    public String getDate() {
        return date;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object w) {
        // If Station IDs equal then Weather data is equal
        if (w instanceof Weather)
            return ((Weather) w).getStation().equals(this.station);
        return false;
    }
}
