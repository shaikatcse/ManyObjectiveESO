package EnergySystems.Aalborg.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.commons.collections.MultiMap;

import EnergySystems.Aalborg.parse.EnergyPLANFileParseForAalborg;
import jmetal.core.Solution;
import jmetal.util.JMException;


public class ExtractAllParameters {
	
	
	static String inputs[] = { "wind", "offshoreWind", "pv",
			"chp3", "boiler3", "hp3" };
	
	static MultiMap simulateEnergyPLAN(String individual) throws JMException {
		MultiMap energyplanmMap = null;
		// index - 0 -> CHP3
				// index - 1 -> HP3
				// index - 2 -> PP
				// index - 3 -> wind
				// index - 4 -> offshore wind
				// index - 5 -> PV
				// index - 6 -> boiler3
		
		
		
		StringTokenizer st = new StringTokenizer(individual);
		
		double chp3 =  Double.parseDouble(st.nextToken().toString());
		double hp3 = Double.parseDouble(st.nextToken().toString());
		double pp = Double.parseDouble(st.nextToken().toString());
		double wind = Double.parseDouble(st.nextToken().toString());
		double offshoreWind = Double.parseDouble(st.nextToken().toString());
		double pv = Double.parseDouble(st.nextToken().toString());
		double boiler3 = Double.parseDouble(st.nextToken().toString());
		writeModificationFile( chp3,  hp3, pp,  wind,  offshoreWind, 
				 pv,  boiler3);
		
		try {
			
			String energyPLANrunCommand = ".\\EnergyPLAN15.1\\EnergyPLAN.exe -i "
					+ "\".\\EnergyPLAN15.1\\energyPlan Data\\Data\\Aalborg_2050_Plan_A_44%ForOptimization_2objctives.txt\" "
					+ "-m \"modification.txt\" -ascii \"result.txt\" ";

			Process process = Runtime.getRuntime().exec(energyPLANrunCommand);
			process.waitFor();
			process.destroy();
			EnergyPLANFileParseForAalborg epfp = new EnergyPLANFileParseForAalborg(".\\result.txt");
			energyplanmMap = epfp.parseFile();
			
			energyplanmMap.put("wind", wind);
			energyplanmMap.put("offshoreWind", offshoreWind);
			energyplanmMap.put("pv", pv);
			energyplanmMap.put("chp3", chp3);
			energyplanmMap.put("boiler3", boiler3);
			energyplanmMap.put("hp3", hp3);
			
			energyplanmMap.put("LFC", calculateThirdObjective(energyplanmMap));
			energyplanmMap.put("ESD", calculateForthObective(energyplanmMap));
			
		} catch (InterruptedException e) {
			System.out.println("Energyplan interrupted");
		}catch(IOException e) {
			e.printStackTrace();
		}

		return energyplanmMap; 
	}
	
	static double calculateForthObective(MultiMap energyplanmMap) {
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
	
	static double calculateThirdObjective(MultiMap energyplanmMap ) {
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

		
		static void writeModificationFile(double chp3, double hp3, double pp, double wind, double offshoreWind, 
				double pv, double boiler3) throws JMException {

		
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
						str = "" + (int) Math.round(chp3);
						bw.write(str);
						bw.newLine();

						str = "input_cap_hp3_el=";
						bw.write(str);
						bw.newLine();
						// str = "" + (int) Math.round(HPGr3);
						str = "" + (int) Math.round(hp3);
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
						str = "" + (int) Math.round(offshoreWind);
						bw.write(str);
						bw.newLine();

						str = "input_RES3_capacity=";
						bw.write(str);
						bw.newLine();
						// str = "" + (int) Math.round(PV);
						str = "" + (int) Math.round(pv);
						bw.write(str);
						bw.newLine();


						str = "input_cap_boiler3_th=";
						bw.write(str);
						bw.newLine();
						// str = "" + (int) Math.round(modification);
						str = "" + (int) Math.round(boiler3);
						bw.write(str);
						bw.newLine();

						str = "input_cap_pp_el=";
						bw.write(str);
						bw.newLine();
						str = "" + (int) Math.round(pp);
						bw.write(str);
						bw.newLine();

						bw.close();
						// file.delete();
					} catch (IOException e) {
						e.printStackTrace();
					}

		}

		static void WriteEnergyPLANParametersToFile(ArrayList <MultiMap> multiMapArray, String path)
				throws IOException {
			Iterator it;
			Collection<String> col;

			File filetmp = new File(path);
			String arrayStr[] = path.split("\\\\");

			File file = new File(filetmp.getParent() + "\\allParameters_"
					+ arrayStr[arrayStr.length - 1] + ".txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// create header of the file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
		
			//create an key array
			ArrayList<String> keys = new ArrayList<String>();
			for(int i=0;i<inputs.length;i++) {
				keys.add(inputs[i]);
			}
			String [] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
			
			for (Object name: multiMapArray.get(0).keySet()) {
			    String key = name.toString();
			    boolean track = true;
			    for(String month: months) {
			    	if(key.startsWith(month)) {
			    		track = false;
			    		break;
			    	}
			    }
			    if(track)
			    	keys.add(key);
			}
			
			String headings = "";
			for (Object s: keys) {
				headings +=  s + "\t";
			}
			bw.write(headings);

			bw.newLine();
			
			
			for(MultiMap mm: multiMapArray) {
				String values="";
				for(String s: keys) {
					
					String ss = mm.get(s).toString();
					values += ss.replaceAll("\\[", "").replaceAll("\\]","") +"\t";
				}
				bw.write(values);

				bw.newLine();

			}
			
			//bw.write(values);

			bw.newLine();
			bw.close();
						
		}

	public static void main(String[] args) throws IOException, JMException {
		
		ArrayList<MultiMap> multiMapArray = new ArrayList<MultiMap>();
		
		
		
		MultiMap energyplanmMap = null;
		// TODO Auto-generated method stub
		FileInputStream fos = new FileInputStream(args[0]);
		InputStreamReader isr = new InputStreamReader(fos);
		BufferedReader br = new BufferedReader(isr);

		String line;
		
		while ((line = br.readLine()) != null) {
			energyplanmMap = simulateEnergyPLAN(line);
			multiMapArray.add(energyplanmMap);
			
		}
		
		WriteEnergyPLANParametersToFile(multiMapArray, args[0]);
		

		br.close();

	}

}
