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
        boolean clauseSatisfied = false;
        if ((assignment.get(Math.abs(cnf[i]) -1 ) - ((Math.signum(cnf[i]) + 1) / 2)) == 0) {
            clauseSatisfied = true;
        } else if ((assignment.get(Math.abs(cnf[i + 1]) -1 ) - ((Math.signum(cnf[i + 1]) + 1) / 2)) == 0) {
            clauseSatisfied = true;
        } else if ((assignment.get(Math.abs(cnf[i + 2]) -1 ) - ((Math.signum(cnf[i + 2]) + 1) / 2)) == 0) {
            clauseSatisfied = true;
        } 
        if (clauseSatisfied) {
            satisfied++;
        }
    }
    return satisfied;
}

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

public void doRawFitness(Chromo X){
    X.rawFitness = getSatisfiedClausesCount(Parameters.CNF, X.chromo);
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
