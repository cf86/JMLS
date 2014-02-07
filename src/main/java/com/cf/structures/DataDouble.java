package com.cf.structures;

public class DataDouble<A, B> {

	/**
	 * first element
	 */
	private A first;

	/**
	 * second element
	 */
	private B second;

	/**
	 * generates a double structure
	 * 
	 * @param first
	 *            first element
	 * @param second
	 *            second element
	 */
	public DataDouble(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * gets the first element
	 * 
	 * @return the element
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * sets the first element
	 * 
	 * @param first
	 *            the element
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * gets the second element
	 * 
	 * @return the element
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * sets the second element
	 * 
	 * @param second
	 *            the element
	 */
	public void setSecond(B second) {
		this.second = second;
	}
}