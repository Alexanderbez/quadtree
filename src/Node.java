import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.*;

/***********************************************
 * @author Alexander Bezobchuk				   *
 * UID: 110463917							   *
 * ID: Abez0829								   *
 * CMSC 420 - Spring 2013					   *
 * Project - Part 5 Submission 				   *
 **********************************************/

public class Node {

	Node lChild; // Pointer to the left child
	Node rChild; // Pointer to the right child
	PointRecord pRecord; // Value of the node (Point record)

	/* The point record consists of a (x,y) point, the name of the location, a boolean status that represents if the
	 * location is functioning, and a type that must be of a predefined type. */
	public static class PointRecord {

		Point point;
		String name;
		int status; // Either 0 (false) or 1 (true)
		String type; // Must be either 'cafe', 'store', or 'restaurant'
		ArrayList<String> childrenType;
		ArrayList<Integer> childrenStatus;

		public PointRecord(int x, int y, String name, int status, String type) {
			Point p = new Point(x,y);
			this.point = p;
			this.name = name;
			this.status = status;
			this.type = type;
			this.childrenStatus = new ArrayList<Integer>();
			this.childrenType = new ArrayList<String>();
		}

	}

	/* A point is a (x,y) coordinate on a 1024x1024 grid map */
	public static class Point {

		int xValue; // Represents the x coord.
		int yValue; // Represents the y coord.
		int maxY = 1023;
		int maxX = 1023;
		int minY = 0;
		int minX = 0;

		public Point(int x, int y) {
			this.xValue = x;
			this.yValue = y;
		}

	}

	/* A constructor for a Node */
	public Node(int x, int y, String name, String type, int status) {
		this.lChild = null;
		this.rChild = null;
		PointRecord pr = new PointRecord(x,y,name,status,type);
		this.pRecord = pr;
	}

	/******************************************* PART 1 *************************************************************/

	/* This method is responsible for creating a new node to insert into the 2-D tree. The actual
	 * insertion is taken care of by an auxiliary method, insertAux (see below), which is passed the
	 * new node as well as the current node and current level. Initially the current node will always
	 * be the root. The method does not return anything.
	 */
	public void insert(Node n, int x, int y, String name, String type, int status) {
		int level = 0; // The initial level
		Node newNode = new Node(x, y, name, type, status); // Create the new node based on the parameters.
		insertAux(n, newNode, level); // Calling the aux method to insert 
	}

	/* This method is responsible for the actual insertion into the 2-D binary tree. The method takes as
	 * parameters a Node n, the current node, a Node newNode, the node to be inserted, and finally level
	 * which represents the current level of depth in the tree in order to know what coordinate will be
	 * the discriminate. The method does not return anything. 
	 */
	private void insertAux(Node n, Node newNode, int level) {

		if (!n.pRecord.childrenType.contains(newNode.pRecord.type))
			n.pRecord.childrenType.add(newNode.pRecord.type);
		if (!n.pRecord.childrenStatus.contains(newNode.pRecord.status))
			n.pRecord.childrenStatus.add(newNode.pRecord.status);
		/* Compare based on x coordinate, since the current level is even */
		if (level % 2 == 0) {
			/* Insert into the RST if the new node x value is >= than the current node */
			if (n.pRecord.point.xValue <= newNode.pRecord.point.xValue) {
				if (n.rChild == null) {
					newNode.pRecord.point.maxY = n.pRecord.point.maxY;
					newNode.pRecord.point.minY = n.pRecord.point.minY;
					newNode.pRecord.point.minX = n.pRecord.point.xValue;
					newNode.pRecord.point.maxX = n.pRecord.point.maxX;
					n.rChild = newNode;
				} else {
					insertAux(n.rChild, newNode, level + 1);
				}
			}
			/* Insert into the LST if the new node x value is < than the current node */
			else {
				if (n.lChild == null) {
					newNode.pRecord.point.minX = n.pRecord.point.minX;
					newNode.pRecord.point.maxX = n.pRecord.point.xValue - 1;
					newNode.pRecord.point.minY = n.pRecord.point.minY;
					newNode.pRecord.point.maxY = n.pRecord.point.maxY;
					n.lChild = newNode;
				} else {
					insertAux(n.lChild, newNode, level + 1);
				}
			}
		}
		/* Compare based on y coordinate, since the current level is odd */
		else {
			/* Insert into the RST if the new node y value is >= than the current node */
			if (n.pRecord.point.yValue <= newNode.pRecord.point.yValue) {
				if (n.rChild == null) {
					newNode.pRecord.point.minX = n.pRecord.point.minX;
					newNode.pRecord.point.maxX = n.pRecord.point.maxX;
					newNode.pRecord.point.minY = n.pRecord.point.yValue;
					newNode.pRecord.point.maxY = n.pRecord.point.maxY;
					n.rChild = newNode;
				} else {
					insertAux(n.rChild, newNode, level + 1);
				}
			}
			/* Insert into the LST if the new node y value is < than the current node */
			else {
				if (n.lChild == null) {
					newNode.pRecord.point.minX = n.pRecord.point.minX;
					newNode.pRecord.point.maxX = n.pRecord.point.maxX;
					newNode.pRecord.point.minY = n.pRecord.point.minY;
					newNode.pRecord.point.maxY = n.pRecord.point.yValue - 1;
					n.lChild = newNode;
				} else {
					insertAux(n.lChild, newNode, level + 1);
				}
			}
		}
	}

	/* This method is responsible for creating a file output1 and writing to the file 
	 * data from the input of a 2-D tree in the form of 3-tuples (NODE-NAME,CHILD-LINK,CHILD-NAME). The 
	 * actual writing to the file is taken care of in a auxiliary method, printAux. This function does not
	 * return anything.
	 */
	public static void print(Node n) {

		try {
			// Create the file/output stream 
			FileWriter fstream = new FileWriter("output1");
			BufferedWriter file = new BufferedWriter(fstream);
			StringBuffer stringBuff = new StringBuffer();

			printAux(n, stringBuff); // The StringBuffer will contain the output
			String str = stringBuff.toString(); // Convert the string buffer to a string
			file.write(str); // Write the string to the file

			// Close the output stream
			file.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	/* This method will help the print function to write to the file output1. It will recursively look
	 * at each node in the 2-D tree and write the 3-tuples. The print order will be pre-order, i.e. visit root,
	 * visit left node, visit right node recursively.
	 */
	private static void printAux(Node n, StringBuffer s) {

		String newline = System.getProperty("line.separator");  // OS dependent carriage return
		if (n.lChild != null) {
			s.append(n.pRecord.name + ",LCHILD," + n.lChild.pRecord.name + newline);
		}
		if (n.rChild != null) {
			s.append(n.pRecord.name + ",RCHILD," + n.rChild.pRecord.name + newline);
		}
		if (n.lChild != null) { // Visit left node
			printAux(n.lChild, s);
		}
		if (n.rChild != null) { // Visit right node
			printAux(n.rChild, s);
		}
	}

	/* Returns a 2-D Binary Tree based on input given in the file "points", each node has a possible
	 * left and right child as well as a point-record. The tree holds the property that for any node in the tree,
	 * the right child and its descendants are >= to the point (either x or y). The opposite holds for the left child
	 * and its descendants. The tree represents a 2-D vector field that is split horizontally and vertically. 
	 * ASSUME INPUT IS VALID
	 * */
	public static Node createTwoDTree() {
		Node root = null;

		// Attempt to open the file "points"
		try {
			// Use necessary java functions to open the input buffer from "points"
			FileInputStream fStream = new FileInputStream("points");
			DataInputStream input = new DataInputStream(fStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			String currLine; // Will represent each current line in the file "points"

			// Read line by line in the file "points"
			while ((currLine = buffer.readLine()) != null) {

				// Parse the line using regular expressions
				Pattern myPattern = Pattern.compile("(\\d{1,4}),(\\d{1,4}),(.+),(cafe|restaurant|store),(0|1)");
				Matcher myMatcher = myPattern.matcher(currLine);

				if (myMatcher.find()) {

					// Attempt to retrieve the individual parts that make up a point-record.
					int x = Integer.parseInt(myMatcher.group(1)); // x coordinate
					int y = Integer.parseInt(myMatcher.group(2)); // y coordinate
					String name = myMatcher.group(3); // Name of the location
					String type = myMatcher.group(4); // Type of the location
					int status = Integer.parseInt(myMatcher.group(5)); // Status of the location

					if (root == null) {
						root = new Node(x, y, name, type, status); // Create the initial tree (root)
					}else {
						root.insert(root, x, y, name, type, status); // Insert :)
					}
				}
			}
			input.close(); // Close the file "points"
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return root; // Return the 2d tree, possibly null
	}

	/******************************************* PART 2 *************************************************************/

	/* Input is given as, a pointer T to the root of a 2d- tree, a point p = (x,y) where 
	 * 0 � x� 1023 and 0 � y � 1023 (but p may not be in the tree T), and two integers low and high 
	 * with low � high. The function will return as output, (point,name) pairs consisting of all 
	 * points q (and associated names) in the tree T such that the Euclidean distance between 
	 * p and q, d(p,q), satisfies the  condition low � d(p,q) � high. */
	public static void twoDTreeDonut(Node n) {

		try {
			FileInputStream fStream;
			fStream = new FileInputStream("donuts");
			DataInputStream input = new DataInputStream(fStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			String queryLine; // Query input information
			StringBuffer outputStr = new StringBuffer();
			int caseNum = 1;
			String nLine = System.getProperty("line.separator");  // OS dependent carriage return


			// Read line by line from the file "donuts"
			while ((queryLine = buffer.readLine()) != null) {

				// Parse the line using regular expressions
				Pattern myPattern = Pattern.compile("(\\d{1,4}),(\\d{1,4}),(\\d{1,4}),(\\d{1,4})");
				Matcher myMatcher = myPattern.matcher(queryLine);

				if (myMatcher.find()) {
					ArrayList<String> pointSet = new ArrayList<String>(); // Will contain the (point,name) entries

					// Attempt to retrieve the individual parts that make up a query.
					int x = Integer.parseInt(myMatcher.group(1)); // x coordinate
					int y = Integer.parseInt(myMatcher.group(2)); // y coordinate
					int low = Integer.parseInt(myMatcher.group(3)); // inner radius of donut
					int high = Integer.parseInt(myMatcher.group(4)); // outer radius of donut
					Point p = new Point(x,y);

					twoDTreeDonutAux(n,p,low,high,pointSet, 0); // Get the (point,name) pairs for this query
					outputStr.append("Case #" + caseNum + ":" + nLine);
					for (int i = 0; i < pointSet.size(); i++)
						outputStr.append(pointSet.get(i));
					caseNum++;
				}
			}

			// Create the file/output stream 
			FileWriter fstream = new FileWriter("output2");
			BufferedWriter file = new BufferedWriter(fstream);			  
			String str = outputStr.toString(); // Convert the string buffer to a string
			file.write(str); // Write the string to the file

			// Close the streams
			file.close();
			input.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());		}

	}

	private static void twoDTreeDonutAux(Node n, Point p, int low, int high, ArrayList<String> pointSet, int level) {

		if (n != null) {

			String nLine = System.getProperty("line.separator");  // OS dependent carriage return
			int xDist = (n.pRecord.point.xValue - p.xValue) * (n.pRecord.point.xValue - p.xValue);
			int yDist = (n.pRecord.point.yValue - p.yValue) * (n.pRecord.point.yValue - p.yValue);
			double euclideanDist = Math.sqrt(xDist + yDist); // d(p,q)

			// Check if low � d(p,q) � high
			if (low <= euclideanDist  && euclideanDist <= high) {
				pointSet.add(n.pRecord.point.xValue + "," + n.pRecord.point.yValue + "," + n.pRecord.name + nLine);
			}
			// Check to see if we must prune LST or RST (either completely contained in the inner circle or
			// completely outside the outer circle)
			if (!queryPrune(n.pRecord.point, p, high, level, "RST") && !inInnerCircle(n.pRecord.point, p, low)) {
				twoDTreeDonutAux(n.rChild,p,low,high,pointSet, level + 1);
			}
			/* Else we PRUNE RST */
			if (!queryPrune(n.pRecord.point, p, high, level, "LST") && !inInnerCircle(n.pRecord.point, p, low)) {
				twoDTreeDonutAux(n.lChild,p,low,high,pointSet, level + 1);
			}
			/* Else we PRUNE LST */

		}
	}

	/* This function will determine if the sub-regions defined by node n (its RST and LST children) should be pruned.
	 * It will look at the distance from the point of n to the center of the donut and compare to the outer radius.
	 * If there is a possible point(s) in the RST or LST in the donut region, then it must be within the outer circle
	 * (inclusive). It will compare distance based on discriminatory factors x or y coordinates. */
	private static boolean queryPrune(Point np, Point p, int high, int level, String child) {

		boolean inRegion = false;

		// Consider pruning the LST
		if (child == "LST") {
			// Compare based on x coordinate value
			if (level % 2 == 0) {
				if (p.xValue - high > np.xValue) { // The x coordinate is smaller than the outer circle radius
					inRegion = true;
				}
				// Compare based on y coordinate value
			} else {
				if (p.yValue - high > np.yValue) { // The y coordinate is smaller than the outer circle radius
					inRegion = true;
				}
			}
			// Consider pruning the RST
		} else if (child == "RST") {
			// Compare based on x coordinate value
			if (level % 2 == 0) {
				if (p.xValue + high < np.xValue) { // The x coordinate is larger than the outer circle radius
					inRegion = true;
				}
				// Compare based on y coordinate value
			} else {
				if (p.yValue + high < np.yValue) { // The y coordinate is larger than the outer circle radius
					inRegion = true;
				}
			}
		}
		return inRegion; // If true, then we should prune (LST or RST)
	}

	/* This function will determine if the region associated with a node is completely contained within
	 * the inner circle of the donut. If it is, then no possible points within its LST and RST could ever be
	 * in the donut, so we must prune the LST and RST. It will determine this by checking the distance from the center
	 * of the circle to all four corners of the region and check if they are all strictly less than the inner radius.
	 * If so, then the region is completely contained in the inner circle and so we prune. */
	private static boolean inInnerCircle(Point np, Point p, int innerRadius) {

		boolean inCircle = false;
		double upperRight = (((np.maxX - p.xValue) * (np.maxX - p.xValue)) + 
				((np.maxY - p.yValue) * (np.maxY - p.yValue)));
		upperRight = Math.sqrt(upperRight);

		double lowerRight = (((np.maxX - p.xValue) * (np.maxX - p.xValue)) + 
				((np.minY - p.yValue) * (np.minY - p.yValue)));
		lowerRight = Math.sqrt(lowerRight);

		double upperLeft = (((np.minX - p.xValue) * (np.minX - p.xValue)) + 
				((np.maxY - p.yValue) * (np.maxY - p.yValue)));
		upperLeft = Math.sqrt(upperLeft);

		double lowerLeft = (((np.minX - p.xValue) * (np.minX - p.xValue)) + 
				((np.minY - p.yValue) * (np.minY - p.yValue)));
		lowerLeft = Math.sqrt(lowerLeft);

		if (upperRight < innerRadius && lowerRight < innerRadius && upperLeft < innerRadius 
				&& lowerLeft < innerRadius) {
			inCircle = true;
		}
		return inCircle; // If true, region defined by np is contained in the inner circle, so PRUNE
	}

	/******************************************* PART 3 *************************************************************/

	/* This method takes as input, a pointer to the root of a 2-d tree, a point (not necessarily in T), a type 
	 * ("cafe", "restaurant", or "store") and a status (0 or 1). The method finds the closest neighbor of p in T that 
	 * is of the specified type and has the specified status. In other words, it must consider the set of all points 
	 * in the tree that have type t and status s and return one of those points that is closest to that point. If no 
	 * point of the specified status and type exists in T, we simply return an empty string. If multiple such points 
	 * exist, we return any one. */
	public static void findNNTypeStatus(Node n) {

		try {
			FileInputStream fStream;
			fStream = new FileInputStream("queries");
			DataInputStream input = new DataInputStream(fStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			String queryLine; // Query input information
			StringBuffer outputStr = new StringBuffer();
			int caseNum = 1;
			String nLine = System.getProperty("line.separator");  // OS dependent carriage return


			while ((queryLine = buffer.readLine()) != null) {

				// Parse the line using regular expressions
				Pattern myPattern = Pattern.compile("(\\d+),(\\d+),(cafe|restaurant|store),(0|1)");
				Matcher myMatcher = myPattern.matcher(queryLine);

				if (myMatcher.find()) {
					StringBuffer sol = new StringBuffer(""); // Will contain the solution (if one exists)

					// Attempt to retrieve the individual parts that make up a query.
					int x = Integer.parseInt(myMatcher.group(1)); // x coordinate
					int y = Integer.parseInt(myMatcher.group(2)); // y coordinate
					String type = myMatcher.group(3); // type
					int status = Integer.parseInt(myMatcher.group(4)); // status
					Point p = new Point(x,y);

					findNNTypeStatusAux(n,p, type, status, sol, Double.MAX_VALUE); // Find the solution
					outputStr.append("Case #" + caseNum + ":" + nLine);
					outputStr.append(sol.toString());
					caseNum++;
				}
			}

			// Create the file/output stream 
			FileWriter fstream = new FileWriter("output3");
			BufferedWriter file = new BufferedWriter(fstream);			  
			String str = outputStr.toString(); // Convert the string buffer to a string
			file.write(str); // Write the string to the file

			// Close the streams
			file.close();
			input.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());}

	}

	private static void findNNTypeStatusAux(Node n, Point p, String type, int status, StringBuffer sol, 
			Double bestDist) {

		if (n != null) {
			Point closestP = findClosestPoint(n.pRecord, p);
			double dist = euclideanDistance(closestP, p);

			// Check if dist(Reg(n), p) < bestDist
			if (dist < bestDist) {
				String nLine = System.getProperty("line.separator");  // OS dependent carriage return
				double euclideanDist = euclideanDistance(n.pRecord.point, p); // d(n, p)

				// Check if we have a better distance with the required specifications 
				if (euclideanDist < bestDist && n.pRecord.status == status && n.pRecord.type.equals(type)) {
					bestDist = euclideanDist;
					sol.delete(0, sol.length()); // Clear the stringbuffer
					sol.append(n.pRecord.point.xValue + "," + n.pRecord.point.yValue + "," + n.pRecord.name + nLine);
				}

				findNNTypeStatusAux(n.lChild, p, type, status, sol, bestDist);
				findNNTypeStatusAux(n.rChild, p, type, status, sol, bestDist);
			}
			/* ELSE PRUNE */
		}
	}

	/* This method will find the closest point in the region, regP, to a point, p, and returns that point 
	 * */
	private static Point findClosestPoint(PointRecord regP, Point p) {

		// If point P is completely contained in the region, return point p
		if (p.xValue <= regP.point.maxX && p.xValue >= regP.point.minX &&
				p.yValue <= regP.point.maxY && p.yValue >= regP.point.minY) {
			return p;
		} else { // Find closest x and closest y coordinates
			int x = regP.point.minX, y = regP.point.minY;

			if (p.xValue >= regP.point.maxX)
				x = regP.point.maxX;
			else if (p.xValue <= regP.point.minX)
				x = regP.point.minX;
			else
				x = p.xValue; // p's x coordinate must be within the region

			if (p.yValue >= regP.point.maxY)
				y = regP.point.maxY;
			else if (p.yValue <= regP.point.minY)
				y = regP.point.minY;
			else
				y = p.yValue; // p's y coordinate must be within the region

			return new Point(x,y);
		}

	}

	/* A simple method that computes the Euclidean distance between two points 
	 * */
	private static double euclideanDistance(Point p1, Point p2) {
		int xDist = (p1.xValue - p2.xValue) * (p1.xValue - p2.xValue);
		int yDist = (p1.yValue - p2.yValue) * (p1.yValue - p2.yValue);
		return Math.sqrt(xDist + yDist); // d(n, p)
	}

	/******************************************* PART 4 *************************************************************/

	/* This method is very similar to what occurs in part 3, however, we are now taking into consideration a rectangular
	 * region defined by a bottom-left point and an upper-right point. We only want to consider points that lay outside
	 * that specified region. We are assuming the rectangular region is inclusive on all four sides. 
	 */
	public static void findRNNTypeStatus(Node n) {
		try {
			FileInputStream fStream;
			fStream = new FileInputStream("queries");
			DataInputStream input = new DataInputStream(fStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			String queryLine; // Query input information
			StringBuffer outputStr = new StringBuffer();
			int caseNum = 1;
			String nLine = System.getProperty("line.separator");  // OS dependent carriage return


			while ((queryLine = buffer.readLine()) != null) {

				// Parse the line using regular expressions
				Pattern myPattern = Pattern.compile("(\\d+),(\\d+),(cafe|restaurant|store),(0|1)," +
						"(\\d+),(\\d+),(\\d+),(\\d+)");
				Matcher myMatcher = myPattern.matcher(queryLine);

				if (myMatcher.find()) {
					StringBuffer sol = new StringBuffer(""); // Will contain the solution (if one exists)

					// Attempt to retrieve the individual parts that make up a query.
					int x = Integer.parseInt(myMatcher.group(1)); // x coordinate
					int y = Integer.parseInt(myMatcher.group(2)); // y coordinate
					String type = myMatcher.group(3); // type
					int status = Integer.parseInt(myMatcher.group(4)); // status
					int lx = Integer.parseInt(myMatcher.group(5)); // Lower x
					int ly = Integer.parseInt(myMatcher.group(6)); // Lower y
					int ux = Integer.parseInt(myMatcher.group(7)); // Upper x
					int uy = Integer.parseInt(myMatcher.group(8)); // Upper y
					Point p = new Point(x,y);
					Point bot = new Point(lx,ly);
					Point top = new Point(ux,uy);

					findRNNTypeStatusAux(n,p, type, status, sol, Double.MAX_VALUE, bot, top); // Find the solution
					outputStr.append("Case #" + caseNum + ":" + nLine);
					outputStr.append(sol.toString());
					caseNum++;
				}
			}

			// Create the file/output stream 
			FileWriter fstream = new FileWriter("output4");
			BufferedWriter file = new BufferedWriter(fstream);			  
			String str = outputStr.toString(); // Convert the string buffer to a string
			file.write(str); // Write the string to the file

			// Close the streams
			file.close();
			input.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());}

	}

	/* This method is where the actual work is done. It compares the distance from point p to the closest point 
	 * associated with a region. If that distance is closer than the current best distance, then we examine the point 
	 * to see if the type and status match the query in addition we check if the point is outside a specified 
	 * rectangular region, if so we update the best distance and best solution. We then visit the right and left child. 
	 */
	private static void findRNNTypeStatusAux(Node n, Point p, String type, int status, StringBuffer sol, 
			double bestDist, Point bot, Point top) {

		if (n != null) {
			Point closestP = findClosestPoint(n.pRecord, p);
			double dist = euclideanDistance(closestP, p);


			// Check if dist(Reg(n), p) < bestDist
			if (dist < bestDist) {
				String nLine = System.getProperty("line.separator");  // OS dependent carriage return
				double euclideanDist = euclideanDistance(n.pRecord.point, p); // d(n, p)

				// Check if we have a better distance with the required specifications 
				if (euclideanDist < bestDist && n.pRecord.status == status && n.pRecord.type.equals(type) &&
						!inRectangle(top,bot,n.pRecord.point)) {
					bestDist = euclideanDist;
					sol.delete(0, sol.length()); // Clear the stringbuffer
					sol.append(n.pRecord.point.xValue + "," + n.pRecord.point.yValue + "," + n.pRecord.name + nLine);
				}

				findRNNTypeStatusAux(n.lChild, p, type, status, sol, bestDist, bot, top);
				findRNNTypeStatusAux(n.rChild, p, type, status, sol, bestDist, bot, top);
			}
			/* ELSE PRUNE */
		}
	}

	/* This method will check if a point p is in a rectangle defined by points top and bot */
	private static boolean inRectangle(Point top, Point bot, Point p) {
		boolean xCoord = false;
		boolean yCoord = false;

		xCoord = p.xValue <= top.xValue && p.xValue >= bot.xValue;
		yCoord = p.yValue <= top.yValue && p.xValue >= bot.yValue;

		return xCoord && yCoord;
	}

	/******************************************* PART 5 *************************************************************/

	/* This method is very similar to what occurs in part 3, however, we are now considering points that are farthest
	 * from a specified point p. We are still only considering points that meet the required specifications of a type
	 * and status. If multiple such points exist, then we return any one of them. If none exist, then we simply return
	 * NIL which in this case is just an empty string.
	 */
	public static void furthestNBRTypeStatus(Node n) {

		try {
			FileInputStream fStream;
			fStream = new FileInputStream("queries");
			DataInputStream input = new DataInputStream(fStream);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(input));
			String queryLine; // Query input information
			StringBuffer outputStr = new StringBuffer();
			int caseNum = 1;
			String nLine = System.getProperty("line.separator");  // OS dependent carriage return


			while ((queryLine = buffer.readLine()) != null) {

				// Parse the line using regular expressions
				Pattern myPattern = Pattern.compile("(\\d+),(\\d+),(cafe|restaurant|store),(0|1)");
				Matcher myMatcher = myPattern.matcher(queryLine);

				if (myMatcher.find()) {
					StringBuffer sol = new StringBuffer(""); // Will contain the solution (if one exists)

					// Attempt to retrieve the individual parts that make up a query.
					int x = Integer.parseInt(myMatcher.group(1)); // x coordinate
					int y = Integer.parseInt(myMatcher.group(2)); // y coordinate
					String type = myMatcher.group(3); // type
					int status = Integer.parseInt(myMatcher.group(4)); // status
					Point p = new Point(x,y);

					furthestNBRTypeStatusAux(n, p, type, status, sol, Double.NEGATIVE_INFINITY); // Find the solution
					outputStr.append("Case #" + caseNum + ":" + nLine);
					outputStr.append(sol.toString());
					caseNum++;
				}
			}

			// Create the file/output stream 
			FileWriter fstream = new FileWriter("output5");
			BufferedWriter file = new BufferedWriter(fstream);			  
			String str = outputStr.toString(); // Convert the string buffer to a string
			file.write(str); // Write the string to the file

			// Close the streams
			file.close();
			input.close();
		} catch (Exception e) {
			System.err.println("Error (PART 5): " + e.getMessage());}
	}

	/* This method is where the actual work is done. It compares the distance from point p to the farthest point 
	 * associated with a region. If that distance is farther than the current worst distance, then we examine the point 
	 * to see if the type and status match the query in addition we check if the point is farther from p than the 
	 * current worst distance, if so we update the best distance and best solution. In addition, we can prune based
	 * on checking if a given n has descendants with the required type and status. 
	 * We then visit the right and left child. 
	 */
	private static void furthestNBRTypeStatusAux(Node n, Point p, String type, int status, StringBuffer sol, 
			double worstDist) {

		if (n != null) {
			Point farthestP = findFarthestPoint(n.pRecord, p);
			double dist = euclideanDistance(farthestP, p);

			// Check if dist(Reg(n), p) > worstDist
			if (dist > worstDist) {
				String nLine = System.getProperty("line.separator");  // OS dependent carriage return
				double euclideanDist = euclideanDistance(n.pRecord.point, p); // d(n, p)

				// Check if we have a farther distance with the required specifications 
				if (euclideanDist > worstDist && n.pRecord.status == status && n.pRecord.type.equals(type)) {
					worstDist = euclideanDist;
					sol.delete(0, sol.length()); // Clear the stringbuffer
					sol.append(n.pRecord.point.xValue + "," + n.pRecord.point.yValue + "," + n.pRecord.name + nLine);
				}

				// Attempt to prune based on the type and status of the descendants of node n
				if (n.pRecord.childrenStatus.contains(status) && n.pRecord.childrenType.contains(type)) {
					furthestNBRTypeStatusAux(n.lChild, p, type, status, sol, worstDist);
					furthestNBRTypeStatusAux(n.rChild, p, type, status, sol, worstDist);
				}
			}
			/* ELSE PRUNE */
		}
	}

	/* This method finds the farthest point in the region regP to point p. Point p could either be in the region or lay
	 * outside the region. However, to avoid numerous if statements, the method simply iterates through every possible
	 * point in the region regP and computes the distance from that current point to the point p. If that computed 
	 * distance is worse (farther) than the current worst distance, then we update the distance and the point to return. 
	 */
	private static Point findFarthestPoint(PointRecord regP, Point p) {

		int x = 0, y = 0;
		double farthestDist = Double.NEGATIVE_INFINITY;

		for (int i = regP.point.minX; i <= regP.point.maxX; i++) {
			for (int j = regP.point.minY; j <= regP.point.maxY; j++) {
				Point pTemp = new Point(i,j);
				double currDist = euclideanDistance(pTemp, p);
				if (currDist > farthestDist) {
					farthestDist = currDist;
					x = i;
					y = j;
				}
			}
		}
		return new Point(x,y);
	}

	/****************************************** END OF CLASS ********************************************************/
}
