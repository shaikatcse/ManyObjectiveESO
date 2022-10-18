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
import jmetal.metaheuristics.nsgaiii.util.EnvironmentalSelectionV1_2;
import jmetal.metaheuristics.nsgaiii.util.ReferencePoint;
import jmetal.qualityIndicator.QualityIndicator;;

public class NSGAIIIV1_2 extends NSGAIII {
	

	/**
	 * Constructor
	 * 
	 * @param problem
	 *            Problem to solve
	 */
	public NSGAIIIV1_2(Problem problem) {
		super(problem);

		
		

	} // NSGAII constructor end
	
	@Override
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
		EnvironmentalSelectionV1_2 selection = new EnvironmentalSelectionV1_2(fronts, populationSize - pop.size(),
				getReferencePointsCopy(), getProblem().getNumberOfObjectives());

		var choosen = selection.execute(last);
		pop.addAll(choosen);

		return pop;
	}


	  

}
