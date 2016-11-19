/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset3;

import java.text.DecimalFormat;
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
        int noOfGeneration = 500;
        int p = 50;
        int n = 130;
        int mut = 20;
        //int t = 10;
        Individual population[];
        Individual fittest = null;
        Data data1[] = readFile1();
        Random rand = new Random();
        ArrayList<Individual> bestList = new ArrayList();

        //test data1
//        for (int i = 0; i < 2000; i++) {
//            System.out.print("Data rule: ");
//            for (int j = 0; j < 6; j++) {
//                System.out.print(data1[i].var[j] + ",");
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

        //fills individual's gene with floating point number randomly
        for (int i = 0; i < p; i++) {
            int counter = 0;
            for (int j = 0; j < n; j++) {
                if (counter < 12) {
                    double randomValue = new Random().nextDouble();
                    DecimalFormat df = new DecimalFormat("0.000000");
                    population[i].gene[j] = Double.parseDouble(df.format(randomValue));
                    counter++;
                } else if (counter == 12) {
                    population[i].gene[j] = rand.nextInt(2);
                    counter = 0;
                }
            }

            population[i].fitness = 0;
        }

        //genome check
//        System.out.println("After init: ");
//        printGenome(population, p, n);
//        System.out.println("");
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
        int genCounter = 0;
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
                    double temp = offspring[i].gene[j];
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
                    if (counter < 12) {
                        if (rand.nextInt(1000) < mut) {
                            double temp = Math.random();
                            while (temp == 0 || temp == offspring[i].gene[j]) {
                                temp = Math.random();
                            }
                            
                            double gene = offspring[i].gene[j];
                            double sum = gene + temp;
                            if(sum > 1){
                                offspring[i].gene[j] += temp*(-1);
                            }else{
                                offspring[i].gene[j] += temp;
                            }
                        }
                        counter++;
                    } else if (counter == 12) {
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
//            System.out.println("After Mutation:");
//            printGenome(offspring, p, n);
//            System.out.println("");
            Rule rule1[] = evaluateIndividuals(offspring, p, n, data1);
//            //test rule1
//            for (int i = 0; i < 10; i++) {
//                System.out.print("Rule rule: ");
//                for (int j = 0; j < 12; j++) {
//                    System.out.print(rule1[i].cond[j] + ",");
//                }
//                System.out.println(" " + rule1[i].action);
//            }

            //total fitness check
            System.out.println("Best fitness check after mutation: " + getBestFitness(offspring, p));
            System.out.println("Mean fitness check after mutation: " + getMeanFitness(offspring, p));
            System.out.println("Total fitness: " + getTotalFitness(offspring, p));
            System.out.println("");

            //pass the best fitness to next gen
            fittest = getFittestIndividual(population, p);

            //pass offspring to next generation
            replaceWorstIndividual(offspring, p, fittest);
            
            //get the fittest individual for every 10th generation for validation
            if(genCounter == 9){
                bestList.add(getFittestIndividual(population, p));
                genCounter = 0;
            }
            genCounter++;
            
            //populate line chart dataset array
            bf[k] = getBestFitness(offspring, p);
            mf[k] = getMeanFitness(offspring, p);
            System.out.println("bf = " + bf[k]);

            //replace old generation with new generation
            population = offspring;

            genTracker++;
        }
        
        int y = 0;
        for(Individual x : bestList){
            System.out.println(y+ ": " + x.fitness);
            y++;
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

    public static Rule[] evaluateIndividuals(Individual pop[], int p, int n, Data[] data) {
        Rule rule[] = null;
        //for each individual
        for (int i = 0; i < p; i++) {
            pop[i].fitness = 0;

            rule = new Rule[10];
            for (int j = 0; j < 10; j++) {
                rule[j] = new Rule(12);
            }

            ArrayList<Double> clist = new ArrayList();
            ArrayList<Double> olist = new ArrayList();

            int counter = 0;
            for (int j = 0; j < n; j++) {
                if (counter < 12) {
                    clist.add(pop[i].gene[j]);
                    counter++;
                } else if (counter == 12) {
                    olist.add(pop[i].gene[j]);
                    counter = 0;
                }
            }

            //populate Rule
            int condCounter = 0;
            for (int j = 0; j < 10; j++) {
                //populate conditon
                for (int k = 0; k < 12; k++) {
                    rule[j].cond[k] = clist.get(condCounter + k);
                }
                condCounter += 12;

                //populate output
                rule[j].action = olist.get(j);
            }

            //make smaller value before larger value
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 12; k += 2) {
                    double temp = 0;
                    if (rule[j].cond[k] > rule[j].cond[k + 1]) {
                        temp = rule[j].cond[k];
                        rule[j].cond[k] = rule[j].cond[k + 1];
                        rule[j].cond[k + 1] = temp;
                    }
                }
            }

            //compare indie's rule with sample rule to determine fitness
            for (int j = 0; j < 2000; j++) { //for each data check to see how many rules got it right
                ruleLoop:
                for (int k = 0; k < 10; k++) { //for each rule check the condition and result
                    int x = 0;
                    for (int l = 0; l < 12; l += 2) {
                        if (data[j].var[x] < rule[k].cond[l] || data[j].var[x] > rule[k].cond[l + 1]) {
                            break;
                        }

                        if (l == 10) {
                            if (data[j].output == rule[k].action) {
                                pop[i].fitness++;
                                //System.out.print("["+rule[k].cond[l]+"]");
                            }
                            break ruleLoop;
                        }

                        x++;
                    }
                }
            }
        }//for each individual
        return rule;
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
        Scanner sc = new Scanner(GeneticAlgorithm.class.getResourceAsStream("/data3.txt"));
        Data data1[] = new Data[2000];
        for (int i = 0; i < 2000; i++) {
            data1[i] = new Data(6);
        }
        for (int i = 0; i < 2000; i++) {
            String temp = sc.nextLine();
            String items[] = temp.split(" ");

            //populate cond
            for (int j = 0; j < 6; j++) {
                data1[i].var[j] = Double.parseDouble(items[j]);
            }

            //populate output
            data1[i].output = Double.parseDouble(items[6]);
        }

        return data1;
    }

}
