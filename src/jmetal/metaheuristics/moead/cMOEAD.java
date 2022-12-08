//  cMOEAD.java
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
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;
import jmetal.util.comparators.IConstraintViolationComparator;
import jmetal.util.comparators.ViolationThresholdComparator;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

// This class implements a constrained version of the MOEAD algorithm based on
// the paper:
// "An adaptive constraint handling approach embedded MOEA/D". DOI: 10.1109/CEC.2012.6252868
//
public class cMOEAD extends MOEAD {

    /** 
   * Constructor
   * @param problem Problem to solve
   */
  public cMOEAD(Problem problem) {
    super (problem) ;

    functionType_ = "_TCHE1";

  } // DMOEA

	@Override
	  void updateProblem(Solution indiv, int id, int type) {
		    // indiv: child solution
		    // id:   the id of current subproblem
		    // type: update solutions in - neighborhood (1) or whole population (otherwise)
		    int size;
		    int time;

		    time = 0;

		    if (type == 1) {
		      size = neighborhood_[id].length;
		    } else {
		      size = population_.size();
		    }
		    int[] perm = new int[size];

		    Utils.randomPermutation(perm, size);

		    for (int i = 0; i < size; i++) {
		      int k;
		      if (type == 1) {
		        k = neighborhood_[id][perm[i]];
		      } else {
		        k = perm[i];      // calculate the values of objective function regarding the current subproblem
		      }
		      double f1, f2;

		      //both are infeasible
		      if(indiv.getOverallConstraintViolation() < 0 && population_.get(k).getOverallConstraintViolation() < 0) {
		    	  if(population_.get(k).getOverallConstraintViolation() > indiv.getOverallConstraintViolation()) {
		    		  population_.replace(k, new Solution(indiv));
		    		  time++;
		    	  }
		      }
		      
		      else if(population_.get(k).getOverallConstraintViolation() < 0 && indiv.getOverallConstraintViolation() >= 0) {
		    	  population_.replace(k, new Solution(indiv));
	    		  time++;
		      }
		      
		      if(indiv.getOverallConstraintViolation() >= 0 && population_.get(k).getOverallConstraintViolation() >= 0) {
		    	 
		      
		      f1 = fitnessFunction(population_.get(k), lambda_[k]);
		      f2 = fitnessFunction(indiv, lambda_[k]);

		      if (f2 < f1) {
		        population_.replace(k, new Solution(indiv));
		        //population[k].indiv = indiv;
		        time++;
		      }
		      
		    	  }
		      // the maximal number of solutions updated is not allowed to exceed 'limit'
		      if (time >= nr_) {
		        return;
		      }
		    }
		  } // updateProblem
    
}
