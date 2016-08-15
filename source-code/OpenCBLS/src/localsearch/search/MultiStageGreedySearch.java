package localsearch.search;

import java.util.HashMap;

import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;
public class MultiStageGreedySearch {

	/**
	 * @param args
	 */
	
	public String name(){
		return "MultiStageGreedySearch";
	}
	public void search(IConstraint S, int maxTime, int maxIter, boolean verbose){
		VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < x.length; i++) map.put(x[i], i);
		
		int it = 0;
		maxTime = maxTime * 1000;
		double t0 = System.currentTimeMillis();
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int best = S.violations();
		int[] x_best = new int[x.length];
		while(it < maxIter && System.currentTimeMillis() - t0 < maxTime){
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			if(verbose)
				System.out.println(name() + "::search --> Step " + it + ", x[" + map.get(sel_x) + "] := " + sel_v + ", S = " + S.violations());
			
			if(S.violations() < best){
				best = S.violations();
				for(int i = 0; i < x.length; i++)
					x_best[i] = x[i].getValue();
			}
			it++;
		}
		
		for(int i = 0; i < x.length; i++)
			x[i].setValuePropagate(x_best[i]);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
