package assign3;

import java.io.*;
import java.util.*;
public class AVLTree {
/*
Implements a ALV tree of ints (the keys) and fixed length character strings fields
stored in a random access file.
Duplicates keys are not allowed. There will be at least 1 character string field
*/
	private RandomAccessFile f;
	private long root; //the address of the root node in the file
	private long free; //the address in the file of the first node in the free list
	private int numStringFields; //the number of fixed length character fields
	private int fieldLengths[]; //the length of each character field
	private int numIntFields; // the number of integer fields
 
	 private class Node {
		
		 private int key;
		 private char stringFields[][];
		 private int intFields[];
		 private long left;
		 private long right;
		 private int height;
		 
		 private Node(long l, int d, long r, char sFields[][], int iFields[]) {
		 //constructor for a new node
			left = l;
	 	 	key = d;
	 	 	right = r;
	 	 	height = 0;
	 	 	//initialize the 2 array fields
	 	 	stringFields = new char[sFields.length][];
	 	 	intFields = new int[iFields.length];
	 	 	//loop through the 2d sFields and set string fields equal to it
	 	 	for(int row = 0; row < sFields.length; row++) {
	 	 		stringFields[row] = new char[sFields[row].length];
				 for(int col = 0; col < stringFields[row].length; col++) {
					 //if sfields[row][col] is null, pad new array
					 if(sFields[row][col] == 0) {
						 stringFields[row][col] = '\0';
					 }
					 stringFields[row][col] = sFields[row][col];
				 } 
			 }
	 	 	//loop through iFields and set intFields equal to it
			 for(int i = 0; i < iFields.length; i++) {
				 intFields[i] = iFields[i];
			 }
		 }
		 
		 private Node(long addr) throws IOException{
		 //constructor for a node that exists and is stored in the file
			//initialize the 2 array fields
			 stringFields = new char[numStringFields][];
			 intFields = new int[numIntFields];
			 //seek to the address passed in
			 f.seek(addr);
			 //read in the key
			 key = f.readInt();
			 //loop through stringFields and read in each char
			 for(int j = 0; j < stringFields.length; j++) {
				 stringFields[j] = new char[fieldLengths[j]];
				 for(int k = 0; k < stringFields[j].length; k++) {
					 stringFields[j][k] = f.readChar();
				 } 
			 }
			//loop numIntFields and read in each int into intFields
			 for(int i = 0; i < numIntFields; i++) {
				 intFields[i] = f.readInt();
			 }
			 //read left, right, and height
			 left = f.readLong();
	 	 	 right = f.readLong();
	 	 	 height = f.readInt();
		 }
		 
		 private void writeNode(long addr) throws IOException {
		 //writes the node to the file at location addr
			 //seek to the address passed in
			 f.seek(addr);
			 //write the key
			 f.writeInt(key);
			//loop through stringFields and write each char
	 	 	 for(int j = 0; j < stringFields.length; j++) {
				 for(int k = 0; k < stringFields[j].length; k++) {
					 f.writeChar(stringFields[j][k]);
				 } 
			 }
	 	 	//loop numIntFields and write in each int 
			 for(int i = 0; i < intFields.length; i++) {
				 f.writeInt(intFields[i]);
			 }
			 //write left, right, and height
			 f.writeLong(left);
	 	 	 f.writeLong(right);
	 	 	 f.writeInt(height);
		 }
	 }
 
	 public AVLTree(String fname, int stringFieldLengths[], int numIntFields) throws IOException {
	 //creates a new empty AVL tree stored in the file fname
	 //the number of character string fields is stringFieldLengths.length
	 //stringFieldLengths contains the length of each string field
		 //create a new file with passed in name
		 File path = new File(fname);
	 	 //delete file if it exists
	 	 if(path.exists()){
	 		 path.delete();
	 	 }
	 	 //make a new random access file
	 	 f = new RandomAccessFile(path, "rw");
 	 	 //set root and free to 0
	 	 root = 0;
 	 	 free = 0;
 	 	 //set the number of string fields equal to the length of stringFieldLengths[]
 	 	 numStringFields = stringFieldLengths.length;
 	 	//set the lenght of string fields equal to a array length of stringFieldLengths[]
 	 	 fieldLengths = new int[stringFieldLengths.length];
 	 	 //loop through stringFieldLengths.length to get the length of the strings
 	 	 for(int i = 0; i < stringFieldLengths.length; i++) {
 	 		fieldLengths[i] = stringFieldLengths[i];
 	 	 }
 	 	 //set numIntFields to the a passed in value
 	 	 this.numIntFields = numIntFields;
 	 	 
 	 	 //write the root and free longs
 	 	 f.writeLong(root);
	 	 f.writeLong(free);
	 	 //write the string field lengths
	 	 f.writeInt(stringFieldLengths.length);
	 	 //loop through the number of string fields and write the length of each
	 	 for(int i = 0; i < stringFieldLengths.length; i++) {
 	 		f.writeInt(stringFieldLengths[i]); 
 	 	 }
	 	 //write the num of int fields
 	 	 f.writeInt(numIntFields);
	 }

	 public AVLTree(String fname) throws IOException {
	 //reuse an existing tree store in the file fname 
		//make a new random access file
		f = new RandomAccessFile(fname, "rw");
		//seek to 0 in file
		f.seek(0);
		//read in and set root, free, and numStringFields
 	    root = f.readLong();
 	    free = f.readLong();
 	 	numStringFields = f.readInt();
 	 	//initialize array fieldLengths
 	 	fieldLengths = new int[numStringFields];
 	 	//loop through fieldLengths.length and read in each int
 	 	for(int i = 0; i < fieldLengths.length; i++) {
 	 		fieldLengths[i] = f.readInt();
 	 	}
 	 	//read in numIntFields
 	 	numIntFields = f.readInt();
	 }
	 
	 public void insert(int k, char sFields[][], int iFields[]) throws IOException {
	 //PRE: the number and lengths of the sFields and iFields match the expected number and lengths
	 //insert k and the fields into the tree the string fields are null (‘\0’) padded if k is in the tree do nothing
		 root = insert(root, k, sFields, iFields);
	 }
	 
	 private long insert(long r, int d, char sFields[][], int iFields[]) throws IOException {
		 //declare a Node
		 Node x;
		 //if the address passed in is 0(at the end of tree)
	 	 if (r == 0) {
	 		 //set x to a new child node
	 	 	 x = new Node(0, d, 0, sFields, iFields);
	 	 	 //get the next free address
	 	 	 long addr = getFree();
	 	 	 //write x to that address
	 	 	 x.writeNode(addr);
	 	 	 //return the address
	 	 	 return addr;
	 	 }
	 	 //if r != 0, set x = the to the node at address r
	 	 x = new Node(r);
	 	 //if the key is less then its parents, x.left changes
	 	 if (d < x.key) x.left = insert(x.left, d, sFields, iFields);
	 	 //if the key is more then its parents, x.right changes
	 	 else if (d > x.key) x.right = insert(x.right, d,  sFields, iFields);
	 	 //set the new height
	 	 x.height = Math.max(height(x.left), height(x.right)) + 1;
	 	 //write the new node to the file
	 	 x.writeNode(r);
	 	 //balance and return
	 	 return balance(x, r);
	 	}
	 
	 private static final int ALLOWED_IMBALENCE = 1;
	 
	 //take in r root node and a root address
	 private long balance(Node r, long a) throws IOException {
		 //set nodes of left and right child
		 Node rootLeft = new Node(r.left);
		 Node rootRight = new Node(r.right);
		 
		 //if difference of height on left is more the one
		 if(height(r.left) - height(r.right) > ALLOWED_IMBALENCE) {
			 //if the left side is taller single left rotate
			 if(height(rootLeft.left) >= height(rootLeft.right)) {
				 a = rotateWithLeftChild(a);
			 } 
			 //if right is taller double rotate
			 else {
				 a = doubleWithLeftChild(a);
			 }	 
		 } 
		 else {
			//if difference of height on right is more the one
			if(height(r.right) - height(r.left) > ALLOWED_IMBALENCE) {
				//if the right side is taller single right rotate
				if(height(rootRight.right) >= height(rootRight.left)) {
					a = rotateWithRightChild(a);
				 } 
				//if left is taller double rotate
				else {
					 a = doubleWithRightChild(a);
				}	 
			}
		 }
		 //return address
		 return a;
	 }
	 
	 private long rotateWithLeftChild(long d) throws IOException {
		 //make node with addr passed in
		 Node k2 = new Node(d);
		 //keep the value of k2.left to return
		 long one = k2.left;
		 //make a node with k2.left
		 Node k1 = new Node(k2.left);
		 
		 k2.left = k1.right;
		 k1.right = d;
		 //update the heights
		 k2.height = Math.max(height(k2.left), height(k2.right)) + 1;
		 k1.height = Math.max(height(k1.left), k2.height) + 1;
		 
		 //write the updated nodes to file
		 k1.writeNode(one);
		 k2.writeNode(d);
		 //return original k2.left
		 return one;
	 }
	 
	 private long rotateWithRightChild(long d) throws IOException {
		//make node with addr passed in
		 Node k2 = new Node(d);
		//keep the value of k2.right to return
		 long one = k2.right;
		//make a node with k2.left
		 Node k1 = new Node(k2.right);
		 
		 k2.right = k1.left;
		 k1.left = d;
		//update the heights
		 k2.height = Math.max(height(k2.right), height(k2.left)) + 1;
		 k1.height = Math.max(height(k1.right), k2.height) + 1;
		 
		//write the updated nodes to file
		 k1.writeNode(one);
		 k2.writeNode(d);
		//return original k2.right
		 return one;
	 }
	 
	 private long doubleWithLeftChild(long r) throws IOException {
		 //make a node with addr passed in
		 Node k3 = new Node(r); 
		 //set left child to rotateWithRightChild
		 k3.left = rotateWithRightChild(k3.left);
		 //write node to file
		 k3.writeNode(r);
		 return rotateWithLeftChild(r);
	 }

	 private long doubleWithRightChild(long r) throws IOException {
		//make a node with addr passed in
		 Node k3 = new Node(r);
		 //set right child to rotateWithLeftChild
		 k3.right = rotateWithLeftChild(k3.right);
		//write node to file
		 k3.writeNode(r);
		 return rotateWithRightChild(r);
	 }
	 
	 //get the next available space and return it
	 private long getFree() throws IOException {
		//go to free list and save the key in r
		//if free list has no addresses
		if(free == 0) {
			//return the address at the end of file
			return f.length();
		} else {
			//store address in a
			long a = free;
			//seek to the addr in free
			f.seek(free);
			//set free to the address there
			free = f.readLong();
			//return a
			return a;
		}
	}
	 
	 //calculate the height of the subtree, , -1 if null
	 private int height(long t) throws IOException {
		 //the the addr is 0 the tree is empty so height is -1
		 if (t == 0) {
			 return -1;
		 }
		 //create a new node with addr passed in
		 Node k = new Node(t);
		 //return the height of that node
	     return k.height;   
	 }
	 
	public void print() throws IOException {
	 //Print the contents of the nodes in the tree is ascending order of the key
	 //do not print the null characters
		print(root);
	}
	
	private void print(long r) throws IOException {
		//if r is 0 return
		if (r == 0) return;
		//make a node x at addr r
	 	Node x = new Node(r);
	 	//print left
	 	print(x.left);
	 	//print key, string fields, int fields, x.left, x.right, and height
	 	System.out.print("(" + x.key + " " );
	 	LinkedList<String> ll = stringFind(x.key);
	 	for(String str: ll)
	      {
	    	  System.out.print(str + " ");
	      }
	 	LinkedList<Integer> ll2 = intFind(x.key);
	 	for(Integer in: ll2)
	      {
	    	  System.out.print(in + " ");
	      }
	 	System.out.println(x.left + " " + x.right + " " + x.height + ")");
	 	//print right
	 	print(x.right);
	 }
		
	public LinkedList<String> stringFind(int k) throws IOException {
	//if k is in the tree return a linked list of the strings fields associated with k
	//otherwise return null
	//The strings in the list must NOT include the padding (i.e the null chars)
		//create a node the addr passed in
		Node temp = new Node(root);
		//call find to find the correct node
		Node x = find(temp, k);
		//if the x is null the node is not in the tree
		if(x == null) {
			return null;
		}
		//create a new linked list of strings
		LinkedList<String> ll = new LinkedList<String>();
		//loop through the number of string fields in x
		for(int i = 0; i < x.stringFields.length; i++) {
			String s = "";
			for(int j = 0; j < x.stringFields[i].length; j++) {
				//the element is padding, leave loop
				if(x.stringFields[i][j] == '\0') {
					break;
				}
				//otherwise add it to the string
				String add = Character.toString(x.stringFields[i][j]);
				s += add;
			} 
			//add the string to the list
			ll.add(s);
		}
		return ll;	
	}
	//used to find a element in the tree and return the node
	private Node find(Node x, int k) throws IOException {
		//if x is null, the element is not in tree
		if(x == null) {
			return null;
		}
		//if x key is more then key, move left
		if(k < x.key) {
			if(x.left == 0) {
				return null;
			}
			Node temp = new Node(x.left);
			return find(temp, k);
		}
		//if x key is less then key, move right
		if(k > x.key) {
			if(x.right == 0) {
				return null;
			}
			Node temp = new Node(x.right);
			return find(temp, k);
		}
		//return the node
		return x;
	}
			
	public LinkedList<Integer> intFind(int k) throws IOException {
	//if k is in the tree return a linked list of the integer fields associated with k
	//otherwise return null
		//make temp node with root
		Node temp = new Node(root);
		//call find to get node with key k
		Node x = find(temp, k);
		//if x == null, the key is not in tree
		if(x == null) {
			return null;
		}
		//make a linked list
		LinkedList<Integer> ll = new LinkedList<Integer>();
		//add each int in node to linked list
		for(int i = 0; i < numIntFields; i++) {
			ll.add(x.intFields[i]);
		}
		return ll;
	}

	public void remove(int k) throws IOException {
	 //if k is in the tree removed the node with key k from the tree
	 //otherwise do nothing
		root = remove(k, root);
	}
	//take in key to remove and root address and return root address
	private long remove(int k, long d) throws IOException {
		//make a node for root
		Node node = new Node(d);
		//if root == 0 return root
		if(d == 0) {
			 return d;
		}
		//if key is less then node key, move left
		if(k < node.key) {
			node.left = remove(k, node.left);
		}
		//if key is more then node key, move left
		else if(k > node.key) {
			node.right = remove(k, node.right);
		}
		//if the node has two children
		else if(node.left != 0 && node.right != 0) {
			//set temp to smallest node on right side
			Node temp = findMin(node.right);
			//set node key = to min right key
			node.key = temp.key;
			//loop string fields and set node string values to temp values
			for(int i = 0; i < temp.stringFields.length; i++) {
				for(int j = 0; j < temp.stringFields[i].length; j++) {
					node.stringFields[i][j] = temp.stringFields[i][j];
				}
			}
			//loop through int fields and set node int fields to temp values
			for(int i = 0; i < temp.intFields.length; i++) {
				node.intFields[i] = temp.intFields[i];
			}
			//set node.right 
			node.right = remove(node.key, node.right);
		}
		//if node has just one child
		else {
			//if node has a right child
			if(node.right != 0) {
				//set a long for nodes right
				long xRight = node.right;
				//set a node for nodes right
				node = new Node(node.right);
				//add the long to free list
				addFreeList(xRight);
			//if node has a left child
			} else if(node.left != 0){
				//set a long for nodes left
				long xLeft = node.left;
				//set a node for nodes left
				node = new Node(node.left);
				////add the long to free list
				addFreeList(xLeft);
			}else {
				addFreeList(d);
				return 0;
			}
		}
		//update height
		node.height = Math.max(height(node.right), height(node.left)) + 1;
		//write updated node to file
		node.writeNode(d);
		//return balance
		return balance(node, d);
	}
	//takes in a long addr and adds it to free list
	private void addFreeList(long r) throws IOException {
		//if free list is empty
		if(free == 0) {
			//set free to r 
			free = r;
			//seek to addr r and write 0
			f.seek(r);
			f.writeLong(0);
		}else {
			//seek to addr free and write addr r
			f.seek(free);
			f.writeLong(r);
			//then seek to addr r and write 0
			f.seek(r);
			f.writeLong(0);
		}
	}
	//takes in long r and returns the smallest node rooted at r
	private Node findMin(long r) throws IOException {
		//create a node with r
		Node x = new Node(r);
		//create a long with x.left
		long left = x.left;
		//loop until all the way left
		while(left != 0) {
			x = new Node(x.left);
			left = x.left;
		}
		//return the left most node
		return x;
	}

	public void close() throws IOException {
	 //update root and free in the file (if necessary)
	 //close the random access file
		f.seek(0);
		f.writeLong(root);
		f.seek(8);
		f.writeLong(free);
		f.close();
	}
}
