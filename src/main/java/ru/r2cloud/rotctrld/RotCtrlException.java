package ru.r2cloud.rotctrld;

public class RotCtrlException extends Exception {

	private static final long serialVersionUID = 3333646721512353704L;
	
	private final int code;

	public RotCtrlException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
}
