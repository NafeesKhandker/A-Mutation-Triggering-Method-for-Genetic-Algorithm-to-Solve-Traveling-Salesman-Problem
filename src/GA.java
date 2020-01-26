import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class GA {
    private static double L_swap = 1;
    private static double L_insert = 1;
    private static double L_opt = 1;
    private static double t_swap = 0;
    private static double t_insert = 0;
    private static double t_opt = 0;
    public static double P_swap = 0;
    public static double P_insert = 0;
    public static double P_opt = 0;
            
    private static double mutationRate = 0.02;
    //private static double mutationRate = 0.2;
    //private static double mutationRate = 0.1;
    //private static double mutationRate = 0.05;
    //private static double mutationRate = 0.025;
    //private static double mutationRate = 0.0125; 
    //private static double mutationRate = 0.00625;
 
    private static final double crossoverRate = 1;
    private static final int tournamentSize = 3;
    private static final boolean elitism = true;
    public static int generation = 1;
    public static int totalGeneration = 0;
    public static double k = 1;

   static int j = 0, counter = 0;
    
    // Evolves a population over one generation
    public static Population evolvePopulation(Population pop) throws IOException {
        Population newPopulation = new Population(pop.populationSize(), false);
        double fitval = 0;
        
        mutationRate = (k / Math.sqrt(generation));
              
        int elitismOffset = 0;
        if (elitism) {
            newPopulation.saveTour(0, pop.getFittest());
            elitismOffset = 1;
        }

        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            newPopulation.saveTour(i, pop.getTour(i));
        }

        //Dynamic Mutation GA starts here
        /*double min = 99999999999999.0;
        for (int i = 0; i < newPopulation.populationSize(); i++) 
        {
            double check = (double) newPopulation.getTour(i).getDistance();
            fitval += (double) newPopulation.getTour(i).getDistance();
            if( check < min)
                min = check;
        }
        
        fitval = fitval / (double)(newPopulation.populationSize());
        double rate = (fitval - min) / min;
        mutationRate = (double) (1 - rate);
        System.out.println(fitval + "\t" + min +"\t" +mutationRate);
        //Dynamic Mutation GA ends here*/
        
        
        
        // Crossover population
        // Loop over the new population's size and create individuals from
        // Current population
        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            // Select parents
            
            if (Math.random() < crossoverRate) {
                // Crossover parents
                Tour parent1 = tournamentSelection(pop);
                Tour parent2 = tournamentSelection(pop);
                Tour child = crossover(parent1, parent2);
                // Add child to new population
                newPopulation.saveTour(i, child);
            }
        }

        for (int i = elitismOffset; i < newPopulation.populationSize(); i++) {
            if (Math.random() < mutationRate) {
                t_swap += 0.5 * (1/L_swap);
                t_insert += 0.5 * (1/L_insert);
                t_opt += 0.5 * (1/L_opt);
                //MutationTrigger(newPopulation, i);
                MutationTriggerACO(newPopulation, i);
                //SwapMutation(newPopulation.getTour(i));
                //InsertionMutation(newPopulation.getTour(i));
                //TwoOptmutate(newPopulation.getTour(i));
            }
        }
        generation++;   
        
        return newPopulation;
    }
    
    // Applies crossover to a set of parents and creates offspring
    public static Tour crossover(Tour parent1, Tour parent2) {
        // Create new child tour
        //System.out.println("Cross in gen:" + generation);
        Tour child = new Tour();

        // Get start and end sub tour positions for parent1's tour
        int startPos = (int) (Math.random() * parent1.tourSize());
        int endPos = (int) (Math.random() * parent1.tourSize());

        // Loop and add the sub tour from parent1 to our child
        for (int i = 0; i < child.tourSize(); i++) {
            // If our start position is less than the end position
            if (startPos < endPos && i > startPos && i < endPos) {
                child.setCity(i, parent1.getCity(i));
            } // If our start position is larger
            else if (startPos > endPos) {
                if (!(i < startPos && i > endPos)) {
                    child.setCity(i, parent1.getCity(i));
                }
            }
        }

        // Loop through parent2's city tour
        for (int i = 0; i < parent2.tourSize(); i++) {
            // If child doesn't have the city add it
            if (!child.containsCity(parent2.getCity(i))) {
                // Loop to find a spare position in the child's tour
                for (int ii = 0; ii < child.tourSize(); ii++) {
                    // Spare position found, add city
                    if (child.getCity(ii) == null) {
                        child.setCity(ii, parent2.getCity(i));
                        break;
                    }
                }
            }
        }
        return child;
    }

    //Mutation Triggering Method
    private static void MutationTrigger(Population newPopulation, int i) {
        double percentFactor = 0.2;
        double selector = totalGeneration - (percentFactor * totalGeneration);
        
        if (generation > selector) {
            //System.out.println("Gen:" + generation + " & Selector: " + selector);
            TwoOptmutate(newPopulation.getTour(i));
        } else if (Math.random() < 0.5) {
            SwapMutation(newPopulation.getTour(i));
        } else {
            InsertionMutation(newPopulation.getTour(i));
        }
    }
    
    private static void MutationTriggerACO(Population newPopulation, int i) throws IOException {
        double total = ((t_swap * (1 / L_swap)) + (t_insert * (1 / L_insert)) + (t_opt * (1 / L_opt)));
        P_swap = (t_swap * (1 / L_swap)) / total;
        P_insert = (t_insert * (1 / L_insert)) / total;
        P_opt = (t_opt * (1 / L_opt)) / total;
        
        double a = 1;
        double b = P_insert + P_opt;
        double c = P_opt;
        double r = Math.random();
       
        DecimalFormat f = new DecimalFormat("##.000000");
        
        System.out.println(f.format(P_swap) + "\t" + f.format(P_insert) + "\t" + f.format(P_opt));
   
        if (b < r && r < a){
            SwapMutation(newPopulation.getTour(i));
            System.out.println("Swap");
        }
        else if (c < r && r < b){
            InsertionMutation(newPopulation.getTour(i));
            System.out.println("Insert");
        }
        else {
            TwoOptmutate(newPopulation.getTour(i));
            System.out.println("2-opt");
        }
    }

    // Swap mutation
    private static void SwapMutation(Tour tour) {        
// Loop through tour cities
        Tour newTour = new Tour();
        for (int i = 0; i < tour.tourSize(); i++) {
            newTour.setCity(i, tour.getCity(i));
        }
         double best_distance = tour.getDistance();

         for (int tourPos1 = 0; tourPos1 < newTour.tourSize(); tourPos1++) {
            // Apply mutation rate
            // Get a second random position in the tour
            int tourPos2 = (int) (newTour.tourSize() * Math.random());

            // Get the cities at target position in tour
            City city1 = newTour.getCity(tourPos1);
            City city2 = newTour.getCity(tourPos2);

            // Swap them around
            newTour.setCity(tourPos2, city1);
            newTour.setCity(tourPos1, city2);
            
            double new_distance = newTour.getDistance();
            
            if(new_distance < best_distance)
            {
                for (int j = 0; j < tour.tourSize(); j++) {
                        tour.setCity(j, newTour.getCity(j));
                }
                L_swap = best_distance = new_distance;
            }
        }
    }

// Swap mutation
    private static void InsertionMutation(Tour tour) {
        // Loop through tour cities
        Tour newTour = new Tour();
        for (int i = 0; i < tour.tourSize(); i++) {
            newTour.setCity(i, tour.getCity(i));
        }
         double best_distance = tour.getDistance();
        
        for (int tourPos1 = 0; tourPos1 < newTour.tourSize(); tourPos1++) {
            // Apply mutation rate
            // Get a second random position in the tour
            int tourPos2 = (int) (newTour.tourSize() * Math.random());

            // Get the cities at target position in tour
            City city = newTour.getCity(tourPos1);
            newTour.DeleteCity(tourPos1);
            newTour.insertCity(tourPos2, city);
            
            double new_distance = newTour.getDistance();
            
            if(new_distance < best_distance)
            {
                for (int j = 0; j < tour.tourSize(); j++) {
                        tour.setCity(j, newTour.getCity(j));
                }
                L_insert = best_distance = new_distance;
            }
        }
    }
    

// Mutate a tour using 2-Opt mutation
    private static void TwoOptmutate(Tour tour) {
        Tour newTour = new Tour();
        for (int i = 0; i < tour.tourSize(); i++) {
            newTour.setCity(i, tour.getCity(i));
        }

        int iteration = 0;

        double best_distance = tour.getDistance();

        for (int i = 1; i < tour.tourSize() - 1; i++) {
            for (int k = i + 1; k < tour.tourSize(); k++) {
                newTour = TwoOptSwap(i, k, tour, newTour);
                iteration++;
                double new_distance = newTour.getDistance();

                if (new_distance < best_distance) {
                    for (int j = 0; j < tour.tourSize(); j++) {
                        tour.setCity(j, newTour.getCity(j));
                    }
                    
                    L_opt = best_distance = new_distance;
                }
            }
        }
    }

    private static Tour TwoOptSwap(int i, int k, Tour _tour, Tour _newTour) {
        int size = _tour.tourSize();

        // 1. take route[0] to route[i-1] and add them in order to new_route
        for (int c = 0; c <= i - 1; ++c) {
            _newTour.setCity(c, _tour.getCity(c));
        }

        // 2. take route[i] to route[k] and add them in reverse order to new_route
        int dec = 0;
        for (int c = i; c <= k; ++c) {
            _newTour.setCity(c, _tour.getCity(k - dec));
            dec++;
        }

        // 3. take route[k+1] to end and add them in order to new_route
        for (int c = k + 1; c < size; ++c) {
            _newTour.setCity(c, _tour.getCity(c));
        }
        return _newTour;
    }

// Selects candidate tour for crossover
    private static Tour tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random candidate tour and
        // add it
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.populationSize());
            tournament.saveTour(i, pop.getTour(randomId));
        }
        // Get the fittest tour
        Tour fittest = tournament.getFittest();
        return fittest;
    }

}
