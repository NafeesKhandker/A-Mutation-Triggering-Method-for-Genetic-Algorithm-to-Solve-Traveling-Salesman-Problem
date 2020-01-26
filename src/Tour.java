
import java.util.ArrayList;
import java.util.Collections;

public class Tour {

    // Holds our tour of cities
    private ArrayList tour = new ArrayList<City>();
    private double fitness = 0;
    private int distance = 0;

    // Constructs a blank tour
    public Tour() {
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            tour.add(null);
        }
    }

    // Creates a random individual
    public void generateIndividual() {
        // Loop through all our destination cities and add them to our tour
        for (int i = 0; i < TourManager.numberOfCities(); i++) {
            setCity(i, TourManager.getCity(i));
        }
        // Randomly reorder the tour
        Collections.shuffle(tour);
    }

    // Gets a city from the tour
    public City getCity(int tourPosition) {
        return (City) tour.get(tourPosition);
    }

    // Sets a city in a certain position within a tour
    public void setCity(int tourPosition, City city) {
        tour.set(tourPosition, city);

        // Reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    public void insertCity(int tourPosition, City city) {
        tour.add(tourPosition, city);
        // Reset the fitness and distance
        fitness = 0;
        distance = 0;
    }

    public void DeleteCity(int tourPosition) {
        tour.remove(tourPosition);
    }

    // Gets the tours fitness
    public double getFitness() {
        if (fitness == 0) {
            fitness = 1 / (double) getDistance();
        }
        return fitness;
    }

    // Gets the total distance of the tour
    public int getDistance() {
        if (distance == 0) {
            int tourDistance = 0;
            // Loop through tour's cities
            for (int i = 0; i < tourSize(); i++) {
                // Get city traveling from
                City fromCity = getCity(i);
                // City traveling to
                City destinationCity;
                // Check we're not on our tour's last city, if we are set our
                // tour's final destination city to our starting city
                if (i + 1 < tourSize()) {
                    destinationCity = getCity(i + 1);
                } else {
                    destinationCity = getCity(0);
                }
                // Get the distance between the two cities
                tourDistance += fromCity.distanceTo(destinationCity);
            }
            distance = tourDistance;
        }
        City fisrt = getCity(0);
        City last = getCity(tourSize()-1);
        distance = distance + (int)last.distanceTo(fisrt);
        return distance;
    }

    // Get number of cities on our tour
    public int tourSize() {
        return tour.size();
    }

    // Check if the tour contains a city
    public boolean containsCity(City city) {
        return tour.contains(city);
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < tourSize(); i++) {
            geneString += getCity(i) + ",";
        }
        return geneString;
    }
}
