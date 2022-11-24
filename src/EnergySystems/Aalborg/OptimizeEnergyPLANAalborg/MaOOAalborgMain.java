package EnergySystems.Aalborg.OptimizeEnergyPLANAalborg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.logging.FileHandler;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.AlgorithmFactory;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.selection.SelectionFactory;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Configuration;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.RandomGenerator;
import jmetal.operators.mutation.MutationFactory;

import EnergySystems.Aalborg.OptimizeEnergyPLANAalborg.problem.*;
import EnergySystems.GiudicarieEsteriori.Problem.MaOOCEIS4D;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class MaOOAalborgMain {

	public static Logger logger_; // Logger object
	public static FileHandler fileHandler_; // FileHandler object

	public static void main(String[] args) throws JMException,
			SecurityException, IOException, ClassNotFoundException {

		logger_ = Configuration.logger_;
		fileHandler_ = new FileHandler("NSGAII_main.log");
		// fileHandler_ = new FileHandler("SPEA2.log");
		logger_.addHandler(fileHandler_);

		Problem problem; // The problem to solve
		Algorithm algorithm; // The algorithm to use
		Operator crossover; // Crossover operator
		Operator mutation; // Mutation operator
		Operator selection; // Selection operator

		HashMap parameters; // Operator parameters

		QualityIndicator indicators; // Object to get quality indicators

		String[] algorithms = {"NSGAII"/*, "SPEA2", "MOEAD"*/};
		String baseDirectory = ".\\Results\\MaOOAalborg4D\\Constrained\\";
		File baseDirectoryfile = new File(baseDirectory);
		baseDirectoryfile.mkdirs();
		
		File track = new File(baseDirectory + "\\track.txt");
		BufferedWriter trackBW = new BufferedWriter(new FileWriter(track.getAbsoluteFile()));
		track.createNewFile();

		
		for (int algorithmNo = 0; algorithmNo < algorithms.length; algorithmNo++) {
			int numberOfRun = 2;
			
			trackBW.write(algorithms[algorithmNo]+"\n");
			
			
			String folder = baseDirectory + algorithms[algorithmNo];
			File file = new File(folder);
			boolean b = file.mkdirs();

			for (int i = 0; i < numberOfRun; i++) {

				trackBW.write(i+"\t");
				
				// PseudoRandom.setRandomGenerator(new
				// RandomGenerator(seed[i]));

				indicators = null;

				// problems for 2 objectives
				problem = new MaOOAalborg4D("Real");
				
				parameters = new HashMap();
				parameters.put("Problem", problem); 

				algorithm = AlgorithmFactory.getAlgorithm(algorithms[algorithmNo], parameters); 
				//algorithm.setInputParameter("folder", folder);

				/*// Algorithm parameters
				int populationSize = 5;
				int maxEvaluations = 10;
				algorithm.setInputParameter("populationSize", populationSize);
				algorithm.setInputParameter("maxEvaluations", maxEvaluations);

				// for spea2
				// algorithm.setInputParameter("archiveSize",150);

				// Mutation and Crossover for Real codification
				parameters = new HashMap();
				parameters.put("probability", 0.9);
				parameters.put("distributionIndex", 10.0);
				crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

				parameters = new HashMap();
				parameters.put("probability", 1.0 / problem.getNumberOfVariables());
				parameters.put("distributionIndex", 10.0);

				mutation = new PolynomialMutation(parameters);

				// Selection Operator
				parameters = null;
				selection = SelectionFactory.getSelectionOperator("BinaryTournament", parameters);

				// Add the operators to the algorithm
				algorithm.addOperator("crossover", crossover);
				algorithm.addOperator("mutation", mutation);
				algorithm.addOperator("selection", selection);

				// Add the indicator object to the algorithm
				algorithm.setInputParameter("indicators", indicators);*/

				// Execute the Algorithm
				long initTime = System.currentTimeMillis();
				SolutionSet population = algorithm.execute();

				long estimatedTime = System.currentTimeMillis() - initTime;

			/*	ExtractEnergyPLANParametersTrentoProvinceV10 ex = new ExtractEnergyPLANParametersTrentoProvinceV10(
						simulatedYear[year]);
				for (int z = 0; z < population.size(); z++) {
					Solution solution = population.get(z);
					
					if (solution.getOverallConstraintViolation() >= 0) {
						String line = "";
						for (int j = 0; j < solution.numberOfVariables(); j++) {
							line = line + " " + solution.getDecisionVariables()[i];
						}
						MultiMap energyplanmMapForSimulation;
						
						energyplanmMapForSimulation = ex.simulateEnergyPLAN(line);
						try {
							ex.WriteEnergyPLANParametersToFile(energyplanmMapForSimulation, folder);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}*/
				
				
				
				// Result messages
				logger_.info("Total execution time: " + estimatedTime + "ms");
				logger_.info("Variables values have been writen to file VAR");
				population.printFeasibleVAR(folder + "\\VAR"+i );
				// population.printFeasibleVAR("VAR");
				logger_.info("Objectives values have been writen to file FUN");
				population.printFeasibleFUN(folder + "\\FUN"+i );
				// population.printFeasibleFUN("FUN");
				trackBW.write("\n");
			}
		}
	}
}
