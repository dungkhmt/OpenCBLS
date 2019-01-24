package localsearch.functions.conditionalsum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.*;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ConditionalSumTmp extends AbstractInvariant implements IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction[] _f;
	private IFunction[] _f1;
	private IFunction[] _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private IFunction _val;
	private HashMap<VarIntLS, Vector<IFunction>> _map;
	private int[] _a;
	private HashMap<IFunction, Integer> _map1;
	private ArrayList<Integer>[] _h;
	public static int hash = 997;

	public static int HashFun(int value) {
		return value % hash;
	}

	public ConditionalSumTmp(IFunction[] cf, IFunction[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = w;
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(IFunction[] cf, int[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, VarIntLS[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;

		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(VarIntLS[] cf, IFunction[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = w;
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(VarIntLS[] cf, int[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(VarIntLS[] cf, VarIntLS[] w, int val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = new FuncVarConst(cf[0].getLocalSearchManager(), val);
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(IFunction[] cf, IFunction[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = w;
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, int[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, VarIntLS[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(VarIntLS[] cf, IFunction[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = w;
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(VarIntLS[] cf, int[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(VarIntLS[] cf, VarIntLS[] w, VarIntLS val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = new FuncVarConst(val);
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, IFunction[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = w;
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, int[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(IFunction[] cf, VarIntLS[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = cf;
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(VarIntLS[] cf, IFunction[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);

		}
		_f2 = w;
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();
	}

	public ConditionalSumTmp(VarIntLS[] cf, int[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(cf[0].getLocalSearchManager(), w[i]);
		}
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	public ConditionalSumTmp(VarIntLS[] cf, VarIntLS[] w, IFunction val) {
		// semantic: represents \sum_{i\in cf.range: cf[i] == val} w[i]
		_f1 = new IFunction[cf.length];
		for (int i = 0; i < _f1.length; i++) {
			_f1[i] = new FuncVarConst(cf[i]);
		}
		_f2 = new IFunction[w.length];
		for (int i = 0; i < _f2.length; i++) {
			_f2[i] = new FuncVarConst(w[i]);
		}
		_val = val;
		_ls = cf[0].getLocalSearchManager();
		post();

	}

	void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();

		_f = new IFunction[_f1.length + _f2.length + 1];

		for (int i = 0; i < _f1.length; i++) {
			_f[i] = _f1[i];
		}
		for (int i = _f1.length; i < _f1.length + _f2.length; i++) {
			_f[i] = _f2[i - _f1.length];
		}
		_f[_f1.length + _f2.length] = _val;

		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] f_x = _f[i].getVariables();
			if(f_x!=null)
			{
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
		_map = new HashMap<VarIntLS, Vector<IFunction>>();
		for (VarIntLS e : _S) {
			_map.put(e, new Vector<IFunction>());
		}
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] s = _f[i].getVariables();
			if(s!=null)
			{
			for (int j = 0; j < s.length; j++) {
				_map.get(s[j]).add(_f[i]);
			}
			}
		}
		_a = new int[_f.length];

		for (int i = 0; i < _a.length; i++) {
			_a[i] = _f[i].getValue();
		}
		_map1 = new HashMap<IFunction, Integer>();
		for (int i = 0; i < _f.length; i++) {
			_map1.put(_f[i], i);
		}

		_h = new ArrayList[hash];
		for (int i = 0; i < _h.length; i++) {
			_h[i] = new ArrayList<Integer>();
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
		int nv = 0;
		if (!(x.IsElement(_x)))
			return 0;

		Vector<IFunction> F = _map.get(x);
		for (IFunction f : F) {
			int k = _map1.get(f);
			_a[k] = f.getValue() + f.getAssignDelta(x, val);

		}

		for (int i = 0; i < _h.length; i++) {
			_h[i].clear();

		}
		for (int i = 0; i < _f1.length; i++) {
			int value1 = _a[i];
			int index = i;
			int findhashValue = HashFun(value1);
			_h[findhashValue].add(new Integer(index));
		}

		int find = _a[_f.length - 1];
		int findhashValue = HashFun(find);

		ArrayList<Integer> list = _h[findhashValue];
		for (Integer i : list) {
			if (_a[i] == find) {
				nv += _a[i + _f1.length];

			}
		}
		for (IFunction f : F) {
			int k1 = _map1.get(f);
			_a[k1] = f.getValue();
		}

		return nv - _value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int tong = 0;
		Vector<IFunction> F1 = _map.get(x);
		Vector<IFunction> F2 = _map.get(y);
		for (IFunction f : F1) {

		}
		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		int tong = 0;

		Vector<IFunction> F = _map.get(x);
		for (IFunction f : F) {
			int k1 = _map1.get(f);
			_a[k1] = f.getValue();
		}
		for (int i = 0; i < _h.length; i++) {
			_h[i].clear();
		}
		
		  for (int i = 0; i < _f1.length; i++) { int value1 = _a[i]; int index
		  = i; int hashValue = HashFun(value1); _h[hashValue].add(new
		  Integer(index)); }
		 
		int find = _a[_f.length - 1];
		int findhashValue = HashFun(find);

		ArrayList<Integer> list = _h[findhashValue];
		for (Integer i : list) {
			if (_a[i] == find) {
				tong += _a[i + _f1.length];

			}
		}

		_value = tong;
	}

	@Override
	public void initPropagate() {
		int tong = 0;

		for (int i = 0; i < _f1.length; i++) {
			int value1 = _a[i];
			int index = i;
			int hashValue = HashFun(value1);
			_h[hashValue].add(new Integer(index));
		}

		int find = _a[_f.length - 1];
		int findhashValue = HashFun(find);

		ArrayList<Integer> list = _h[findhashValue];
		for (Integer i : list) {
			if (_a[i] == find) {
				tong += _a[i + _f1.length];
			}
		}

		_value = tong;

	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int value = 0;
		for(int i = 0; i < _f1.length; i++){
			if(_f1[i].getValue() == _val.getValue()){
				value = value + _f2[i].getValue();
			}
		}
		if(value != _value){
			System.out.println(name() + "::verify --> vailed, _value = " + _value + " which differs from value = " + value + " by recomputation");
			return false;
		}
		return true;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[1000];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10000);
			x[i].setValue(i);
		}
		//x[0].setValue(2);
		for (int i = 0; i < 500; i++) {
			x[i].setValue(2);
		}

		IFunction[] cf = new IFunction[x.length];
		for(int i=0;i<cf.length;i++)
		{
			cf[i]=new FuncPlus(x[i], 1);
		}
       int[] w1=new int[x.length];
		for (int i = 0; i < w1.length; i++) {
			w1[i] = 10;
		}
		ConditionalSumTmp s = new ConditionalSumTmp(cf, w1, 3);

		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(s,100000);
		
		/*
		System.out.println(s.getValue());
		
		int oldv = s.getValue();
		int dem = 0;
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 1000);
			System.out.println("r1      =  " + r1);
			int r2 = (int) (Math.random() * 3);

			System.out.println("r2      =  " + r2);
			int dv = s.getAssignDelta(x[r1], r2);

			System.out.println("s.get   =   " + dv);
			x[r1].setValuePropagate(r2);
			int dd = s.getValue();

			System.out.println("value   =   " + dd);
			if (dd == dv + oldv) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
			System.out.println("------------------------------------------");

		}
		System.out.println("dem =  " + dem);
        */     
	}

}