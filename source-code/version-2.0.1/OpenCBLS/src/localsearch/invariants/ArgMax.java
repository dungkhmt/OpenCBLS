package localsearch.invariants;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;

import java.util.*;
public class ArgMax extends AbstractInvariant {

	/**
	 * @param args
	 */
	private ArrayList<Integer> _set;
	private IFunction[]		_f;
	private HashMap<IFunction, Integer> _map;
	private IFunction[]		_hf;// heap
	private LocalSearchManager	_ls;
	private int[]      _a;
	private VarIntLS[]         _x;
	private HashMap<VarIntLS, Vector<IFunction>>    _hash;
	 
	public ArgMax(IFunction[] f){
		_f = f;
		if(_f[0] != null)
			_ls = _f[0].getLocalSearchManager();
		else _ls = null;
		_set = new ArrayList<Integer>();
		post();
	}
	public ArrayList<Integer> getIndices(){
		return _set;
	}
	void post(){
		_map = new HashMap<IFunction, Integer>();
		for(int i = 0; i < _f.length; i++)
			_map.put(_f[i], i);
		HashSet<VarIntLS> _S=new HashSet<VarIntLS>();
		for(int i=0;i<_f.length;i++)
		{
			VarIntLS[] f_x=_f[i].getVariables();
			if(f_x!=null)
			{
			for(int j=0;j<f_x.length;j++)
			{
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
		
		_x=new VarIntLS[_S.size()];
		int u=0;
		for(VarIntLS e:_S)
		{
			_x[u]=e;
			u++;
		}
		_a=new int[_f.length];
		for(int i=0;i<_f.length;i++)
		{
			_a[i]=_f[i].getValue();
		}
		
		
		_ls.post(this);
	}
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		System.out.println("AbstractInvariant::getVariables, this must be implemented within subclass");
		//assert(false);
		return _x;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		Vector<IFunction>  F=_hash.get(x);
		int u=_set.get(0);
		
		for(IFunction f: F)
		{
			int k=_map.get(f);
			_a[k]=f.getValue();
			
		}
		int max=-10000000;
		for(int i=0;i<_a.length;i++)
		{
			if(_a[i]>=max)
			{
				max=_a[i];
			}
		}
		if(max>=_a[u])
		{
			_set.clear();
		}
		for(int i=0;i<_a.length;i++)
		{
			if(_a[i]==max)
			{
				_set.add(i);
			}
		}
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		//System.out.println("AbstractInvariant::initPropagate, this must be implemented within subclass");
		
		//assert(false);
		int max=-10000000;
		for(int i=0;i<_a.length;i++)
		{
			if(_a[i]>=max)
			{
				max=_a[i];
			}
		}
		for(int i=0;i<_a.length;i++)
		{
			if(_a[i]==max)
			{
				_set.add(i);
			}
			
		}
		
		
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		//assert(false);
		return null;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		assert(false);
		return false;
	}

	public String name(){
		return "ArgMax";
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
     x[1].setValue(30);
     x[2].setValue(30);
     x[3].setValue(30);
       IFunction[] f=new IFunction[x.length];
       for(int i=0;i<f.length;i++)
       {
    	   f[i]=new FuncPlus(x[i],1);
       }
       
       ArgMax m=new ArgMax(f);
       ls.close();
       System.out.println(m.getIndices());
       x[2].setValuePropagate(40);
       System.out.println(m.getIndices());
	}

}
