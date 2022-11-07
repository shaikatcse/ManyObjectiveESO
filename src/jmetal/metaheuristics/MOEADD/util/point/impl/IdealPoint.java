package jmetal.metaheuristics.MOEADD.util.point.impl;

import java.util.Arrays;
import java.util.List;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.metaheuristics.MOEADD.util.errorchecking.Check;

/**d
 * Class representing an ideal point (minimization is assumed)
 *
 * @author Antonio J.Nebro <antonio@lcc.uma.es>
 */
public class IdealPoint extends ArrayPoint {

  public IdealPoint(int dimension) {
    super(dimension) ;
    Arrays.fill(point, Double.POSITIVE_INFINITY);
  }

  @Override
  public void update(double[] point) {
    Check.that(point.length == this.point.length, "The point to be update have a dimension of " + point.length + " "
            + "while the parameter point has a dimension of " + point.length);

    for (int i = 0; i < point.length; i++) {
      if (this.point[i] > point[i]) {
        this.point[i] = point[i];
      }
    }
  }

  public void update(SolutionSet solutionSet) {
	  int numberOfObjectives = solutionSet.get(0).getNumberOfObjectives();
	  for(int i=0;i<solutionSet.size();i++) {
		  double objectives[] = new double[solutionSet.get(0).getNumberOfObjectives()]; 
		  Solution s = solutionSet.get(i);
		  for(int j=0; j<numberOfObjectives; j++) {
			  objectives[j] = s.getObjective(j);
		  }
		  update(objectives);
	  }
		     
  }
}
