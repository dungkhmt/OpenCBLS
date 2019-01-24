package localsearch.functions.occurrence;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class OccurrenceFunctionConstant extends AbstractInvariant implements
		IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction[] _f;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	//private int[] _occ;
	private int _val;

	private HashMap<VarIntLS, Vector<IFunction>> _map;
	//private HashMap<Integer, Integer> _map1;
	private HashMap<VarIntLS, Integer> _map2;

	public OccurrenceFunctionConstant(IFunction[] f, int val) {
		// maintain the number of occurrences of the value val in the array f

		_f = f;
		_val = val;
		_ls = _f[0].getLocalSearchManager();
		post();

	}

	void post() {
		//_map1 = new HashMap<Integer, Integer>();
		/*
		for (int i = 0; i < _f.length; i++) {
			_minValue = Math.min(_minValue, _f[i].getMinValue());
			_maxValue = Math.max(_maxValue, _f[i].getMaxValue());
		}
		_occ = new int[_maxValue - _minValue + 1];
		for (int i = 0; i < _occ.length; i++)
			_occ[i] = 0;
		*/
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();

		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] f_x = _f[i].getVariables();
			if (f_x != null) {
				for (int j = 0; j < f_x.length; j++) {
					_S.add(f_x[j]);
				}
			}
		}
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for (VarIntLS e : _S) {
			_x[u] = e;
			u++;
		}
		_map2 = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < _x.length; i++) {
			_map2.put(_x[i], i);
		}
		_map = new HashMap<VarIntLS, Vector<IFunction>>();

		for (VarIntLS e : _S) {
			_map.put(e, new Vector<IFunction>());
		}

		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] s = _f[i].getVariables();
			if (s != null) {
				for (int j = 0; j < s.length; j++) {
					_map.get(s[j]).add(_f[i]);
				}
			}
		}

		_minValue = 0;
		_maxValue = _f.length;

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
		if (_map2.get(x) == null)
			return 0;

		int nv = _value;

		Vector<IFunction> F = _map.get(x);

		for (IFunction f : F) {

			if (f.getValue() == _val) {
				if (f.getValue() + f.getAssignDelta(x, val) == _val)
					nv = nv;
				else
					nv--;

			} else {
				if (f.getValue() + f.getAssignDelta(x, val) == _val)
					nv++;
				else
					nv = nv;
			}
		}

		return nv - _value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if (_map2.get(x) == null && _map2.get(y) == null)
			return 0;

		if (_map2.get(x) != null && _map2.get(y) == null)
			return getAssignDelta(x, y.getValue());

		if (_map2.get(x) == null && _map2.get(y) != null)
			return getAssignDelta(y, x.getValue());
		int nv = _value;
		Vector<IFunction> F1 = _map.get(x);
		Vector<IFunction> F2 = _map.get(y);
		HashSet<IFunction> F = new HashSet<IFunction>();

		if (F1 != null) {
			for (IFunction f : F1) {
				F.add(f);
			}
		}
		if (F2 != null) {
			for (IFunction f : F2) {
				F.add(f);
			}

		}

		for (IFunction f : F) {

			if (f.getValue() == _val) {
				if (f.getValue() + f.getSwapDelta(x, y) == _val) {
					//nv = nv;
				} else {
					nv--;
				}
			} else {
				if (f.getValue() + f.getSwapDelta(x, y) == _val) {
					nv++;
				} else {
					//nv = nv;
				}
			}

		}

		return nv - _value;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {

		if (_map2.get(x) == null)
			return;
		int t = x.getOldValue();

		Vector<IFunction> F = _map.get(x);
		int nv = _value;
		for (IFunction f : F) {
			if (f.getValue() + f.getAssignDelta(x, t) == _val) {
				if (f.getValue() == _val)
					nv = nv;
				else
					nv--;

			} else {
				if (f.getValue() == _val)
					nv++;
				else
					nv = nv;
			}

		}
		_value = nv;

	}

	@Override
	public void initPropagate() {
		/*
		for (IFunction e : _f)
			_occ[e.getValue() - _minValue]++;

		for (int i = _minValue; i <= _maxValue; i++)
			_map1.put(i, _occ[i - _minValue]);
		if (!_map1.containsKey(_val))
			_value = 0;
		else

			_value = _map1.get(_val);
		 */
		_value = 0;
		for(int i = 0; i < _f.length; i++)
			if(_f[i].getValue() == _val) _value++;
	}

	public String name() {
		return "OccurrenceFunctionConstant";
	}

	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public boolean verify() {
		// TODO Auto-generated method stub
		int count = 0;
		for (IFunction f : _f) {
			if (f.getValue() == _val)
				count++;
		}
		if (count != _value) {
			System.out.println(name() + "::veirfy --> failed, _value = "
					+ _value + " differs from value = " + count
					+ " by recomputation");
			return false;
		} else {

		}
		return true;
	}

	public static void main(String[] args) {
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[1000];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 100);
			x[i].setValue(i);

		}
		for (int i = 0; i < x.length / 2; i++) {
			x[i].setValue(2);
		}
		IFunction[] f = new IFunction[x.length];
		for (int i = 0; i < f.length; i++) {
			f[i] = new FuncPlus(x[i], 1);
		}
		OccurrenceFunctionConstant o = new OccurrenceFunctionConstant(f, 5);
		ls.close();
		System.out.println(o.getValue());
		localsearch.applications.Test t = new localsearch.applications.Test();
		t.test(o, 10000);
	}
}
