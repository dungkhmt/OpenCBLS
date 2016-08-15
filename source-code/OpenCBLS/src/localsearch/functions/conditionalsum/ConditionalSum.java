package localsearch.functions.conditionalsum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import localsearch.functions.basic.*;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ConditionalSum extends AbstractInvariant implements IFunction {

	private IFunction _f;
	private LocalSearchManager _ls = null;
	public ConditionalSum(IFunction[] f, IFunction[] w, IFunction val){
		
		_f = new ConditionalSumFuncFuncFunc(f,w,val);
	}
	public ConditionalSum(VarIntLS[] x, int val){
		int[] w = new int[x.length];
		for(int i = 0; i < x.length; i++)
			w[i] = 1;
		_f = new ConditionalSumVarInt(x,w,val);
	}
	public ConditionalSum(VarIntLS[] x, int[] w, int val){
		
		_f = new ConditionalSumVarInt(x,w,val);
	}
	void post() {
		
		
	}

	public String name(){
		return "ConditionalSum";
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

		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[1000];
		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 10000);
			x[i].setValue(i);
		}
		//x[0].setValue(2);
		for (int i = 0; i < 500; i++) {
			x[i].setValue(2);
		}

		IFunction[] cf = new IFunction[x.length];
		for(int i=0;i<cf.length;i++)
		{
			cf[i]=new FuncPlus(x[i], 1);
		}
       IFunction[] w1=new IFunction[x.length];
		for (int i = 0; i < w1.length; i++) {
			w1[i] = new FuncVarConst(ls,10);
		}
		IFunction val = new FuncVarConst(ls,3);
		ConditionalSum s = new ConditionalSum(cf, w1, val);

		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(s,100000);
		
		/*
		System.out.println(s.getValue());
		
		int oldv = s.getValue();
		int dem = 0;
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 1000);
			System.out.println("r1      =  " + r1);
			int r2 = (int) (Math.random() * 3);

			System.out.println("r2      =  " + r2);
			int dv = s.getAssignDelta(x[r1], r2);

			System.out.println("s.get   =   " + dv);
			x[r1].setValuePropagate(r2);
			int dd = s.getValue();

			System.out.println("value   =   " + dd);
			if (dd == dv + oldv) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
			System.out.println("------------------------------------------");

		}
		System.out.println("dem =  " + dem);
        */     
	}

}