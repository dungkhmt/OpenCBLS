package localsearch.functions.element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.*;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ElementTmp extends AbstractInvariant implements IFunction {
	private int          _length;
	private int _value;
	private int _minValue;
	private int _maxValue;
	private IFunction _f1;
	private IFunction _f2;
	private VarIntLS[] _x;
	private LocalSearchManager _ls;
	private IFunction[]    _f;
	private IFunction[]     _ff;
	private HashMap<VarIntLS, Vector<IFunction>>     _map1;
	private HashMap<IFunction, Integer>             _map2;
	private int[]                           _a;
	

	public ElementTmp(int[] c, IFunction index){
		_length=1;
		_f=new IFunction[c.length];
		for(int i=0;i<_f.length;i++)
		{
			_f[i]=new FuncVarConst(index.getLocalSearchManager(),c[i]);
		}
		_f1=index;
		_f2=new FuncVarConst(index.getLocalSearchManager(),0);
		_ls=index.getLocalSearchManager();
		post();
		
		
	}
	public ElementTmp(VarIntLS[] x, IFunction index){
		_length=1;
		_f=new IFunction[x.length];
		for(int i=0;i<_f.length;i++)
		{
			_f[i]=new FuncVarConst(x[i]);
		}
		_f1=index;
		_f2=new FuncVarConst(_f[0].getLocalSearchManager(),0);
		_ls=_f[0].getLocalSearchManager();
		post();
		
	}
	public ElementTmp(int[] c, VarIntLS index){
		_length=1;
		_f=new IFunction[c.length];
		for(int i=0;i<_f.length;i++)
		{
			_f[i]=new FuncVarConst(index.getLocalSearchManager(), c[i]);
		}
		_f1=new FuncVarConst(index);
		_f2=new FuncVarConst(index.getLocalSearchManager(), 0);
		_ls=index.getLocalSearchManager();
		post();
		
		
	}
	public ElementTmp(VarIntLS[] x, VarIntLS index){
		_length=1;
		_f=new IFunction[x.length];
		for(int i=0;i<_f.length;i++)
		{
			_f[i]=new FuncVarConst(x[i]);
		}
		_f1=new FuncVarConst(index);
		_f2=new FuncVarConst(x[0].getLocalSearchManager(),0);
		_ls=x[0].getLocalSearchManager();
		post();
		
		
	}
	public ElementTmp(int[][] c, IFunction i, IFunction j){
		_f1=i;
		_f2=j;
		int n=c.length;
		int m=c[0].length;
		_length=m;
		_f=new IFunction[n*m];
		for(int u=0;u<n;u++)
		{
			for(int v=0;v<m;v++)
			{
				int k=m*u+v;
				_f[k]=new FuncVarConst(i.getLocalSearchManager(),c[u][v]);
			}
		}
		_ls=i.getLocalSearchManager();
		post();
		
		
	}
	public ElementTmp(VarIntLS[][] x, IFunction i, IFunction j){
		_f1=i;
		_f2=j;
		int n=x.length;
		int m=x[0].length;
		_length=m;
		_f=new IFunction[n*m];
		for(int u=0;u<n;u++)
		{
			for(int v=0;v<m;v++)
			{
				int k=m*u+v;
				_f[k]=new FuncVarConst(x[u][v]);
			}
		}
		_ls=i.getLocalSearchManager();
		post();
		
	}
	public ElementTmp(int[][] c, VarIntLS i, VarIntLS j){
		_f1=new FuncVarConst(i);
		_f2=new FuncVarConst(j);
		int n=c.length;
		int m=c[0].length;
		_length=m;
		_f=new IFunction[n*m];
		for(int u=0;u<n;u++)
		{
			for(int v=0;v<m;v++)
			{
				int k=m*u+v;
				_f[k]=new FuncVarConst(i.getLocalSearchManager(),c[u][v]);
			}

		}
		_ls=i.getLocalSearchManager();
		post();
		
	}
	public ElementTmp(VarIntLS[][] x, VarIntLS i, VarIntLS j){
		_f1=new FuncVarConst(i);
		_f2=new FuncVarConst(j);
		int n=x.length;
		int m=x[0].length;
		_length=m;
		_f=new IFunction[n*m];
		for(int u=0;u<n;u++)
		{
			for(int v=0;v<m;v++)
			{
				int k=m*u+v;
				_f[k]=new FuncVarConst(x[u][v]);
			}
		}
		_ls=i.getLocalSearchManager();
		post();
		
		
	}
	void post()
	{
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
		VarIntLS[] x1=_f1.getVariables();
		VarIntLS[] x2=_f2.getVariables();
		if(x1!=null)
		{
		for(int i=0;i<x1.length;i++)
		{
			_S.add(x1[i]);
		}
		}
		if(x2!=null)
		{
		for(int i=0;i<x2.length;i++)
		{
			_S.add(x2[i]);
		}
		}
		
		
		_x=new VarIntLS[_S.size()];
		int u=0;
		for(VarIntLS e: _S)
		{
			_x[u]=e;
			u++;
		}
		
		
		_ff=new IFunction[_f.length+2];
		for(int i=0;i<_f.length;i++)
		{
			_ff[i]=_f[i];
		}
		_ff[_f.length]=_f1;
		_ff[_f.length+1]=_f2;
		_map2=new HashMap<IFunction, Integer>();
		for(int i=0;i<_ff.length;i++)
		{
			_map2.put(_ff[i],i);
		}
		_map1=new HashMap<VarIntLS, Vector<IFunction>>();
		for(VarIntLS e:_S)
		{
			_map1.put(e, new Vector<IFunction>());
		}
		for(int i=0;i<_ff.length;i++)
		{
			VarIntLS[] s=_ff[i].getVariables();
			if(s!=null)
			{
			for(int j=0;j<s.length;j++)
			{
				_map1.get(s[j]).add(_ff[i]);
			}
			}
		}
		
		
		_a=new int[_ff.length];
		for(int i=0;i<_a.length;i++)
		{
			_a[i]=_ff[i].getValue();
		}
		;
		
		
		
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
		int nv=0;
		Vector<IFunction> F=_map1.get(x);
		for(IFunction f:F)
		{
			
			int r=_map2.get(f);
			_a[r]=f.getValue()+f.getAssignDelta(x, val);
			
		}
		
		int u=_a[_f.length];
		
		
		int v=_a[_f.length+1];
		
		int k=u*_length+v;
	
		nv=_a[k];
		
		
		for(IFunction f:F)
		{
			int r=_map2.get(f);
			_a[r]=f.getValue();
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
		Vector<IFunction> F=_map1.get(x);
		for(IFunction f:F)
		{
			int r=_map2.get(f);
			_a[r]=f.getValue();
		}
		int u=_a[_f.length];
		int v=_a[_f.length+1];
		int k=_length*u+v;
		
		_value=_a[k];
		
		
	}

	@Override
	public void initPropagate() {
		
		int i=_a[_f.length];
		int j=_a[_f.length+1];
		
	
		
	
		
		int k=_length*i+j;
		
		_value=_a[k];
		
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
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
		VarIntLS[][] x1=new VarIntLS[10][10];
		for(int i=0;i<10;i++)
		{
			for(int j=0;j<10;j++)
			{
				x1[i][j]=new VarIntLS(ls, 0, 100);
				x1[i][j].setValue(i+j);
			}
		}
		int [][]x2=new int[10][10];
		for(int i=0;i<10;i++)
		{
		for(int j=0;j<10;j++)
		{
			x2[i][j]=i+j;
		}
		}
		IFunction f1=new FuncPlus(x[1],1);
		IFunction f2=new FuncPlus(x[2], 5);
		ElementTmp t=new ElementTmp(x2,x[5],x[3]);
		ls.close();
		System.out.println(t.getValue());
		//System.out.println(t.getAssignDelta(x[1],5));
		//x[1].setValuePropagate(5);
		//System.out.println(t.getValue());
		
		int oldv=t.getValue();
		int dem=0;
		for(int i=0;i<10000;i++)
		{
			int r1=(int)(Math.random()*3);
			int r2=(int)(Math.random()*4);
			int r3=(int)(Math.random()*100);
			int dv=t.getAssignDelta(x1[r1][r2],r3);
			x1[r1][r2].setValuePropagate(r3);
			int dd=t.getValue();
			
			if(dd==dv+oldv)
			{
				oldv=dd;
				dem++;
			}
			else
			{
				System.out.println("ERROR"); break;
			}
		}
		
		System.out.println("dem  =  "+dem);
		
		
			}

}
