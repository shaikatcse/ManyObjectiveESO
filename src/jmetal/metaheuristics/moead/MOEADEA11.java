//  MOEAD.java
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

package jmetal.metaheuristics.moead;

import jmetal.core.*;
import jmetal.operators.crossover.DifferentialEvolutionCrossover;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;



public class MOEADEA11 extends MOEADGenEA {

  /** 
   * Constructor
   * @param problem Problem to solve
   */
  public MOEADEA11(Problem problem) {
    super (problem) ;


  } // DMOEA

  public SolutionSet execute() throws JMException, ClassNotFoundException {
    int maxEvaluations;

    evaluations_ = 0;
    maxEvaluations = ((Integer) this.getInputParameter("maxEvaluations")).intValue();
    populationSize_ = ((Integer) this.getInputParameter("populationSize")).intValue();
    dataDirectory_ = this.getInputParameter("dataDirectory").toString();
    System.out.println("POPSIZE: "+ populationSize_) ;

    population_ = new SolutionSet(populationSize_);
    indArray_ = new Solution[problem_.getNumberOfObjectives()];

    T_ = ((Integer) this.getInputParameter("T")).intValue();
    nr_ = ((Integer) this.getInputParameter("nr")).intValue();
    delta_ = ((Double) this.getInputParameter("delta")).doubleValue();

/*
    T_ = (int) (0.1 * populationSize_);
    delta_ = 0.9;
    nr_ = (int) (0.01 * populationSize_);
*/
    neighborhood_ = new int[populationSize_][T_];

    z_ = new double[problem_.getNumberOfObjectives()];
    //lambda_ = new Vector(problem_.getNumberOfObjectives()) ;
    lambda_ = new double[populationSize_][problem_.getNumberOfObjectives()];

    crossover_ = operators_.get("crossover"); // default: DE crossover
    mutation_ = operators_.get("mutation");  // default: polynomial mutation

    ArrayList<Everything> offSpring = new ArrayList<>();
    
    // STEP 1. Initialization
    // STEP 1.1. Compute euclidean distances between weight vectors and find T
    initUniformWeight();
    //for (int i = 0; i < 300; i++)
   // 	System.out.println(lambda_[i][0] + " " + lambda_[i][1]) ;
    
    initNeighborhood();

    // STEP 1.2. Initialize population
    initPopulation();

    // STEP 1.3. Initialize z_
    initIdealPoint();

    // STEP 2. Update
    do {
      int[] permutation = new int[populationSize_];
      Utils.randomPermutation(permutation, populationSize_);

      for (int i = 0; i < populationSize_; i++) {
        int n = permutation[i]; // or int n = i;
        //int n = i ; // or int n = i;
        int type;
        double rnd = PseudoRandom.randDouble();

        // STEP 2.1. Mating selection based on probability
        if (rnd < delta_) // if (rnd < realb)    
        {
          type = 1;   // neighborhood
        } else {
          type = 2;   // whole population
        }
        Vector<Integer> p = new Vector<Integer>();
        matingSelection(p, n, 2, type);

        // STEP 2.2. Reproduction
        Solution child;
        

        // Apply DE crossover 
        
        if(!(crossover_ instanceof DifferentialEvolutionCrossover)) {
        	Solution[] parents = new Solution[2];
        	
            parents[0] = population_.get(p.get(0));
            parents[1] = population_.get(p.get(1));

        	Solution [] solutions = (Solution []) crossover_.execute(parents);
        	if(PseudoRandom.randDouble()<= 0.5) {
        		child = solutions[0];
        	}else {
        		child = solutions[1];
        	}
        	
        }else {
        	Solution[] parents = new Solution[3];

        	parents[0] = population_.get(p.get(0));
            parents[1] = population_.get(p.get(1));
            parents[2] = population_.get(n);

        	child = (Solution) crossover_.execute(new Object[]{population_.get(n), parents});
        }
        // Apply mutation
        mutation_.execute(child);

        offSpring.add(new Everything(child, type, n));
        
          evaluations_++;

        // STEP 2.3. Repair. Not necessary
          //1, 5, 11, 15, 33, 55, 165
          if(evaluations_%11==0) {
          SolutionSet s = new SolutionSet(populationSize_);
         // System.out.println(offSpring.size());
          for(int i1=0;i1<offSpring.size();i1++) {
              
        	  Everything e = offSpring.get(i1);
        	  Solution child1 = e.solution;
        	  s.add(child1);
          }
         
          problem_.evaluateAll(s);
          
          // STEP 2.4. Update z_
          
          
          for(int i2=0;i2<offSpring.size();i2++) {
          
        	  Everything e = offSpring.get(i2);
        	  Solution child2 = e.solution;
        	  int type2 = e.type;
        	  int n2 = e.n;
        	  
        	  updateReference(child2);

        	  // STEP 2.5. Update of solutions
        	  updateProblem(child2, n2, type2);
        	  
        	  
          }
          offSpring.clear();    
}
      } // for 
      

    
    } while (evaluations_ < maxEvaluations);

    return population_;
  }

 } // MOEAD

