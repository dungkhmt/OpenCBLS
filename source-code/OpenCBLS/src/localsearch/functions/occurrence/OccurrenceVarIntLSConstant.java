package localsearch.functions.occurrence;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.applications.Test;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class OccurrenceVarIntLSConstant extends AbstractInvariant implements IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	//private int[] _occ;
	private int _val;
	//private int _maxV;
	//private int _minV;
	//private HashMap<Integer, Integer>     _map;
	private HashMap<VarIntLS, Integer>     _map;

	
	
	public OccurrenceVarIntLSConstant(VarIntLS[] x, int val) {
		
		_x=x;
		_val=val;
		_ls=x[0].getLocalSearchManager();
		post();
	}

	void post() {
		//_map=new HashMap<Integer, Integer>();
		/*
		 _maxV=-1000000000;
		 _minV=1000000000;
		for(int i=0;i<_x.length;i++)
		{
			if(_x[i].getValue()<_minV)
			{
				_minV=_x[i].getValue();
						
			}
			if(_x[i].getValue()>_maxV)
			{
				_maxV=_x[i].getValue();
			}
		}
		
		System.out.println("min  =   "+_minV+"   max   =   "+_maxV);
		
		_occ=new int[_maxV-_minV+1];
		for(int i=0;i<_occ.length;i++)
		{
			_occ[i]=0;
		}
		*/
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		_minValue=0;
		_maxValue=_x.length;
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
		if(_map.get(x)==null) return 0;
		int nv=_value;
		if(x.getValue()==_val)
		{
			if(val==_val)
			{
				//nv=nv;
			}
			else
			{
				nv--;
			}
			
		}
		else
		{
			if(val==_val)
			{nv++;}
			else
			{
				//nv=nv;
			}
		}
		return nv-_value;
		
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		
		if(_map.get(x)==null&&_map.get(y)==null) return 0;
		if(_map.get(x)!=null&&_map.get(y)==null) return getAssignDelta(x,y.getValue());
		if(_map.get(x)==null&&_map.get(y)!=null) return getAssignDelta(y,x.getValue());
		
		
		return 0;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(_map.get(x)==null) return;
		int nv=_value;
		int t=x.getOldValue();
		if(t==_val)
		{
			if(val==_val)
			{
				nv=nv;
			}
			else
			{
				nv--;
			}
		}
		else
		{
			if(val==_val)
			{
				nv++;
			}
			else
			{
				nv=nv;
			}
		}
		_value=nv;
		

		

	}

	@Override
	public void initPropagate() {
		/*
		for(int i=0;i<_x.length;i++)
		{
			_occ[_x[i].getValue()-_minV]++;
		}
		for(int i=_minV;i<=_maxV;i++)
		{
			_map.put(i,_occ[i-_minV]);
		}
		if(!_map.containsKey(_val))
				{
			_value=0;
				}
		else
		{
		_value=_map.get(_val);
		}
		*/
		_value = 0;
		for(int i = 0;i < _x.length; i++)
			if(_x[i].getValue() == _val) _value++;
		
		
	}
	public String name(){
		return "OccurrenceVarIntLSConstant";
	}
	
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int v=0;
		for(int i=0;i<_x.length;i++)
		{
			if(_x[i].getValue()==_val)
			{
				v++;
			}
		}
		if(v==_value)
		{
			return true;
		}
		else
			
		return false;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[10000];

		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10000);
			x[i].setValue(i);
		}
		for(int i=0;i<x.length/2;i++)
		{
			x[i].setValue(2);
		}

	
		OccurrenceVarIntLSConstant o=new OccurrenceVarIntLSConstant(x, 2);
		ls.close();
		System.out.println("o   =   " + o.getValue());
		
		
		localsearch.applications.Test test=new localsearch.applications.Test();
		test.test(o, 10000);
		
		/*

		int oldv = o.getValue();
		int dem = 0;
        
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 10000);
			int r2 = (int) (Math.random() * 10000);
			int dv = o.getAssignDelta(x[r1], r2);
			x[r1].setValuePropagate(r2);
			int dd = o.getValue();
			if (dd == dv + oldv&&o.verify()==true) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
		}

		System.out.println("dem = " + dem);
		System.out.println("o  =   "+o.getValue());
           */		
	}

}
