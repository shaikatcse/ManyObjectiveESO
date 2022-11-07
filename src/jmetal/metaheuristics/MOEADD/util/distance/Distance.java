package jmetal.metaheuristics.MOEADD.util.distance;

/**
 * Interface representing distances between two entities
 *
 * @author <antonio@lcc.uma.es>
 */
@FunctionalInterface
public interface Distance {
  double compute(Object element1, Object element2) ;
}
