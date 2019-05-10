package localsearch.search;

public class Move {

	/**
	 * @param args
	 */
	private MoveType type;
	private double evaluation;
	public Move(MoveType type, double evaluation){
		this.type = type;
		this.evaluation = evaluation;
	}
	public MoveType getType(){ return type;}
	public double getEvaluation(){ return evaluation;}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
