/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Parameters
{

/*******************************************************************************
*                            INSTANCE VARIABLES                                *
*******************************************************************************/

	public static String expID;
	public static String problemType;

	public static String dataInputFileName;

	public static int numRuns;
	public static int numXRuns;
	public static int generations;
	public static int popSize;

	public static int genCap;
	public static int fitCap;

	public static String minORmax;
	public static int selectType;
	public static int scaleType;
	public static int fitnessFunct;

	public static int xoverType;
	public static double xoverRate;
	public static int mutationType;
	public static double mutationRate;

	public static long seed;
	public static int numGenes;
	public static int geneSize;

	public static int numClauses;
	public static int numVariables;
	public static int clauses[][];
	public static boolean negation[][];
	public static double immigrationRate;
	public static int immigrationPeriod;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/

	public Parameters(String parmfilename) throws java.io.IOException{

		String readLine;
		BufferedReader parmInput = new BufferedReader(new FileReader (parmfilename));

		expID = parmInput.readLine().substring(30);
		problemType = parmInput.readLine().substring(30);

		dataInputFileName = parmInput.readLine().substring(30);

		numXRuns = Integer.parseInt(parmInput.readLine().substring(30).trim());
		numRuns = Integer.parseInt(parmInput.readLine().substring(30).trim());
		generations = Integer.parseInt(parmInput.readLine().substring(30).trim());
		popSize = Integer.parseInt(parmInput.readLine().substring(30).trim());

		selectType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		scaleType = Integer.parseInt(parmInput.readLine().substring(30).trim());

		fitnessFunct = Integer.parseInt(parmInput.readLine().substring(30).trim());

		xoverType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		xoverRate = Double.parseDouble(parmInput.readLine().substring(30).trim());
		mutationType = Integer.parseInt(parmInput.readLine().substring(30).trim());
		mutationRate = Double.parseDouble(parmInput.readLine().substring(30).trim());

		seed = Long.parseLong(parmInput.readLine().substring(30).trim());
		numGenes = Integer.parseInt(parmInput.readLine().substring(30).trim());
		geneSize = Integer.parseInt(parmInput.readLine().substring(30).trim());
		immigrationRate = Double.parseDouble(parmInput.readLine().substring(30).trim());
		immigrationPeriod = Integer.parseInt(parmInput.readLine().substring(30).trim());

		parmInput.close();

		if (scaleType==0 || scaleType==2) minORmax = "max";
		else minORmax = "min";

		File file = new File(dataInputFileName);
		Scanner sc = new Scanner(file);
		while (!sc.next().equals("cnf"));
		numVariables = sc.nextInt();
		numClauses = sc.nextInt();
		clauses = new int[numClauses][3];
		negation = new boolean[numClauses][3];
		for (int i = 0; i<numClauses; i++){
			for (int j = 0; j<3; j++){
				clauses[i][j] = 0;
				negation[i][j] = false;
			}
		}
		int temp = 0;
		for (int i = 0; i<numClauses; i++){
			for (int j = 0; j<3; j++){
				temp = sc.nextInt();
				if (temp<0){
					negation[i][j] = true;
					temp*= -1;
				}
				clauses[i][j] = temp - 1;
			}
			sc.nextInt();
		}

	}

/*******************************************************************************
*                                MEMBER METHODS                                *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void outputParameters(FileWriter output) throws java.io.IOException{


		output.write("Experiment ID                :  " + expID + "\n");
		output.write("Problem Type                 :  " + problemType + "\n");

		output.write("Data Input File Name         :  " + dataInputFileName + "\n");

		output.write("Number of Runs               :  " + numRuns + "\n");
		output.write("Generations per Run          :  " + generations + "\n");
		output.write("Population Size              :  " + popSize + "\n");

		output.write("Selection Method             :  " + selectType + "\n");
		output.write("Fitness Function             :  " + fitnessFunct + "\n");
		output.write("Min or Max Fitness           :  " + minORmax + "\n");

		output.write("Crossover Type               :  " + xoverType + "\n");
		output.write("Crossover Rate               :  " + xoverRate + "\n");
		output.write("Mutation Type                :  " + mutationType + "\n");
		output.write("Mutation Rate                :  " + mutationRate + "\n");

		output.write("Random Number Seed           :  " + seed + "\n");
		output.write("Number of Genes/Points       :  " + numGenes + "\n");
		output.write("Size of Genes                :  " + geneSize + "\n");
		output.write("Number of Genes/Points       :  " + numGenes + "\n");
		output.write("Size of Genes                :  " + geneSize + "\n");
		output.write("Random Immigration           :  " + immigrationRate + "\n");
		output.write("Immigration Period           :  " + immigrationPeriod + "\n");

		output.write("\n\n");

	}
}   // End of Parameters.java **************************************************
