package jmetal.metaheuristics;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaiii.NSGAIIIV1_2;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.operators.mutation.BitFlipMutation;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.NonUniformMutation;
import jmetal.operators.mutation.PolynomialMutation;
import jmetal.operators.mutation.SwapMutation;
import jmetal.util.Configuration;
import jmetal.util.JMException;

public class AlgorithmFactory {

	
	 public static Algorithm getAlgorithm(String name, HashMap parameters) throws JMException{
		 
		    if (name.equalsIgnoreCase("NSGAII"))
		      return new NSGAII((Problem) parameters.get("Problem"));
		    else if (name.equalsIgnoreCase("SPEA2"))
		      return new SPEA2((Problem) parameters.get("Problem"));
		    else if (name.equalsIgnoreCase("NSGAIII"))
		      return new NSGAIIIV1_2((Problem) parameters.get("Problem"), (int) parameters.get("numberOfDivision") );
		    /*else if (name.equalsIgnoreCase("SwapMutation"))
		      return new SwapMutation(parameters);*/
		    else
		    {
		      Configuration.logger_.severe("Operator '" + name + "' not found ");
		      Class cls = java.lang.String.class;
		      String name2 = cls.getName() ;    
		      throw new JMException("Exception in " + name2 + ".getMutationOperator()") ;
		    }        
		  }
}
