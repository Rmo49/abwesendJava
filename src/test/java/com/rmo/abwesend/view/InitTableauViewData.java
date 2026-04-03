package com.rmo.abwesend.view;

import com.rmo.abwesend.model.Tableau;
import com.rmo.abwesend.model.TableauData;
import com.rmo.abwesend.util.Config;

/**
 * Initialisiert die Tableaus, damit die nicht von Hand eingegeben werden muss.
 *
 * @author Ruedi
 *
 */
public class InitTableauViewData {

	/**
	 * Test ein Tableau dazufügen, Daten normal
	 */
//	@BeforeAll
	static void testAddMany() {
		Config.setConnectionToLocal();
	}

//	@Test
	void addAllTableau() {
		addTableau("MS R1/R6");
		addTableau("MS R1/R9 (Retourné)");
		addTableau("MS R7/R9 (inkl. NC)");
		addTableau("MS 45+ R1/R6");
		addTableau("MS 45+ R7/R9");
		addTableau("WS R1/R9");
		addTableau("WS R1/R9 (Retourné)");
		addTableau("WS 40+ R1/R9");
		addTableau("MD R1/R9 (7-13)");
		addTableau("MD R1/R9 (Retourné)");
		addTableau("MD R4/R9 (14-20)");
		addTableau("WD R1/R9 (7-13)");
		addTableau("WD R1/R9 (Retourné)");
		addTableau("WD R4/R9 (14-20)");
		addTableau("DM R1/R9 (7-13)");
		addTableau("DM R1/R9 (Retourné)");
		addTableau("DM R4/R9 (14-20)");
	}

//	@Test
	void addTableau(String bezeichnung) {
		Tableau tableau = new Tableau();
		tableau.setId(-1);
		tableau.setBezeichnung(bezeichnung);
		try {
			TableauData.instance().add(tableau);
		} catch (Exception ex) {
			// nix tun, ist ok wenns schon hat
		}
	}

}
