package jmetal.metaheuristics.MOEADD.util.distance.impl;

import java.util.List;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.MOEADD.util.distance.Distance;

/**
 * Class for calculating the Euclidean distance between a {@link Solution} object a list of {@link Solution}
 * objects in objective space.
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace
       
        implements Distance {

  private EuclideanDistanceBetweenVectors distance ;

  public EuclideanDistanceBetweenSolutionAndASolutionListInObjectiveSpace() {
    distance = new EuclideanDistanceBetweenVectors() ;
  }

  @Override
  public double compute( Object  solution, Object solutionSet) {
    double bestDistance = Double.MAX_VALUE;

    Solution s= ((Solution) solution);
    SolutionSet conSolutionSet = ((SolutionSet) solutionSet);
    
    
    
    for (int i=0; i<  conSolutionSet.size();i++) {
    	Solution tmpSolution;
    	tmpSolution = conSolutionSet.get(i);
      double aux = distance.compute(s.getObjectives(), tmpSolution.getObjectives());
      if (aux < bestDistance)
        bestDistance = aux;
    }

    return bestDistance;
  }
}
