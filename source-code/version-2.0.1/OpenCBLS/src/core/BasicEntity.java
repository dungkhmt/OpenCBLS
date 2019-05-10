/*
 * Author: PHAM Quang Dung (dungkhmt@gmail.com)
 */
package core;

import localsearch.model.VarIntLS;

public class BasicEntity {

	/**
	 * @param args
	 */
	
	private int _id;
	public BasicEntity() {};
	public BasicEntity(int id){ this._id = id;}
	public int getID(){ return this._id;}
	public void setID(int id){ this._id = id;}
	/*
	public VarInt[] getVariables(){
		//System.out.println("BasicLocalSearchEntity::getVariables, this must be implemented within subclass");
		assert(false);
		return null;
	}
	public void initPropagate(){
		//System.out.println("BasicLocalSearchEntity::initPropagate, this must be implemented within subclass");
		assert(false);
	}
	
	public void propagateInt(VarIntLS x, int val){
		//System.out.println("BasicLocalSearchEntity::propagate, this must be implemented within subclass");
		assert(false);
		
	}
	*/
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
