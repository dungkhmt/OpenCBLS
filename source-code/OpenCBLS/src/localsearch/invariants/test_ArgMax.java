package localsearch.invariants;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class test_ArgMax extends AbstractInvariant implements IFunction {
	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction[] _f;
	private VarIntLS[] _x;
	private HashMap<IFunction, Integer> _map;
	private HashMap<VarIntLS, Vector<IFunction>> _hash;
	private int[] _a;
	private int[] _b;
	private int[] _c;

	private LocalSearchManager _ls;

	public test_ArgMax(IFunction[] f) {
		// maintain the maximal value of the array x
		// use Heap data structures for the implementation
		_f = f;
		_ls = _f[0].getLocalSearchManager();
		post();
		// _ls.post(this);

	}

	

	private void post() {
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] f_x = _f[i].getVariables();
			if(f_x!=null)
			{
			for (int j = 0; j < f_x.length; j++) {
				_S.add(f_x[j]);
			}
			}
		}
		_hash = new HashMap<VarIntLS, Vector<IFunction>>();
		for (VarIntLS e : _S) {
			_hash.put(e, new Vector<IFunction>());

		}
		for (int i = 0; i < _f.length; i++) {
			VarIntLS[] s = _f[i].getVariables();
			if(s!=null)
			{
			for (int j = 0; j < s.length; j++) {
				_hash.get(s[j]).add(_f[i]);
			}
			}

		}
		_x = new VarIntLS[_S.size()];
		int u = 0;
		for (VarIntLS e : _S) {
			_x[u] = e;
			u++;
		}
		_map = new HashMap<IFunction, Integer>();
		for (int i = 0; i < _f.length; i++) {
			_map.put(_f[i], i);
		}

		_a = new int[_f.length];
		for (int i = 0; i < _a.length; i++) {
			_a[i] = _f[i].getValue();
		}
		_b = new int[_f.length];
		_c = new int[_f.length];
		for (int i = 0; i < _b.length; i++) {
			_b[i] = i;
			_c[i] = i;

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
	public int getAssignDelta(VarIntLS x, int val) {
		
		// TODO Auto-generated method stub
		return 0;
		
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return 0;
		
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {

		

	}

	@Override
	public void initPropagate() {
		heapMe(_a, _b, _c);
		
		
		
		

		

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

	public static void heapMe(int[] a, int[] b, int[] c) {
		int kk;
		for (kk = (a.length) / 2; kk >= 0; kk--) {
			heapify(a, b, c, kk);
		}
	}

	public static void heapify(int[] a, int[] b, int[] c, int i) {
		int largest;
		int left = 2 * i + 1;
		int right = 2 * i + 2;
		if (((left < a.length) && (a[left] > a[i]))) {
			largest = left;
		} else {
			largest = i;
		}
		if (((right < a.length) && (a[right] > a[largest]))) {
			largest = right;
		}
		if (largest != i) {

			swap(a, i, largest);
			int k1 = c[i];
			int k2 = c[largest];
			swap(c, i, largest);
			swap(b, k1, k2);

			heapify(a, b, c, largest);
		}
	}

	private static void swap(int[] A, int i, int largest) {
		int t = A[i];
		A[i] = A[largest];
		A[largest] = t;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
	return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 LocalSearchManager ls=new LocalSearchManager();
	       VarIntLS[] x=new VarIntLS[10];
	       for(int i=0;i<x.length;i++)
	       {
	    	   x[i]=new VarIntLS(ls,0,100);
	    	   x[i].setValue(i);
	    	   
	    	   
	       }
	     
	       IFunction[] f=new IFunction[x.length];
	       for(int i=0;i<f.length;i++)
	       {
	    	   f[i]=new FuncPlus(x[i],1);
	       }
	       
	       ArgMax m=new ArgMax(f);
	       ls.close();
	       System.out.println(m.getIndices());
		}
		
		
		 
	

}