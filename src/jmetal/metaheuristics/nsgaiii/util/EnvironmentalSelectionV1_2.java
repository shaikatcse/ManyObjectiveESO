package jmetal.metaheuristics.nsgaiii.util;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import jmetal.core.Solution;
import jmetal.util.JMException;
import jmetal.util.PseudoRandom;


@SuppressWarnings("serial")
public class EnvironmentalSelectionV1_2 extends  EnvironmentalSelection{



  public EnvironmentalSelectionV1_2(
      List<List<Solution>> fronts,
      int solutionsToSelect,
      List<ReferencePoint> referencePoints,
      int numberOfObjectives) {
    
	  super(fronts, solutionsToSelect, referencePoints, numberOfObjectives);
	  
  }
  
  
  /*vector<double> FindMaxObjectives(const CPopulation &pop)
  {
  	const size_t NumObj = pop[0].objs().size();

  	vector<double> max_point(NumObj, -numeric_limits<double>::max());
  	for (size_t i=0; i<pop.size(); i+=1)
  	{
  		for (size_t f=0; f<NumObj; f+=1)
  		{
  			max_point[f] = std::max(max_point[f], pop[i].objs()[f]);
  		}
  	}

  	return max_point;
  }*/

List<Double> FindMaxObjectives(List<Solution> population){
	List<Double> maxPoint = new ArrayList<>() {{
		for(int i=0;i<numberOfObjectives;i++) {
			add(Double.NEGATIVE_INFINITY);
		}
	}};
	
	for(int i=0; i<population.size();i++) {
		for(int obj = 0; obj<numberOfObjectives; obj++) {
			maxPoint.set(obj, Math.max(maxPoint.get(obj), population.get(i).getObjective(obj)));
		}
	}
	
	return maxPoint;
}
  
  @Override 
  public List<Double> constructHyperplane(List<Solution> population, List<Solution> extreme_points) {
    // Check whether there are duplicate extreme points.
    // This might happen but the original paper does not mention how to deal with it.
    boolean duplicate = false;
    for (int i = 0; !duplicate && i < extreme_points.size(); i += 1) {
      for (int j = i + 1; !duplicate && j < extreme_points.size(); j += 1) {
        duplicate = extreme_points.get(i).equals(extreme_points.get(j));
      }
    }

    List<Double> intercepts = new ArrayList<>();

    boolean negativeIntercept = false;
    if (!duplicate) // cannot construct the unique hyperplane (this is a casual method to deal with
    {               // the condition)
      // Find the equation of the hyperplane
      List<Double> b = new ArrayList<>(); // (pop[0].objs().size(), 1.0);
      for (int i = 0; i < numberOfObjectives; i++) b.add(1.0);

      List<List<Double>> A = new ArrayList<>();
      for (Solution s : extreme_points) {
        List<Double> aux = new ArrayList<>();
        for (int i = 0; i < numberOfObjectives; i++) aux.add ( ((List<Double>) s.getAdditionalAttributes().get("conv_obj")).get(i) );
        A.add(aux);
      }
      List<Double> x = guassianElimination(A, b);

      // Find intercepts
      for (int f = 0; f < numberOfObjectives; f += 1) {
        intercepts.add(1.0 / x.get(f));
        if(x.get(f) < 0)
		{
			negativeIntercept = true;
			break;
		}
        
      }
    }
    
    if (duplicate || negativeIntercept) // v1.2: follow the method in Yuan et al. (GECCO 2015)
	{
		List<Double> max_objs = FindMaxObjectives(population);
		for (int f=0; f<numberOfObjectives; f+=1)
		{
			intercepts.add ( max_objs.get(f));
		}
	}
    
    
    return intercepts;
  }

  
  

}

