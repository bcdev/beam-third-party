package uk.ac.ucl.mssl.climatephysics.utilities;

import java.util.Arrays;
import java.util.Comparator;

public class ArrayArgSort {
	private final double[] data;

	private class ArgSortComparator implements Comparator<Integer> {
		private double[] comparatorData;
		private Comparator<Double> comparator;
		public ArgSortComparator (double[] data, Comparator<Double> comparator){
			this.comparatorData = data;
			this.comparator = comparator;
		}
		public int compare(Integer a, Integer b){
			return comparator.compare(comparatorData[a], comparatorData[b]);
		}
	}
	public ArrayArgSort(double data[]){
		this.data = data;
	}

	public Integer[] indices() {
		// TODO this class should be available somewhere else, but could not find
		// TODO it right away
		class NaturalOrdering implements Comparator<Double> {
			public int compare(Double a, Double b){
				return a.compareTo(b);
			}
		}
		return indices(new NaturalOrdering());
	}

	public Integer[] indicesReversed() {
		// TODO this class should be available somewhere else, but could not find
		// TODO it right away
		class ReverseNaturalOrdering implements Comparator<Double> {
			public int compare(Double a, Double b) {
				return a.compareTo(b) * -1;
			}
		}
		return indices(new ReverseNaturalOrdering());
	}

	public Integer[] indices(Comparator<Double> comparator) {
		Integer[] indices = new Integer[data.length];
		for (int i = 0; i < indices.length; i++){
			indices[i] = i;
		}
		ArgSortComparator argSortComparator = new ArgSortComparator(data, comparator);
		Arrays.sort(indices, 0, indices.length, argSortComparator);
		return indices;
	}
}
