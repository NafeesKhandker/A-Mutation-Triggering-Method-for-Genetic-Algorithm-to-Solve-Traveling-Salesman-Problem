/*
* TSP_GA.java
* Create a tour and evolve a solution
 */

import java.util.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TSP_GA {

    public static final int generation = 1000;
    public static final int popSize = 50;
    public static int totalCost = 0;
    public static double totalTime = 0;
    public static int best = 100000000;
    public static int worst = 0;

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(new File("pr439.txt"));
        FileWriter fw = new FileWriter(new File("rat195_time_distance.txt"), true);
        FileWriter fw1 = new FileWriter(new File("probability_rat195.txt"), true);
        fw.write("ACOGA" + "\r\n");
        for (int j = 0; j < 5; j++) 
        {
            double startTime = System.currentTimeMillis();
            while (sc.hasNextLine()) {
                String str = sc.nextLine();
                str = str.replaceAll("\\s+", "");
                String[] values = str.split(",");
                //System.out.println(Integer.parseInt(values[2]));
                City city = new City(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
                TourManager.addCity(city);
            }

            // Initialize population
            Population pop = new Population(popSize, true);
            //System.out.println("Initial distance: " + pop.getFittest().getDistance());
            int co = 0;
            // Evolve population for 100 generations
            GA.totalGeneration = generation;
            DecimalFormat f = new DecimalFormat("##.000000");
        
            fw1.write(f.format(GA.P_swap) + "\t" + f.format(GA.P_insert) + "\t" + f.format(GA.P_opt) + "\r\n");
            pop = GA.evolvePopulation(pop);
            co = pop.getFittest().getDistance();
            //fw.write(co + "\r\n");
            for (int i = 1; i <= generation; i++) 
            {
                pop = GA.evolvePopulation(pop);
                //Tour t = pop.getFittest();
                co = pop.getFittest().getDistance();
                fw1.write(f.format(GA.P_swap) + "\t" + f.format(GA.P_insert) + "\t" + f.format(GA.P_opt) + "\r\n");
                //fw.write(co+"\r\n");   
            }
            GA.generation = 1;
            int cost = pop.getFittest().getDistance();
            fw.write(cost + "\t");
            
            double endTime = System.currentTimeMillis();
            double duration = (endTime - startTime) / 1000;
            fw.write(duration + "\r\n");
            fw1.write("\r\n\r\n\r\n Closed   " + j + "\r\n\r\n\r\n");
            
//totalCost += cost;
            if(cost < best)
                best = cost;
            if (cost > worst)
                worst = cost;
            // Print final results
            System.out.println("Finished");
            System.out.println("Final distance: " + cost);
            System.out.println("Solution:");
            Tour t = pop.getFittest();
            System.out.println(t);
            //fw.write(t.toString());

            //double endTime = System.currentTimeMillis();
            //double duration = (endTime - startTime) / 1000;
            //totalTime += duration;
        }

        System.out.println("Best Cost= " + best);
        System.out.println("Worst Cost= " + worst);
        System.out.println("Average Cost= " + totalCost);
        System.out.println("Average Time= " + totalTime);
        fw.close();
        fw1.close();
    }
}
