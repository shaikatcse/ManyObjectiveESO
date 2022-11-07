package jmetal.metaheuristics.MOEADD.util.distance.impl;

import jmetal.metaheuristics.MOEADD.util.distance.Distance;
import jmetal.metaheuristics.MOEADD.util.errorchecking.Check;

/**
 * Class for calculating the Euclidean distance between two vectors
 *
 * @author <antonio@lcc.uma.es>
 */
public class EuclideanDistanceBetweenVectors implements Distance {

  @Override
  public double compute(Object v1, Object v2) {
	  double [] vector1 = (double []) v1;
	  double [] vector2 = (double []) v2;
	  
    Check.notNull(vector1);
    Check.notNull(vector2);
    Check.that(vector1.length == vector2.length, "The vectors have different" +
            "dimension: " + vector1.length + " and " + vector2.length);

    double distance = 0.0;

    double diff;
    for (int i = 0; i < vector1.length ; i++){
      diff = vector1[i] - vector2[i];
      distance += diff * diff ;
    }

    return Math.sqrt(distance);
  }
}
