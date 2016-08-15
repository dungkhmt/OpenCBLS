package localsearch.model;

import java.util.Set;
import java.util.TreeSet;

import core.VarInt;

public class VarIntLS extends VarInt {

	/**
	 * @param args
	 */
	private LocalSearchManager _mgr;
	private int _oldValue;
	private TreeSet<Integer> _domain;
	
	public VarIntLS(LocalSearchManager mgr, int min, int max){
		super(min,max);
		_mgr = mgr;
		_domain = new TreeSet<Integer>();
		for(int  v = min; v <= max; v++)
			_domain.add(v);
		_mgr.post(this);
	}
	private int getMin(TreeSet<Integer> S){
		int min = 0;
		boolean first = true;
		for(int v : S){
			if(first){
				min = v; first = false;
			} else min = min < v ? min : v;
		}
		return min;
	}
	private int getMax(TreeSet<Integer> S){
		int max = 0;
		boolean first = true;
		for(int v : S){
			if(first){
				max = v; first = false;
			} else max = max > v ? max : v;
		}
		return max;
	}
	public VarIntLS(LocalSearchManager mgr, Set<Integer> domain){
		super(1,0);
		_mgr = mgr;
		_domain = new TreeSet<Integer>();
		int min = 0;
		int max = 0;
		for(int v : domain){
			min = v; max = v; break;
		}
		for(int v : domain){
			_domain.add(v);
			min = min < v ? min : v;
			max = max > v ? max : v;
		}
		initBound(min,max);
		_mgr.post(this);
	}
	public TreeSet<Integer> getDomain(){
		return _domain;
	}
	public LocalSearchManager getLocalSearchManager(){ return this._mgr;}
	
	public void setValuePropagate(int v){
		_oldValue = getValue();
		super.setValue(v);
		if(_mgr.closed())
			_mgr.propagateInt(this, v);
	}
	public void swapValuePropagate(VarIntLS y){
		int tv = this.getValue();
		setValuePropagate(y.getValue());
		y.setValuePropagate(tv);
	}
	public int getOldValue(){
		return _oldValue;
	}
		
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


}
