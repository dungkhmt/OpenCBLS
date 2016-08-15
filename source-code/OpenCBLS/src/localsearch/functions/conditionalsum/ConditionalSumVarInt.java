package localsearch.functions.conditionalsum;

import java.util.HashMap;
import java.util.HashSet;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ConditionalSumVarInt extends AbstractInvariant implements
		IFunction {

	private int _value;
	private int _minValue;
	private int _maxValue;
	private int _val;
	
	
	private int[]  _w;
	private VarIntLS[] _x;
	
	private LocalSearchManager _ls;
	private HashMap<VarIntLS, Integer> _map;
	private boolean _posted;
	public ConditionalSumVarInt(VarIntLS[] x, int[] w, int val) {
		_x=x;
		_w=w;
		_val=val;
		_ls=x[0].getLocalSearchManager();
		_posted = false;
		post();
	}

	private void post() {
		if(_posted) return;
		_posted = true;
		
		HashSet<VarIntLS> _S=new HashSet<VarIntLS>();
		for(int i=0;i<_x.length;i++)
		{
			_S.add(_x[i]);
		}
		_map=new HashMap<VarIntLS, Integer>();
		for(int i=0;i<_x.length;i++)
		{
			_map.put(_x[i],i);
		}
		
		_minValue = 0;
		_maxValue = 0;
		for(int i = 0; i < _w.length; i++)
			_maxValue = _maxValue + _w[i];
		
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
		//if(!(x.IsElement(_x))) return 0;
		if(_map.get(x) == null) return 0;
		int nv=_value;
		int k=_map.get(x);
		if(x.getValue()==_val)
		{
			if(_val==val)
			{
				nv=nv;
			}
			else
			{
				nv=nv-_w[k];
			}
		}
		else
		{
			if(_val==val)
			{
				nv=nv+_w[k];
			}
			else
			{
				nv=nv;
			}
		}
		
		return nv-_value;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		//if(!(x.IsElement(_x))&&!(y.IsElement(_x))) return 0;
		//if((x.IsElement(_x))&&!(y.IsElement(_x))) return getAssignDelta(x,y.getValue());
		//if(!(x.IsElement(_x))&&(y.IsElement(_x))) return getAssignDelta(y, x.getValue());
		if(_map.get(x) == null&&_map.get(y) == null) return 0;
		if(_map.get(x) != null&&_map.get(y) == null) return getAssignDelta(x,y.getValue());
		if(_map.get(x) == null&&_map.get(y) != null) return getAssignDelta(y, x.getValue());
		
		int nv=_value;
		int k1=_map.get(x);
		int k2=_map.get(y);
		if(x.getValue()==_val&&y.getValue()==_val)
		{
			nv=nv;
		}
		else if(x.getValue()==_val&&y.getValue()!=_val)
		{
			nv=nv-_w[k1]+_w[k2];
		}
		else
			if(x.getValue()!=_val&&y.getValue()==_val)
			{
				nv=nv-_w[k2]+_w[k1];
			}
			else if(x.getValue()!=_val&&y.getValue()!=_val)
			{
				nv=nv;
			}
			return nv-_value;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		//if(!(x.IsElement(_x))) return ;
		if(_map.get(x) == null) return ;
		int nv=_value;
		int t=x.getOldValue();
		
		int k=_map.get(x);
		if(t==_val)
		{
			if(x.getValue()==_val)
			{
				nv=nv;
			}
			else
			{
				nv=nv-_w[k];
			}
		}
		else
		{
			if(x.getValue()==_val)
			{
				nv=nv+_w[k];
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
		int tong=0;
		for(int i=0;i<_x.length;i++)
		{
			if(_val==_x[i].getValue())
			{
				tong+=_w[i];
			}
		}
		_value=tong;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		for(int i=0;i<_x.length;i++)
		{
			if(_x[i].getValue()==_val)
			{
				nv+=_w[i];
			}
		}
		if(nv==_value)
			return true;
		else
		System.out.println(name() + "::verify --> failed, _value = " + _value + " which differs from new value = " + nv + " by recomputation");
		return false;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	public static void main(String[] args) {
		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[1000];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10);
			x[i].setValue(i);
		}
		for(int i=0;i<500;i++)
		{
			x[i].setValue(2);
		}
		int[] b = new int[x.length];
		for (int i = 0; i < b.length; i++) {
			b[i] = 10;
		}
		//b[3]=20;

		ConditionalSumVarInt c = new ConditionalSumVarInt(x, b, 2);
		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(c,100000);
		
		/*
		System.out.println(c.getValue());
		//System.out.println(c.getAssignDelta(x[1],5));
		//x[1].setValuePropagate(5);
		//System.out.println(c.getValue());
		System.out.println(c.getSwapDelta(x[7], x[3]));
		
       
		x[1].setValuePropagate(2);
		x[5].setValuePropagate(2);

		System.out.println(c.getValue());

		int oldv = c.getValue();
		int dem = 0;
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 1000);
			int r2 = (int) (Math.random() * 3);
			int dv = c.getAssignDelta(x[r1], r2);
			System.out.println("dv  =  "+dv);
			x[r1].setValuePropagate(r2);
			
			int dd = c.getValue();
			System.out.println("dd  =   "+dd);
			if (dd == oldv + dv && c.verify()==true) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
		}
		System.out.println("dem  =  " + dem+"   c new  =   "+c.getValue());
       	*/
	}

}
