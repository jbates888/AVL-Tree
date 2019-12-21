package assign3;

import java.io.IOException;

public class tester {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		int stringLength[] = {4};
		
		AVLTree tree = new AVLTree("o", stringLength, 3);
		
		char[][] sFeilds = new char[1][4];
		sFeilds[0][0] ='h';
		sFeilds[0][1] ='i';
		sFeilds[0][2] ='i';
		sFeilds[0][3] ='i';
		
		int[] iFeild = {200, 8, 3};
		
		tree.insert(10,  sFeilds,  iFeild);
		tree.insert(8,  sFeilds,  iFeild);
		tree.insert(20,  sFeilds,  iFeild);
		

		//tree.remove(25);
		
	
		//tree.print();
		
//		System.out.println();
//		System.out.print(tree.stringFind(10));
//		
//		System.out.println();
//		System.out.print(tree.intFind(10));
		System.out.println();
		
		tree.close();
		
		
		AVLTree tree2 = new AVLTree("o");
		
		tree2.print();
		
		tree2.close();

	}

}
