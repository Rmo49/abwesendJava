package com.rmo.abwesend.model;

import java.util.List;

/**
 * Die Beziehung von einem Spieler zu mehreren Tableaux.
 * @author Ruedi
 *
 */
public class SpielerTableau {

	private int spielerId = -1;			// die ID des Spielers
	private List<Integer> tableauList;

	public int getSpielerId() {
		return spielerId;
	}
	public void setSpielerId(int spielerId) {
		this.spielerId = spielerId;
	}
	public List<Integer> getTableauList() {
		return tableauList;
	}
	public void setTableauList(List<Integer> tableauList) {
		this.tableauList = tableauList;
	}



}
