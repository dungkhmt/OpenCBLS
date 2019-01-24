package localsearch.model;

import core.BasicEntity;

public abstract class AbstractInvariant extends BasicEntity implements Invariant {

	@Override
	public VarIntLS[] getVariables() {
		// TODO Auto-generated method stub
		System.out.println("AbstractInvariant::getVariables, this must be implemented within subclass");
		assert(false);
		return null;
	}

	@Override
	public void propagateInt(VarIntLS x, int val) {
		// TODO Auto-generated method stub
		System.out.println("AbstractInvariant::propagateInt, this must be implemented within subclass");
		assert(false);
	}

	@Override
	public void initPropagate() {
		// TODO Auto-generated method stub
		System.out.println("AbstractInvariant::initPropagate, this must be implemented within subclass");
		assert(false);
	}

	@Override
	public LocalSearchManager getLocalSearchManager() {
		// TODO Auto-generated method stub
		System.out.println("AbstractInvariant::getLocalSearchManager, this must be implemented within subclass");
		assert(false);
		return null;
	}

	@Override
	public boolean verify() {
		// TODO Auto-generated method stub
		assert(false);
		return false;
	}

	public String name(){
		return "AbstractInvariant";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
