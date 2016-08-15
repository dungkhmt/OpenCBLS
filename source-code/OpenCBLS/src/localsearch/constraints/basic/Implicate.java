package localsearch.constraints.basic;



import java.util.HashSet;

import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import core.*;

public class Implicate extends AbstractInvariant implements IConstraint {

	private IConstraint _c1;
	private IConstraint _c2;
	private VarIntLS[] _x;
	private int _violation;
	private LocalSearchManager _ls;
	
	public Implicate(IConstraint c1,IConstraint c2)
	{
		_c1 = c1;
		_c2 = c2;
		_ls = c1.getLocalSearchManager();
		post();		
	}
	
	public void post()
	{
		HashSet<VarIntLS> _S = new HashSet<VarIntLS>();
		VarIntLS[] x1 = _c1.getVariables();
		VarIntLS[] x2 = _c2.getVariables();
		for (int i = 0; i < _c1.getVariables().length; i++)
			_S.add(x1[i]);					
		for (int i = 0; i < _c2.getVariables().length; i++)
			_S.add(x2[i]);
		_x = new VarIntLS[_S.size()];
		int i = 0;
		for (VarIntLS e : _S){
			_x[i] = e;
			i++;
		}		
		_ls.post(this);
	}
	
	@Override
	public int violations() {
		// TODO Auto-generated method stub
		return _violation;
	}
	
	
	public int violations(VarIntLS x) {
		if (_violation != 0)
			return (x.IsElement(_x)) ? 1 : 0;
		else
			return 0;
	}
	
	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		return _x;
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		int nv;
		int a=_c1.violations();
		int b=_c2.violations();
		int x1=_c1.getAssignDelta(x, val);
		int x2=_c2.getAssignDelta(x, val);
		int nv1=a+x1;
		int nv2=b+x2;
		if(nv1 != 0) 
			nv = 0;
		else 
			nv = nv2;
		return nv - _violation;		
	}
	
	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		int nv;
		int a=_c1.violations();
		int b=_c2.violations();
		int x1=_c1.getSwapDelta(x, y);
		int x2=_c2.getSwapDelta(x, y);
		int nv1=a+x1;
		int nv2=b+x2;
		if (nv1 != 0) 
			nv = 0;
		else 
			nv = nv2;
		return nv - _violation;
	}
	
	public void initPropagate()
	{
		if(_c1.violations()!=0){
			_violation = 0;
		}
		else
			_violation = _c2.violations();
	}
	
	public void propagateInt(VarIntLS x,int val)
	{
		if(_c1.violations() != 0) 
			_violation = 0;
		else
			_violation = _c2.violations();
		
	}
	
	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _ls;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static void main(String []args)
	{
		LocalSearchManager ls=new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(ls);
		VarIntLS x1=new VarIntLS(ls,0, 500);
		VarIntLS x2=new VarIntLS(ls,0, 500);
		VarIntLS y1=new VarIntLS(ls,0, 500);
		VarIntLS y2=new VarIntLS(ls,0, 500);
		VarIntLS z=new VarIntLS(ls,0, 500);
		VarIntLS t=new VarIntLS(ls,0, 500);
		x1.setValue(10);
		x2.setValue(20);
		y1.setValue(15);
		y2.setValue(25);
		z.setValue(20);
		t.setValue(15);
		IFunction f1=new FuncPlus(x1, x2);
		IFunction f2=new FuncPlus(y1, y2);
		//LessOrEqual c1=new LessOrEqual(f1,f2);
		//LessOrEqual c2=new LessOrEqual(z,t);
		Implicate c3=new Implicate(new LessOrEqual(f1, f2),new LessOrEqual(z, t));
		//S.post(c1);
		//S.post(c2);
		S.post(c3);
		S.close();		
		ls.close();
		//System.out.println(c1.violations());
		//System.out.println(c1.getAssignDelta(x1, 20));
		//x1.setValuePropagate(11);
		System.out.println(S.violations());		
		System.out.println(S.getAssignDelta(x1, 30));
	}
}
