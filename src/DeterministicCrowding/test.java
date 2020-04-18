import java.io.*;
import java.util.*;

public class test {
    
		
        
    public static void main(String[] args) throws java.io.IOException {

		int nbvar = 0, nbclauses = 0;
		
		int[] cnf = null;
		String dataInputFileName = "uf20-01.cnf";
		try (BufferedReader br = new BufferedReader(new FileReader(dataInputFileName))) {
			String line;
			int clauseIndex = 0;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.length() == 0) {
					continue;
				}
				switch (line.charAt(0)) {
					case 'p':
						String[] token = line.split("[\\s]+");
						nbvar = Integer.parseInt(token[2]);
						nbclauses = Integer.parseInt(token[3]);
						cnf = new int[3 * nbclauses];
						break;
					case 'c':
					case '%':
					case '0':
						break;
				
					default:
						String[] vars = line.split("[\\s]+");
						cnf[clauseIndex++] = Integer.parseInt(vars[0]);
						cnf[clauseIndex++] = Integer.parseInt(vars[1]);
						cnf[clauseIndex++] = Integer.parseInt(vars[2]);
						break;
				}
			}
		}
		SAT satProblem = new SAT();
        
		System.out.printf("nbvar: %d, nbclauses: %d\n", nbvar, nbclauses);
		// for (int i = 0; i < satProblem.getCNF().length; i++) {
		// 	System.out.println(satProblem.getCNF()[i]);
		// }

		Random r = new Random();
		ArrayList<Integer> assignment = new ArrayList<Integer>(20);
		for (int i = 0; i < 20; i++) {
			assignment.add(r.nextInt(2));
			System.out.printf("%d, ", assignment.get(i));
		}
		System.out.println();
		System.out.println(satProblem.getSatisfiedClausesCount(cnf, assignment));
        
    }
}
