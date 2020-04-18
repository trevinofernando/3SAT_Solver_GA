/******************************************************************************
*  A Teaching GA					  Developed by Hal Stringer & Annie Wu, UCF
*  Version 2, January 18, 2004
*******************************************************************************/

import java.io.*;
import java.util.*;

public class Search {

/*******************************************************************************
*                           INSTANCE VARIABLES                                 *
*******************************************************************************/

/*******************************************************************************
*                           STATIC VARIABLES                                   *
*******************************************************************************/

	public static FitnessFunction problem;

	public static ArrayList<Chromo> member;
	public static ArrayList<Chromo> newMember;

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
	public static double sumSclFitness;
	public static double sumProFitness;
	public static double defaultBest;
	public static double defaultWorst;

	public static double averageRawFitness;

	public static int G;
	public static int R;
	public static Random r = new Random();
	private static double randnum;

	private static int memberIndex[];
	private static double memberFitness[];
	private static int TmemberIndex;
	private static double TmemberFitness;

/*******************************************************************************
*                              CONSTRUCTORS                                    *
*******************************************************************************/


/*******************************************************************************
*                             MEMBER METHODS                                   *
*******************************************************************************/


/*******************************************************************************
*                             STATIC METHODS                                   *
*******************************************************************************/

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

		//  Write Stats To Output CSV File (RunIndex, GenIndex, BestFitness, AvgFitness)
		String genStatsFileName = Parameters.expID + "_gen_stats.csv";
		FileWriter genStatsOutput = new FileWriter(genStatsFileName);
		genStatsOutput.write("R,G,bestF,avgF\n");
		StringBuilder genStatsBuilder = new StringBuilder();

	//	Problem Specific Setup - For new new fitness function problems, create
	//	the appropriate class file (extending FitnessFunction.java) and add
	//	an else_if block below to instantiate the problem.
 
		if (Parameters.problemType.equals("SAT")){
			problem = new SAT();
		}
		else System.out.println("Invalid Problem Type");

		System.out.println(problem.name);

	//	Initialize RNG, array sizes and other objects
		r.setSeed(Parameters.seed);
		memberIndex = new int[Parameters.popSize];
		memberFitness = new double[Parameters.popSize];
		member = new ArrayList<Chromo>(Parameters.popSize);
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

		//  Start program for multiple runs
		for (R = 1; R <= Parameters.numRuns; R++){

			bestOfRunChromo.rawFitness = defaultBest;
			System.out.println();

			//	Initialize First Generation
			for (int i=0; i<Parameters.popSize; i++){
				member.add(new Chromo());
			}

			//	Begin Each Run
			for (G=0; G<Parameters.generations; G++){

				newMember = new ArrayList<Chromo>(Parameters.popSize);

				sumProFitness = 0;
				sumSclFitness = 0;
				sumRawFitness = 0;
				bestOfGenChromo.rawFitness = defaultBest;

				//	Test Fitness of Each Member
				for (int i=0; i<Parameters.popSize; i++){

					member.get(i).rawFitness = 0;
					member.get(i).sclFitness = 0;
					member.get(i).proFitness = 0;

					problem.doRawFitness(member.get(i));

					sumRawFitness = sumRawFitness + member.get(i).rawFitness;
					
					if (Parameters.minORmax.equals("max")){
						if (member.get(i).rawFitness > bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member.get(i));
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member.get(i).rawFitness > bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member.get(i));
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member.get(i).rawFitness > bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member.get(i));
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
					else {
						if (member.get(i).rawFitness < bestOfGenChromo.rawFitness){
							Chromo.copyB2A(bestOfGenChromo, member.get(i));
							bestOfGenR = R;
							bestOfGenG = G;
						}
						if (member.get(i).rawFitness < bestOfRunChromo.rawFitness){
							Chromo.copyB2A(bestOfRunChromo, member.get(i));
							bestOfRunR = R;
							bestOfRunG = G;
						}
						if (member.get(i).rawFitness < bestOverAllChromo.rawFitness){
							Chromo.copyB2A(bestOverAllChromo, member.get(i));
							bestOverAllR = R;
							bestOverAllG = G;
						}
					}
				}

				averageRawFitness = sumRawFitness / Parameters.popSize;
				
				genStatsBuilder.append(R + "," + G + "," + bestOfGenChromo.rawFitness + "," + averageRawFitness + "\n");



				// Output generation statistics to screen
				// System.out.println(R + "\t" + G +  "\t" + bestOfGenChromo.rawFitness + "\t" + averageRawFitness);


		// *********************************************************************
		// **************** SCALE FITNESS OF EACH MEMBER AND SUM ***************
		// *********************************************************************

				switch(Parameters.scaleType){

				case 0:     // No change to raw fitness
					for (int i=0; i<Parameters.popSize; i++){
						member.get(i).sclFitness = member.get(i).rawFitness + .000001;
						sumSclFitness += member.get(i).sclFitness;
					}
					break;

				case 1:     // Fitness not scaled.  Only inverted.
					for (int i=0; i<Parameters.popSize; i++){
						member.get(i).sclFitness = 1/(member.get(i).rawFitness + .000001);
						sumSclFitness += member.get(i).sclFitness;
					}
					break;

				case 2:     // Fitness scaled by Rank (Maximizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member.get(i).rawFitness;
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
						member.get(memberIndex[i]).sclFitness = i;
						sumSclFitness += member.get(memberIndex[i]).sclFitness;
					}

					break;

				case 3:     // Fitness scaled by Rank (minimizing fitness)

					//  Copy genetic data to temp array
					for (int i=0; i<Parameters.popSize; i++){
						memberIndex[i] = i;
						memberFitness[i] = member.get(i).rawFitness;
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
						member.get(memberIndex[i]).sclFitness = i;
						sumSclFitness += member.get(memberIndex[i]).sclFitness;
					}

					break;

				case 4: // Sutract for Minimization
					for (int i = 0; i < Parameters.popSize; i++) {
						member.get(i).sclFitness = Parameters.nbclauses - member.get(i).rawFitness;
						sumSclFitness += member.get(i).sclFitness;
					}
					break;

				default:
					System.out.println("ERROR - No scaling method selected");
				}


		// *********************************************************************
		// ****** PROPORTIONALIZE SCALED FITNESS FOR EACH MEMBER AND SUM *******
		// *********************************************************************

				for (int i=0; i<Parameters.popSize; i++){
					member.get(i).proFitness = member.get(i).sclFitness/sumSclFitness;
					sumProFitness = sumProFitness + member.get(i).proFitness;
				}

		// *********************************************************************
		// ************ CROSSOVER AND CREATE NEXT GENERATION *******************
		// *********************************************************************

				int parent1_index = -1;
				int parent2_index = -1;
				Chromo parent1;
				Chromo parent2;


				//  Assumes always two offspring per mating
				for (int i=0; i<Parameters.popSize / 2; i++){

					//	Select Two Parents
					parent1_index = r.nextInt(member.size());
					parent1 = member.get(parent1_index);
					member.remove(parent1_index);
					
					parent2_index = r.nextInt(member.size());
					parent2 = member.get(parent2_index);
					member.remove(parent2_index);

					Chromo child1 = new Chromo();
					Chromo child2 = new Chromo();

					//	Crossover Two Parents to Create Two Children
					Chromo.mateParents(parent1, parent2, child1, child2);
					child1.doMutation();
					child2.doMutation();
					problem.doRawFitness(child1);
					problem.doRawFitness(child2);

					if ( (parent1.distanceTo(child1) + parent2.distanceTo(child2)) <= (parent1.distanceTo(child2) + parent2.distanceTo(child1)) ) {
						if (child1.rawFitness < parent1.rawFitness) {
							newMember.add(child1);
						} else {
							newMember.add(parent1);
						}
						if (child2.rawFitness < parent2.rawFitness) {
							newMember.add(child2);
						} else {
							newMember.add(parent2);
						}
					} else {
						if (child2.rawFitness < parent1.rawFitness) {
							newMember.add(child2);
						} else {
							newMember.add(parent1);
						}
						if (child1.rawFitness < parent2.rawFitness) {
							newMember.add(child1);
						} else {
							newMember.add(parent2);
						}
					}

				} // End Crossover


				//	Swap new gen with Last Generation
				member = newMember;


				// catasterophe
				if (G % 100 == 0) {
					// member.clear();
					for (int i=0; i<Parameters.popSize; i++){
						if (r.nextDouble() < 0.9) {
							member.set(i, new Chromo());							
						}
					}
					member.set(0, bestOfGenChromo);
				}
				

				if (bestOfGenChromo.rawFitness == 0) {
					System.out.println("\n------------------------\nFound Optimal - Early Stop");
					System.out.println(R + "\t" + G +  "\t" + bestOfGenChromo.rawFitness + "\t" + averageRawFitness);
	
					System.out.println("Best Gen Assignment:  " + bestOfGenChromo.chromo + "\n");
					System.out.println("------------------------\n");
					break;
				}

			} //  Repeat the above loop for each generation

			Hwrite.left(bestOfRunR, 4, summaryOutput);
			Hwrite.right(bestOfRunG, 4, summaryOutput);

			problem.doPrintGenes(bestOfRunChromo, summaryOutput);

			System.out.println(R + "\t" + "B" + "\t"+ bestOfRunChromo.rawFitness);
			System.out.println("Best Run Assignment:  " + bestOfRunChromo.chromo + "\n");

		} //End of a Run

		Hwrite.left("B", 8, summaryOutput);

		Hwrite.left("R: " + bestOverAllR, 8, summaryOutput);
		Hwrite.left("G: " + bestOverAllG, 8, summaryOutput);

		Hwrite.left("\n", 8, summaryOutput);

		problem.doPrintGenes(bestOverAllChromo, summaryOutput);

		System.out.println("\nNumber of Clauses:  " + Parameters.nbclauses);


		System.out.println("\nBest Overall Fitness:  " + bestOverAllChromo.rawFitness);
		System.out.println("Best Overall Number of Satisfied Clauses:  " + ((SAT)problem).getSatisfiedClausesCount(Parameters.CNF, bestOverAllChromo.chromo) );
		System.out.println("Best Overal Assignment:  " + bestOverAllChromo.chromo);

		//	Output Fitness Statistics matrix for last run
		/* summaryOutput.write("Gen                 AvgFit              BestFit \n");
		for (int i=0; i<Parameters.generations; i++){
			Hwrite.left(i, 15, summaryOutput);
			Hwrite.left(fitnessStats[0][i]/Parameters.numRuns, 20, 2, summaryOutput);
			Hwrite.left(fitnessStats[1][i]/Parameters.numRuns, 20, 2, summaryOutput);
			summaryOutput.write("\n");
		} */

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

