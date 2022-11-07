package jmetal.metaheuristics.MOEADD.util.errorchecking;

import java.util.Collection;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.EmptyCollectionException;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.InvalidConditionException;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.InvalidProbabilityValueException;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.NegativeValueException;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.NullParameterException;
import jmetal.metaheuristics.MOEADD.util.errorchecking.exception.ValueOutOfRangeException;

/**
 * Static class for error checking
 */
public class Check {
  public static void notNull(Object object) {
    if (null == object) {
      throw new NullParameterException() ;
    }
  }

  public static void probabilityIsValid(double value) {
    if ((value < 0.0) || (value > 1.0)) {
      throw new InvalidProbabilityValueException(value) ;
    }
  }

  public static void valueIsInRange(double value, double lowestValue, double highestValue) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }
  }

  public static void valueIsInRange(int value, int lowestValue, int highestValue) {
    if ((value < lowestValue) || (value > highestValue)) {
      throw new ValueOutOfRangeException(value, lowestValue, highestValue) ;
    }
  }

  public static void collectionIsNotEmpty(Collection<?> collection) {
    if (collection.isEmpty()) {
      throw new EmptyCollectionException() ;
    }
  }

  public static void that(boolean expression, String message) {
    if (!expression) {
        throw new InvalidConditionException(message) ;
    }
  }

  public static void valueIsNotNegative(double value) {
    if (value < 0.0) {
      throw new NegativeValueException(value) ;
    }
  }
}
