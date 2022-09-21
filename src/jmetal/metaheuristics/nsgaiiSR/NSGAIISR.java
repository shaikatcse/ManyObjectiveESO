//  NSGAII.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.metaheuristics.nsgaiiSR;

import java.util.HashMap;

import jmetal.core.*;
import jmetal.operators.selection.Tournament;
import jmetal.qualityIndicator.QualityIndicator;
import jmetal.util.Distance;
import jmetal.util.JMException;
import jmetal.util.Ranking;
import jmetal.util.RankingATM;
import jmetal.util.comparators.CrowdingComparator;
import jmetal.util.comparators.DominanceComparator2;
import jmetal.util.comparators.OverallConstraintViolationComparator;

/** 
 *  Implementation of NSGA-II.
 *  This implementation of NSGA-II makes use of a QualityIndicator object
 *  to obtained the convergence speed of the algorithm. This version is used
 *  in the paper:
 *     A.J. Nebro, J.J. Durillo, C.A. Coello Coello, F. Luna, E. Alba 
 *     "A Study of Convergence Speed in Multi-Objective Metaheuristics." 
 *     To be presented in: PPSN'08. Dortmund. September 2008.
 */

public class NSGAIISR extends Algorithm {
  /**
   * Constructor
   * @param problem Problem to solve
   */
	
	OverallConstraintViolationComparator constraintsViolatonComparator;
  public NSGAIISR(Problem problem) {
    super (problem) ;
    constraintsViolatonComparator = new OverallConstraintViolationComparator();
  } // NSGAII

  /**   
   * Runs the NSGA-II algorithm.
   * @return a <code>SolutionSet</code> that is a set of non dominated solutions
   * as a result of the algorithm execution
   * @throws JMException 
   */
  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int populationSize;
    int maxEvaluations;
    int evaluations;

    QualityIndicator indicators; // QualityIndicator object
    int requiredEvaluations; // Use in the example of use of the
    // indicators object (see below)

    SolutionSet population;
    SolutionSet offspringPopulation, archive;
    SolutionSet union;

    Operator mutationOperator;
    Operator crossoverOperator;
    Operator selectionOperator;

    Distance distance = new Distance();

    //Read the parameters
    populationSize = ((Integer) getInputParameter("populationSize")).intValue();
    maxEvaluations = ((Integer) getInputParameter("maxEvaluations")).intValue();
    indicators = (QualityIndicator) getInputParameter("indicators");

    //Initialize the variables
    population = new SolutionSet(populationSize);
    evaluations = 0;

    requiredEvaluations = 0;

    //Read the operators
    mutationOperator = operators_.get("mutation");
    crossoverOperator = operators_.get("crossover");
    selectionOperator = operators_.get("selection");

    // Create the initial solutionSet
    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      problem_.evaluate(newSolution);
      problem_.evaluateConstraints(newSolution);
      evaluations++;
      population.add(newSolution);
    
      
    } //for       
    
    
    
    while( !(populationSize - calculateNumberOfIndiConstViolated(population)>0)) {
    	int t = populationSize - calculateNumberOfIndiConstViolated(population);
    	//give fitness to all individual on their sum of constraint violation
    	//done in evaluate constraints
    	 // rank the individuals based on fitness in 1
        
        Operator selectionOperator1;
        HashMap parameters = new HashMap() ;
        parameters.put("comparator", new DominanceComparator2());
        selectionOperator1 = new Tournament(parameters);
        Solution [] parents = new Solution[2];
        
        parents[0] = (Solution) selectionOperator.execute(population);
        parents[1] = (Solution) selectionOperator.execute(population);
        Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
        mutationOperator.execute(offSpring[0]);
        mutationOperator.execute(offSpring[1]);
        problem_.evaluate(offSpring[0]);
        problem_.evaluateConstraints(offSpring[0]);
        
        problem_.evaluate(offSpring[1]);
        problem_.evaluateConstraints(offSpring[1]);
        
        population.sort(constraintsViolatonComparator);
        population.replace(populationSize-1, offSpring[0]);
        population.replace(populationSize-2, offSpring[1]);
        
        }
        
  
    	
    	
    
    

  
    // Generations 
    while (evaluations < maxEvaluations) {

      // Create the offSpring solutionSet      
      offspringPopulation = new SolutionSet(populationSize);
      Solution[] parents = new Solution[2];
      for (int i = 0; i < (populationSize / 2); i++) {
        if (evaluations < maxEvaluations) {
          //obtain parents
          parents[0] = (Solution) selectionOperator.execute(population);
          parents[1] = (Solution) selectionOperator.execute(population);
          Solution[] offSpring = (Solution[]) crossoverOperator.execute(parents);
          mutationOperator.execute(offSpring[0]);
          mutationOperator.execute(offSpring[1]);
          problem_.evaluate(offSpring[0]);
          problem_.evaluateConstraints(offSpring[0]);
          problem_.evaluate(offSpring[1]);
          problem_.evaluateConstraints(offSpring[1]);
          offspringPopulation.add(offSpring[0]);
          offspringPopulation.add(offSpring[1]);
          evaluations += 2;
        } // if                            
      } // for
      
      

      // Create the solutionSet union of solutionSet and offSpring
      union = ((SolutionSet) population).union(offspringPopulation);

      // set constraint violation for all the solutions
      setOverAllConstraintVIolation(union);
      //set all distance
      setDistances(union);
      //set all penalty
      setPenalties(union);
      
      //assing new modified objective values
      for(int i=0;i<union.size();i++) {
    	  Solution solution = union.get(i);
    	  for(int j=0;j<getProblem().getNumberOfObjectives();j++) {
    		  solution.setObjective(j, calculateModifiedObjectiveValue(solution, j));
    	  }
      }
      
      // Ranking the union
      RankingATM ranking = new RankingATM(union);

      int remain = populationSize;
      int index = 0;
      SolutionSet front = null;
      population.clear();

      // Obtain the next front
      front = ranking.getSubfront(index);

      while ((remain > 0) && (remain >= front.size())) {
        //Assign crowding distance to individuals
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        //Add the individuals of this front
        for (int k = 0; k < front.size(); k++) {
          population.add(front.get(k));
        } // for

        //Decrement remain
        remain = remain - front.size();

        //Obtain the next front
        index++;
        if (remain > 0) {
          front = ranking.getSubfront(index);
        } // if        
      } // while

      // Remain is less than front(index).size, insert only the best one
      if (remain > 0) {  // front contains individuals to insert                        
        distance.crowdingDistanceAssignment(front, problem_.getNumberOfObjectives());
        front.sort(new CrowdingComparator());
        for (int k = 0; k < remain; k++) {
          population.add(front.get(k));
        } // for

        remain = 0;
      } // if                               

      // This piece of code shows how to use the indicator object into the code
      // of NSGA-II. In particular, it finds the number of evaluations required
      // by the algorithm to obtain a Pareto front with a hypervolume higher
      // than the hypervolume of the true Pareto front.
      if ((indicators != null) &&
          (requiredEvaluations == 0)) {
        double HV = indicators.getHypervolume(population);
        if (HV >= (0.98 * indicators.getTrueParetoFrontHypervolume())) {
          requiredEvaluations = evaluations;
        } // if
      } // if
    } // while

  
    //evaluate all the indv again
    for(int i = 0;i<populationSize;i++) {
    	problem_.evaluate(population.get(i));
    	problem_.evaluateConstraints(population.get(i));
    }
    
    
    // Return as output parameter the required evaluations
    setOutputParameter("evaluations", requiredEvaluations);

    // Return the first non-dominated front
    Ranking ranking = new Ranking(population);
    ranking.getSubfront(0).printFeasibleFUN("FUN_NSGAII") ;

    return ranking.getSubfront(0).getFeasibleSolution();
  } // execute


  double calculateMinObjective(SolutionSet population, int objNumber) {
	  double min=Double.MAX_VALUE;
	  for(int i=0;i<population.size();i++) {
		  if(min < population.get(i).getObjective(objNumber)) {
			  min = population.get(i).getObjective(objNumber);
		  }
	  }
	  return min;
  }
  
  double calculateMaxObjective(SolutionSet population, int objNumber) {
	  double max=Double.MIN_VALUE;
	  for(int i=0;i<population.size();i++) {
		  if(max < population.get(i).getObjective(objNumber)) {
			  max = population.get(i).getObjective(objNumber);
		  }
	  }
	  return max;
  }
  
  double calculateNormalizedObjective(Solution solution, SolutionSet population, int objNumber) {
	  
	  double normalObjective = solution.getObjective(objNumber);
	  double normalizedOjective = (normalObjective - calculateMinObjective(population, objNumber)) / 
			  (calculateMaxObjective(population, objNumber) - calculateMinObjective(population, objNumber));
	  return normalizedOjective;
  }
 
  int calculateNumberOfIndiConstViolated(SolutionSet population) {
	  int sum = 0;
	  for(int i = 0;i<population.size();i++) {
		  if(population.get(i).getOverallConstraintViolation()<0) {
			  sum++;
		  }
	  }
	  return sum;
  }
  
  void setDistances(SolutionSet population) {
	  double r = (population.size()- calculateNumberOfIndiConstViolated(population))/population.size();
	  
	  
	  
	  if(r==0.0) {
		  for(int i=0;i<problem_.getNumberOfObjectives();i++) {
			  for(int j=0;j<population.size();j++) {
				  Solution solution = population.get(j);
				  solution.setSRDistance(solution.getOverallConstraintViolation(), i);
			  }
		  }
		  
	  }else {
		  for(int i=0;i<problem_.getNumberOfObjectives();i++) {
			  for(int j=0;j<population.size();j++) {
				  Solution solution = population.get(j);
				  solution.setSRDistance(Math.sqrt(Math.pow(calculateNormalizedObjective(solution, population, i), 2)
						  + Math.pow(solution.getOverallConstraintViolation(), 2)), i);
					  
				  	
			  }
		  }
	  } 
  }
  
  void setPenalties( SolutionSet population) {
	  int r = (population.size()- calculateNumberOfIndiConstViolated(population))/population.size();
	  
	  
	  for(int i=0;i<problem_.getNumberOfObjectives();i++) {
		  for(int k=0;k<population.size();k++) {
			  double capX, capY;
			  Solution solution = population.get(k);
			  if(r==0.0) {
				  capX = 0.0;
				  
			  }else {
				  capX = solution.getOverallConstraintViolation();
			  }
			  
			  if(solution.getOverallConstraintViolation()==0.0){
				  capY = 0;
			  }
			  else {
				  capY = calculateNormalizedObjective(solution, population, i); 
			  }
			  
			  solution.setSRPenalty((1-r)*capX+r*capY, i);
		  }
	  }
	  
  }

  double calculateModifiedObjectiveValue(Solution solution, int objNumber) {
	   return  solution.getSRdistance(objNumber)+solution.getSRPenalty(objNumber);  
	  
	  
  }
  
  void setOverAllConstraintVIolation(SolutionSet population) throws JMException{
	  if(problem_.getName().equals("Constr_Ex")) {
		    
		  for(int z=0;z<population.size();z++) {
		  
		  double [] constraint = new double[problem_.getNumberOfConstraints()];

		  Solution solution = population.get(z);
		  
		    double x1 = solution.getDecisionVariables()[0].getValue();
		    double x2 = solution.getDecisionVariables()[1].getValue();
		        
		    constraint[0] =  (x2 + 9*x1 -6.0) ;
		    constraint[1] =  (-x2 + 9*x1 -1.0);
		        
		    /*double total = 0.0;
		    int number = 0;
		    for (int i = 0; i < this.getNumberOfConstraints(); i++)
		      if (constraint[i]<0.0){
		        total+=constraint[i];
		        number++;
		      }
		    */
		    
		    //calculate cjMIn 
		    double cjMin = Double.MIN_VALUE;
		    for(int i=0;i<problem_.getNumberOfConstraints();i++) {
		    	double cj = Double.min(0, constraint[i]);
		    	if(cj>cjMin) {
		    		cjMin = cj;
		    	}
		    	
		    }
		    
		    //calculate each cj
		    double sumCJ=0.0; 
		    for(int i=0;i<problem_.getNumberOfConstraints();i++) {
		    	double cj = Double.max(0, constraint[i]);
		    	sumCJ += (cj/cjMin);
		    }
		    
		    double sumConstraintVIolation = sumCJ/problem_.getNumberOfConstraints();
		    solution.setOverallConstraintViolation(sumConstraintVIolation);
		  }
	  }
  }
  
  
} // NSGA-II
