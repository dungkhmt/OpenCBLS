package localsearch.selectors;

import localsearch.model.*;
import java.util.*;
public class MinMaxSelector {

	/**
	 * @param args
	 */
	
	private IConstraint _S;
	private VarIntLS[]	_vars;
	private int			_minValue;
	private int			_maxValue;
	private Random		_R;
	private ArrayList<Integer>   _L;
	public MinMaxSelector(IConstraint S){
		_S = S;
		_vars = _S.getVariables();
		
		_R = new Random();
		_L = new ArrayList<Integer>();
		
		_minValue = 100000000;
		_maxValue = -_minValue;
		for(int i = 0; i < _vars.length; i++){
			if(_minValue > _vars[i].getMinValue()) _minValue = _vars[i].getMinValue();
			if(_maxValue < _vars[i].getMaxValue()) _maxValue = _vars[i].getMaxValue();
		}
	}
	public VarIntLS selectMostViolatingVariable(){
		int sel_i = -1;
		int maxV = -1;
		_L.clear();
		for(int i = 0; i < _vars.length; i++){
			int v = _S.violations(_vars[i]);
			if(maxV < v){
				maxV = v;
				_L.clear();
				_L.add(i);
			}else if(maxV == v){
				_L.add(i);
			}
		}
		sel_i = _L.get(_R.nextInt(_L.size()));
		return _vars[sel_i];
	}
	public int selectMostPromissingValue(VarIntLS x){
		int sel_v = -1;
		int minD = 10000000;
		_L.clear();
		for(int v = x.getMinValue(); v <= x.getMaxValue(); v++){
			int d = _S.getAssignDelta(x, v);
			if(minD > d){
				minD = d;
				_L.clear();
				_L.add(v);
			}else if(minD == d){
				_L.add(v);
			}
		}
		
		sel_v = _L.get(_R.nextInt(_L.size()));
		return sel_v;
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
