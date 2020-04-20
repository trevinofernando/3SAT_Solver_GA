/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;


public class SAT extends FitnessFunction {

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/


/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

public SAT(){
    name = "3-SAT Problem";
}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/


public int getSatisfiedClausesCount(int[] cnf, List<Integer> assignment){
    int satisfied = 0;
    for (int i = 0; i < cnf.length; i+=3) {
        if ((assignment.get(Math.abs(cnf[i]) -1 ) - ((Math.signum(cnf[i]) + 1) / 2)) == 0) {
            satisfied++;
        } else if ((assignment.get(Math.abs(cnf[i + 1]) -1 ) - ((Math.signum(cnf[i + 1]) + 1) / 2)) == 0) {
            satisfied++;
        } else if ((assignment.get(Math.abs(cnf[i + 2]) -1 ) - ((Math.signum(cnf[i + 2]) + 1) / 2)) == 0) {
            satisfied++;
        }
    }
    return satisfied;
}

public double getVariableViolationsV0(int[] cnf, List<Integer> assignment){
    double violations = 0;
    double[] variableViolations = new double[assignment.size()];
    for (int i = 0; i < cnf.length; i+=3) {
        boolean satisfied = false;
        if ((assignment.get(Math.abs(cnf[i]) - 1) - ((Math.signum(cnf[i]) + 1) / 2)) == 0) {
            satisfied = true;
        } else if ((assignment.get(Math.abs(cnf[i + 1]) - 1) - ((Math.signum(cnf[i + 1]) + 1) / 2)) == 0) {
            satisfied = true;
        } else if ((assignment.get(Math.abs(cnf[i + 2]) - 1) - ((Math.signum(cnf[i + 2]) + 1) / 2)) == 0) {
            satisfied = true;
        }
        if (!satisfied) {
            if ((assignment.get(Math.abs(cnf[i]) - 1) - ((Math.signum(cnf[i]) + 1) / 2)) != 0) {
                variableViolations[Math.abs(cnf[i]) - 1]++;
            } 
            if ((assignment.get(Math.abs(cnf[i + 1]) - 1) - ((Math.signum(cnf[i + 1]) + 1) / 2)) != 0) {
                variableViolations[Math.abs(cnf[i + 1]) - 1]++;
            } 
            if ((assignment.get(Math.abs(cnf[i + 2]) - 1) - ((Math.signum(cnf[i + 2]) + 1) / 2)) != 0) {
                variableViolations[Math.abs(cnf[i + 1]) - 1]++;
            } 
        }
    }
    for (int i = 0; i < variableViolations.length; i++) {
        violations += variableViolations[i] / Parameters.nbclauses;
    }
    return violations;
}

public double getVariableViolations(int[] cnf, List<Integer> assignment){
    double violations = 0;
    for (int i = 0; i < cnf.length; i+=3) {
        boolean satisfied = false;
        int clauseViolations = 0;
        if ((assignment.get(Math.abs(cnf[i]) - 1) - ((Math.signum(cnf[i]) + 1) / 2)) == 0) {
            satisfied = true;
        } else {
            clauseViolations++;
        } 
        if ((assignment.get(Math.abs(cnf[i + 1]) - 1) - ((Math.signum(cnf[i + 1]) + 1) / 2)) == 0) {
            satisfied = true;
        } else {
            clauseViolations++;
        } 
        if ((assignment.get(Math.abs(cnf[i + 2]) - 1) - ((Math.signum(cnf[i + 2]) + 1) / 2)) == 0) {
            satisfied = true;
        } else {
            clauseViolations++;
        } 
        if (!satisfied) {
            violations += clauseViolations;
        }
    }
    return violations;
}

public void doGreedySearch(Chromo X){
    double fitness = X.rawFitness;
    ArrayList<Integer> assignment = new ArrayList<Integer>(X.chromo);

    for (int i = 0; i < Parameters.numGenes; i++) {
        assignment.set(i, 1 - assignment.get(i));
        double newFitness = getSatisfiedClausesCount(Parameters.CNF, assignment);
        if (newFitness > fitness) {
            fitness = newFitness;
        } else {
            assignment.set(i, 1 - assignment.get(i));
        }
    }
    X.chromo = assignment;
    X.rawFitness = fitness;
}

public void doStochasticGreedySearch(Chromo X){
    double fitness = X.rawFitness;
    ArrayList<Integer> assignment = new ArrayList<Integer>(X.chromo);

    for (int i = 0; i < Parameters.numGenes * 20; i++) {
        int index1 = Search.r.nextInt(Parameters.numGenes);
        int index2;
        do {
            index2 = Search.r.nextInt(Parameters.numGenes);
        } while (index1 == index2);
        assignment.set(index1, 1 - assignment.get(index1));
        assignment.set(index2, 1 - assignment.get(index2));
        double newFitness = getSatisfiedClausesCount(Parameters.CNF, assignment);
        if (newFitness > fitness) {
            fitness = newFitness;
        } else {
            assignment.set(index1, 1 - assignment.get(index1));
            assignment.set(index2, 1 - assignment.get(index2));
        }
    }
    X.chromo = assignment;
    X.rawFitness = fitness;
}

public void doStochasticGreedySearchV2(Chromo X){
    double fitness = X.rawFitness;
    int[] index = new int[20];
    ArrayList<Integer> assignment = new ArrayList<Integer>(X.chromo);

    for (int i = 0; i < Parameters.numGenes * 20; i++) {
        for (int j = 0; j < Search.r.nextInt(index.length - 1) + 1; j++) {
            index[j] = Search.r.nextInt(Parameters.numGenes);
            assignment.set(index[j], 1 - assignment.get(index[j]));
        }
        double newFitness = getSatisfiedClausesCount(Parameters.CNF, assignment);
        if (newFitness > fitness) {
            fitness = newFitness;
        } else {
            for (int j = 0; j < index.length; j++) {
                assignment.set(index[j], 1 - assignment.get(index[j]));
            }
        }
    }
    X.chromo = assignment;
    X.rawFitness = fitness;
}

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

public void doRawFitness(Chromo X){
    X.rawFitness = getSatisfiedClausesCount(Parameters.CNF, X.chromo);
    //X.rawFitness = getVariableViolations(Parameters.CNF, X.chromo);
}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{
//TODO
    for (int i=0; i<Parameters.numGenes; i++){
        Hwrite.right(X.chromo.get(i),11,output);
    }
    output.write("   RawFitness");
    output.write("\n        ");
    
    Hwrite.right((int) X.rawFitness,13,output);
    output.write("\n\n");
    return;
}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

    
}
