package com.ShomazzApp.drumhero.game;

public class Vector {
	public float x, y;

	public Vector(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector() {
	}

	public Vector set(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector set(Vector to) {
		this.x = to.x;
		this.y = to.y;
		return this;
	}

	public Vector add(Vector what) {
		this.x += what.x;
		this.y += what.y;
		return this;
	}

	public Vector sub(float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}

	public Vector scale(float scl) {
		this.x *= scl;
		this.y *= scl;
		return this;
	}

	public float dst2(Vector to) {
		return (float) (Math.pow(to.x - x, 2) + Math.pow(to.y - y, 2));
	}

	public Vector norm() {
	    double d = Math.sqrt(dst2(new Vector(0, 0)));
	    x /= d;
	    y /= d;
		return this;
	}
}
