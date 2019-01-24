package localsearch.functions.element;



import java.util.HashMap;
import java.util.HashSet;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ElementVarFun extends AbstractInvariant implements IFunction {
	private int                _value;
	private int                _minValue;
	private int                _maxValue;
	private VarIntLS[]         _x;
	private IFunction          _f;
    private VarIntLS[]          _x1;
    private LocalSearchManager      _ls;
    private int[]                   _a;
    private HashMap<VarIntLS, Integer>      _map;
    private HashMap<VarIntLS, Integer>       _map1;
    
    
	
	public ElementVarFun(VarIntLS[] x,IFunction index)
	{
		_x1=x;
		_f=index;
		_ls=index.getLocalSearchManager();
		post();
	}
	void post()
	{
		HashSet<VarIntLS> _S=new HashSet<VarIntLS>();
		for(int i=0;i<_x1.length;i++)
		{
			_S.add(_x1[i]);
			
		}
		VarIntLS[] f_x=_f.getVariables();
		if(f_x!=null)
		{
			for(int i=0;i<f_x.length;i++)
			{
				_S.add(f_x[i]);
			}
		}
		int u=0;
		_x=new VarIntLS[_S.size()];
		for(VarIntLS e:_S)
		{
			_x[u]=e;
			u++;
		}
		_map1=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map1.put(_x[i],i);
		}
		_a=new int[_x1.length+1];
		for(int i=0;i<_x1.length;i++)
		{
			_a[i]=_x1[i].getValue();
			
		}
		_a[_x1.length]=_f.getValue();
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x1.length;i++)
		{
			_map.put(_x1[i],i);
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
		if(_map1.get(x)==null) return 0;
		else
		{
		if(_map.get(x)==null)
		{
			int u=_f.getValue()+_f.getAssignDelta(x, val);
			return _a[u]-_value;
		}
		int k=_map.get(x);
		
		if(_f.getValue()==_f.getValue()+_f.getAssignDelta(x, val))
		{
			int nv=0;
		  
		  _a[k]=val;
		  nv=_a[_f.getValue()];
		  _a[k]=x.getValue();
		
		
		  return nv-_value;
		  
		  
		  
				  
		  
		}
		else
		{
			int nv=0;
			
			_a[k]=val;
			int a=_f.getValue()+_f.getAssignDelta(x, val);
			nv=_a[a];
			_a[k]=x.getValue();
			
		
			return nv-_value;
		}
		}
		
		
		
		
		
		
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(_map1.get(x)==null) return ;
		
		if(_map.get(x)==null)
		{
			int u=_f.getValue();
			_value=_a[u];
			
		}
		else
		{
		int k=_map.get(x);
		_a[k]=x.getValue();
		_value=_a[_f.getValue()];
		}
		
		
	}

	@Override
	public void initPropagate() {
		_value=_x1[_f.getValue()].getValue();
		
		_minValue=_x1[0].getMinValue();
		_maxValue=_x1[0].getMaxValue();
		for(int i=0;i<_x1.length;i++)
		{
			if(_minValue<_x1[i].getMinValue())
			{
				_minValue=_x1[i].getMinValue();
			}
			if(_maxValue>_x1[i].getMaxValue())
			{
				_maxValue=_x1[i].getMaxValue();
			}
		}
		
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "ElementVarFun";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		nv=_x1[_f.getValue()].getValue();
		if(nv==_value)
			return true;
		else
		
		return false;
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
LocalSearchManager ls=new LocalSearchManager();
		
		VarIntLS[] x=new VarIntLS[10];
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(ls, 0, 100);
			x[i].setValue(i);
			
		}
		VarIntLS x1=new VarIntLS(ls, 0, 100);
		x1.setValue(3);
		IFunction f=new FuncPlus(x1, 3);
		ElementVarFun e=new ElementVarFun(x, f);
		
		ls.close();
		System.out.println("e    =   "+e.getValue());
		System.out.println("get   =   "+e.getAssignDelta(x[1],2));
		x1.setValuePropagate(2);
		System.out.println("e =   "+e.getValue());
		int oldv =e.getValue();
		int dem=0;
		for(int i=0;i<10000000;i++)
		{
			int r1=(int)(Math.random()*x.length);
			int dv=e.getAssignDelta(x[r1],6);
			x[r1].setValuePropagate(6);
			int dd=e.getValue();
			if(dd==dv+oldv&&e.verify()==true)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("ERROR"); break;
			}
		}
		System.out.println("dem  = "+dem +"  enew   =   "+e.getValue());
		
		
	}		

}
