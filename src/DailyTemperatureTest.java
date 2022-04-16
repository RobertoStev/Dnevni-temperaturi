import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * I partial exam 2016
 */
public class DailyTemperatureTest {
    public static void main(String[] args) {
        DailyTemperatures dailyTemperatures = new DailyTemperatures();
        dailyTemperatures.readTemperatures(System.in);
        System.out.println("=== Daily temperatures in Celsius (C) ===");
        dailyTemperatures.writeDailyStats(System.out, 'C');
        System.out.println("=== Daily temperatures in Fahrenheit (F) ===");
        dailyTemperatures.writeDailyStats(System.out, 'F');
    }
}

// Vashiot kod ovde
abstract class Temperature {
    private int value;

    public Temperature(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    //Factory design pattern
    public static Temperature createTemperature(String part) {

        char type = part.charAt(part.length() - 1);
        int value = Integer.parseInt(part.substring(0, part.length() - 1));
        if (type == 'C')
            return new CTemperature(value);
        else
            return new FTemperature(value);
    }

    abstract public double getCelsius();

    abstract public double getFahrenheit();
}

class CTemperature extends Temperature {

    public CTemperature(int value) {
        super(value);
    }

    @Override
    public double getCelsius() {
        return (double) getValue();
    }

    @Override
    public double getFahrenheit() {
        return getValue() * 9.0 / 5 + 32.0;
    }

    @Override
    public String toString() {
        return String.format("%dC", getValue());

    }
}

class FTemperature extends Temperature {

    public FTemperature(int value) {
        super(value);
    }

    @Override
    public double getCelsius() {
        return (getValue() - 32) * 5.0 / 9.0;
    }

    @Override
    public double getFahrenheit() {
        return (double) getValue();
    }

    @Override
    public String toString() {
        return String.format("%dF", getValue());

    }
}

class DailyMeasurement implements Comparable<DailyMeasurement> {
    private int day;
    private List<Temperature> temperatures;

    public DailyMeasurement(int day, List<Temperature> temperatures) {
        this.day = day;
        this.temperatures = temperatures;
    }

    public static DailyMeasurement createDailyMeasurement(String line) {
        String[] parts = line.split("\\s+");
        int day = Integer.parseInt(parts[0]);
        List<Temperature> temperatures = new ArrayList<>();
        for (int i = 1; i < parts.length; i++) {
            temperatures.add(Temperature.createTemperature(parts[i]));
        }
        //List<Temperature> temperatures = Arrays.stream(parts).skip(1)
        //.map(part -> Temperature.createTemperature(part))
        //.collect(Collectors.toList());
        return new DailyMeasurement(day, temperatures);
    }

    @Override
    public int compareTo(DailyMeasurement o) {
        return Integer.compare(this.day, o.day);
    }

    public String toString(char scale) {
        DoubleSummaryStatistics dss = temperatures.stream()
                .mapToDouble(t -> {
                    if (scale == 'C')
                        return t.getCelsius();
                    else
                        return t.getFahrenheit();
                }).summaryStatistics();

        /*DoubleSummaryStatistics dss = temperatures.stream()
                .mapToDouble(t -> scale=='C'? t.getCelsius() : t.getFahrenheit())
                .summaryStatistics();**/
        return String.format("%3d: Count: %3d Min: %6.2f%c Max: %6.2f%c Avg: %6.2f%c",
                day,
                dss.getCount(),
                dss.getMin(),
                scale,
                dss.getMax(),
                scale,
                dss.getAverage(),
                scale);
    }
}

class DailyTemperatures {
    private List<DailyMeasurement> dailyMeasurements;

    public DailyTemperatures() {
        dailyMeasurements = new ArrayList<>();
    }

    public void readTemperatures(InputStream inputStream) {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        dailyMeasurements = br.lines().map(line -> DailyMeasurement.createDailyMeasurement(line))
                .collect(Collectors.toList());

        /*String line;
        while ((line = br.readLine()) != null) {
                dailyMeasurements.add(DailyMeasurement.createDailyMeasurement(line));
        }**/
    }

    public void writeDailyStats(OutputStream outputStream, char scale) {
        PrintWriter pw = new PrintWriter(outputStream);
        //dailyMeasurements.sort(Comparator.naturalOrder());
        Collections.sort(dailyMeasurements);
        dailyMeasurements.forEach(dm -> pw.println(dm.toString(scale)));
        pw.flush();
    }
}