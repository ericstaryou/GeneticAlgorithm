/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biocompassignment;

import java.util.ArrayList;
import java.util.Random;
import org.jfree.ui.RefineryUtilities;
import java.util.Scanner;

/**
 *
 * @author Eric
 */
public class GeneticAlgorithm {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int genTracker = 1;
        int noOfGeneration = 50;
        int p = 50;
        int n = 60;
        int mut = 60;
        //int t = 10;
        Individual population[];
        Individual fittest = null;
        Data data1[] = readFile1();
        Random rand = new Random();

        //test data1
//        for (int i = 0; i < 10; i++) {
//            System.out.print("Condition: ");
//            for (int j = 0; j < 5; j++) {
//                System.out.print(data1[i].var[j]);
//            }
//            System.out.println(" " + data1[i].output);
//        }
        //array for line chart dataset
        int bf[] = new int[noOfGeneration];
        double mf[] = new double[noOfGeneration];

        //create new population
        population = new Individual[p];

        for (int i = 0; i < p; i++) {
            population[i] = new Individual(n);
        }

        //fills individual's gene with binary randomly
        for (int i = 0; i < p; i++) {
            int counter = 0;
            for (int j = 0; j < n; j++) {
                if (counter < 5) {
                    population[i].gene[j] = rand.nextInt(3);
                    counter++;
                } else if (counter == 5) {
                    population[i].gene[j] = rand.nextInt(2);
                    counter = 0;
                }
            }

            population[i].fitness = 0;
        }

        //genome check
        System.out.println("After init: ");
        printGenome(population, p, n);
        System.out.println("");

        //evaluate each individual
        GeneticAlgorithm.evaluateIndividuals(population, p, n, data1);

        //total fitness check
        System.out.println("_GEN: " + genTracker);
        System.out.println("Best fitness check of population of gen " + (genTracker) + ": " + getBestFitness(population, p));
        System.out.println("Mean fitness check of population of gen " + (genTracker) + ": " + getMeanFitness(population, p));
        System.out.println("");

        //populate dataset with first generation
        bf[0] = getBestFitness(population, p);
        mf[0] = getMeanFitness(population, p);

        /**
         * Generation cycle starts here
         */
        for (int k = 1; k < noOfGeneration; k++) {

            System.out.println("_GEN: " + (genTracker + 1));

            //tournament selection
            Individual offspring[] = new Individual[p];

            int parent1, parent2;

            for (int i = 0; i < p; i++) {
                parent1 = rand.nextInt(p); //randomly choose parent
                parent2 = rand.nextInt(p); //randomly choose parent
                if (population[parent1].fitness >= population[parent2].fitness) {
                    offspring[i] = population[parent1].clone();
                } else {
                    offspring[i] = population[parent2].clone();
                }
            }

//            //genome check
//            System.out.println("Before Crossover: ");
//            printGenome(offspring, p, n);
//            System.out.println("");
            //crossover
            for (int i = 0; i < p; i = i + 2) {
                int splitPoint = rand.nextInt(n);
                for (int j = splitPoint; j < n; j++) {
                    int temp = offspring[i].gene[j];
                    offspring[i].gene[j] = offspring[i + 1].gene[j];
                    offspring[i + 1].gene[j] = temp;
                }
            }

            //genome check
//            System.out.println("After Crossover:");
//            printGenome(offspring, p, n);
//            System.out.println("");
            //evaluate each individual by counting the number of 1s after crossover
            evaluateIndividuals(offspring, p, n, data1);

            //total fitness check
            System.out.println("Best fitness check after selection & crossover: " + getBestFitness(offspring, p));
            System.out.println("Mean fitness check after selection & crossover: " + getMeanFitness(offspring, p));
            System.out.println("");

            //Mutation
            for (int i = 0; i < p; i++) {
                int counter = 0;
                for (int j = 0; j < n; j++) {
                    if (counter < 5) {
                        if (rand.nextInt(1000) < mut) {
                            if (offspring[i].gene[j] == 0) {
                                int temp = rand.nextInt(3);
                                while (temp == 0) {
                                    temp = rand.nextInt(3);
                                }
                                offspring[i].gene[j] = temp;
                            } else if (offspring[i].gene[j] == 1) {
                                int temp = rand.nextInt(3);
                                while (temp == 1) {
                                    temp = rand.nextInt(3);
                                }
                                offspring[i].gene[j] = temp;
                            } else if (offspring[i].gene[j] == 2) {
                                int temp = rand.nextInt(3);
                                while (temp == 2) {
                                    temp = rand.nextInt(3);
                                }
                                offspring[i].gene[j] = temp;
                            }
                        }
                        counter++;
                    } else if (counter == 5) {
                        if (rand.nextInt(1000) < mut) {
                            if (offspring[i].gene[j] == 0) {
                                offspring[i].gene[j] = 1;
                            } else {
                                offspring[i].gene[j] = 0;
                            }
                        }
                        counter = 0;
                    }
                }
            }

            //genome check
            System.out.println("After Mutation:");
            printGenome(offspring, p, n);
            System.out.println("");
            evaluateIndividuals(offspring, p, n, data1);

            //total fitness check
            System.out.println("Best fitness check after mutation: " + getBestFitness(offspring, p));
            System.out.println("Mean fitness check after mutation: " + getMeanFitness(offspring, p));
            System.out.println("Total fitness: " + getTotalFitness(offspring, p));
            System.out.println("");

            //pass the best fitness to next gen
            fittest = getFittestIndividual(population, p);

            //pass offspring to next generation
            replaceWorstIndividual(offspring, p, fittest);

            //populate line chart dataset array
            bf[k] = getBestFitness(offspring, p);
            mf[k] = getMeanFitness(offspring, p);

            //replace old generation with new generation
            population = offspring;

            genTracker++;
        }

        //Creating Line Chart
        final ChartUI lc = new ChartUI("Genetic Algorithm Best Fitness", noOfGeneration, bf, mf);
        lc.pack();
        RefineryUtilities.centerFrameOnScreen(lc);
        lc.setVisible(true);
    }

    public static void printGenome(Individual pop[], int p, int n) {
        for (int i = 0; i < p; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(pop[i].gene[j]);
            }
            System.out.println("");
        }
    }

    public static int getTotalFitness(Individual pop[], int p) {
        int totalFitness = 0;
        for (int i = 0; i < p; i++) {
            totalFitness = totalFitness + pop[i].fitness;
        }
        return totalFitness;
    }

    public static int getBestFitness(Individual pop[], int p) {
        int largest = Integer.MIN_VALUE;
        for (int i = 0; i < p; i++) {
            if (pop[i].fitness > largest) {
                largest = pop[i].fitness;
            }
        }
        return largest;
    }

    public static int getWorstFitness(Individual pop[], int p) {
        int smallest = Integer.MAX_VALUE;
        for (int i = 0; i < p; i++) {
            if (pop[i].fitness < smallest) {
                smallest = pop[i].fitness;
            }
        }
        return smallest;
    }

    public static double getMeanFitness(Individual pop[], int p) {
        double meanFitness = getTotalFitness(pop, p) / (double) p;
        return meanFitness;
    }

    public static void evaluateIndividuals(Individual pop[], int p, int n, Data[] data) {

        //for each individual
        for (int i = 0; i < p; i++) {
            Rule rule[] = new Rule[10];
            for (int j = 0; j < 10; j++) {
                rule[j] = new Rule(5);
            }

            pop[i].fitness = 0;
            ArrayList<String> list = new ArrayList();   //make an arraylist
            for (int j = 0; j < n; j++) {
                list.add(Integer.toString(pop[i].gene[j]));
            }

            StringBuilder geneString = new StringBuilder();

            for (String s : list) {
                geneString.append(s);
            }

            //System.out.println("GENE HERE : " + geneString);
            String geneArr[] = geneString.toString().split("(?<=\\G.{6})");

            //for each rule
            for (int j = 0; j < 10; j++) {
                String cond = geneArr[j].substring(0, 5);
                String act = geneArr[j].substring(5);
                String condition[] = cond.split("");

                //populate cond
                for (int k = 0; k < 5; k++) {
                    rule[j].cond[k] = Integer.parseInt(condition[k]);
                }

                //populate output
                rule[j].action = Integer.parseInt(act);
            }

            //compare indie's rule with sample rule to determine fitness
            for (int j = 0; j < 32; j++) { //for each data check to see how many rules got it right
                ruleLoop:
                for (int k = 0; k < 10; k++) { //for each rule check the condition and result
                    for (int l = 0; l < 5; l++) {
                        if (rule[k].cond[l] != 2 && data[j].var[l] != rule[k].cond[l]) {
                            break;
                        } 
                        if (l == 4) {
                            if (data[j].output == rule[k].action) {
                                pop[i].fitness++;
                            }
                            break ruleLoop;
                        }
                    }
                }
            }                          
        }
    }

    public static Individual getFittestIndividual(Individual pop[], int p) {
        Individual fittest = null;
        int largest = Integer.MIN_VALUE;

        for (int i = 0; i < p; i++) {
            if (pop[i].fitness > largest) {
                largest = pop[i].fitness;
                fittest = pop[i];
            }
        }
        return fittest;
    }

    public static void replaceWorstIndividual(Individual pop[], int p, Individual fittest) {
        int wf = getWorstFitness(pop, p);
        for (int i = 0; i < p; i++) {
            if (pop[i].fitness == wf) {
                pop[i] = fittest;
                break;
            }
        }
    }

    public static Data[] readFile1() {
        Scanner sc = new Scanner(GeneticAlgorithm.class.getResourceAsStream("/data1.txt"));
        Data data1[] = new Data[32];
        for (int i = 0; i < 32; i++) {
            data1[i] = new Data(5);
        }
        for (int i = 0; i < 32; i++) {
            String temp = sc.nextLine();
            String items[] = temp.split(" ");
            String condition[] = items[0].split("");

            //populate cond
            for (int j = 0; j < 5; j++) {
                data1[i].var[j] = Integer.parseInt(condition[j]);
            }

            //populate output
            data1[i].output = Integer.parseInt(items[1]);
        }

        return data1;
    }

}
