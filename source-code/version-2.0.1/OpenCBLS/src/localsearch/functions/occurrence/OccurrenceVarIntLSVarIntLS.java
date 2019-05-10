package localsearch.functions.occurrence;

import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class OccurrenceVarIntLSVarIntLS extends AbstractInvariant implements
		IFunction {
	private int             _value;
	private int             _minValue;
	private int             _maxValue;
	private VarIntLS[]      _x;
	private VarIntLS[]      _x1;
	private VarIntLS      _x2;
	private LocalSearchManager   _ls;
	private HashMap<VarIntLS, Integer>    _map;
	private HashMap<Integer, Integer>      _map1;
	private int          _maxV;
	private int           _minV;
	private int[]            _occ;

	public OccurrenceVarIntLSVarIntLS(VarIntLS[] lstX, VarIntLS x){
		_x1=lstX;
		_x2=x;
		_ls=lstX[0].getLocalSearchManager();
		
		
		post();
		
	}
	void post()
	{
		_x=new VarIntLS[_x1.length+1];
		for(int i=0;i<_x1.length;i++)
		{
			_x[i]=_x1[i];
		}
		_x[_x1.length]=_x2;
		
		_maxV=-1000000000;
		_minV=1000000000;
		for(int i=0;i<_x.length;i++)
		{
			if(_x[i].getMinValue()<_minV)
			{
				_minV=_x[i].getMinValue();
			}
			if(_x[i].getMaxValue()>_maxV)
			{
				_maxV=_x[i].getMaxValue();
			}
		}
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		_occ=new int[_maxV-_minV+1];
		for(int i=0;i<_occ.length;i++)
		{
			_occ[i]=0;
		}
		
		_map1=new HashMap<Integer, Integer>();
		_minValue=0;
		_maxValue=_x1.length;
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
	public VarIntLS[] getVariables() {
		return _x;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _value;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		
		if(_map.get(x)==null) return 0;
		int nv=0;
		if(x==_x2)
		{
			int a=val-_minV;
			if(a<0||a>_maxV)
			{
				nv=0;
			}
			else
			{
				nv=_occ[a];
			}
			
		}
		else
		{
			int u=x.getValue()-_minV;
			
			_occ[u]--;
			
			_occ[val-_minV]++;
			nv=_occ[_x2.getValue()-_minV];
			
			_occ[u]++;
			_occ[val-_minV]--;
			
			
			
		}
		return nv-_value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		
		
		if(_map.get(x)==null&&_map.get(y)==null) return 0;
		if(_map.get(x)!=null&&_map.get(x)==null) return getAssignDelta(x,y.getValue());
		if(_map.get(x)==null&&_map.get(y)!=null) return getAssignDelta(y, x.getValue());
		
		int nv=0;
		if(x==_x2)
		{
		
			_occ[y.getValue()-_minV]--;
			_occ[x.getValue()-_minV]++;
			
			nv=_occ[y.getValue()-_minV];
			_occ[y.getValue()-_minV]++;
			_occ[x.getValue()-_minV]--;
			
		}
		if(y==_x2)
		{
			
			_occ[x.getValue()-_minV]--;
			_occ[y.getValue()-_minV]++;
			nv=_occ[x.getValue()-_minV];
			
			_occ[x.getValue()-_minV]++;
			_occ[y.getValue()-_minV]--;
		}
		if(x!=_x2 && y!=_x2)
		{
			nv=_value;
		}
		return nv-_value;
		
		
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		
		if(_map.get(x)==null) return ;
		int nv=0;
		if(x==_x2)
		{
			int a=val-_minV;
			if(a<0||a>_maxV)
			{
				nv=0;
			}
			else
			{
				nv=_occ[a];
			}
			
		}
		else
		{
			int u=x.getOldValue()-_minV;
			
			_occ[u]--;
			
			_occ[val-_minV]++;
			nv=_occ[_x2.getValue()-_minV];
			
			
		}
		
		 _value=nv;
		
	
	}

	@Override
	public void initPropagate() {
		
		for(int i=0;i<_x1.length;i++)
		{
			
			_occ[_x1[i].getValue()-_minV]++;
		}
		
		
		int a=_x2.getValue()-_minV;
		if(a<0||a>_maxV)
		{
			_value=0;
		}
		else
		{
			_value=_occ[a];
		}
		
		
		
				
			
	}
	@Override
	public boolean verify() {
		
		int nv=0;
		for(int i=0;i<_x1.length;i++)
		{
			if(_x1[i].getValue()==_x2.getValue())
			{
				nv++;
			}
		}
		if(nv==_value)
		{
		return true;
		}
		else
		{
			return false;
		}
	
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long t=System.currentTimeMillis();
		LocalSearchManager ls=new LocalSearchManager();
		VarIntLS[] x=new VarIntLS[1000];
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(ls, 0, 1000);
			x[i].setValue(i);
		}
		for(int i=0;i<x.length/2;i++)
		{
			x[i].setValue(7);
		}
		VarIntLS x1=new VarIntLS(ls, 0, 1000);
		x1.setValue(5);
		OccurrenceVarIntLSVarIntLS o=new OccurrenceVarIntLSVarIntLS(x, x1);
		ls.close();
		System.out.println("o  =   "+o.getValue());
		System.out.println("get   =   "+o.getSwapDelta(x[1], x1));
		int u=x1.getValue();
		x1.setValuePropagate(x[1].getValue());
		x[1].setValuePropagate(u);
	//x1.setValuePropagate(7);
		
		System.out.println("o  =   "+o.getValue());
		
		localsearch.applications.Test test=new localsearch.applications.Test();
		test.test(o, 10000);
			
		/*
		int oldv=o.getValue();
		int dem=0;
		for(int i=0;i<10000;i++)
		{
			int r1=(int)(Math.random()*10);
			System.out.println("r1  =  "+r1);
			
			int dv=o.getSwapDelta(x[r1], x1);
			int a=x[r1].getValue();
			x[r1].setValuePropagate(x1.getValue());
			x1.setValuePropagate(a);
			int dd=o.getValue();
			if(dd==dv+oldv&&o.verify()==true)
			{
				dem++;
				oldv=dd;
			}
			else
			{
				System.out.println("ERROR");break;
			}
					
		}
		System.out.println("dem  =   "+dem+"  onew   =   "+o.getValue());
		System.out.println(" t=   "+(System.currentTimeMillis()-t));
          */
	}

}
