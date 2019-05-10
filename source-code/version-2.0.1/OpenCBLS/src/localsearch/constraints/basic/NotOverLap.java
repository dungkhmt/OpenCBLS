package localsearch.constraints.basic;

import localsearch.model.*;
import java.util.HashMap;
public class NotOverLap extends AbstractInvariant implements IConstraint {
	private VarIntLS[] _vars;
	private VarIntLS	_x;
	private VarIntLS	_y;
	private int _lx;
	private int _ly;
	private int	_violations;
	private LocalSearchManager	_lsm;
	private HashMap<VarIntLS, Integer> _map;
	
	public NotOverLap(VarIntLS x, int lx, VarIntLS y, int ly){
		// semantic: [x,x+lx] and [y,y+ly] are not overlap
		_x = x;
		_y = y;
		_lx = lx;
		_ly = ly;
		_lsm = x.getLocalSearchManager();
		
		post();
		
		
	}
	private void post(){
		_vars = new VarIntLS[2];
		_vars[0] = _x;
		_vars[1] = _y;
		_map = new HashMap<VarIntLS, Integer>();
		for(int i = 0; i < 2; i++)
			_map.put(_vars[i], i);
		_lsm.post(this);
	}
	public LocalSearchManager getLocalSearchManager(){
		return _lsm;
	}
	public VarIntLS[] getVariables(){
		return _vars;
	}
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null) return 0;
		return _violations;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		int newV = 0;
		if(x == _x){
			if(x.getValue() == val) return 0;
			if(val <= _y.getValue() && _y.getValue() <= val + _lx)
				newV = Math.min(val + _lx - _y.getValue(), _ly);
			else if(_y.getValue() <= val && val <= _y.getValue() + _ly)
				newV = Math.min(_y.getValue() + _ly - val, _lx);			
		}else if(x == _y){
			if(x.getValue() == val) return 0;
			if(_x.getValue() <= val && val <= _x.getValue() + _lx)
				newV = Math.min(_x.getValue() + _lx - val, _ly);
			else if(val <= _x.getValue() && _x.getValue() <= val + _ly)
				newV = Math.min(val + _ly - _x.getValue(), _lx);			
		}
		return newV - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(_map.get(x) == null && _map.get(y) == null) return 0;
		if(_map.get(x) == null && _map.get(y) != null) return getAssignDelta(y,x.getValue());
		if(_map.get(x) != null && _map.get(y) == null) return getAssignDelta(x,y.getValue());
		
		//otherwise, both x and y are present in the constraint
		int newViolations = 0;
		int vx = y.getValue();
		int vy = x.getValue();
		if(vx <= vy && vy <= vx + _lx)
			newViolations = Math.min(vx + _lx - vy, _ly);
		else if(vy <= vx && vx <= vy + _ly)
			newViolations = Math.min(vy + _ly - vx, _lx);
		return newViolations - _violations;
	}

	public void propagateInt(VarIntLS x, int val){
		if(x == _x || _y == x){
			_violations = 0;
			if(_x.getValue() <= _y.getValue() && _y.getValue() <= _x.getValue() + _lx)
				_violations = Math.min(_x.getValue() + _lx - _y.getValue(), _ly);
			else if(_y.getValue() <= _x.getValue() && _x.getValue() <= _y.getValue() + _ly)
				_violations = Math.min(_y.getValue() + _ly - _x.getValue(), _lx);
		}
	}
	public void initPropagate() {
		// TODO Auto-generated method stub
		_violations = 0;
		if(_x.getValue() <= _y.getValue() && _y.getValue() <= _x.getValue() + _lx)
			_violations = Math.min(_x.getValue() + _lx - _y.getValue(), _ly);
		else if(_y.getValue() <= _x.getValue() && _x.getValue() <= _y.getValue() + _ly)
			_violations = Math.min(_y.getValue() + _ly - _x.getValue(), _lx);
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int V = 0;
		if(_x.getValue() <= _y.getValue() && _y.getValue() <= _x.getValue() + _lx)
			V = Math.min(_x.getValue() + _lx - _y.getValue(), _ly);
		else if(_y.getValue() <= _x.getValue() && _x.getValue() <= _y.getValue() + _ly)
			V = Math.min(_y.getValue() + _ly - _x.getValue(), _lx);
		if(V != _violations){
			System.out.println("NotOverlap::verify --> failed, _violations = " + violations() + ", but when recomputing, V = " + V);
		}
		return V == _violations;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
