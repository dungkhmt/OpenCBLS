package localsearch.functions.occurrence;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.applications.Test;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class OccurrenceFunctionFunction extends AbstractInvariant implements
		IFunction {
	

	private int            _value;
	private int             _minValue;
	private int              _maxValue;
	private IFunction[]         _f;
	private IFunction[]       _f1;
	private IFunction             _f2;
	private LocalSearchManager      _ls;
	private VarIntLS[]              _x;
	private int []                   _occ;
	private int                       _minV;
	private int                       _maxV;
	private HashMap<VarIntLS, Integer>            _map;
	private HashMap<VarIntLS, Vector<IFunction>>       _map1;
	
	

	public OccurrenceFunctionFunction(IFunction[] lstF, IFunction f){
		_f1=lstF;
		_f2=f;
		_ls=_f1[0].getLocalSearchManager();
		post();
		
		
	}
	void post()
	{
		_f=new IFunction[_f1.length+1];
		for(int i=0;i<_f1.length;i++)
		{
			_f[i]=_f1[i];
		}
		_f[_f1.length]=_f2;
		
		
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
		
		System.out.println(name() + "::post, set of variables _S = " + _S.size());
		
		_x=new VarIntLS[_S.size()];
		int u=0;
		for(VarIntLS e:_S)
		{
			_x[u]=e;
			u++;
		}
		
		_minV=1000000000;
		_maxV=-100000000;
		for(int i=0;i<_f.length;i++)
		{
			if(_f[i].getMinValue()<_minV)
			{
				_minV=_f[i].getMinValue();
			}
			
			if(_f[i].getMaxValue()>_maxV)
			{
				_maxV=_f[i].getMaxValue();
			}
			
		}
		
		
		_occ=new int[_maxV-_minV+1];
		
		for(int i=0;i<_occ.length;i++)
		{
			_occ[i]=0;
		}
		
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		
		_map1=new HashMap<VarIntLS, Vector<IFunction>>();
		
		for(VarIntLS e:_S)
		{
			_map1.put(e, new Vector<IFunction>());
		}
		
		for(int i=0;i<_f.length;i++)
		{
			VarIntLS[] s=_f[i].getVariables();
			if(s!=null)
			{
				for(int j=0;j<s.length;j++)
				{
					_map1.get(s[j]).add(_f[i]);
					
				}
			}
				
		}
		
		
		
		_minValue=0;
		_maxValue=_f1.length;
		
		
		
		
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

	
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		
		if(_map.get(x)==null) return 0;
		
		int nv=0;
		Vector<IFunction> F=_map1.get(x);
		
		for(IFunction f:F)
		{
			int u=f.getValue()-_minV;
			if(_occ[u]>0)
			{
				_occ[u]--;
			}
		}
		for(IFunction f:F)
		{
			int u=f.getValue()+f.getAssignDelta(x, val)-_minV;
			_occ[u]++;
		}
		
		nv=_occ[_f2.getValue()+_f2.getAssignDelta(x, val)-_minV]-1;
		
		for(IFunction f:F)
		{
			int v1=f.getValue()-_minV;
			int v2=f.getValue()+f.getAssignDelta(x, val)-_minV;
			_occ[v1]++;
			_occ[v2]--;
		}
		
		
		
		
		return nv-_value;
		
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		
		if(_map.get(x)==null&&_map.get(y)==null) return 0;
		if(_map.get(x)!=null&&_map.get(y)==null) return getAssignDelta(x,y.getValue());
		if(_map.get(x)==null&&_map.get(y)!=null) return getAssignDelta(y,x.getValue());
		int nv=0;
		Vector<IFunction> F1=_map1.get(x);
		Vector<IFunction> F2=_map1.get(y);
		HashSet<IFunction> F=new HashSet<IFunction>();
		
		if(F1!=null)
		{
			for(IFunction f:F1)
			{
				F.add(f);
			}
		}
		if(F2!=null)
		{
			for(IFunction f:F2)
			{
				F.add(f);
			}
			
		}
		for(IFunction f:F)
		{
			int u=f.getValue()-_minV;
			if(_occ[u]>0)
			{
				_occ[u]--;
			}
		}
		
		for(IFunction f:F)
		{
			int u=f.getValue()+f.getSwapDelta(x, y)-_minV;
			_occ[u]++;
		}
		
		nv=_occ[_f2.getValue()+_f2.getSwapDelta(x, y)-_minV]-1;
		
		for(IFunction f:F)
		{
			int v1=f.getValue()+f.getSwapDelta(x, y)-_minV;
			int v2=f.getValue()-_minV;
			
			_occ[v1]--;
			_occ[v2]++;
			
		}
		
		
		
		
		
		return nv-_value;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		
if(_map.get(x)==null) return ;
		int t=x.getOldValue();
		int nv=0;
		Vector<IFunction> F=_map1.get(x);
		
		for(IFunction f:F)
		{
			int u=f.getValue()+f.getAssignDelta(x,t)-_minV;
			if(_occ[u]>0)
			{
				_occ[u]--;
			}
		}
		for(IFunction f:F)
		{
			int u=f.getValue()-_minV;
			_occ[u]++;
		}
		
		nv=_occ[_f2.getValue()-_minV]-1;
		
		
		
		
		
		
		_value=nv;
	
	}
		

	@Override
	public void initPropagate() {
		for(int i=0;i<_f.length;i++)
		{
			_occ[_f[i].getValue()-_minV]++;
		}
		
		
		int a=_f2.getValue()-_minV;
		
		
		_value=_occ[a]-1;
		
		
		

	}
	
	
	@Override
	public boolean verify(){
		int nv=0;
		for(int i=0;i<_f1.length;i++)
		{
			if(_f2.getValue()==_f1[i].getValue())
			{
				nv++;
			}
		}
		if(nv==_value)
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
		LocalSearchManager ls=new LocalSearchManager();
	       VarIntLS[] x=new VarIntLS[1000];
	       for(int i=0;i<x.length;i++)
	       {
	    	   x[i]=new VarIntLS(ls,0,10000);
	    	   x[i].setValue(i);
	    	   
	    	   
	       }
	    for(int i=0;i<x.length/2;i++)
	    {
	    	x[i].setValue(2);
	    }
	       IFunction[] f=new IFunction[x.length];
	       for(int i=0;i<f.length;i++)
	       {
	    	   f[i]=new FuncPlus(x[i],1);
	       }
	       IFunction f1=new FuncPlus(x[1], x[9]);
	       OccurrenceFunctionFunction o=new OccurrenceFunctionFunction(f, f1);
	       ls.close();
	       System.out.println("  o  =   "+o.getValue());
	       System.out.println(o.getAssignDelta(x[1],5));
	      x[1].setValuePropagate(5);
	      System.out.println("o =   "+o.getValue());
	      
	      localsearch.applications.Test t=new localsearch.applications.Test();
	      t.test(o,10000);
	      
	      /*
	      int oldv=o.getValue();
	      int dem=0;
	      for(int i=0;i<100000;i++)
	      {
	    	  int r1=(int)(Math.random()*1000);
	    	  int r2=(int)(Math.random()*3);
	    	  int dv=o.getAssignDelta(x[r1], r2);
	    	  x[r1].setValuePropagate(r2);
	    	  int dd=o.getValue();
	    	  if(dd==dv+oldv&&o.verify()==true)
	    	  {
	    		  oldv=dd;
	    		  dem++;
	    	  }
	    	  else
	    	  {
	    		  break;
	    	  }
	      }
	       
	       System.out.println("dem =  "+dem+"   onew  =   "+o.getValue());
            
            */
	}

}
