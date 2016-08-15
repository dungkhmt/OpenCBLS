package localsearch.functions.max_min;

import java.util.Comparator;

public class Pair {
	int first;
	int second;
	
	public Pair(int first, int second) {
		this.first = first;
		this.second = second;
	}
}

class CompareMaxPair implements Comparator<Pair> {

	@Override
	public int compare(Pair o1, Pair o2) {
		return (o1.first != o2.first) ? o2.first - o1.first : o1.second - o2.second;
	}
	
}

class CompareMinPair implements Comparator<Pair> {

	@Override
	public int compare(Pair o1, Pair o2) {
		return (o1.first != o2.first) ? o1.first - o2.first : o1.second - o2.second;
	}
	
}
