package EnergySystems.Aalborg.OptimizeEnergyPLANAalborg.parse;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public class EnergyPLANFileParseForAalborg{

	BufferedReader br = null;
	MultiMap multiMap = new MultiValueMap();

	public EnergyPLANFileParseForAalborg(String filePath) {
		try {
			br = new BufferedReader(new FileReader(filePath));
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		}
	}

	public MultiMap parseFile() {

		try {

			String line;
			boolean trackAnnualCost = false;

			
			//read 2 lines starting with "ANNUAL CO2 EMISSIONS (kt):"
			while (!(line = br.readLine()).startsWith("ANNUAL CO2 EMISSIONS (Mt):") ) {
				;
			}
			
			//1st line
			line = br.readLine();
			String StringSplit[] = line.split("\t");
			String tmpKey=StringSplit[0].trim(); String tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			//2nd line
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			
			//read 3 lines starting with "SHARE OF RES (incl. Biomass):"
			while (!(line = br.readLine()).startsWith("SHARE OF RES (incl. Biomass):") ) {
				;
			}
			
			//1st line
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			//2nd line
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			//3rd line
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			
			//read 12 lines starting with "ANNUAL FUEL CONSUMPTIONS (TWh/year)"
			while (!(line = br.readLine()).startsWith("ANNUAL FUEL CONSUMPTIONS (TWh/year)") ) {
				;
			}
			for(int i=0;i<12;i++){
				line = br.readLine();
				StringSplit = line.split("\t");
				tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
				multiMap.put(tmpKey, tmpValue);
								
			}
			
			//read 9 lines starting with "ANNUAL COSTS (k EUR)"
			while (!(line = br.readLine()).startsWith("ANNUAL COSTS (M DKK)") ) {
				;
			}
			for(int i=0;i<9;i++){
				line = br.readLine();
				StringSplit = line.split("\t");
				tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
				multiMap.put(tmpKey, tmpValue);
			}
			
			//read "Ngas Exchange costs"
			while (!(line = br.readLine()).startsWith("Ngas Exchange costs") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[2].trim();
			multiMap.put(tmpKey, tmpValue);
		
			//read "Electricity exchange"
			while (!(line = br.readLine()).startsWith("Electricity exchange") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[2].trim();
			multiMap.put(tmpKey, tmpValue);
			//read import
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[3].trim();
			multiMap.put(tmpKey, tmpValue);
			//read export
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[3].trim();
			multiMap.put(tmpKey, tmpValue);
			//read Bottleneck
			line = br.readLine();
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[3].trim();
			multiMap.put(tmpKey, tmpValue);
			
			
			//read "Variable costs"
			while (!(line = br.readLine()).startsWith("Variable costs") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			//read "Fixed operation costs"
			while (!(line = br.readLine()).startsWith("Fixed operation costs") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			
			//read "Annual Investment costs"
			while (!(line = br.readLine()).startsWith("Annual Investment costs") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			
			//read "TOTAL ANNUAL COSTS"
			while (!(line = br.readLine()).startsWith("TOTAL ANNUAL COSTS") ) {
				;
			}
			StringSplit = line.split("\t");
			tmpKey=StringSplit[0].trim(); tmpValue = StringSplit[1].trim();
			multiMap.put(tmpKey, tmpValue);
			
			//skip 12 lines
			for(int i=0;i<12;i++){
				String temp = br.readLine();
			}
		
			String line1 = br.readLine();
			String line2 = br.readLine();

			String l1[] = line1.split("\t");
			String l2[] = line2.split("\t");

			for (int i = 0; i < l1.length; i++) {
				l1[i] = l1[i].trim();
				l2[i] = l2[i].trim();

			}
			while (!(line = br.readLine()).equals("TOTAL FOR ONE YEAR (TWh/year):")) {
				;
			}
			// read annual line
			line = br.readLine();
			String lineTmp[] = line.split("\t");
			for (int i = 0; i < lineTmp.length; i++) {
				lineTmp[i] = lineTmp[i].trim();
			}

			for (int i = 1; i < lineTmp.length; i++) {
				String key = "Annual" + l1[i] + l2[i];
				String value = lineTmp[i];
				multiMap.put(key, value);

			}

			// read monthly line
			line = br.readLine();
			line = br.readLine();

			for (int j = 0; j < 12; j++) {
				line = br.readLine();
				lineTmp = line.split("\t");
				for (int i = 0; i < lineTmp.length; i++) {
					lineTmp[i] = lineTmp[i].trim();
				}
				for (int i = 1; i < lineTmp.length; i++) {
					String key = lineTmp[0] + l1[i] + l2[i];
					String value = lineTmp[i];
					multiMap.put(key, value);

				}
			}
			
			//read average, maximum, minimum
			line = br.readLine();
			for (int j = 0; j < 3; j++) {
				line = br.readLine();
				lineTmp = line.split("\t");
				for (int i = 0; i < lineTmp.length; i++) {
					lineTmp[i] = lineTmp[i].trim();
				}
				for (int i = 1; i < lineTmp.length; i++) {
					String key = lineTmp[0] + l1[i] + l2[i];
					String value = lineTmp[i];
					multiMap.put(key, value);

				}
			}
			
			
			//read hour values
			/*line = br.readLine();
			line = br.readLine();
			for (int j = 0; j < 8784; j++) {
				line = br.readLine();
				lineTmp = line.split("\0");
				for (int i = 0; i < lineTmp.length; i++) {
					lineTmp[i] = lineTmp[i].trim();
				}
				for (int i = 1; i < lineTmp.length; i++) {
					String key = lineTmp[0] + l1[i] + l2[i];
					String value = lineTmp[i];
					multiMap.put(key, value);

				}
			}*/
			

			//additional calculation
			
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return multiMap;
	}
	
	public static void main(String args[]){
		MultiMap energyplanmMap;
		EnergyPLANFileParseForAalborg epfp = new EnergyPLANFileParseForAalborg(".\\result.txt");
		energyplanmMap = epfp.parseFile();
	}

}