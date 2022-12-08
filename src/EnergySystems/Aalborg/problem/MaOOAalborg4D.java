package EnergySystems.Aalborg.problem;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.variable.ArrayReal;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import jmetal.util.wrapper.XReal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import EnergySystems.EnergySystemOptimizationProblem;
import EnergySystems.Aalborg.parse.EnergyPLANFileParseForAalborg;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.AnnotationUtils;

/*
 * Two problems are solved in this version
 * 1. Only one evolution of EnergyPLAN
 * 2. CHP capacity <= pp capacity
 */
public class MaOOAalborg4D extends EnergySystemOptimizationProblem {

	MultiMap energyplanmMap;

	// large Value for boiler & PP and other information
	int largeValueOfBoiler = 1500, largeValueOfPP = 1000;
	int boilerLifeTime = 20, PPLifeTime = 30;
	double interest = 0.03, fixedMOForBoilerinPercentage = 0.03, fixedMOForPPinPercentage = 0.02;
	double boilerCostInMDDK = 1.0, PPCostInMDKK = 0.0;

	/**
	 * Creates a new instance of problem ZDT1.
	 * 
	 * @param numberOfVariables
	 *            Number of variables.
	 * @param solutionType
	 *            The solution type must "Real", "BinaryReal, and "ArrayReal".
	 */
	public MaOOAalborg4D(String solutionType) {
		numberOfVariables_ = 7;
		numberOfObjectives_ = 4;
		numberOfConstraints_ = 3;
		problemName_ = "MaOOAalborg4D";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		// Establishes upper and lower limits for the variables
		int var;

		// capacities for CHP, HP, PP
		// index - 0 -> CHP3
		// index - 1 -> HP3
		// index - 2 -> PP
		// index - 3 -> wind
		// index - 4 -> offshore wind
		// index - 5 -> PV
		// index - 6 -> boiler3
		
		

		for (var = 0; var < 2; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1000.0;
		} // for

		// capacity for PP, its a dummy, the value will be set in evaluation
		// methods
		// the pp capacity do not need to be optimize, it is just use here
		// to print the value
		lowerLimit_[2] = 0.0;
		upperLimit_[2] = 1000.0;

		for (var = 3; var < numberOfVariables_ - 1; var++) {
			// capacities of wind (index: 3) , off-shore wind (index: 4) , PV
			// (index: 5)
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1500.0;
		} // for

		// capacity for boiler, its a dummy, the value will be set in evaluation
		// methods
		// the boiler capacity do not need to be optimize, it is just use here
		// to print the value
		lowerLimit_[6] = 0.0;
		upperLimit_[6] = 10000.0;

		/*
		 * // capacity of heat storage group 3 lowerLimit_[var] = 0.0; upperLimit_[var]
		 * = 5.0;
		 * 
		 * //capacity for boiler, its a dummy, the value will be set in evaluation
		 * methods //the boiler capacity do not need to be optimize, it is just use here
		 * to point the value lowerLimit_[7]=0.0; upperLimit_[7]=10000.0;
		 */

		/*
		 * for (; var < numberOfVariables_; var++) { // share of coal, oil and
		 * natural-gas lowerLimit_[var] = 0.0; upperLimit_[var] = 1.0;
		 * 
		 * }
		 */

		if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
	} // constructor end

	@Override
	public MultiMap writeIntoInputFile(Solution s, String fileName) throws IOException, JMException {

		MultiMap modifyMap = new MultiValueMap();

		File modifiedInput = new File(".\\EnergyPLAN15.1\\spool\\" + fileName + ".txt");
		if (modifiedInput.exists()) {
			modifiedInput.delete();
		}
		modifiedInput.createNewFile();

		FileWriter fw = new FileWriter(modifiedInput.getAbsoluteFile());
		BufferedWriter modifiedInputbw = new BufferedWriter(fw);

		String path = ".\\EnergyPLAN15.1\\energyPlan Data\\Data\\Aalborg_2050_Plan_A_44%ForOptimization_2objctives.txt";

		BufferedReader mainInputbr = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-16"));


		// CHP group 3
		double CHPGr3 = s.getDecisionVariables()[0].getValue();

		// HP group 3
		double HPGr3 = s.getDecisionVariables()[1].getValue();

		// PP
		// double PP = solution.getDecisionVariables()[2].getValue();

		// wind
		double wind = s.getDecisionVariables()[3].getValue();
		// off-shore wind
		double offShoreWind = s.getDecisionVariables()[4].getValue();
		// PV
		double PV = s.getDecisionVariables()[5].getValue();

		modifyMap.put("CHPGr3", CHPGr3);
		modifyMap.put("HPGr3", HPGr3);
		modifyMap.put("Wind", wind);
		modifyMap.put("OffShoreWind", offShoreWind);
		modifyMap.put("PV", PV);

		String modifiedParameters[] = { 
				"input_cap_chp3_el=", // chp3 (0)
				"input_cap_hp3_el=", // 1
				"input_RES1_capacity=", // 2 wind
				"input_RES2_capacity=", // 3 offshorewind
				"input_RES3_capacity=", // 4 //PV
				
		};
		String cooresPondingValues[] = new String[modifiedParameters.length];
		int index = 0;
		cooresPondingValues[index++] = "" + CHPGr3;
		cooresPondingValues[index++] = "" + HPGr3;
		cooresPondingValues[index++] = "" + wind;
		cooresPondingValues[index++] = "" + offShoreWind;
		cooresPondingValues[index++] = "" + PV;
	
		String line;
		while ((line = mainInputbr.readLine()) != null) {
			line.trim();
			boolean trackBreak=false;
			for (int i = 0; i < modifiedParameters.length; i++) {
				if (line.startsWith(modifiedParameters[i]) || line.endsWith(modifiedParameters[i])) {
					modifiedInputbw.write(line+"\n" );
					line = mainInputbr.readLine();
					line=line.replace(line, cooresPondingValues[i]);
					modifiedInputbw.write(line+"\n");
					trackBreak=true;
					break;
				}
			}
			if(trackBreak)
				continue;
			else
				modifiedInputbw.write(line+"\n");

		}
		
		modifiedInputbw.close();
		mainInputbr.close();

		
		return modifyMap;

	}

	@Override
	public void extractInformation(Solution solution, MultiMap modifyMap, int serial) throws JMException {
		
		EnergyPLANFileParseForAalborg epfp = new EnergyPLANFileParseForAalborg(".\\EnergyPLAN15.1\\spool\\results\\modifiedInput"+serial+".txt.txt");
		energyplanmMap = epfp.parseFile();

		Iterator it;
		Collection<String> col;

		/*//extract all data
		for (Object name: energyplanmMap.keySet()) {
		    String key = name.toString();
		    String value = energyplanmMap.get(name).toString();
		    System.out.println(key + " " + value);
		}*/
		
		
		// extracting maximum Boiler configuration (group # 3)
		col = (Collection<String>) energyplanmMap.get("Annual MaximumBoiler 3Heat");
		it = col.iterator();
		double maximumBoilerGroup3 = Double.parseDouble(it.next().toString());
		// extracting maximum PP configuration
		col = (Collection<String>) energyplanmMap.get("Annual MaximumPPElectr.");
		it = col.iterator();
		double maximumPP = Double.parseDouble(it.next().toString());
		// if chp>PP, do a 2nd evolution with energyplan where chp=pp
		if (solution.getDecisionVariables()[0].getValue() > maximumPP) {
			/*
			 * 1. make chp = pp 2. evaluate with energyPLAN
			 */

			// chp=pp
			solution.getDecisionVariables()[0].setValue(maximumPP);

			// set the decision variable according to the maximum pp
			// capacity
			solution.getDecisionVariables()[2].setValue(maximumPP);

			// set the decision variable according to the maximum boiler
			// capacity
			solution.getDecisionVariables()[6].setValue(maximumBoilerGroup3);

			writeModificationFile(solution, maximumBoilerGroup3, maximumPP);

			try {
				
				String energyPLANrunCommand = ".\\EnergyPLAN15.1\\EnergyPLAN.exe -i "
						+ "\".\\EnergyPLAN15.1\\energyPlan Data\\Data\\Aalborg_2050_Plan_A_44%ForOptimization_2objctives.txt\" "
						+ "-m \"modification.txt\" -ascii \"result.txt\" ";

				Process process = Runtime.getRuntime().exec(energyPLANrunCommand);
				process.waitFor();
				process.destroy();
				epfp = new EnergyPLANFileParseForAalborg(".\\result.txt");
				energyplanmMap = epfp.parseFile();

				// objective # 1
				col = (Collection<String>) energyplanmMap.get("CO2-emission (corrected)");
				it = col.iterator();
				solution.setObjective(0, Double.parseDouble(it.next().toString()));
				// objective # 2
				col = (Collection<String>) energyplanmMap.get("TOTAL ANNUAL COSTS");
				it = col.iterator();
				solution.setObjective(1, Double.parseDouble(it.next().toString()));

				//3rd obective 
				double thirdObjective = calculateThirdObjective(energyplanmMap);
				solution.setObjective(2, thirdObjective);
	
				//4th Obective
				double ESD = calculateForthObective(energyplanmMap);
				solution.setObjective(3, ESD);
				
				
				
				
				// check warning
				col = (Collection<String>) energyplanmMap.get("WARNING");
				if (col != null) {
					/*
					 * System.out.println("No warning"); } else {
					 */
					@SuppressWarnings("rawtypes")
					Iterator it3 = col.iterator();
					String warning = it3.next().toString();
					if (!warning.equals("PP too small. Critical import is needed")
							&& !warning.equals("Grid Stabilisation requierments are NOT fullfilled"))
						throw new IOException("warning!!" + warning);
					// System.out.println("Warning " +
					// it3.next().toString());

				}
			} catch (IOException e) {
				System.out.println("Energyplan.exe has some problem");
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("Energyplan interrupted");
			}
		} else {
			// just use numerical way to calculate annual cost
			double reductionInvestmentCost = Math
					.round(((largeValueOfBoiler - maximumBoilerGroup3) * boilerCostInMDDK * interest)
							/ (1 - Math.pow((1 + interest), -boilerLifeTime))
							+ ((largeValueOfPP - maximumPP) * PPCostInMDKK * interest)
									/ (1 - Math.pow((1 + interest), -PPLifeTime)));

			double reduceFixedOMCost = Math.round(
					((largeValueOfBoiler - maximumBoilerGroup3) * boilerCostInMDDK * fixedMOForBoilerinPercentage)
							+ ((largeValueOfPP - maximumPP) * PPCostInMDKK * fixedMOForPPinPercentage));
			// objective # 1
			col = (Collection<String>) energyplanmMap.get("CO2-emission (corrected)");
			it = col.iterator();
			solution.setObjective(0, Double.parseDouble(it.next().toString()));
			// objective # 2
			col = (Collection<String>) energyplanmMap.get("TOTAL ANNUAL COSTS");
			it = col.iterator();
			double tempAnnaulCost = Double.parseDouble(it.next().toString());
			double actualAnnualCost = tempAnnaulCost - reductionInvestmentCost - reduceFixedOMCost;
			solution.setObjective(1, actualAnnualCost);

			
		

			//3rd obective 
			double thirdObjective = calculateThirdObjective(energyplanmMap);
			solution.setObjective(2, thirdObjective);

			//4th Obective
			double ESD = calculateForthObective(energyplanmMap);
			solution.setObjective(3, ESD);

			
			}
			//constraints
		
			//as PP is considered as import  
			col = (Collection<String>) energyplanmMap.get("Annual MaximumPPElectr.");
			it = col.iterator();
			int maximumImport = Integer.parseInt(it.next().toString());
			col = (Collection<String>) energyplanmMap.get("Annual MinimumStabil.Load");
			it = col.iterator();
			int mimimumGridStabPercentage = Integer.parseInt(it.next().toString());

			// constraints about heat3-balance: balance<=0
			col = (Collection<String>) energyplanmMap.get("AnnualBalance3Heat");
			it = col.iterator();
			double annualHeat3Balance = Double.parseDouble(it.next().toString());

			double constraints[] = new double[numberOfConstraints_];
			constraints[0] = 160 - maximumImport;
			constraints[1] = mimimumGridStabPercentage - 100;
			constraints[2] = 0 - annualHeat3Balance;

			double totalViolation = 0.0;
			int numberOfViolation = 0;
			for (int i = 0; i < numberOfConstraints_; i++) {
				if (constraints[i] < 0.0) {
					totalViolation += constraints[i];
					numberOfViolation++;
				}
			}
			/*
			 * if (constraints[0] < 0.0) {
			 * solution.setOverallConstraintViolation(constrints);
			 * solution.setNumberOfViolatedConstraint(1);
			 */

			solution.setOverallConstraintViolation(totalViolation);
			solution.setNumberOfViolatedConstraint(numberOfViolation);

		
		
		

		col = (Collection<String>) energyplanmMap.get("WARNING");
		if (col != null) {
			/*
			 * System.out.println("No warning"); } else {
			 */
			@SuppressWarnings("rawtypes")
			Iterator it3 = col.iterator();
			String warning = it3.next().toString();
			if (!warning.equals("PP too small. Critical import is needed")
					&& !warning.equals("Grid Stabilisation requierments are NOT fullfilled"))
				try {
					throw new IOException("warning!!" + warning);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			// System.out.println("Warning " + it3.next().toString());

		}
	}

	

	/**
	 * Evaluates a solution.
	 * 
	 * @param solution
	 *            The solution to evaluate.
	 * @throws JMException
	 */
	@SuppressWarnings("unchecked")
	public void evaluate(Solution solution) throws JMException {

		MultiMap mm = new MultiValueMap() ;
		mm=createModificationFiles(solution, 0 );
		
	
	
	simulateAllScenarios(1);
	
	extractInformation(solution, mm, 0);
	}

	@SuppressWarnings("unchecked")
	public void evaluateConstraints(Solution solution) throws JMException {
	
	}

	void writeModificationFile(Solution solution, File f) throws JMException {

		// DecimalFormat twoDForm = new DecimalFormat("0.00");

		// CHP group 3
		double CHPGr3 = solution.getDecisionVariables()[0].getValue();

		// HP group 3
		double HPGr3 = solution.getDecisionVariables()[1].getValue();

		// PP
		// double PP = solution.getDecisionVariables()[2].getValue();

		// wind
		double wind = solution.getDecisionVariables()[3].getValue();
		// off-shore wind
		double offShoreWind = solution.getDecisionVariables()[4].getValue();
		// PV
		double PV = solution.getDecisionVariables()[5].getValue();
		// heat starage group 3
		// double heatStorageGr3 =
		// solution.getDecisionVariables()[6].getValue();

		/*
		 * // PP coal share double PP_coal_share =
		 * solution.getDecisionVariables()[4].getValue(); // pp oil sahre double
		 * PP_oil_share = solution.getDecisionVariables()[5].getValue(); // pp Ngas
		 * share double PP_ngas_share = solution.getDecisionVariables()[6].getValue();
		 * 
		 * final double PP_coal_eff=0.35; final double PP_oil_eff=0.45; final double
		 * PP_ngas_eff=0.55;
		 * 
		 * //efficiency calculation for PP //normalized the share double
		 * nor_PP_coal_share = PP_coal_share /
		 * (PP_coal_share+PP_oil_share+PP_ngas_share); double nor_PP_oil_share =
		 * PP_oil_share / (PP_coal_share+PP_oil_share+PP_ngas_share); double
		 * nor_PP_ngas_share = PP_ngas_share /
		 * (PP_coal_share+PP_oil_share+PP_ngas_share);
		 * 
		 * 
		 * double overall_eff_other = ((PP*nor_PP_coal_share)*PP_coal_eff +
		 * (PP*nor_PP_oil_share)*PP_oil_eff + (PP*nor_PP_ngas_share)*PP_ngas_eff)/PP;
		 * 
		 * //efficiency calculation from Dr. Marco
		 * 
		 * double overall_eff_marco = 1 / ((nor_PP_coal_share/PP_coal_eff) +
		 * (nor_PP_oil_share/PP_oil_eff) + (nor_PP_ngas_share/PP_ngas_eff) ) ;
		 */

		try {

			FileWriter fw = new FileWriter(f.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String str = "EnergyPLAN version";
			bw.write(str);
			bw.newLine();
			str = "698";
			bw.write(str);
			bw.newLine();

			str = "input_cap_chp3_el=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(CHPGr3);
			str = "" + (int) Math.round(CHPGr3);
			bw.write(str);
			bw.newLine();

			str = "input_cap_hp3_el=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(HPGr3);
			str = "" + (int) Math.round(HPGr3);
			bw.write(str);
			bw.newLine();

			/*
			 * str = "input_cap_pp_el="; bw.write(str); bw.newLine(); // str = "" + (int)
			 * Math.round(PP); str = "" + (int) Math.round(PP); bw.write(str); bw.newLine();
			 */

			str = "input_RES1_capacity=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(wind);
			str = "" + (int) Math.round(wind);
			bw.write(str);
			bw.newLine();

			str = "input_RES2_capacity=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(offShoreWind);
			str = "" + (int) Math.round(offShoreWind);
			bw.write(str);
			bw.newLine();

			str = "input_RES3_capacity=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(PV);
			str = "" + (int) Math.round(PV);
			bw.write(str);
			bw.newLine();

			bw.close();
			// file.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void writeModificationFile(Solution solution, double boilerCap, double PPCap) throws JMException {

		// CHP group 3
				double CHPGr3 = solution.getDecisionVariables()[0].getValue();

				// HP group 3
				double HPGr3 = solution.getDecisionVariables()[1].getValue();

				// PP
				// double PP = solution.getDecisionVariables()[2].getValue();

				// wind
				double wind = solution.getDecisionVariables()[3].getValue();
				// off-shore wind
				double offShoreWind = solution.getDecisionVariables()[4].getValue();
				// PV
				double PV = solution.getDecisionVariables()[5].getValue();
				// heat starage group 3
				// double heatStorageGr3 =
				// solution.getDecisionVariables()[6].getValue();

				/*
				 * // PP coal share double PP_coal_share =
				 * solution.getDecisionVariables()[4].getValue(); // pp oil sahre double
				 * PP_oil_share = solution.getDecisionVariables()[5].getValue(); // pp
				 * Ngas share double PP_ngas_share =
				 * solution.getDecisionVariables()[6].getValue();
				 * 
				 * final double PP_coal_eff=0.35; final double PP_oil_eff=0.45; final
				 * double PP_ngas_eff=0.55;
				 * 
				 * //efficiency calculation for PP //normalized the share double
				 * nor_PP_coal_share = PP_coal_share /
				 * (PP_coal_share+PP_oil_share+PP_ngas_share); double nor_PP_oil_share =
				 * PP_oil_share / (PP_coal_share+PP_oil_share+PP_ngas_share); double
				 * nor_PP_ngas_share = PP_ngas_share /
				 * (PP_coal_share+PP_oil_share+PP_ngas_share);
				 * 
				 * 
				 * double overall_eff_other = ((PP*nor_PP_coal_share)*PP_coal_eff +
				 * (PP*nor_PP_oil_share)*PP_oil_eff +
				 * (PP*nor_PP_ngas_share)*PP_ngas_eff)/PP;
				 * 
				 * //efficiency calculation from Dr. Marco
				 * 
				 * double overall_eff_marco = 1 / ((nor_PP_coal_share/PP_coal_eff) +
				 * (nor_PP_oil_share/PP_oil_eff) + (nor_PP_ngas_share/PP_ngas_eff) ) ;
				 */

				try {

					File file = new File("modification.txt");
					if (file.exists()) {
						file.delete();

					}

					file.createNewFile();

					FileWriter fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					String str = "EnergyPLAN version";
					bw.write(str);
					bw.newLine();
					str = "698";
					bw.write(str);
					bw.newLine();

					str = "input_cap_chp3_el=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(CHPGr3);
					str = "" + (int) Math.round(CHPGr3);
					bw.write(str);
					bw.newLine();

					str = "input_cap_hp3_el=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(HPGr3);
					str = "" + (int) Math.round(HPGr3);
					bw.write(str);
					bw.newLine();

					/*
					 * str = "input_cap_pp_el="; bw.write(str); bw.newLine(); // str =
					 * "" + (int) Math.round(PP); str = "" + (int) Math.round(PP);
					 * bw.write(str); bw.newLine();
					 */

					str = "input_RES1_capacity=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(wind);
					str = "" + (int) Math.round(wind);
					bw.write(str);
					bw.newLine();

					str = "input_RES2_capacity=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(offShoreWind);
					str = "" + (int) Math.round(offShoreWind);
					bw.write(str);
					bw.newLine();

					str = "input_RES3_capacity=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(PV);
					str = "" + (int) Math.round(PV);
					bw.write(str);
					bw.newLine();


					str = "input_cap_boiler3_th=";
					bw.write(str);
					bw.newLine();
					// str = "" + (int) Math.round(modification);
					str = "" + (int) Math.round(boilerCap);
					bw.write(str);
					bw.newLine();

					str = "input_cap_pp_el=";
					bw.write(str);
					bw.newLine();
					str = "" + (int) Math.round(PPCap);
					bw.write(str);
					bw.newLine();

					bw.close();
					// file.delete();
				} catch (IOException e) {
					e.printStackTrace();
				}

	}

	void modifyModificationFile(double modificationBoiler, double modificationPP) throws JMException {
		// now only modify boiler in group # 3
		try {

			File file = new File("modification.txt");
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);

			String str = "input_cap_boiler3_th=";
			bw.write(str);
			bw.newLine();
			// str = "" + (int) Math.round(modification);
			str = "" + (int) Math.round(modificationBoiler);
			bw.write(str);
			bw.newLine();

			str = "input_cap_pp_el=";
			bw.write(str);
			bw.newLine();
			str = "" + (int) Math.round(modificationPP);
			bw.write(str);
			bw.newLine();

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override 
public void simulateAllScenarios(int numberOfScenarios) {
		
		String runSpoolEnergyPLAN = ".\\EnergyPLAN15.1\\EnergyPLAN.exe -spool "+numberOfScenarios+ "  ";
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
	
	double calculateForthObective(MultiMap energyplanmMap) {
		//double PEFImport = 2.12; // 1/0.47
		
		Collection<String> col = (Collection<String>) energyplanmMap.get("Ngas Consumption");
		Iterator<String> it = col.iterator();
		double nGasConsumption = Double.parseDouble(it.next().toString());

		//local production
		//wind
		col = (Collection<String>) energyplanmMap.get("AnnualWindElectr.");
		it = col.iterator();
		double annualWindEl = Double.parseDouble(it.next().toString());
		
		//offshore wind
		col = (Collection<String>) energyplanmMap.get("AnnualOffshoreElectr.");
		it = col.iterator();
		double annualOffShoreWindEl = Double.parseDouble(it.next().toString());
		
		//PV
		col = (Collection<String>) energyplanmMap.get("AnnualPVElectr.");
		it = col.iterator();
		double annualPVEl = Double.parseDouble(it.next().toString());
		
		//chp
		col = (Collection<String>) energyplanmMap.get("AnnualCHPElectr.");
		it = col.iterator();
		double annualChpEl = Double.parseDouble(it.next().toString());
		
		//cshp
		col = (Collection<String>) energyplanmMap.get("AnnualCSHPElectr.");
		it = col.iterator();
		double annualCshpEl = Double.parseDouble(it.next().toString());
		
		double localProduction = annualWindEl +annualOffShoreWindEl + annualPVEl + annualChpEl + annualCshpEl ;
		
		col = (Collection<String>) energyplanmMap.get("AnnualExportElectr.");
		it = col.iterator();
		double annualExport = Double.parseDouble(it.next().toString());
		//Export PEF 
		double PEFLocalProduction = ( annualWindEl*1 + annualOffShoreWindEl*1 + annualPVEl*1 
				+ annualChpEl * (1.0/0.25) + annualCshpEl * (1.0/0.54))/localProduction;
		
		col = (Collection<String>) energyplanmMap.get("Biomass Consumption");
		it = col.iterator();
		double biomassConsumption = Double.parseDouble(it.next().toString());
		
		col = (Collection<String>) energyplanmMap.get("Waste Input");
		it = col.iterator();
		double wasteConsumption = Double.parseDouble(it.next().toString());
		
		//HP heat
		col = (Collection<String>) energyplanmMap.get("AnnualHP 2Heat");
		it = col.iterator();
		double hp2Heat = Double.parseDouble(it.next().toString());
		
		col = (Collection<String>) energyplanmMap.get("AnnualHP 3Heat");
		it = col.iterator();
		double hp3Heat = Double.parseDouble(it.next().toString());
		double hpHeat = hp2Heat + hp3Heat;
		
		double ESD = nGasConsumption/( (localProduction- annualExport) * PEFLocalProduction + nGasConsumption + biomassConsumption + wasteConsumption 
										+ (hpHeat - (hpHeat/3.6) ) );
		return ESD;
	}
	
	double calculateThirdObjective(MultiMap energyplanmMap ) {
		Collection<String> col = (Collection<String>) energyplanmMap.get("AnnualPPElectr.");
		Iterator<String> it = col.iterator();
		double annualImport = Double.parseDouble(it.next().toString());
		
		col = (Collection<String>) energyplanmMap.get("AnnualExportElectr.");
		it = col.iterator();
		double annualExport = Double.parseDouble(it.next().toString());
		
		col = (Collection<String>) energyplanmMap.get("AnnualElectr.Demand");
		it = col.iterator();
		double annualElDemand = Double.parseDouble(it.next().toString());
		
		double thirdObjective = (annualImport+ annualExport)/annualElDemand;
		return thirdObjective;
	}
}
