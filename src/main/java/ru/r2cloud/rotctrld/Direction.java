package ru.r2cloud.rotctrld;

public enum Direction {

	UP(2), DOWN(4), LEFT(8), RIGHT(16);

	private final int code;

	private Direction(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

}
