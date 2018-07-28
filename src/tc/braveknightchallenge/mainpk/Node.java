package tc.braveknightchallenge.mainpk;

public class Node implements Comparable<Node>{

	private Vec2 position;
	private int steps;
	private int estimated;
	
	/** Create a new Node with its position, current jumps, and 
	 * estimated minimum number of jumps to the goal */
	public Node(Vec2 position, int steps, int estimated) {
		this.position = position;
		this.steps = steps;
		this.estimated = estimated;
	}
	
	/** Set the number of jumps done */
	public void setSteps(int n) {
		steps = n;
	}
	
	/** Get the current number of jumps done */
	public int getSteps() {
		return steps;
	}

	/** Get the row and col of this node */
	public Vec2 getPos() {
		return position;
	}
	
	public String key() {
		return position.row()+","+position.col();
	}
	
	@Override
	public int compareTo(Node o) {
		if((this.steps+this.estimated) < (o.steps+o.estimated)) {
			return -1;
		}else if((this.steps+this.estimated) > (o.steps+o.estimated)) {
			return 1;
		}
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return position.eq(((Node)o).position);
	}
	
	@Override
	public String toString() {
		return "Node: ("+position.row()+", "+position.col()+")";
	}
	
}
