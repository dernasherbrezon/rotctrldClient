package ru.r2cloud.rotctrld;

public class Position {

	private double azimuth;
	private double elevation;

	public double getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(double azimuth) {
		this.azimuth = azimuth;
	}

	public double getElevation() {
		return elevation;
	}

	public void setElevation(double elevation) {
		this.elevation = elevation;
	}

	@Override
	public String toString() {
		return "Position [azimuth=" + azimuth + ", elevation=" + elevation + "]";
	}

}
