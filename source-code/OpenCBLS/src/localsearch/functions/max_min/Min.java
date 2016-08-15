package localsearch.functions.max_min;



import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Vector;

import localsearch.functions.basic.FuncPlus;
import localsearch.functions.basic.FuncVarConst;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Min extends AbstractInvariant implements IFunction {
	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction[] _f;
	private VarIntLS[] _x;
	private HashMap<IFunction, Integer> _map;
	private HashMap<VarIntLS, Vector<IFunction>> _hash;
	private LocalSearchManager _ls;
	
	private TreeSet<Pair> _tree;
	private int[] _oldV;
	private long[] _t;
	private long current;
	
	private final int MAX_INT = (1 << 31) - 1;

	public Min(IFunction[] f) {
		// maintain the maximal value of the array x
		// use Heap data structures for the implementation
		_f = f;
		_ls = _f[0].getLocalSearchManager();
		post();

	}

	public Min(VarIntLS[] x) {
		// maintain the maximal value of the array x
		// use Heap data structures for the implementation
		_f = new IFunction[x.length];
		for (int i = 0; i < _f.length; i++) {
			_f[i] = new FuncVarConst(x[i]);
		}
		_ls = x[0].getLocalSearchManager();
		post();
	}

	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] f_x = _f[i].getVariables();
			if(f_x!=null) 
				for (int j = 0; j < f_x.length; j++) _S.add(f_x[j]);
		}
		
		_hash = new HashMap<VarIntLS, Vector<IFunction>>();
		for (VarIntLS e : _S) 
			_hash.put(e, new Vector<IFunction>());
		
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] s = _f[i].getVariables();
			if(s!=null)
				for (int j = 0; j < s.length; j++) 
					_hash.get(s[j]).add(_f[i]);
		}
		
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for (VarIntLS e : _S) {
			_x[u] = e;
			u++;
		}
		
		_map = new HashMap<IFunction, Integer>();
		for (int i = 0; i < _f.length; i++) 
			_map.put(_f[i], i);

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
	public int getAssignDelta(VarIntLS x, int val) {
		if (!_hash.containsKey(x)) return 0;
		
		current++;
		int mf = MAX_INT;
		Vector<IFunction> V = _hash.get(x);
		for (IFunction f : V) {
			int pos = _map.get(f);
			mf = Math.min(mf, f.getAssignDelta(x, val) + f.getValue());
			_t[pos] = current;
		}
		
		for (Pair p : _tree)  
			if (_t[p.second] < current) {
				mf = Math.min(mf, p.first);
				break;
			}
		return mf - _value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		if (!_hash.containsKey(x))
			return getAssignDelta(y, x.getValue());
		if (!_hash.containsKey(y))
			return getAssignDelta(x, y.getValue());
		
		HashSet<IFunction> V = new HashSet<IFunction>();
		V.addAll(_hash.get(x));
		V.addAll(_hash.get(y));
		
		current++;
		int mf = MAX_INT;
		for (IFunction f : V) {
			int pos = _map.get(f);
			mf = Math.min(mf, f.getSwapDelta(x, y) + f.getValue());
			_t[pos] = current;
		}
		
		for (Pair p : _tree) 
			if (_t[p.second] < current) {
				mf = Math.min(mf, p.first);
				break;
			}
		
		return mf - _value;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if (!_hash.containsKey(x)) return;
		Vector<IFunction> V = _hash.get(x);
		for (IFunction f : V) {
			int pos = _map.get(f);
			_tree.remove(new Pair(_oldV[pos], pos));
			_tree.add(new Pair(f.getValue(), pos));
			_oldV[pos] = f.getValue();
		}
		_value = _tree.first().first;
	}

	@Override
	public void initPropagate() {
		current = 0;
		_t = new long[_f.length];
		_oldV = new int[_f.length];
		_tree = new TreeSet<Pair>(new CompareMinPair());
		for (int i = 0; i < _f.length; i++) {
			_tree.add(new Pair(_f[i].getValue(), i));
			_oldV[i] = _f[i].getValue();
			_t[i] = current;
		}
		_value = _tree.first().first;
	}

	@Override
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int minV = _f[0].getValue();
		for (int i = 0; i < _f.length; i++) {
			if (minV > _f[i].getValue()) {
				minV = _f[i].getValue();
			}

		}

		if (minV != _value){
			System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from minV = " + minV + " by recomputation");
			return false;
		}else
			return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long t=System.currentTimeMillis();
		LocalSearchManager ls = new LocalSearchManager();
		int n = 1000;
		VarIntLS[] x = new VarIntLS[n];
		IFunction[] f = new IFunction[n];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 300);
			x[i].setValue(i);
			f[i] = new FuncPlus(x[i],i);
		}
		IFunction m = new Min(f);
		ls.close();
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(m, 100000);
		
		System.out.println(m.getValue());
		System.out.println(m.getAssignDelta(x[9], 10));
		System.out.println(m.getSwapDelta(x[9], x[8]));
		x[1].setValuePropagate(10);
	}	

}