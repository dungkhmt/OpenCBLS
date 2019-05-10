package localsearch.functions.sum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.model.AbstractInvariant;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class SumVarConstraints extends AbstractInvariant implements IFunction {
	private int _value;
	private int _maxValue;
	private int _minValue;
	private IConstraint[] _c;
	private VarIntLS[] _x1;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private HashMap<VarIntLS, Vector<IConstraint>> _map;
	private HashMap<VarIntLS, Integer> _map1;
	private HashMap<VarIntLS, Integer> _map2;
	private HashMap<IConstraint, Integer> _map3;

	private int[] _a;

	public SumVarConstraints(VarIntLS[] x, IConstraint[] c) {
		// semantic: \sum_{i = 0..f.length-1}x[i]*(c[i].violations() == 0)
		_x1 = x;
		_c = c;
		_ls = c[0].getLocalSearchManager();
		post();
	}

	void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for (int i = 0; i < _c.length; i++) {
			VarIntLS[] c_x = _c[i].getVariables();
			if (c_x != null) {
				for (int j = 0; j < c_x.length; j++) {
					_S.add(c_x[j]);
				}
			}
		}
		_map = new HashMap<VarIntLS, Vector<IConstraint>>();
		for (VarIntLS e : _S) {
			_map.put(e, new Vector<IConstraint>());
		}
		for (int i = 0; i < _c.length; i++) {
			VarIntLS[] s = _c[i].getVariables();
			if (s != null) {
				for (int j = 0; j < s.length; j++) {
					_map.get(s[j]).add(_c[i]);
				}
			}
		}
		for (int i = 0; i < _x1.length; i++) {
			_S.add(_x1[i]);
		}

		_x = new VarIntLS[_S.size()];
		int u = 0;
		for (VarIntLS e : _S) {
			_x[u] = e;
			u++;
		}
		_map1 = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < _x.length; i++) {
			_map1.put(_x[i], i);
		}
		_a = new int[_x1.length];
		for (int i = 0; i < _a.length; i++) {
			_a[i] = _x1[i].getValue();
		}
		_map2 = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < _x1.length; i++) {
			_map2.put(_x1[i], i);
		}
		_map3 = new HashMap<IConstraint, Integer>();
		for (int i = 0; i < _c.length; i++) {
			_map3.put(_c[i], i);
		}

		_ls.post(this);
	}

	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _minValue;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _maxValue;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		if (_map1.get(x) == null)
			return 0;
		int nv = _value;
		int k = _map2.get(x);
		// System.out.println("k  = "+k);
		_a[k] = val;
		Vector<IConstraint> C = _map.get(x);
		for (IConstraint c : C) {
			int r = _map3.get(c);
			// System.out.println("r  =  "+r);
			if (c.violations() == 0) {
				if (c.violations() + c.getAssignDelta(x, val) != 0) {
					if (x == _x1[r]) {
						nv = nv - x.getValue();
					} else {
						nv = nv - _a[r];
					}
					// System.out.println("nv   == "+nv);
				} else {
					if (x == _x1[r]) {
						nv = nv + val - x.getValue();
					} else {
						nv = nv;
					}
				}
			} else {
				if (c.violations() + c.getAssignDelta(x, val) == 0) {
					if (x == _x1[r]) {
						nv = nv + val;
					} else {
						nv = nv + _a[r];
					}
				} else {
					nv = nv;
				}
			}
		}
		_a[k] = x.getValue();
		return nv - _value;

	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {

		if (_map1.get(x) == null)
			return;

		int t = x.getOldValue();

		int nv = _value;
		int k = _map2.get(x);
		// System.out.println("k  = "+k);
		_a[k] = val;
		Vector<IConstraint> C = _map.get(x);
		for (IConstraint c : C) {
			int r = _map3.get(c);
			// System.out.println("r  =  "+r);
			if (c.violations() + c.getAssignDelta(x, t) == 0) {
				if (c.violations() != 0) {
					if (x == _x1[r]) {
						nv = nv - t;
					} else {
						nv = nv - _a[r];
					}
					// System.out.println("nv   == "+nv);
				} else {
					if (x == _x1[r]) {
						nv = nv + val - t;
					} else {
						nv = nv;
					}
				}
			} else {
				if (c.violations() == 0) {
					if (x == _x1[r]) {
						nv = nv + val;
					} else {
						nv = nv + _a[r];
					}
				} else {
					nv = nv;
				}
			}
		}

		_value = nv;

	}

	@Override
	public void initPropagate() {
		for (int i = 0; i < _c.length; i++) {
			if (_c[i].violations() == 0) {
				_value += _x1[i].getValue();
			}

			_maxValue += _x1[i].getMaxValue();
		}
		_minValue = 0;

	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public String name() {
		return "sumVarConstraint";
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv = 0;
		for (int i = 0; i < _c.length; i++) {
			if (_c[i].violations() == 0) {
				nv += _x1[i].getValue();
			}

		}
		if (nv == _value)
			return true;
		else
			return false;
	}

	public static void main(String[] args) {
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[10000];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10000);
			x[i].setValue(10);
		}
		x[0].setValue(17);
		x[1].setValue(10);
		IConstraint[] c = new IConstraint[x.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = new LessOrEqual(x[i], x[0]);
		}
		SumVarConstraints s = new SumVarConstraints(x, c);
		ls.close();
		System.out.println(s.getValue());
		System.out.println(s.getAssignDelta(x[1], 30));
		x[1].setValuePropagate(30);
		System.out.println("snew  =  " + s.getValue());

		x[1].setValuePropagate(25);
		System.out.println(s.getValue());
		int oldv = s.getValue();
		int dem = 0;
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 10000);
			int r2 = (int) (Math.random() * 10000);
			int dv = s.getAssignDelta(x[r1], r2);
			x[r1].setValuePropagate(r2);
			int dd = s.getValue();
			if (dd == dv + oldv && s.verify() == true) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
		}
		System.out.println("dem  =   " + dem + "  snew  =  " + s.getValue());

	}

}
