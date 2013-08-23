
public class Driver {

	public static void main(String[] args) {

		Node kdTree = Node.createTwoDTree(); // We now have a 2-D tree from the date given in the file "points"
		
		/* PART 1 */
		/* Node.print(kdTree);*/ // Write contents of the tree to a file called "output1"
		
		/* PART 2 */
		/* Node.twoDTreeDonut(kdTree); */
		
		/* PART 3 */	
		/* Node.findNNTypeStatus(kdTree); */
		
		/* PART 4 */
		/* Node.findRNNTypeStatus(kdTree); */
		
		/* PART 5 */
		Node.furthestNBRTypeStatus(kdTree);

	}

	
	
	/****** END OF DRIVER ******/
}
