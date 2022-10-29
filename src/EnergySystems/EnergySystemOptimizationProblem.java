package EnergySystems;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.util.JMException;

public abstract class EnergySystemOptimizationProblem extends Problem{
	
	
	
	public void simulateAllScenarios(int numberOfScenarios) {
		
		String runSpoolEnergyPLAN = ".\\EnergyPLAN161\\EnergyPLAN.exe -spool "+numberOfScenarios+ "  ";
		for(int i=0;i<numberOfScenarios;i++) {
			runSpoolEnergyPLAN = runSpoolEnergyPLAN + "modifiedInput"+i+".txt ";
		}
		runSpoolEnergyPLAN = runSpoolEnergyPLAN + "-ascii run";
		
			
		try {
			Process process = Runtime.getRuntime().exec(runSpoolEnergyPLAN);
			process.waitFor();
			process.destroy();

		} catch (IOException e) {
			System.out.println("Energyplan.exe has some problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("Energyplan interrupted");
		}
		
	}

	
	public MultiMap createModificationFiles(Solution solution, int serial) throws JMException {

		MultiMap modifyMap = new MultiValueMap();

		try {
			modifyMap = writeIntoInputFile(solution, ".\\modifiedInput" + serial);
		}catch(IOException e) {
			System.out.print("Pobrlem writting in modified Input file");
		}

		return modifyMap;
	}
	
	
	
	public abstract  MultiMap writeIntoInputFile(Solution solution, String fileName) throws IOException, JMException;
	

	public abstract void extractInformation(Solution solution, MultiMap modifyMap, int serial) throws JMException;
	
	
	public  void  evaluateAll(SolutionSet solutionSet) throws JMException {
			
			ArrayList<MultiMap> multiMapList = new ArrayList<MultiMap>();
			
			
			for(int i=0;i<solutionSet.size();i++) {
				Solution solution = solutionSet.get(i);
				MultiMap mm = new MultiValueMap() ;
				mm=createModificationFiles(solution, i );
				multiMapList.add(mm);
				
			}
			
			simulateAllScenarios(solutionSet.size());
			
			for(int i=0;i<solutionSet.size();i++) {
				Solution solution = solutionSet.get(i);
				MultiMap mm = multiMapList.get(i); 
				extractInformation(solution, mm, i);
			
			}
	}
	
}
