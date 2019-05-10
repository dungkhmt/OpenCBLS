package localsearch.functions.element;



import java.util.HashSet;

import localsearch.functions.basic.*;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ElementConsFun extends AbstractInvariant implements IFunction {
	
	private int   _value;
	private int   _minValue;
	private int   _maxValue;
	private VarIntLS[]     _x;
	private IFunction       _f;
	private LocalSearchManager    _ls;
	private int[]                    _c;
	
	
	public ElementConsFun(int[] c,IFunction index)
	{
		_c=c;
		_f=index;
		_ls=index.getLocalSearchManager();
		post();
	}
	void post()
	{
		HashSet<VarIntLS> _S=new HashSet<VarIntLS>();
		VarIntLS[] x1=_f.getVariables();
		if(x1!=null)
		{
			for(int i=0;i<x1.length;i++)
			{
				_S.add(x1[i]);
			}
		}
		int u=0;
		_x=new VarIntLS[_S.size()];
		for(VarIntLS e:_S)
		{
			_x[u]=e;
			u++;
		}
		_minValue = _c[0];
		_maxValue = _c[0];
		for(int i = 0; i < _c.length; i++){
			if(_minValue > _c[i]) _minValue = _c[i];
			if(_maxValue < _c[i]) _maxValue = _c[i];
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
		if(!(x.IsElement(_x))) return 0;
		
		if(_f.getValue()==_f.getValue()+_f.getAssignDelta(x, val)) return 0;
		else
		{
			int nv=0;
			int a=_f.getValue()+_f.getAssignDelta(x, val);
			nv=_c[a];
			return nv-_value;
		}
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		if(!x.IsElement(_x) && !y.IsElement(_x)) return 0;
		
		int nf = _f.getValue() + _f.getSwapDelta(x, y);
		
		int nv = _c[nf];
		return nv - _value;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(!(x.IsElement(_x))) return;
		_value = _c[_f.getValue()];
		
	}

	@Override
	public void initPropagate() {
		_value=_c[_f.getValue()];
		/*
		_minValue=_c[0];
		_maxValue=_c[0];
		for(int i=0;i<_c.length;i++)
		{
			if(_c[i]<_minValue)
			{
				_minValue=_c[i];
			}
			if(_c[i]>_maxValue)
			{
				_maxValue=_c[i];
						
			}
		}
		*/
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "Element_cons_fun_1";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		for(int i=0;i<_c.length;i++)
		{
			nv=_c[_f.getValue()];
		}
		if(nv==_value)
			return true;
		else
		
		return false;
	}
	public static void main(String[] args)
	{
		LocalSearchManager ls=new LocalSearchManager();
		
		int[] c=new int[]{1,2,3,4,5,6,7,8,9};
		VarIntLS x=new VarIntLS(ls, 0, 100);
		x.setValue(4);
		IFunction f=new FuncPlus(x, 1);
		ElementConsFun e=new ElementConsFun(c,f);
		ls.close();
		System.out.println(e.getValue());
		System.out.println(e.getMaxValue());
		int oldv=e.getValue();
		int dem=0;
		for(int i=0;i<100000;i++)
		{
			int r1=(int)(Math.random()*c.length)-1;
			int dv=e.getAssignDelta(x,r1);
			x.setValuePropagate(r1);
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
		System.out.println("dem  =   "+dem);
		
	}

}
