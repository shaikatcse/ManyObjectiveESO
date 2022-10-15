package jmetal.metaheuristics.nsgaiii;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.metaheuristics.nsgaiii.util.EnvironmentalSelection;
import jmetal.metaheuristics.nsgaiii.util.ReferencePoint;
import jmetal.qualityIndicator.QualityIndicator;;

public class NSGAIII extends Algorithm {
	protected int populationSize;
	protected int maxEvaluations;
	protected int evaluations;

	protected QualityIndicator indicators; // QualityIndicator object

	// operators
	protected Operator mutationOperator;
	protected Operator crossoverOperator;
	protected Operator selectionOperator;

	protected int numberOfDivisions;
	protected List<ReferencePoint> referencePoints = new Vector<>();

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public NSGAIII(Problem problem, int numberOfDivisions) {
		super(problem);

		

		(new ReferencePoint()).generateReferencePoints(referencePoints, getProblem().getNumberOfObjectives(),
				numberOfDivisions);

		int populationSize = referencePoints.size();
		while (populationSize % 4 > 0) {
			populationSize++;
		}

		System.out.println("rpssize: " + referencePoints.size());
		

	} // NSGAII constructor end

	protected void initProgress() {
		evaluations = 1;
	}

	protected void updateProgress() {
		
		evaluations++;
		System.out.println(evaluations);
	}

	protected boolean isStoppingConditionReached() {
		return evaluations >= maxEvaluations;
	}

	protected List<Solution> evaluatePopulation(List<Solution> population) throws JMException {

		SolutionSet solutionset = convertListToSolutionSet(population);
		
		
		problem_.evaluateAll(solutionset);
		

		return convertSolutionsetToList(solutionset);
	}

	protected List<Solution> selection(List<Solution> population) throws JMException {
		List<Solution> matingPopulation = new ArrayList<>(population.size());
		for (int i = 0; i < populationSize; i++) {
			Solution solution = (Solution) selectionOperator.execute(convertListToSolutionSet(population));
			matingPopulation.add(solution);
		}

		return matingPopulation;
	}

	protected List<Solution> reproduction(List<Solution> population) throws JMException {
		List<Solution> offspringPopulation = new ArrayList<>(populationSize);
		for (int i = 0; i < populationSize; i += 2) {
			Solution[] parents = new Solution[2];
			parents[0] = (Solution) population.get(i);
			parents[1] = (Solution) population.get(Math.min(i + 1, populationSize - 1));
			Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);

			// List<Solution> parents = new ArrayList<>(2);
			// parents.add(population.get(i));
			// parents.add(population.get(Math.min(i + 1, populationSize-1)));

			// List<Solution> offspring = crossoverOperator.execute(parents);

			mutationOperator.execute(offSpring[0]);
			mutationOperator.execute(offSpring[1]);

			offspringPopulation.add(offSpring[0]);
			offspringPopulation.add(offSpring[1]);
		}
		return offspringPopulation;
	}

	protected List<ReferencePoint> getReferencePointsCopy() {
		List<ReferencePoint> copy = new ArrayList<>();
		for (ReferencePoint r : this.referencePoints) {
			copy.add(new ReferencePoint(r));
		}
		return copy;
	}

	SolutionSet convertListToSolutionSet(List<Solution> population) {
		SolutionSet populationSet = new SolutionSet(population.size());
		for (Solution s : population) {
			populationSet.add(s);
		}
		return populationSet;
	}

	List<Solution> convertSolutionsetToList(SolutionSet s) {
		List<Solution> convertedList = new ArrayList<Solution>(s.size());
		for (int i = 0; i < s.size(); i++) {
			convertedList.add(s.get(i));
		}
		return convertedList;

	}

	protected List<Solution> replacement(List<Solution> population, List<Solution> offspringPopulation)
			throws JMException {

		/*
		 * List<Solution> jointPopulation = new ArrayList<>();
		 * jointPopulation.addAll(population) ;
		 * jointPopulation.addAll(offsprtingPopulation) ;
		 */

		SolutionSet populationSet = convertListToSolutionSet(population);
		SolutionSet offspringPopulationSet = convertListToSolutionSet(offspringPopulation);

		SolutionSet union = ((SolutionSet) populationSet).union(offspringPopulationSet);

		// Ranking<Solution> ranking = computeRanking(jointPopulation);

		Ranking ranking = new Ranking(union);

		// List<Solution> pop = crowdingDistanceSelection(ranking);
		List<Solution> last = new ArrayList<>();
		List<Solution> pop = new ArrayList<>();
		List<List<Solution>> fronts = new ArrayList<>();
		int rankingIndex = 0;
		int candidateSolutions = 0;
		while (candidateSolutions < populationSize) {
			last = convertSolutionsetToList(ranking.getSubfront(rankingIndex));
			fronts.add(last);
			candidateSolutions += last.size();
			if ((pop.size() + last.size()) <= populationSize)
				pop.addAll(last);
			rankingIndex++;
		}

		if (pop.size() == this.populationSize)
			return pop;

		// A copy of the reference list should be used as parameter of the environmental
		// selection
		EnvironmentalSelection selection = new EnvironmentalSelection(fronts, populationSize - pop.size(),
				getReferencePointsCopy(), getProblem().getNumberOfObjectives());

		var choosen = selection.execute(last);
		pop.addAll(choosen);

		return pop;
	}

	protected List<Solution> createInitialPopulation() throws ClassNotFoundException, JMException {
		SolutionSet population = new SolutionSet(populationSize);
		List<Solution> populationList = new ArrayList<>(populationSize);
		Solution newSolution;
		for (int i = 0; i < populationSize; i++) {
			newSolution = new Solution(problem_);
			problem_.evaluate(newSolution);
			problem_.evaluateConstraints(newSolution);
			evaluations++;
			population.add(newSolution);
		}
		populationList = convertSolutionsetToList(population);
		return populationList;
	}

	@Override
	public SolutionSet execute() throws JMException, ClassNotFoundException {
		// Read the parameters
		populationSize = ((Integer) getInputParameter("populationSize")).intValue();
	    		
		maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
				indicators = (QualityIndicator) getInputParameter("indicators");

				mutationOperator = operators_.get("mutation");
				crossoverOperator = operators_.get("crossover");
				selectionOperator = operators_.get("selection");
		
		List<Solution> population = new ArrayList<>(populationSize); 
		List<Solution> offspringPopulation;
		    List<Solution> matingPopulation;

		    population = createInitialPopulation();
		    population = evaluatePopulation(population);
		    initProgress();
		    while (!isStoppingConditionReached()) {
		      matingPopulation = selection(population);
		      offspringPopulation = reproduction(matingPopulation);
		      offspringPopulation = evaluatePopulation(offspringPopulation);
		      population = replacement(population, offspringPopulation);
		      updateProgress();
	}
		    return convertListToSolutionSet(population);
	}

	  
	 
	  

}
