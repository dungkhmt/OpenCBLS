package localsearch.functions.element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.*;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Element extends AbstractInvariant implements IFunction {
	private IFunction _f;
	

	public Element(int[] c, IFunction index){
		_f = new ElementConsFun(c,index);
	}
	public Element(int[][] c, int r, VarIntLS x){
		_f = new ElementTwoConstConstVarInt(c,r,x);
	}
	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return _f.getMinValue();
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return _f.getMaxValue();
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _f.getValue();
	}
	@Override
	public VarIntLS[] getVariables() {
		return _f.getVariables();
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _f.getAssignDelta(x,val);
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x,y);
	}
	@Override
	public void propagateInt(VarIntLS x, int val) {
		// DO NOTHING
	}

	@Override
	public void initPropagate() {
		// DO NOTHING
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		return _f.verify();
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _f.getLocalSearchManager();
	}
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		LocalSearchManager ls=new LocalSearchManager();
		int n = 1;
		int m = 1000;
		VarIntLS[] x=new VarIntLS[n];
		java.util.Random R = new java.util.Random();
		for(int i=0;i<x.length;i++)
		{
			x[i]=new VarIntLS(ls,0,m-1);
		}
		int[] c = new int[m];
		for(int i = 0; i < c.length; i++)
			c[i] = R.nextInt(m);
		IFunction index = new FuncVarConst(x[0]);
		
		IFunction F = new Element(c,index);
		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(F, 1000);
		
	}
		
}
