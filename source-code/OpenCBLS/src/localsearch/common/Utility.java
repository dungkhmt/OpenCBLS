package localsearch.common;

import java.util.Set;

public class Utility {

	public static boolean equalSet(Set<Integer> S1, Set<Integer> S2){
		for(int i : S1)
			if(!S2.contains(i)) return false;
		for(int i : S2)
			if(!S1.contains(i)) return false;
		return true;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
