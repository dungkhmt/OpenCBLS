package localsearch.functions.sum;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class SumFun extends AbstractInvariant implements IFunction {
	private int       _value;
	private int        _minValue;
	private int         _maxValue;
	private IFunction[]     _f;
	private VarIntLS[]      _x;
	private LocalSearchManager       _ls;
	private HashMap<VarIntLS, Vector<IFunction>>     _map;
	private HashMap<VarIntLS, Integer>              _map1;
	private boolean               _posted;
	
	
	public SumFun(IFunction[] f)
	{
		_f=f;
		_ls=f[0].getLocalSearchManager();
		post();
		
	}
	void post()
	{
		if(_posted) return ;
		_posted=true;
		HashSet<VarIntLS>  _S=new HashSet<VarIntLS>();
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
		_x=new VarIntLS[_S.size()];
		int u=0;
		for(VarIntLS e:_S)
		{
			_x[u]=e;
			u++;
		}
		_map=new HashMap<VarIntLS, Vector<IFunction>>();
		for(VarIntLS e:_S)
		{
			_map.put(e, new Vector<IFunction>());
		}
		
		for(int i=0;i<_f.length;i++)
		{
			VarIntLS[] s=_f[i].getVariables();
			if(s!=null)
			{
				for(int j=0;j<s.length;j++)
				{
					_map.get(s[j]).add(_f[i]);
				}
			}
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
		Vector<IFunction> F=_map.get(x);
		for(IFunction f:F)
		{
			nv+=f.getAssignDelta(x, val);
			//System.out.println(f.getAssignDelta(x, val));
		}
		return nv;
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		//System.out.println("SumFun::getSwapDelta-> not implemented yet");
		//System.exit(1);
		if(_map.get(x) == null) return getAssignDelta(y,x.getValue());
		if(_map.get(y) == null) return getAssignDelta(x,y.getValue());
		int nv=0;
		Vector<IFunction> Fx = _map.get(x);
		Vector<IFunction> Fy = _map.get(y);
		HashSet<IFunction> SF = new HashSet<IFunction>();
		for(IFunction f: Fx) SF.add(f);
		for(IFunction f: Fy) SF.add(f);
		for(IFunction f:SF)
		{
			nv+=f.getSwapDelta(x, y);
			//System.out.println(f.getAssignDelta(x, val));
		}
		return nv;
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		if(_map.get(x)==null) return ;
		int nv=0;
		int t=x.getOldValue();
		Vector<IFunction> F=_map.get(x);
		for(IFunction f:F)
		{
			nv-=f.getAssignDelta(x,t);
			
		}
		_value=_value+nv;
	}

	@Override
	public void initPropagate() {
		
		for(int i=0;i<_f.length;i++)
		{
			_value+=_f[i].getValue();
		}
		
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}
	
	public String name(){
		return "sum_fun";
	}
	
	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		int nv=0;
		for(int i=0;i<_f.length;i++)
		{
			nv+=_f[i].getValue();
		}
		if(nv==_value) return true;
		else
		return false;
	}
	public static void main(String []args)
	{
		LocalSearchManager  ls=new LocalSearchManager();
		VarIntLS[] x=new VarIntLS[10000];
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(ls, 0, 10000);
			x[i].setValue(i);
			
		}
		
		IFunction [] f=new IFunction[x.length];
		for(int i=2;i<f.length;i++)
		{
			f[i]=new FuncPlus(x[i],1);
		}
		f[0]=new FuncPlus(x[0],x[1]);
		f[1]=new FuncPlus(x[1], x[2]);
		
		SumFun s =new SumFun(f);
		ls.close();
		System.out.println(s.getValue());
		System.out.println(s.getAssignDelta(x[2],10));
		x[2].setValuePropagate(10);
		System.out.println(s.getValue());
		
		int oldv=s.getValue();
		int dem=0;
		for(int i=0;i<100000;i++)
		{
			int r1=(int)(Math.random()*10000);
			int r2=(int)(Math.random()*100);
			int dv=s.getAssignDelta(x[r1], r2);
			x[r1].setValuePropagate(r2);
			int dd=s.getValue();
			if(dd==dv+oldv&&s.verify()==true)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("ERROR"); break;
			}
			
		}
		System.out.println("dem  =  "+dem+"    snew   =   "+s.getValue());
	}


}
