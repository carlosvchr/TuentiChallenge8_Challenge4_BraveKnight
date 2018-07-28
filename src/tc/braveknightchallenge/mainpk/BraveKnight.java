package tc.braveknightchallenge.mainpk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;

public class BraveKnight {

	public static final char GROUND = '.';
	public static final char LAVA = '#';
	public static final char TRAMPOLINE = '*';
	public static final char KNIGHT = 'S';
	public static final char PRINCESS = 'P';
	public static final char GOAL = 'D';
	
	
	public static void main(String[] args) {
		Scanner s = new Scanner(System.in);	
		// Gets the input file path from the user.
		System.out.print("Input file path: ");
		String ipath = s.nextLine();
		
		// Gets the input file path from the user.
		System.out.print("Output file path (e.g. /home/user/output.txt): ");		
		String opath = s.nextLine();
		
		// Closes the scanner stream
		s.close();
		
		// Gets the reader and writer
		BufferedReader br = IOManager.getReader(ipath);
		PrintWriter pw = IOManager.getWriter(opath);

		// Check writer and reader has been created successfully. Else, ends the program safely.
		if(br == null || pw == null) {
			System.out.println("Error when reader or writer has been created.");
			return;
		}
		
		// Calculates all cases and generate the output
		if(processCases(br, pw)) {
			System.out.println("Results has been generated successfully!");
		}else {
			System.out.println("Terminated with errors.");
		}
		
		// Closes input and output streams
		IOManager.closeStreams(br, pw);

	}
	
	
	/** Process all cases and write the output */
	public static boolean processCases(BufferedReader br, PrintWriter pw) {
		try {
			int ncases = Integer.parseInt(br.readLine());
			// Iterate all cases
			for(int i=0; i<ncases; i++) {
				// Each readed line corresponds to a row
				String rc[] = br.readLine().split(" ");
				int rows = Integer.parseInt(rc[0]);
				int cols = Integer.parseInt(rc[1]);
				Vec2 knight = new Vec2(0,0);
				Vec2 princess = new Vec2(0,0);
				Vec2 goal = new Vec2(0,0);
				
				// Create the map and get the knight, princess and goal positions
				char map[][] = new char[rows][cols];
				for(int j=0; j<map.length; j++) {
					map[j] = br.readLine().toCharArray();		
					for(int k=0; k<map[j].length; k++) {
						switch(map[j][k]) {
						case KNIGHT:
							map[j][k] = GROUND;
							knight = new Vec2(j, k);
							break;
						case PRINCESS:
							map[j][k] = GROUND;
							princess = new Vec2(j, k);
							break;
						case GOAL:
							map[j][k] = GROUND;
							goal = new Vec2(j, k);
							break;
						default: break;	
						}
					}
				}
				// Prints the percent processed
				if(ncases>0)
					System.out.println(i*100/ncases+"%");
				
				/* Min number of jumps from knight to princess 
				 * and then from princess to goal.*/
				int k2p = pathFinding(map, knight, princess);
				int p2g = pathFinding(map, princess, goal);
				
				/* If some path was unreachable, then print IMPOSSIBLE */
				if(k2p!=-1 && p2g!=-1) {				
					pw.println("Case #"+(i+1)+": "+(k2p+p2g));
				}else {
					pw.println("Case #"+(i+1)+": "+"IMPOSSIBLE");
				}
			}
		}catch(NumberFormatException nfe) {
			// Error when parsing number of cases
			System.out.println("File hasn't got a right format.");
			nfe.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	/** Return the minimum number of steps from a origin (o) to a destination (d) */
	public static int pathFinding(char map[][], Vec2 o, Vec2 d) {
		ArrayList<Node> nodes = new ArrayList<>();
		Hashtable<String, Node> visited = new Hashtable<>();
		Node origin = new Node(o, 0, getEstimatedSteps(map, o, d));
		nodes.add(origin);
		visited.put(origin.key(), origin);
		
		/* Always finishes because it cannot repeat a node 
		 * So in case it does not find a solution, it is going 
		 * to check all nodes just once and then finishing.*/		
		while(true) {
			Node cur = getMostPromising(nodes);
			nodes.remove(cur);
			visited.replace(cur.key(), new Node(null, 0, 0));
			
			// Goal found
			if(cur.getPos().eq(d)) {
				return cur.getSteps();
			}
					
			// Get the neighbours of the current node
			Node neighbours[] = getNeighbours(map, cur.getPos(), cur.getSteps(), d);
			
			// Insert the neighbours not visited
			insertUnique(nodes, visited, neighbours);	
					
			if(nodes.size() == 0) {
				return -1;
			}
		}

	}
	
	/** Adds all neighbours not visited to the list */
	public static void insertUnique(ArrayList<Node> nodes, Hashtable<String, Node> v, Node[] neighbours) {
		for(Node n : neighbours) {
			if(!v.containsKey(n.key())) {
				nodes.add(n);
				v.put(n.key(), n);
			}else {
				if(v.get(n.key()).getPos()!=null) {
					if(nodes.get(nodes.indexOf(v.get(n.key()))).getSteps()>n.getSteps()) {
						nodes.get(nodes.indexOf(v.get(n.key()))).setSteps(n.getSteps());
					}
				}
			}
		}
	}
	
	/** Get most promise node */
	public static Node getMostPromising(List<Node> nodes) {
		Node n[] = nodes.toArray(new Node[nodes.size()]);
		Arrays.sort(n);
		return n[0];
	}
	
	/** Calculate the estimated steps (best case) from the current node to the destination */
	public static int getEstimatedSteps(char map[][], Vec2 o, Vec2 d) {
		// The best case is by using trampolines
		int hdist = Math.abs(o.col() - d.col());
		int vdist = Math.abs(o.row() - d.row());
		int maxdist = (hdist>vdist) ? hdist : vdist;
		// We can move in every direction by 1, 2, or 4 so we will count minimum needed jumps
		return maxdist/4 + (maxdist%4/2) + (maxdist%4%2);
	}
	
	/** Get a list of valid neighbours for a given node */
	public static Node[] getNeighbours(char map[][], Vec2 node, int steps, Vec2 d){
		
		List<Vec2> neighbours = new ArrayList<>();
		int w = map.length;
		int h = map[0].length;
		
		if(map[node.row()][node.col()] == GROUND) {
			if(node.row()+2<w && node.col()+1<h) {
				if(map[node.row()+2][node.col()+1]!=LAVA) {
					neighbours.add(new Vec2(node.row()+2, node.col()+1));
				}
			}
			if(node.row()+2<w && node.col()-1>=0) {
				if(map[node.row()+2][node.col()-1]!=LAVA) {
					neighbours.add(new Vec2(node.row()+2, node.col()-1));
				}
			}
			if(node.row()-2>=0 && node.col()+1<h) {
				if(map[node.row()-2][node.col()+1]!=LAVA) {
					neighbours.add(new Vec2(node.row()-2, node.col()+1));
				}
			}
			if(node.row()-2>=0 && node.col()-1>=0) {
				if(map[node.row()-2][node.col()-1]!=LAVA) {
					neighbours.add(new Vec2(node.row()-2, node.col()-1));
				}
			}
			if(node.row()+1<w && node.col()+2<h) {
				if(map[node.row()+1][node.col()+2]!=LAVA) {
					neighbours.add(new Vec2(node.row()+1, node.col()+2));
				}
			}
			if(node.row()+1<w && node.col()-2>=0) {
				if(map[node.row()+1][node.col()-2]!=LAVA) {
					neighbours.add(new Vec2(node.row()+1, node.col()-2));
				}
			}
			if(node.row()-1>=0 && node.col()+2<h) {
				if(map[node.row()-1][node.col()+2]!=LAVA) {
					neighbours.add(new Vec2(node.row()-1, node.col()+2));
				}
			}
			if(node.row()-1>=0 && node.col()-2>=0) {
				if(map[node.row()-1][node.col()-2]!=LAVA) {
					neighbours.add(new Vec2(node.row()-1, node.col()-2));
				}
			}			
		}else if(map[node.row()][node.col()] == TRAMPOLINE) {
			if(node.row()+4<w && node.col()+2<h) {
				if(map[node.row()+4][node.col()+2]!=LAVA) {
					neighbours.add(new Vec2(node.row()+4, node.col()+2));
				}
			}
			if(node.row()+4<w && node.col()-2>=0) {
				if(map[node.row()+4][node.col()-2]!=LAVA) {
					neighbours.add(new Vec2(node.row()+4, node.col()-2));
				}
			}
			if(node.row()-4>=0 && node.col()+2<h) {
				if(map[node.row()-4][node.col()+2]!=LAVA) {
					neighbours.add(new Vec2(node.row()-4, node.col()+2));
				}
			}
			if(node.row()-4>=0 && node.col()-2>=0) {
				if(map[node.row()-4][node.col()-2]!=LAVA) {
					neighbours.add(new Vec2(node.row()-4, node.col()-2));
				}
			}
			if(node.row()+2<w && node.col()+4<h) {
				if(map[node.row()+2][node.col()+4]!=LAVA) {
					neighbours.add(new Vec2(node.row()+2, node.col()+4));
				}
			}
			if(node.row()+2<w && node.col()-4>=0) {
				if(map[node.row()+2][node.col()-4]!=LAVA) {
					neighbours.add(new Vec2(node.row()+2, node.col()-4));
				}
			}
			if(node.row()-2>=0 && node.col()+4<h) {
				if(map[node.row()-2][node.col()+4]!=LAVA) {
					neighbours.add(new Vec2(node.row()-2, node.col()+4));
				}
			}
			if(node.row()-2>=0 && node.col()-4>=0) {
				if(map[node.row()-2][node.col()-4]!=LAVA) {
					neighbours.add(new Vec2(node.row()-2, node.col()-4));
				}
			}
		}
		
		Node[] nodeNeighbours = new Node[neighbours.size()];
		
		for(int i=0; i<neighbours.size(); i++) {
			nodeNeighbours[i] = new Node(
					neighbours.get(i),
					steps+1,
					getEstimatedSteps(map, neighbours.get(i), d)
					);
		}
		
		return nodeNeighbours;
	}
	
	
}
