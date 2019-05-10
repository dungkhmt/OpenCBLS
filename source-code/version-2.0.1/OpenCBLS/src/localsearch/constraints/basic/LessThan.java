package localsearch.constraints.basic;

import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.model.*;

public class LessThan extends AbstractInvariant implements IConstraint {
	private VarIntLS[] _vars;
	private VarIntLS _x;
	private VarIntLS _y;
	private int _violations;
	private LocalSearchManager _ls;
	
	public LessThan(IFunction fx, IFunction fy){
		
	}
	public LessThan(VarIntLS x, VarIntLS y){
		this._x = x;
		this._y = y;
		_ls = _x.getLocalSearchManager();
		_vars = new VarIntLS[2];
		_vars[0] = x; _vars[1] = y;
		_ls.post(this);
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _vars;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		//System.out.println("LessThan::propagateInt");
		if(x != _x && x != _y) return;
		if(_x.getValue() < _y.getValue()) _violations = 0;
		else _violations = _x.getValue() - _y.getValue() + 1;
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		//System.out.println("LessThan::initPropagate");
		if(_x.getValue() < _y.getValue()) _violations = 0;
		else{
			_violations = _x.getValue() - _y.getValue() + 1;
		}
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv = 0;
		if(_x.getValue() >= _y.getValue())
			nv = _x.getValue() - _y.getValue() + 1;
		return nv == _violations;
	}

	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violations;
	}

	@Override
	public int violations(VarIntLS x) {
		// TODO Auto-generated method stub
		if(x == _x || x == _y) return _violations;
		return 0;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if(x != _x && x != _y) return 0;
		int nv = 0;
		if(x == _x){
			if(val >= _y.getValue()) nv = val - _y.getValue() + 1;
		}else{
			if(_x.getValue() >= val) nv = _x.getValue() - val + 1;
		}
		return nv - _violations;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(x != _x && x != _y) return getAssignDelta(y,x.getValue());
		if(y != _x && y != _y) return getAssignDelta(x,y.getValue());
		if(x == _y && y == _x){
			VarIntLS tmp = x; x = y; y = tmp;
		}
		// x = _x && y = _y
		int nv = 0;
		if(_y.getValue() >= _x.getValue())
			nv = _y.getValue() - _x.getValue() + 1;
		return nv - _violations;
	}
	public static void main(String[] args){
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS x = new VarIntLS(ls,1,10);
		VarIntLS y = new VarIntLS(ls,1,10);
		VarIntLS z = new VarIntLS(ls,1,10);
		ConstraintSystem S = new ConstraintSystem(ls);
		S.post(new LessThan(x,y));
		ls.close();
		x.setValuePropagate(3);
		y.setValuePropagate(1);
		System.out.println("S = " + S.violations());
		int d = S.getSwapDelta(x, y);
		x.swapValuePropagate(y);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		d = S.getAssignDelta(z, 7);
		z.setValuePropagate(7);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		d = S.getSwapDelta(x, z);
		x.swapValuePropagate(z);
		System.out.println("Delta = " + d + ", S = " + S.violations());
		
		
	}
}

