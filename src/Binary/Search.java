/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;
import java.text.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static Chromo[] member;
	public static Chromo[] child;

	public static Chromo bestOfGenChromo;
	public static int bestOfGenR;
	public static int bestOfGenG;
	public static Chromo bestOfRunChromo;
	public static int bestOfRunR;
	public static int bestOfRunG;
	public static Chromo bestOverAllChromo;
	public static int bestOverAllR;
	public static int bestOverAllG;

	public static double sumRawFitness;
	public static double sumRawFitness2;	// sum of squares of fitness
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;
	public static double stdevRawFitness;

	public static int G;
	public static int R;
	public static int XR;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

	private static double fitnessStats[][];  // 0=Avg, 1=Best

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

	public static void swap(int[] x, int i, int j){
		int temp = x[i];
		x[i] = x[j];
		x[j] = temp;
	}

	public static void main(String[] args) throws java.io.IOException{

		Calendar dateAndTime = Calendar.getInstance(); 
		Date startTime = dateAndTime.getTime();

	//  Read Parameter File
		System.out.println("\nParameter File Name is: " + args[0] + "\n");
		Parameters parmValues = new Parameters(args[0]);

	//  Write Parameters To Summary Output File
		String summaryFileName = Parameters.expID + "_summary.txt";
		FileWriter summaryOutput = new FileWriter(summaryFileName);
		parmValues.outputParameters(summaryOutput);

		//  Write Stats To Output CSV File (XRunIndex, RunIndex, GenIndex, BestFitness, AvgFitness)
		String genStatsFileName = Parameters.expID + "_gen_stats.csv";
		FileWriter genStatsOutput = new FileWriter(genStatsFileName);
		genStatsOutput.write("XR,R,G,bestF,avgF\n");
		StringBuilder genStatsBuilder = new StringBuilder();

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("NM")){
				problem = new NumberMatch();
		}
		else if (Parameters.problemType.equals("OM")){
				problem = new OneMax();
		}
		else if (Parameters.problemType.equals("3SAT")){
				problem = new ThreeSAT();
		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new Chromo[Parameters.popSize];
		child = new Chromo[Parameters.popSize];
		bestOfGenChromo = new Chromo();
		bestOfRunChromo = new Chromo();
		bestOverAllChromo = new Chromo();

		if (Parameters.minORmax.equals("max")){
			defaultBest = 0;
			defaultWorst = 999999999999999999999.0;
		}
		else{
			defaultBest = 999999999999999999999.0;
			defaultWorst = 0;
		}

		bestOverAllChromo.rawFitness = defaultBest;

		//  Start program for multiple XRuns
		for (XR = 1; XR <= Parameters.numXRuns; XR++){
			//  Start program for multiple runs
			int successCount = 0;
			for (R = 1; R <= Parameters.numRuns; R++){

				bestOfRunChromo.rawFitness = defaultBest;
				System.out.println();

				//	Initialize First Generation
				for (int i=0; i<Parameters.popSize; i++){
					member[i] = new Chromo();
					child[i] = new Chromo();
				}

				//	Begin Each Run
				for (G=0; G<Parameters.generations; G++){
					//	Random Immigration
					if (G%Parameters.immigrationPeriod == 0){
						int num = (int)(Parameters.popSize * Parameters.immigrationRate);
						int index[] = new int[Parameters.popSize];
						for (int i = 0; i < Parameters.popSize; i++)
							index[i] = i;
						int t;
						int toReplace[] = new int[num];
						for (int i = 0; i<num; i++){
							t = r.nextInt(Parameters.popSize-i);
							swap(index, t, num-i-1);
							toReplace[i] = index[num-i-1];
						}

						for (int i = 0; i < num; i++)
							member[toReplace[i]] = new Chromo();
					}

					sumProFitness = 0;
					sumSclFitness = 0;
					sumRawFitness = 0;
					sumRawFitness2 = 0;
					bestOfGenChromo.rawFitness = defaultBest;

					//	Test Fitness of Each Member
					for (int i=0; i<Parameters.popSize; i++){

						//member[i].rawFitness = 0;
						member[i].sclFitness = 0;
						member[i].proFitness = 0;

						problem.doRawFitness(member[i]);

						sumRawFitness = sumRawFitness + member[i].rawFitness;
						sumRawFitness2 = sumRawFitness2 +
							member[i].rawFitness * member[i].rawFitness;

						if (Parameters.minORmax.equals("max")){
							if (member[i].rawFitness > bestOfGenChromo.rawFitness){
								Chromo.copyB2A(bestOfGenChromo, member[i]);
								bestOfGenR = R;
								bestOfGenG = G;
							}
							if (member[i].rawFitness > bestOfRunChromo.rawFitness){
								Chromo.copyB2A(bestOfRunChromo, member[i]);
								bestOfRunR = R;
								bestOfRunG = G;
							}
							if (member[i].rawFitness > bestOverAllChromo.rawFitness){
								Chromo.copyB2A(bestOverAllChromo, member[i]);
								bestOverAllR = R;
								bestOverAllG = G;
							}
						}
						else {
							if (member[i].rawFitness < bestOfGenChromo.rawFitness){
								Chromo.copyB2A(bestOfGenChromo, member[i]);
								bestOfGenR = R;
								bestOfGenG = G;
							}
							if (member[i].rawFitness < bestOfRunChromo.rawFitness){
								Chromo.copyB2A(bestOfRunChromo, member[i]);
								bestOfRunR = R;
								bestOfRunG = G;
							}
							if (member[i].rawFitness < bestOverAllChromo.rawFitness){
								Chromo.copyB2A(bestOverAllChromo, member[i]);
								bestOverAllR = R;
								bestOverAllG = G;
							}
						}
					}

					averageRawFitness = sumRawFitness / Parameters.popSize;
					
					genStatsBuilder.append(XR + "," + R + "," + G + "," + ThreeSAT.maxClauseSatisfied + "," + averageRawFitness + "\n");

					// Output generation statistics to screen
					// System.out.println(R + "\t" + G +  "\t" + (int)bestOfGenChromo.rawFitness + "\t" + averageRawFitness + "\t" + stdevRawFitness);

				// Output generation statistics to summary file
				summaryOutput.write(" R ");
				Hwrite.right(R, 3, summaryOutput);
				summaryOutput.write(" G ");
				Hwrite.right(G, 3, summaryOutput);
				Hwrite.right((int)bestOfGenChromo.rawFitness, 7, summaryOutput);
				Hwrite.right(ThreeSAT.maxClauseSatisfied, 7, summaryOutput);
				Hwrite.right(averageRawFitness, 11, 3, summaryOutput);
				Hwrite.right(stdevRawFitness, 11, 3, summaryOutput);
				summaryOutput.write("\n");


			// *********************************************************************
			// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
			// *********************************************************************

					switch(Parameters.scaleType){

					case 0:     // No change to raw fitness
						for (int i=0; i<Parameters.popSize; i++){
							member[i].sclFitness = member[i].rawFitness + .000001;
							sumSclFitness += member[i].sclFitness;
						}
						break;

					case 1:     // Fitness not scaled.  Only inverted.
						for (int i=0; i<Parameters.popSize; i++){
							member[i].sclFitness = 1/(member[i].rawFitness + .000001);
							sumSclFitness += member[i].sclFitness;
						}
						break;

					case 2:     // Fitness scaled by Rank (Maximizing fitness)

						//  Copy genetic data to temp array
						for (int i=0; i<Parameters.popSize; i++){
							memberIndex[i] = i;
							memberFitness[i] = member[i].rawFitness;
						}
						//  Bubble Sort the array by floating point number
						for (int i=Parameters.popSize-1; i>0; i--){
							for (int j=0; j<i; j++){
								if (memberFitness[j] > memberFitness[j+1]){
									TmemberIndex = memberIndex[j];
									TmemberFitness = memberFitness[j];
									memberIndex[j] = memberIndex[j+1];
									memberFitness[j] = memberFitness[j+1];
									memberIndex[j+1] = TmemberIndex;
									memberFitness[j+1] = TmemberFitness;
								}
							}
						}
						//  Copy ordered array to scale fitness fields
						for (int i=0; i<Parameters.popSize; i++){
							member[memberIndex[i]].sclFitness = i;
							sumSclFitness += member[memberIndex[i]].sclFitness;
						}

						break;

					case 3:     // Fitness scaled by Rank (minimizing fitness)

						//  Copy genetic data to temp array
						for (int i=0; i<Parameters.popSize; i++){
							memberIndex[i] = i;
							memberFitness[i] = member[i].rawFitness;
						}
						//  Bubble Sort the array by floating point number
						for (int i=1; i<Parameters.popSize; i++){
							for (int j=(Parameters.popSize - 1); j>=i; j--){
								if (memberFitness[j-i] < memberFitness[j]){
									TmemberIndex = memberIndex[j-1];
									TmemberFitness = memberFitness[j-1];
									memberIndex[j-1] = memberIndex[j];
									memberFitness[j-1] = memberFitness[j];
									memberIndex[j] = TmemberIndex;
									memberFitness[j] = TmemberFitness;
								}
							}
						}
						//  Copy array order to scale fitness fields
						for (int i=0; i<Parameters.popSize; i++){
							member[memberIndex[i]].sclFitness = i;
							sumSclFitness += member[memberIndex[i]].sclFitness;
						}

						break;

					default:
						System.out.println("ERROR - No scaling method selected");
					}


			// *********************************************************************
			// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
			// *********************************************************************

					for (int i=0; i<Parameters.popSize; i++){
						member[i].proFitness = member[i].sclFitness/sumSclFitness;
						sumProFitness = sumProFitness + member[i].proFitness;
					}

			// *********************************************************************
			// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
			// *********************************************************************

					int parent1 = -1;
					int parent2 = -1;

					//  Assumes always two offspring per mating
					for (int i=0; i<Parameters.popSize; i=i+2){

						//	Select Two Parents
						parent1 = Chromo.selectParent();
						parent2 = parent1;
						while (parent2 == parent1){
							parent2 = Chromo.selectParent();
						}

						//	Crossover Two Parents to Create Two Children
						randnum = r.nextDouble();
						if (randnum < Parameters.xoverRate){
							Chromo.mateParents(parent1, parent2, member[parent1], member[parent2], child[i], child[i+1]);
						}
						else {
							Chromo.mateParents(parent1, member[parent1], child[i]);
							Chromo.mateParents(parent2, member[parent2], child[i+1]);
						}
					} // End Crossover

					//	Mutate Children
					for (int i=0; i<Parameters.popSize; i++){
						child[i].doMutation();
					}

					//	Swap Children with Last Generation
					for (int i=0; i<Parameters.popSize; i++){
						Chromo.copyB2A(member[i], child[i]);
					}
					ThreeSAT.fitnessCalculated = false;




					if (ThreeSAT.maxClauseSatisfied == Parameters.numClauses) {
						successCount++;
						System.out.println("\n------------------------\nFound Optimal - Early Stop");
						System.out.println(R + "\t" + G +  "\t" + bestOfGenChromo.rawFitness + "\t" + averageRawFitness);
		
						System.out.println("Best Gen Assignment:  " + bestOfGenChromo.chromo + "\n");
						System.out.println("------------------------\n");
						break;
					}

				} //  Repeat the above loop for each generation

				Hwrite.left(bestOfRunR, 4, summaryOutput);
				Hwrite.right(bestOfRunG, 4, summaryOutput);

				summaryOutput.write("\n");
				problem.doPrintGenes(bestOfRunChromo, summaryOutput);

				System.out.println(R + "\t" + "B" + "\t"+ (int)bestOfRunChromo.rawFitness);

			} //End of a Run
			System.out.println("XRun: " + XR + " Success Rate: " + (float)successCount / Parameters.numRuns + "\n");
			System.out.println("--------------------------------------------------------------------------------------");
		} //End of a XRun

		Hwrite.left("B", 8, summaryOutput);
		summaryOutput.write("\n");
		summaryOutput.write("\nClauses: ");
		Hwrite.left(Parameters.numClauses, 10, summaryOutput);
		summaryOutput.write("\n");

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		if (Parameters.numClauses != bestOverAllChromo.rawFitness)
			summaryOutput.write("NOT ");
		summaryOutput.write("SATISFIED!\n");


		summaryOutput.write("\n");
		summaryOutput.close();


		// Output Stats to CSV File
		genStatsOutput.write(genStatsBuilder.toString());
		genStatsOutput.close();



		System.out.println();
		System.out.println("\nStart:  " + startTime);
		dateAndTime = Calendar.getInstance(); 
		Date endTime = dateAndTime.getTime();
		System.out.println("End  :  " + endTime);

	} // End of Main Class

}   // End of Search.Java ******************************************************

