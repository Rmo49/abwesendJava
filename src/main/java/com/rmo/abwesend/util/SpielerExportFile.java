package com.rmo.abwesend.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import com.rmo.abwesend.model.Spieler;
import com.rmo.abwesend.model.SpielerData;
import com.rmo.abwesend.model.SpielerTableauData;
import com.rmo.abwesend.model.Tableau;

/**
 * Spieler in ein File schreiben.
 * 
 * @author ruedi
 *
 */
public class SpielerExportFile {

	// maximale Anzahl Tableau wo sich ein Spieler anmelden kann.
	public SpielerExportFile() {
	}

	/**
	 * Exportieren Zeile um Zeile. Wenn true zurück gibt, dann keine Fehler
	 * gefunden, sonst Fehler in Trace
	 */
	public boolean startExport(String dirName, String fileName) throws Exception {
		Trace.println(3, "SpielerExportFile.startExport()");
		boolean fehler = false;
		String dirFile = dirName + "/" + fileName;
		BufferedWriter out = new BufferedWriter(new FileWriter(dirFile));

		StringBuffer line = new StringBuffer(80);
		List<Spieler> spielerList = SpielerData.instance().readAllSpieler();
		for (Spieler lSpieler : spielerList) {
			line.append(lSpieler.getName());
			line.append(",");
			line.append(lSpieler.getVorName());
			line.append(",");
			List<Tableau> tableauList = SpielerTableauData.instance().readAllSpielerTableau(lSpieler.getId());
			Iterator<Tableau> iter = tableauList.iterator();
			while (iter.hasNext()) {
				line.append(iter.next().getBezeichnung());
				line.append(",");
			}
			line.append("\r\n");
			out.write(line.toString());
			line.delete(0, line.length());
		}
		out.close();
		Trace.println(3, "SpielerExportFile.startExport() <<< End");
		return fehler;
	}

}
