/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class ThreeSAT extends FitnessFunction{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/


/*******************************************************************************
*                            STATIC VARIABLES                                  *
*******************************************************************************/


/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public ThreeSAT(){
		name = "3SAT Problem";
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){

		X.rawFitness = 0;
		boolean temp = false;
		for (int i = 0; i< Parameters.numClauses; i++){
			for (int j = 0; j < 3; j++){
				temp = (X.chromo.charAt(Parameters.clauses[i][j]) == '1');
				if (temp ^ Parameters.negation[i][j]){
					X.rawFitness++;
					break;
				}
			}
		}
	}

//  PRINT OUT AN INDIVIDUAL GENE TO THE SUMMARY FILE *********************************

	public void doPrintGenes(Chromo X, FileWriter output) throws java.io.IOException{

		for (int i=0; i<Parameters.numVariables; i+=10)
			Hwrite.left(i, 10, output);
		output.write("\n");
		for (int i=0; i<Parameters. numVariables; i+=10)
			Hwrite.left("|", 10, output);
		output.write("\n");
		output.write(X.chromo);
		output.write("\n");
		output.write("   RawFitness");
		output.write("\n        ");
		Hwrite.right((int) X.rawFitness,13,output);
		output.write("\n\n");
		return;
	}

/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

}   // End of OneMax.java ******************************************************

