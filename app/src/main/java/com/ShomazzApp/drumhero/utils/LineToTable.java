package com.ShomazzApp.drumhero.utils;

public class LineToTable {

	public int numberOfLine;
	public double startSecond;

	public LineToTable(int numberOfLine, double startSecond) {
		this.numberOfLine = numberOfLine;
		this.startSecond = startSecond;
	}

	public double getStartSecond() {
		return startSecond;
	}

	public String getLineString() {
		String s = numberOfLine + "		" + startSecond;
		return s;
	}

}
