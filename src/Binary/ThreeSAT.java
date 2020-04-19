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

	public static boolean fitnessCalculated;
	public static int maxClauseSatisfied;
/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public ThreeSAT(){
		name = "3SAT Problem";
		fitnessCalculated = false;
	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/

//  COMPUTE A CHROMOSOME'S RAW FITNESS *************************************

	public void doRawFitness(Chromo X){
		if (!fitnessCalculated && Parameters.fitnessFunct == 1){
			for (int i = 0; i < Parameters.popSize; i++)
				Search.member[i].rawFitness = 0;
			
			int numberSatisfied[] = new int[Parameters.popSize];
			for (int i = 0; i < Parameters.popSize; i++)
				numberSatisfied[i] = 0;
			int memberSatisfied[] = new int[Parameters.popSize];
			int sizemember;
			boolean temp;
			for (int i = 0; i < Parameters.numClauses; i++){
				sizemember = 0;
				for (int j = 0; j < Parameters.popSize; j++){
					for (int k = 0; k < 3; k++){
						temp = (Search.member[j].chromo.charAt(Parameters.clauses[i][k]) == '1');
						if (temp ^ Parameters.negation[i][k]){
							memberSatisfied[sizemember] = j;
							numberSatisfied[j]++;
							sizemember++;
							break;
						}
					}
				}
				for (int j = 0; j < sizemember; j++){
					
					Search.member[memberSatisfied[j]].rawFitness = Search.member[memberSatisfied[j]].rawFitness + (1.0/(double)sizemember);
				}
			}
			int max = 0;
			for (int i = 0; i < Parameters.popSize; i++)
				if (numberSatisfied[i]>max)
					max = numberSatisfied[i];
			maxClauseSatisfied = max;
			fitnessCalculated = true;
		}
		else if (Parameters.fitnessFunct == 0){
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
		output.write("\n");
		output.write("Clauses Satisfied: ");
		Hwrite.left((int) clausesSatisfied(X),13,output);
		output.write("\n\n");
		return;
	}


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static int clausesSatisfied(Chromo X){
		int num = 0;
		boolean temp = false;
		for (int i = 0; i< Parameters.numClauses; i++){
			for (int j = 0; j < 3; j++){
				temp = (X.chromo.charAt(Parameters.clauses[i][j]) == '1');
				if (temp ^ Parameters.negation[i][j]){
					num++;
					break;
				}
			}
		}
		return num;
	}

}   // End of OneMax.java ******************************************************

