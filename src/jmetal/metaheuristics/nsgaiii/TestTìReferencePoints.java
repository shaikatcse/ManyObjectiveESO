package jmetal.metaheuristics.nsgaiii;

import java.util.List;
import java.util.Vector;

import jmetal.metaheuristics.nsgaiii.util.ReferencePoint;

public class TestTÏReferencePoints {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<ReferencePoint> referencePoints = new Vector<>();
		
		(new ReferencePoint()).generateReferencePoints(referencePoints, 4,
				8);
		
		for(ReferencePoint r: referencePoints) {
			for(Double d: r.position) {
				System.out.print(d+ " ");
			}
			System.out.println();
		}
		
		
	}

}
