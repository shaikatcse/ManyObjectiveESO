package EnergySystems.GiudicarieEsteriori.Problem;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

import EnergySystems.EnergySystemOptimizationProblem;
import EnergySystems.GiudicarieEsteriori.ParseFile.EnergyPLANFileParseForCivis;

/*
 * This is a problem file that is dealing with Transport (mainly introduction of electric cars)
 */
public class MaOOCEIS4D extends EnergySystemOptimizationProblem {

	HashMap<ArrayList<String>, MultiMap> hmForAllSolutions;
	EnergyPLANFileParseForCivis ex;

	MultiMap energyplanmMap;
	String simulatedYear, folder;

	public static final double PVInvestmentCostInKEuro = 2.6;
	public static final double hydroInvestmentCostInKEuro = 1.9;
	public static final double individualBoilerInvestmentCostInKEuro = 0.588;
	public static final double BiogasInvestmentCostInKEuro = 4.0;
	public static final double interest = 0.04;

	public static final double currentPVCapacity = 7514;
	public static final double currentHydroCapacity = 4000;
	public static final double currentBiogasCapacity = 500;
	public static final double currentIndvBiomassBoilerCapacity = 14306;
	public static final double currentIndvOilBoilerCapacity = 9155;
	public static final double currentIndvLPGBoilerCapacity = 3431;

	public static final double totalHeatDemand = 55.82;

	public static final double boilerLifeTime = 15;
	public static final double PVLifeTime = 30;
	public static final double HydroLifeTime = 50;
	public static final double BiogasLifeTime = 20;
	public static final double geoBoreHoleLifeTime = 100;

	public static final double COP = 3.2;

	public static final double maxHeatDemandInDistribution = 1.0;
	public static final double sumOfAllHeatDistributions = 3112.94;

	public static final double geoBoreholeCostInKWe = 3.2;

	public static final double oilBoilerEfficiency = 0.80;
	public static final double ngasBoilerEfficiency = 0.90;
	public static final double biomassBoilerEfficiency = 0.75;

	public static final double addtionalCostPerGWhinKEuro = 106.27;

	// Transport related data
	public static final int currentNumberOfPertrolCars = 2762;
	public static final int currentNumberOfDieselCars = 2094;
	public static final int averageKMPerYearForPetrolCar = 7250;
	public static final int averageKMPerYearForDieselCar = 13400;
	// lower calorific value (LCV): KWh/l (ref:
	// http://www.withouthotair.com/c3/page_31.shtml) check with Diego
	public static final double LCVPetrol = 8.86;
	public static final double LCVDiesel = 10.12;
	public static final double KWhPerKMElecCar = 0.168;
	public static final double petrolCarRunsKMperL = 15.5;
	public static final double DieselCarRunsKMperL = 18.2;
	public static final int totalKMRunByCars = 48084100;
	public static final double costOfElectricCarInKeuro = 18.690;
	public static final int electricCarLifeTime = 15;
	public static final double electricCarOperationalAndMaintanenceCost = 0.055; // 5.5 percent of Investment cost
																					// (costOfElectricCarInKeuro)

	
	/**
	 * Creates a new instance of problem ZDT1.
	 * 
	 * @param numberOfVariables
	 *            Number of variables.
	 * @param solutionType
	 *            The solution type must "Real", "BinaryReal, and "ArrayReal".
	 */
	public MaOOCEIS4D(String solutionType) {

		numberOfVariables_ = 6;
		numberOfObjectives_ = 4;
		numberOfConstraints_ = 1;
		/*
		 * at this moment, there are two objectives, 0 -> PV 1 -> Heat pump
		 */
		// numberOfConstraints_ = 3;

		problemName_ = "OptimizeEnergyPLANCivisCeDis";

		upperLimit_ = new double[numberOfVariables_];
		lowerLimit_ = new double[numberOfVariables_];

		// decision variables
		// index - 0 -> PV Capacity
		// index - 1 -> oil boiler heat percentage
		// index - 2 -> LPG boiler heat percentage
		// index - 3 -> Biomass boiler heat percentage
		// index - 4 -> Biomass micro chp heat percentage
		// last percentage will go to individual HP percentage
		// index - 5 -> electric car percentage

		// PV upper and lower limit
		lowerLimit_[0] = 5000.0;
		upperLimit_[0] = 42000.0;

		// other are the percentage from limit [0,1]
		for (int var = 1; var < numberOfVariables_; var++) {
			lowerLimit_[var] = 0.0;
			upperLimit_[var] = 1.0;
		} // for

		if (solutionType.compareTo("Real") == 0)
			solutionType_ = new RealSolutionType(this);
		else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
	} // constructor end

	@Override
	public MultiMap createModificationFiles(Solution solution, int serial) throws JMException {

		MultiMap modifyMap = new MultiValueMap();

		try {
			modifyMap = writeIntoInputFile(solution, ".\\modifiedInput" + serial

			);
		} catch (IOException e) {
			System.out.print("Pobrlem writting in modified Input file");
		}

		return modifyMap;
	}
	
	MultiMap writeIntoInputFile(final Solution s, String fileName) throws IOException, JMException {

	
		MultiMap modifyMap = new MultiValueMap();

	
		File modifiedInput = new File(".\\EnergyPLAN161\\spool\\"+fileName+".txt");
		if (modifiedInput.exists()) {
			modifiedInput.delete();
		}
		modifiedInput.createNewFile();

		FileWriter fw = new FileWriter(modifiedInput.getAbsoluteFile());
		BufferedWriter modifiedInputbw = new BufferedWriter(fw);
	
		String path = ".\\src\\EnergySystems\\GiudicarieEsteriori\\data\\CEIS_Complete_Current.txt";
	

		BufferedReader mainInputbr = new BufferedReader(new InputStreamReader(new FileInputStream(path), "UTF-16"));

		
		// PV
		double pv = s.getDecisionVariables()[0].getValue();
		modifyMap.put("PVCap", (int) s.getDecisionVariables()[0].getValue());


		// index - 1 -> oil boiler heat percentage
				// index - 2 -> LPG boiler heat percentage
				// index - 3 -> Biomass boiler heat percentage
				// index - 4 -> Biomass micro chp heat percentage
				// last percentage will go to individual HP percentage
		// Oil-boiler heat percentage
		// index - 5 -> electric car percentage  
		
		double percentages[] = new double[4];
		for (int i = 0; i < 4; i++) {
			percentages[i] = s.getDecisionVariables()[i + 1].getValue();
		}

	
		
		Arrays.sort(percentages);

		for (int i = 1; i < numberOfVariables_-1; i++) {
			s.getDecisionVariables()[i].setValue(percentages[i - 1]);
		}
		// oil-boiler percentage
		double oilBoilerHeatPercentage = percentages[0];
		// Ngas-boiler heat percentage
		double LPGBoilerHeatPercentage = percentages[1] - percentages[0];
		// biomass-boiler heat percentage
		double biomassBoilerHeatPercentage = percentages[2] - percentages[1];
		// ngas chp heat percentage
		double biomassCHPHeatPercentage = percentages[3] - percentages[2];

		// heat pump heat percentage
		double hpHeatPercentage = 1.0 - percentages[3];

		modifyMap.put("oilBoilerHeatPercentage", percentages[0]);
		modifyMap.put("LPGBoilerHeatPercentage", percentages[1] - percentages[0]);
		modifyMap.put("biomassBoilerHeatPercentage", percentages[2] - percentages[1]);
		modifyMap.put("biomassCHPHeatPercentage", percentages[3] - percentages[2]);
		modifyMap.put("hpHeatPercentage", 1.0 - percentages[3]);
		
		//electric car percentage
		double electricCarPercentage = s.getDecisionVariables()[5].getValue();
		modifyMap.put("electricCarPercentage", s.getDecisionVariables()[5].getValue());
		int reducedNumberOfPetrolCars = (int) Math.round(currentNumberOfPertrolCars - currentNumberOfPertrolCars * electricCarPercentage);
		int reducedNumberOfDieselCars = (int) Math.round(currentNumberOfDieselCars - currentNumberOfDieselCars * electricCarPercentage);
		double reducedPetrolDemandInGWh = reducedNumberOfPetrolCars * averageKMPerYearForPetrolCar * LCVPetrol / (petrolCarRunsKMperL*1000000);
		double reducedDieselDemandInGWh = reducedNumberOfDieselCars * averageKMPerYearForDieselCar * LCVDiesel / (DieselCarRunsKMperL*1000000);
		
		int elecCarRunKM = totalKMRunByCars - (reducedNumberOfPetrolCars * averageKMPerYearForPetrolCar) - (reducedNumberOfDieselCars * averageKMPerYearForDieselCar);
		double elecCarElectricityDemandInGWh =  elecCarRunKM * KWhPerKMElecCar / 1000000;
		double totalNumberOfElectricCars =  (int) Math.round(currentNumberOfPertrolCars + currentNumberOfDieselCars - reducedNumberOfPetrolCars - reducedNumberOfDieselCars); 
				
		String modifiedParameters[] = { "input_RES1_capacity=", // pv (0)
										"input_fuel_Households[2]=", //1
										"input_fuel_Households[3]=", //2
										"input_fuel_Households[4]=", //3
										"input_HH_BioCHP_heat=", //4
										"input_HH_HP_heat=", //5
										"input_transport_TWh=", //6
										"input_fuel_Transport[2]=", //7
										"input_fuel_Transport[5]=", //8
										"Filnavn_transport=" //9
									};
		String cooresPondingValues[] = new String[modifiedParameters.length];
		int index = 0;
		cooresPondingValues[index++] = "" + pv;
		cooresPondingValues[index++] = "" + oilBoilerHeatPercentage * totalHeatDemand / oilBoilerEfficiency;
		cooresPondingValues[index++] =""+ LPGBoilerHeatPercentage * totalHeatDemand / ngasBoilerEfficiency;
		cooresPondingValues[index++] ="" + biomassBoilerHeatPercentage * totalHeatDemand / biomassBoilerEfficiency;
		cooresPondingValues[index++] ="" + biomassCHPHeatPercentage * totalHeatDemand;
		cooresPondingValues[index++] ="" + hpHeatPercentage * totalHeatDemand;
		cooresPondingValues[index++] ="" + elecCarElectricityDemandInGWh;
		cooresPondingValues[index++] =""+reducedDieselDemandInGWh;
		cooresPondingValues[index++] =""+reducedPetrolDemandInGWh;
		cooresPondingValues[index] = "CIVIS_Transport_NC.txt";
		
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
		
		
	EnergyPLANFileParseForCivis epfp = new EnergyPLANFileParseForCivis(".\\EnergyPLAN161\\spool\\results\\modifiedInput"+serial+".txt.txt");
		energyplanmMap = epfp.parseFile();
		energyplanmMap.putAll(modifyMap);
		

		double percentages[] = new double[4];
		for (int i = 0; i < 4; i++) {
			percentages[i] = solution.getDecisionVariables()[i + 1].getValue();
		}

	
		
		Arrays.sort(percentages);

		for (int i = 1; i < numberOfVariables_-1; i++) {
			solution.getDecisionVariables()[i].setValue(percentages[i - 1]);
		}
		// oil-boiler percentage
		double oilBoilerHeatPercentage = percentages[0];
		// Ngas-boiler heat percentage
		double LPGBoilerHeatPercentage = percentages[1] - percentages[0];
		// biomass-boiler heat percentage
		double biomassBoilerHeatPercentage = percentages[2] - percentages[1];
		// ngas chp heat percentage
		double biomassCHPHeatPercentage = percentages[3] - percentages[2];

		// heat pump heat percentage
		double hpHeatPercentage = 1.0 - percentages[3];

		
		// electric car percentage
				double electricCarPercentage = solution.getDecisionVariables()[5].getValue();
				int reducedNumberOfPetrolCars = (int) Math
						.round(currentNumberOfPertrolCars - currentNumberOfPertrolCars * electricCarPercentage);
				int reducedNumberOfDieselCars = (int) Math
						.round(currentNumberOfDieselCars - currentNumberOfDieselCars * electricCarPercentage);
				double reducedPetrolDemandInGWh = reducedNumberOfPetrolCars * averageKMPerYearForPetrolCar * LCVPetrol
						/ (petrolCarRunsKMperL * 1000000);
				double reducedDieselDemandInGWh = reducedNumberOfDieselCars * averageKMPerYearForDieselCar * LCVDiesel
						/ (DieselCarRunsKMperL * 1000000);

				int elecCarRunKM = totalKMRunByCars - (reducedNumberOfPetrolCars * averageKMPerYearForPetrolCar)
						- (reducedNumberOfDieselCars * averageKMPerYearForDieselCar);
				double elecCarElectricityDemandInGWh = elecCarRunKM * KWhPerKMElecCar / 1000000;
				double totalNumberOfElectricCars = (int) Math.round(currentNumberOfPertrolCars + currentNumberOfDieselCars
						- reducedNumberOfPetrolCars - reducedNumberOfDieselCars);

		
		
		Iterator it;
		Collection<String> col;

		// objective # 1
		col = (Collection<String>) energyplanmMap
				.get("CO2-emission (corrected)");
		it = col.iterator();
		solution.setObjective(0, Double.parseDouble(it.next().toString()));
		// objective # 2
		col = (Collection<String>) energyplanmMap
				.get("Variable costs");
		              
		it = col.iterator();
		String totalVariableCostStr = it.next().toString();
			double totalVariableCost = Double.parseDouble(totalVariableCostStr);

		col = (Collection<String>) energyplanmMap
				.get("Fixed operation costs");
		it = col.iterator();
		String fixedOperationalCostStr = it.next().toString();
		double fixedOperationalCost = Double
				.parseDouble(fixedOperationalCostStr);

		col = (Collection<String>) energyplanmMap.get("AnnualHydroElectr.");
		it = col.iterator();
		double hydroPowerProduction = Double.parseDouble(it.next()
				.toString());
		// extract anual PV production
		col = (Collection<String>) energyplanmMap.get("AnnualPVElectr.");
		it = col.iterator();
		double PVproduction = Double.parseDouble(it.next().toString());
		
		//extract biogas production (it is named as wave power)
		col = (Collection<String>) energyplanmMap.get("AnnualWaveElectr.");
		it = col.iterator();
		double BiogasElecProduction = Double.parseDouble(it.next()
				.toString());

		// extract annual import
		col = (Collection<String>) energyplanmMap.get("AnnualImportElectr.");
		it = col.iterator();
		double Import = Double.parseDouble(it.next().toString());

		// extract annual export
		col = (Collection<String>) energyplanmMap.get("AnnualExportElectr.");
		it = col.iterator();
		double Export = Double.parseDouble(it.next().toString());

		//extract biomass CHP electricity production
		col = (Collection<String>) energyplanmMap.get("AnnualHH-CHPElectr.");
		it = col.iterator();
		double biomassCHPElecProduction = Double.parseDouble(it.next().toString());

		// calculate additional cost
		// (hydroProduction+PVproduction+Import-Export)*average additional
		// cost (85.74)
		double totalAdditionalCost = Math.round((hydroPowerProduction
				+ PVproduction + Import - Export + biomassCHPElecProduction)
				* addtionalCostPerGWhinKEuro);

		// new capacity of individual boilers
		/*
		 * double newHeatdemandForBoilers = (totalHeatDemand *
		 * oilBoilerHeatPercentage + totalHeatDemand *
		 * ngasBoilerHeatPercentage + totalHeatDemand *
		 * biomassBoilerHeatPercentage); double
		 * capacityOfBoilerforNewHeatDemand =
		 * Math.round(maxHeatDemandInDistribution *
		 * newHeatdemandForBoilers*Math.pow(10,
		 * 6)*1.5/sumOfAllHeatDistributions);
		 */

		double capacityOfHeatPump = Math.round((maxHeatDemandInDistribution
				* hpHeatPercentage * totalHeatDemand * Math.pow(10, 6))
				/ (COP * sumOfAllHeatDistributions));
		double geoBoreHoleInvestmentCost = (capacityOfHeatPump
				* geoBoreholeCostInKWe * interest)
				/ (1 - Math.pow((1 + interest), -geoBoreHoleLifeTime));

		// see annual investment cost formula in EnergyPLAN manual

		double newCapacityBiomassBoiler = Math
				.round((totalHeatDemand * biomassBoilerHeatPercentage)
						* Math.pow(10, 6) * 1.5 / sumOfAllHeatDistributions);
		double investmentCostReductionBiomassBoiler = 0.0;
		if (newCapacityBiomassBoiler > currentIndvBiomassBoilerCapacity) {
			investmentCostReductionBiomassBoiler = (currentIndvBiomassBoilerCapacity
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		} else {
			investmentCostReductionBiomassBoiler = (newCapacityBiomassBoiler
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		}

		double newCapacityOilBoiler = Math
				.round((totalHeatDemand * oilBoilerHeatPercentage)
						* Math.pow(10, 6) * 1.5 / sumOfAllHeatDistributions);
		double investmentCostReductionOilBoiler = 0.0;
		if (newCapacityOilBoiler > currentIndvOilBoilerCapacity) {
			investmentCostReductionOilBoiler = (currentIndvOilBoilerCapacity
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		} else {
			investmentCostReductionOilBoiler = (newCapacityOilBoiler 
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		}

		double newCapacityLPGBoiler = Math
				.round((totalHeatDemand * LPGBoilerHeatPercentage)
						* Math.pow(10, 6) * 1.5 / sumOfAllHeatDistributions);
		double investmentCostReductionLPGBoiler = 0.0;
		if (newCapacityLPGBoiler > currentIndvLPGBoilerCapacity) {
			investmentCostReductionLPGBoiler = (currentIndvLPGBoilerCapacity
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		} else {
			investmentCostReductionLPGBoiler = (newCapacityLPGBoiler
					* individualBoilerInvestmentCostInKEuro * interest)
					/ (1 - Math.pow((1 + interest), -boilerLifeTime));
		}

		double reductionInvestmentCost = (currentPVCapacity
				* PVInvestmentCostInKEuro * interest)
				/ (1 - Math.pow((1 + interest), -PVLifeTime))
				+ (currentHydroCapacity * hydroInvestmentCostInKEuro * interest)
				/ (1 - Math.pow((1 + interest), -HydroLifeTime))
				+ (currentBiogasCapacity * BiogasInvestmentCostInKEuro * interest)
				/ (1 - Math.pow((1 + interest), -BiogasLifeTime))
				+ investmentCostReductionBiomassBoiler
				+ investmentCostReductionOilBoiler
				+ investmentCostReductionLPGBoiler;

		// extract
		col = (Collection<String>) energyplanmMap
				.get("Annual Investment costs");
		it = col.iterator();
		String invest = it.next().toString();
		double investmentCost = Double.parseDouble(invest);
		double realInvestmentCost = investmentCost
				- reductionInvestmentCost + geoBoreHoleInvestmentCost;

		//Electric car related cost
		double totalInvestmentCostOfElectricCars = (totalNumberOfElectricCars * costOfElectricCarInKeuro * interest)/ (1 - Math.pow((1 + interest), -electricCarLifeTime));
		double totalFixOperationalAndInvestmentCostOfElectricCars = totalNumberOfElectricCars * costOfElectricCarInKeuro * electricCarOperationalAndMaintanenceCost ;
		
		double actualAnnualCost = totalVariableCost + fixedOperationalCost
				+ realInvestmentCost + totalAdditionalCost + totalInvestmentCostOfElectricCars + totalFixOperationalAndInvestmentCostOfElectricCars;

		solution.setObjective(1, actualAnnualCost);

		
		//3rd objective
		//Trasportation
		col = (Collection<String>) energyplanmMap
				.get("AnnualFlexibleElectr.");
		it = col.iterator();
		double transportElecDemand = Double.parseDouble(it.next().toString());
		
		
		col = (Collection<String>) energyplanmMap.get("AnnualElectr.Demand");
		it = col.iterator();
		double annualElecDemand = Double.parseDouble(it.next().toString());

		//Individual house HP electric demand
		col = (Collection<String>) energyplanmMap.get("AnnualHH-HPElectr.");
		it = col.iterator();
		double annualHPdemand = Double.parseDouble(it.next().toString());
		
		solution.setObjective(2, (Import + Export) / (annualElecDemand+transportElecDemand +annualHPdemand) );
		
		//extract ngas consuption
		col = (Collection<String>) energyplanmMap
				.get("Ngas Consumption");
		it = col.iterator();
		double nGasConsumption = Double.parseDouble(it.next().toString());
		
		//extract oil consumption
		col = (Collection<String>) energyplanmMap
				.get("Oil Consumption");
		it = col.iterator();
		double oilConsumption = Double.parseDouble(it.next().toString());
		
		//extract biomass consupmtion
		col = (Collection<String>) energyplanmMap.get("Biomass Consumption");
		it = col.iterator();
		double BiomassConsumption = Double.parseDouble(it.next().toString());
		
		
		double PVPEF = 1.0;
		double HYPEF = 1.0;
		double BioGasPEF = 1/0.262;
		double BiomassPEF = 1/0.18;
		double PEFImport = 2.17; 
		
		double totalPEForElecrcity = PVproduction * PVPEF + hydroPowerProduction * HYPEF + BiogasElecProduction*BioGasPEF + biomassCHPElecProduction * BiomassPEF;
		double totalLocalElecProduction = PVproduction + hydroPowerProduction + BiogasElecProduction+biomassCHPElecProduction;
		double PEFLocalElec = totalPEForElecrcity/ totalLocalElecProduction;
		
		double totalPEConsumtion = (totalLocalElecProduction - Export) * PEFLocalElec + Import * PEFImport + BiomassConsumption + oilConsumption + nGasConsumption
				+ (totalHeatDemand* hpHeatPercentage - totalHeatDemand* hpHeatPercentage/COP);
		
		double ESD = (Import * PEFImport + oilConsumption + nGasConsumption)/totalPEConsumtion;
		
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
					&& !warning
							.equals("Grid Stabilisation requierments are NOT fullfilled")
					&& !warning
							.equals("Critical Excess Electricity Production"))
				throw new JMException("warning!!" + warning);
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

		
		
	}

	
}
