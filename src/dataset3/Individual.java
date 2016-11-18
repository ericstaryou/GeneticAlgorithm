/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataset3;

import dataset2.*;

/**
 *
 * @author Eric
 */
public class Individual {

    int gene[];
    int fitness;

    public Individual(int n) {
        this.gene = new int[n];
    }

    @Override
    public Individual clone() {
        Individual clone = new Individual(gene.length);
        clone.gene = gene.clone();
        clone.fitness = 0;
        return clone;
    }
}
