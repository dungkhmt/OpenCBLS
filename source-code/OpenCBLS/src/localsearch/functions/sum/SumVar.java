package localsearch.functions.sum;



import java.util.HashMap;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class SumVar extends AbstractInvariant implements IFunction {
	private int          _value;
	private int          _minValue;
	private int          _maxValue;
	private VarIntLS[]   _x;
	private int[]           _a;
	private LocalSearchManager     _ls;
	private boolean _posted;
	private HashMap<VarIntLS, Integer>    _map;
	
	
	
	public SumVar(VarIntLS[] x)
	{
		if(x.length==0)
		{
			System.out.println(name() + "::constructor exception, input array is null");
			assert(false);
		}
		_x=x;
		_ls=x[0].getLocalSearchManager();
		_posted = false;
		post();
		
	}
	void post()
	{
		if(_posted) return;
		_posted = true;
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
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
		if(_map.get(x)==null) return 0;
		
		return val-x.getValue();
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(_map.get(x)==null&&_map.get(y)==null) return 0;
		if(_map.get(y)==null&&_map.get(x)!=null) return getAssignDelta(x,y.getValue());
		if(_map.get(y)!=null&&_map.get(x)==null) return getAssignDelta(y, x.getValue());
		return 0;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(_map.get(x)==null) return;
		_value=_value+val-x.getOldValue();
		
	}

	@Override
	public void initPropagate() {
		_value=0;
		for(int i=0;i<_x.length;i++)
		{
			_value+=_x[i].getValue();
			_minValue+=_x[i].getMinValue();
			_maxValue+=_x[i].getMaxValue();
		}
		
		
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "SumVar";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		for(int i=0;i<_x.length;i++)
		{
			nv+=_x[i].getValue();
		}
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
		
		SumVar s=new SumVar(x);
		ls.close();
		System.out.println(s.getValue());
		System.out.println(s.getAssignDelta(x[1],10));
		x[1].setValuePropagate(10);
		System.out.println("snew   =   "+s.getValue());
		int oldv=s.getValue();
		int dem=0;
		for(int i=0;i<100000;i++)
		{
			int r1=(int)(Math.random()*10);
			int r2=(int)(Math.random()*100);
			int dv=s.getAssignDelta(x[r1],r2);
			x[r1].setValuePropagate(r2);
			int dd=s.getValue();
			if(dd==oldv+dv&&s.verify()==true)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("ERROR"); break;
			}
		}
		System.out.println("dem  =   "+dem+"   snew  =   "+s.getValue());
	}

}
