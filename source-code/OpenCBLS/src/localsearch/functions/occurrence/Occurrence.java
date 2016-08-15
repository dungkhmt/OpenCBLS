package localsearch.functions.occurrence;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;


import localsearch.functions.basic.FuncPlus;
import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Occurrence extends AbstractInvariant implements IFunction {

	IFunction	_f;
	//private LocalSearchManager _ls;
	
	public Occurrence(IFunction[] f, int val) {
		// maintain the number of occurrences of the value val in the array x
		//_ls=f[0].getLocalSearchManager();
		_f = new OccurrenceFunctionConstant(f,val);
		post();

	}

	public Occurrence(VarIntLS[] x, int val) {
		// maintain the number of occurrences of the value val in the array x
		//_ls=x[0].getLocalSearchManager();
		_f = new OccurrenceVarIntLSConstant(x, val);
		
		post();
	}
	public Occurrence(IFunction[] lstF, IFunction f){
		//_ls=lstF[0].getLocalSearchManager();
		_f = new OccurrenceFunctionFunction(lstF,f);
		post();
	}
	public Occurrence(VarIntLS[] lstX, VarIntLS x){
		//_ls=lstX[0].getLocalSearchManager();
		
		_f = new OccurrenceVarIntLSVarIntLS(lstX,x);
		post();
	}
	void post() {
		//_ls = _f.getLocalSearchManager();
		//_ls.post(this);

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
		return _f.getAssignDelta(x, val);
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _f.getSwapDelta(x, y);
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// DO NOTHING, _f has been propagated
	}

	@Override
	public void initPropagate() {
		// DO NOTHING, _f has been propagated
	}
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		return _f.getLocalSearchManager();
	}
	public boolean verify() {
		// TODO Auto-generated method stub
		
		return _f.verify();
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LocalSearchManager ls = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[10];

		for (int i = 0; i < x.length; i++) {
			x[i] = new VarIntLS(ls, 0, 100);
			x[i].setValue(i);
		}

		IFunction[] f = new IFunction[x.length];
		for (int i = 0; i < f.length; i++) {
			f[i] = new FuncPlus(x[i], 1);
		}
		Occurrence o = new Occurrence(f, 5);
		ls.close();
		
		localsearch.applications.Test T = new localsearch.applications.Test();
		T.test(o, 10000);
		/*
		for(int i = 0; i < x.length; i++)
			System.out.println("x[" +  i + "] = " + x[i].getValue() + ", f[" + i + "] = " + f[i].getValue());
		System.out.println("o   =   " + o.getValue());
		
		x[3].setValuePropagate(4);
		for(int i = 0; i < x.length; i++)
			System.out.println("x[" +  i + "] = " + x[i].getValue() + ", f[" + i + "] = " + f[i].getValue());
		System.out.println("o   =   " + o.getValue());
		
		x[1].setValuePropagate(4);
		for(int i = 0; i < x.length; i++)
			System.out.println("x[" +  i + "] = " + x[i].getValue() + ", f[" + i + "] = " + f[i].getValue());
		System.out.println("o   =   " + o.getValue());
		
		//if(true) return;
		int oldv = o.getValue();
		int dem = 0;

		for (int i = 0; i < 1000000; i++) {
			int r1 = (int) (Math.random() * 10);
			int r2 = (int) (Math.random() * 100);
			int dv = o.getAssignDelta(x[r1], r2);
			x[r1].setValuePropagate(r2);
			int dd = o.getValue();
			if (dd == dv + oldv) {
				
				if(!o.verify()){
					System.out.println("error");
					break;
				}
				oldv = dd;
				dem++;
				System.out.println(o.getValue());
			} else {
				System.out.println("ERROR");
				break;
			}
		
		}

		System.out.println("dem = " + dem);
		*/
	}

}
