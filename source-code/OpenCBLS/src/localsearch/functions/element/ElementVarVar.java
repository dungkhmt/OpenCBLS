package localsearch.functions.element;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ElementVarVar extends AbstractInvariant implements IFunction {
	private int           _value;
	private int            _minValue;
	private int             _maxValue;
	private VarIntLS[]       _x;
	private VarIntLS[]          _x1;
	private VarIntLS            _x2;
	private LocalSearchManager     _ls;
	private HashMap<VarIntLS, Integer>     _map;
	private HashMap<VarIntLS, Integer>     _map1;
	private int[]                          _a;
	
	public ElementVarVar(VarIntLS[] x,VarIntLS index)
	{
		_x1=x;
		_x2=index;
		_ls=index.getLocalSearchManager();
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
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x1.length;i++)
		{
			_map.put(_x1[i],i);
		}
		_a=new int[_x1.length];
		for(int i=0;i<_x1.length;i++)
		{
			_a[i]=_x1[i].getValue();
		}
		_map1=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map1.put(_x[i], i);
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
		int nv=0;
		if(_map.get(x)==null)
		{
			nv=_a[val];
		}
		else
		{
			int k=_map.get(x);
			_a[k]=val;
			nv=_a[_x2.getValue()];
			_a[k]=x.getValue();
		}
		return nv-_value;
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
			_value=_a[val];
		}
		else
		{
			int k=_map.get(x);
			_a[k]=val;
			_value=_a[_x2.getValue()];
		}
		
	}

	@Override
	public void initPropagate() {
		_value=_x1[_x2.getValue()].getValue();
		_minValue=_x1[0].getMinValue();
		_maxValue=_x1[0].getMaxValue();
		for(int i=0;i<_x1.length;i++)
		{
			if(_minValue>_x1[i].getMinValue())
			{
				_minValue=_x1[i].getMinValue();
			}
			if(_maxValue<_x1[i].getMaxValue())
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
		return "ElementVarVar";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		nv=_x1[_x2.getValue()].getValue();
		if(nv==_value) return true;
		else
		
		return false;
	}
	public static void main(String[] args)
	{
		LocalSearchManager ls=new LocalSearchManager();
		VarIntLS[] x=new VarIntLS[10];
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(ls, 0, 100);
			x[i].setValue(i);
		}
		VarIntLS x1=new VarIntLS(ls, 0, 100);
		x1.setValue(3);
		ElementVarVar e=new ElementVarVar(x, x1);
		ls.close();
		System.out.println(e.getValue());
		int oldv =e.getValue();
		int dem=0;
		for(int i=0;i<100000;i++)
		{
			int r1=(int)(Math.random()*x.length);
			int r2=(int)(Math.random()*100);
			int dv=e.getAssignDelta(x[r1], r2);
			x[r1].setValuePropagate(r2);
			int dd=e.getValue();
			if(dd==dv+oldv&& e.verify()==true)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("ERROR"); break;
			}
		}
		
			System.out.println("dem   =   "+dem+"   enew   =   "+e.getValue());
	
	}
}
