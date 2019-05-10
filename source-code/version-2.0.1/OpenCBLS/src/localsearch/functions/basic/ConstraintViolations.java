package localsearch.functions.basic;

import localsearch.model.*;

public class ConstraintViolations extends AbstractInvariant implements
		IFunction {
	
	private IConstraint _c;
	private boolean		_posted;
	public ConstraintViolations(IConstraint c){
		this._c = c;
		_posted = false;
		post();
	}
	private void post(){
		if(!_posted){
			_posted = true;
			_c.getLocalSearchManager().post(this);
		}
	}
	
	public String name(){
		return "ConstraintViolations";
	}
	@Override
	public int getMinValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		System.out.println(name() + "::getMaxValue() --> this has not been implemented yet");
		assert(false);
		return 0;
	}

	@Override
	public int getValue() {
		// TODO Auto-generated method stub
		return _c.violations();
	}

	@Override
	public int getAssignDelta(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		return _c.getAssignDelta(x, val);
	}

	@Override
	public int getSwapDelta(VarIntLS x, VarIntLS y) {
		// TODO Auto-generated method stub
		return _c.getSwapDelta(x, y);
	}

	public VarIntLS[] getVariables(){
		return _c.getVariables();
	}
	public LocalSearchManager getLocalSearchManager() {
		return _c.getLocalSearchManager();
	}
	public void initPropagate() {
		// DO NOTHING
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
