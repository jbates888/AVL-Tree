# AVL-Tree

340 assign 3

Creates an avl tree stored in a random access file

Each node contains an integer key, one or more fixed length character strings, one or more integers, a left child reference, 
a right child reference and a height

does not load the whole tree into memory, each operation only makes copies of the nodes it needs for that
operation. Modified nodes must be written back to the file.
