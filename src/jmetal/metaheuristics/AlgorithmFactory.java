package jmetal.metaheuristics;

import java.util.HashMap;

import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.metaheuristics.moead.configurations.MOEAD_Configurations;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.configurations.NSGAII_Configurations;
import jmetal.metaheuristics.nsgaiii.NSGAIIIV1_2;
import jmetal.metaheuristics.spea2.SPEA2;
import jmetal.metaheuristics.spea2.configurations.SPEA2_Configurations;
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
		      return new NSGAII_Configurations((Problem) parameters.get("Problem")).configure();
		    else if (name.equalsIgnoreCase("SPEA2"))
		      return new SPEA2_Configurations((Problem) parameters.get("Problem")).configure();
		    else if (name.equalsIgnoreCase("NSGAIII"))
		      return new NSGAIIIV1_2((Problem) parameters.get("Problem"), (int) parameters.get("numberOfDivision") );
		    else if (name.equalsIgnoreCase("MOEAD"))
		      return new MOEAD_Configurations((Problem) parameters.get("Problem")).configure();
		    else
		    {
		      Configuration.logger_.severe("Operator '" + name + "' not found ");
		      Class cls = java.lang.String.class;
		      String name2 = cls.getName() ;    
		      throw new JMException("Exception in " + name2 + ".getMutationOperator()") ;
		    }        
		  }
}
