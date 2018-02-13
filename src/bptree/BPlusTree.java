package bptree;

/**
 * The {@code BPlusTree} class implements B+-trees. Each {@code BPlusTree} stores its elements in the main memory (not
 * on disks) for simplicity.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class BPlusTree<K extends Comparable<K>, P> {
	int temp_val = 0;
	int index =0;
	int indexOfNdash = 0;
	/**
	 * The maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	protected int degree;

	/**
	 * The root node of this {@code BPlusTree}.
	 */
	protected Node<K, P> root;

	/**
	 * Constructs a {@code BPlusTree}.
	 * 
	 * @param degree
	 *            the maximum number of pointers that each {@code Node} of this {@code BPlusTree} can have.
	 */
	public BPlusTree(int degree) {
		this.degree = degree;
	}

	/**
	 * Copy-constructs a {@code BPlusTree}.
	 * 
	 * @param tree
	 *            another {@code BPlusTree} to copy from
	 */
	public BPlusTree(BPlusTree<K, P> tree) {
		this.degree = tree.degree;
		if (tree.root instanceof LeafNode)
			this.root = new LeafNode<K, P>(null, (LeafNode<K, P>) tree.root);
		else
			this.root = new NonLeafNode<K, P>(null, (NonLeafNode<K, P>) tree.root);
	}

	/**
	 * Returns the degree of this {@code BPlusTree}.
	 * 
	 * @return the degree of this {@code BPlusTree}
	 */
	public int degree() {
		return degree;
	}

	/**
	 * Returns the root {@code Node} of this {@code BPlusTree}.
	 * 
	 * @return the root {@code Node} of this {@code BPlusTree}
	 */
	public Node<K, P> root() {
		return root;
	}

	/**
	 * Finds the {@code LeafNode} in this {@code BPlusTree} that must be responsible for the specified key.
	 * 
	 * @param k
	 *            the search key
	 * @return the {@code LeafNode} in this {@code BPlusTree} that must be responsible for the specified key
	 */
	public LeafNode<K, P> find(K k) {
		return root.find(k);
	}

	/**
	 * Inserts the specified key and the pointer into this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 */
	public void insert(K k, P p) {
		LeafNode<K, P> l; // will eventually be set to the leaf node that should contain the specified key
		if (root == null) { // if the tree is empty
			System.out.println("Degree of root is null :: " + degree);
			l = new LeafNode<K, P>(degree); // create an empty leaf node
			System.out.println("Node L ::" + l);
			root = l; // the new leaf node is also the root
			System.out.println("Root Node :: " + root);
		} else // if the tree is not empty
			l = find(k); // find the leaf node l that should contain the specified key
			System.out.println("Leaf Node L that should contain the Key :: " + k + " Value of Leaf Node :: " + l +" Key is :: " + l.keys +" Pointer is ::" + l.pointers);
		if (l.contains(k, p)) // no duplicate key-pointer entries are allowed in the tree
			return;
		if (!l.isFull()) { // if leaf node l has room for the specified key
			l.insert(k, p); // insert the specified key and pointer into leaf node l
		} else { // if leaf node l is full and thus needs to be split
			LeafNode<K, P> t = new LeafNode<K, P>(degree + 1); // create a temporary leaf node t
			t.append(l, 0, degree - 2);// copy everything to temporary node t
			t.insert(k, p); // insert the key and pointer into temporary node t
			LeafNode<K, P> lp = new LeafNode<K, P>(degree); // create a new leaf node lp
			lp.setSuccessor(l.successor()); // chaining from lp to the next leaf node
			l.clear(); // clear leaf node l
			l.setSuccessor(lp); // chaining from leaf node l to leaf node lp
			int m = (int) Math.ceil(degree / 2.0); // compute the split point
			l.append(t, 0, m - 1); // copy the first half to leaf node l
			lp.append(t, m, degree - 1); // copy the second half to leaf node lp
			insertInParent(l, lp.key(0), lp); // use the first key of lp as the separating key
		}
	}

	/**
	 * Inserts pointers to the specified {@code Node}s into an appropriate parent {@code Node}.
	 * 
	 * @param n
	 *            a {@code Node}
	 * @param k
	 *            the key between the {@code Node}s
	 * @param np
	 *            a new {@code Node}
	 */
	void insertInParent(Node<K, P> n, K k, Node<K, P> np) {
		if (n == root) { // if n is the root of the tree
			root = new NonLeafNode<K, P>(degree, n, k, np); // create a new root node containing n, k, np
			return;
		}
		NonLeafNode<K, P> p = n.parent(); // find the parent p of n
		if (!p.isFull()) { // if parent node p has room for a new entry
			p.insertAfter(k, np, n); // insert k and np right after n
		} else { // if p is full and thus needs to be split
			NonLeafNode<K, P> t = new NonLeafNode<K, P>(degree + 1); // crate a temporary node
			t.copy(p, 0, p.keyCount()); // copy everything of p to the temporary node
			t.insertAfter(k, np, n); // insert k and np after n
			p.clear(); // clear p
			NonLeafNode<K, P> pp = new NonLeafNode<K, P>(degree); // create a new node pp
			int m = (int) Math.ceil(degree / 2.0); // compute the split point
			p.copy(t, 0, m - 1); // copy the first half to parent node p
			pp.copy(t, m, degree); // copy the second half to new node pp
			insertInParent(p, t.keys[m - 1], pp); // use the middle key as the separating key
		}
	}

	/**
	 * Removes the specified key and the pointer from this {@code BPlusTree}.
	 * 
	 * @param k
	 *            the key to delete
	 * @param p
	 *            the pointer to delete
	 */
	public void delete(K k, P p) {
		
		// Finding the Leaf Node l that contains the key k.
		LeafNode<K, P> N;
		System.out.println("Inside delete method to find entry.");
		// Implemented find(K k, P p) method in LeafNode.java.
		N = find(k, p); 
		System.out.println("Calling delete_entry Method.");
		// Creating method for the deleting the Entry of Node whose key value is k and pointer value is p.
		if(N.contains(k, p))
		{
			delete_entry(N,k,p);
		}
		
	}

	private LeafNode<K, P> find(K k, P p) {
		// TODO Auto-generated method stub
		System.out.println("Before Calling Node Class.");
		return root.find(k,p);
	}

	@SuppressWarnings("unchecked")
	private void delete_entry(Node<K, P> N, K k, P p) {
		// TODO Auto-generated method stub
		System.out.println("Entry to be Deleted :: " + k + "  " + " From Node :: " + N);
//		if(N != null)
//		{
		NonLeafNode<K,P> parentNode = N.parent;
		System.out.println("Parent N Val :: " + parentNode );
		System.out.println("K value :: " + k + " Pointer Value :: " + p);
			N.delete(k,p);
//		// Checking if the LeafNode l is the root node or not.
//		for(int i=0; i< N.keys.length-1;i++)
//		{
//			System.out.println("For Index :: " + i + " Keys value ::" + N.keys[i].toString() + " Pointer Value :: " + N.pointers[i]);
//		}
		
		//System.out.println("LeafNode l's pointers count : " + N.pointers.length);
		System.out.println("Checking if the LeafNode l is the root node or not. Value of Key :: " + k + " Node :: " + N);
		if( N == root )
		{
			System.out.println("If L is root");
			// If N has only one child.
			if( N.pointerCount() == 1)
			{
				System.out.println("If N has only One Child.");
				// root = ((LeafNode) N).successor();
				root = (Node<K, P>) N.pointers[0];
				N.clear();
			}
			return;
		}
		// If N is underUtilized.
		else if( N.isUnderUtilized(degree) )
		{

//			System.out.println("N is an instance of LeafNode.");
			Node<K, P> Ndash=  null;
			int flag = 0;
//			System.out.println("PointerCount of N :: " + N.pointerCount());
//			int deg =(int) Math.ceil((degree-1) / 2) ;
//			if(N.pointerCount() < deg)
//			{
				System.out.println("Its under Utilized.");
//				// Checking that If Parent has more than one chile than only do merge. Otherwise, There is no mean to merge.
//				if(((LeafNode) N).parent.childCount() > 1)
//				{
					System.out.println("Inside If of Line 215.");
//					for(int i=0; i< N.pointers.length; i++)
//					{
					
					System.out.println("Before for Loop Node N :: " + N + " Parent :: " + parentNode);
						for(int i =0; i<parentNode.pointers.length ;i++ )
						{
							// Finding the index of Node N at Parent Pointer.
							if(N.parent.pointers[i] == N)
							{
								index = i;
								System.out.println("Index of Node at Parent :: " + index);
								// If N is the index as 0 then we only can assign Ndash who is the successor of N.
								if(index == 0)
								{
									System.out.println("Choosing Ndash as Successor of N.");
									Ndash = successor(N,index);
									indexOfNdash = 1;
								}
								else	// If N is the middle index in the parent node.
								{
									// Assigning the Ndash as Predecessor of N to merge N with Ndash.
									Ndash = Predecessor(N,index);
									indexOfNdash = index - 1;
									System.out.println("Choosing Ndash as Predecessor of N.");
									// But, If Ndash is full in the Left side, Checking that If Ndash is available in successor.
									if(Ndash.isFull())
									{
										System.out.println("If Ndash is full Choosing Ndash as Successor of N After Checking Predecessor.");
										
										if(N.parent.pointers[index+1] != null)
										{
											Ndash = successor(N,index);
											indexOfNdash = index + 1;
										}
										else  // If both successor and Predecessor are full then assigning a flag to 1;
										{
											flag  = 1;
											System.out.println("Now we have to call redistribution.");
										}
									}
								}
							}
						}
						//System.out.println("K Kdash = N.parent.keys[index] :: index val :: " + index + "Kdash Val :: " + Kdash );
						System.out.println("Parent N :: " + N.parent + " Index :" + index);
						System.out.println("Node N :: " + N + " Node Ndash :: " + Ndash);
						
						// Checking that N is mergable with the Ndash and Ndash is full in Predecessor or successor.
						if(N.mergeable(Ndash) && flag != 1)  //  && !(Ndash.isFull())
						{
							if(successor(Ndash, indexOfNdash) == N)
							{
								K Kdash = N.parent.keys[index-1];
								int indexOfKdash = N.parent.findIndexL(Kdash);
								System.out.println("Inside Merge where Ndash is on Left.");
								merge(Ndash, Kdash, N);
							}
							else
							{
								K Kdash = N.parent.keys[index];
								int indexOfKdash = N.parent.findIndexL(Kdash);
								System.out.println("Inside Merge where Ndash is on Right.");
								merge(N, Kdash, Ndash);
							}
						}
						else
						{
							System.out.println("If N is not mergable with Ndash then in else part of if.");
							// Redistribution Scenario
							if(successor(Ndash, indexOfNdash) == N)
							{
								K Kdash = N.parent.keys[index-1];
								int indexOfKdash = N.parent.findIndexL(Kdash);

								if(N instanceof NonLeafNode)
								{
									((NonLeafNode<K,P>)N).insert(Kdash, 0, (Node<K, P>) Ndash.pointers[Ndash.keyCount], 0);
									
									K temp_key = Ndash.keys[Ndash.keyCount];
									
									((NonLeafNode<K,P>)Ndash).delete(Ndash.keys[Ndash.keyCount],Ndash.pointers[Ndash.keyCount]);
									
									N.parent.keys[indexOfKdash] = temp_key;
								}
								else
								{
									System.out.println("Before Insert Node N Val :: " + N + " Node Ndash Val :: " + Ndash);
									((LeafNode<K,P>)N).insert( 0, Ndash.keys[Ndash.keyCount - 1], (P) Ndash.pointers[Ndash.keyCount - 1]); // write keycount - 1 from keycount
									System.out.println("After Insert Node N Val :: " + N + " Node Ndash Val :: " + Ndash);
									K temp_key = Ndash.keys[Ndash.keyCount - 1];
									System.out.println("Temp_key :: " + temp_key);
									
									((LeafNode<K,P>)Ndash).delete(Ndash.keys[Ndash.keyCount - 1], Ndash.pointers[Ndash.keyCount - 1]);
									
									System.out.println("After Delete Node N Val :: " + N + " Node Ndash Val :: " + Ndash);
									System.out.println("Index Of Kdash :: " + indexOfKdash + " Parent :: " + N.parent);
									N.parent.keys[indexOfKdash+1] = temp_key;   // did +1 in indexOfKdash
									
								}
								
							}
							else
							{
								K Kdash = N.parent.keys[index];
								int indexOfKdash = N.parent.findIndexL(Kdash);

								if(successor(N, index) == Ndash)
								{
									if(N instanceof NonLeafNode)
									{
										((NonLeafNode<K,P>)Ndash).insert(Kdash, 0, (Node<K, P>) N.pointers[N.keyCount], 0);
										
										K temp_key = N.keys[N.keyCount];
										
										((NonLeafNode<K,P>)N).delete(N.keys[N.keyCount],N.pointers[N.keyCount]);
										
										N.parent.keys[indexOfKdash] = temp_key;	
									}
									else
									{
										System.out.println("N data of Keycount to be added :: " + N.keys[N.keyCount] + "N data of POinters to be added :: " + (P) N.pointers[N.keyCount]);
										
										System.out.println("Before Insert data of Both Node N :: " + N + " data of Ndash :: " + Ndash);
										
										if(N.pointerCount() == 0)
										{
											
										} 
										else
										{
											((LeafNode<K,P>)Ndash).insert( 0, N.keys[N.keyCount], (P) N.pointers[N.keyCount]);
										}
									
										System.out.println("After Insert data of Both Node N :: " + N + " data of Ndash :: " + Ndash);
										
										K temp_key = N.keys[N.keyCount];
										
										System.out.println("Temperory Key :: " + temp_key);
										((NonLeafNode<K,P>)N).delete(N.keys[N.keyCount], N.pointers[N.keyCount]);
										
										N.parent.keys[indexOfKdash] = temp_key;
										
									}
								}
							}
						}		
		}
		
//		}
		
	}

	@SuppressWarnings("unchecked") // changed from LeafNode to Node
	// Finding the Predecessor of the Node.
	private Node<K, P> Predecessor(Node<K, P> n, int index) {
		// TODO Auto-generated method stub
		if(n.parent.pointers[index-1] != null)
		{
			return (Node<K, P>) n.parent.pointers[index-1];
		}
		return null;		
	}

	@SuppressWarnings("unchecked")
	// Finding the Successor of the Node.
	private Node<K, P> successor(Node<K, P> n,int index) {
		// TODO Auto-generated method stub
		if(n.parent.pointers[index+1] != null)
		{
			return (Node<K, P>) n.parent.pointers[index+1];
		}
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	// Merging the Nodes.
	private void merge(Node<K,P> N, K Kdash, Node<K,P> Ndash)
	{
	
		
		if (!(N instanceof LeafNode))
		{
			System.out.println("N is the instance of the NonLeaf Node.");
		//	Ndash.append(N.parent, Kdash, Kdash);
//			int indexOfKdash = N.parent.findIndexL(Kdash);
//			P pointerValofKdash = (P) N.parent.pointers[indexOfKdash];
//			Ndash.append(N.parent, indexOfKdash ,indexOfKdash);
			System.out.println("Kdash Val :: " + Kdash + " Parent :: " + N.parent + " Ndash val :: " + Ndash + " N val ::" + N);
			
				insertAfter(Kdash, Ndash, N);
				
			System.out.println(" Non Leaf InserAfter Node N :: " + N + " Ndash :: " + Ndash );
			
			
		}
		else
		{
			System.out.println("IN Merge val of N :: " + N + " Parent N :: " + N.parent);
			System.out.println("N is the instance of the Leaf Node. N Keycount :: " + N.keyCount + "After Ndash Value of Key :: " + N.keys[0] + " Pointers :: " + N.pointers[0] + "Value of Key :: " + N.keys[1] + " Pointers :: " + N.pointers[1]);
			System.out.println("Before append Ndash Value of Key :: " + Ndash.keys[0] + " Pointers :: " + Ndash.pointers[0] + "Value of Key :: " + Ndash.keys[1] + " Pointers :: " + Ndash.pointers[1]);
			System.out.println("Before append N Value of Key :: " + N.keys[0] + " Pointers :: " + N.pointers[0] + "Value of Key :: " + N.keys[1] + " Pointers :: " + N.pointers[1]);
			
			N.append(Ndash, 0, Ndash.keyCount-1);
			
			System.out.println("After append Ndash Value of Key :: " + Ndash.keys[0] + " Pointers :: " + Ndash.pointers[0] + "Value of Key :: " + Ndash.keys[1] + " Pointers :: " + Ndash.pointers[1]);
//			Ndash.pointers.length = N.pointers.length - 1;
			//N.append(Ndash, 0, Ndash.keyCount-1);
			System.out.println("After append N Value of Key :: " + N.keys[0] + " Pointers :: " + N.pointers[0] + "Value of Key :: " + N.keys[1] + " Pointers :: " + N.pointers[1]);

//			Ndash.pointers.length = Ndash.pointers.length -1 ;
			if(Ndash == successor(N, index)){
				System.out.println("Ndash is successor of N.");
				System.out.println("Successor of Ndash Node :: " + ((LeafNode<K,P>)Ndash).successor());
				((LeafNode<K,P>)N).setSuccessor(((LeafNode<K,P>)Ndash).successor());
				System.out.println("After setting Ndash successor to N - Successor of N :: " + ((LeafNode<K,P>)N).successor());
			}
			else{
				System.out.println("N is successor of Ndash.");
				((LeafNode<K,P>)Ndash).setSuccessor(((LeafNode<K,P>)N).successor());
			}
//			Ndash.pointers[Ndash.keyCount] = N.pointers[N.keyCount];
			//N.pointers[N.keyCount] = Ndash.pointers[Ndash.keyCount];
		}
		
		System.out.println("Index of Parent :: " + N.parent.findIndexL(Kdash) );
		System.out.println("Value of Kdash :: " + Kdash + " Value of Parent of N :: " + N.parent +" Pointer of N to be deleted :: " +  (P) Ndash);
		N = Ndash;
		System.out.println("IN Merge Before Delete val of N :: " + N + " Parent N :: " + N.parent);
		delete_entry(N.parent,Kdash,(P) N);
		N.clear();
	}

	// Implementing the insertAfter Method again and using it.
	private void insertAfter(K kdash, Node<K, P> ndash, Node<K, P> n) {
		// TODO Auto-generated method stub
		System.out.println("Inside Manual Insert After Method");
		((NonLeafNode<K,P>)n).keys[n.keyCount] = kdash;
		System.out.println("Kdash Val :: " + kdash + " Parent :: " + n.parent + " Ndash val :: " + ndash + " N val ::" + n);
		
		// Creating the child of Ndash as ndashChild
		Node<K,P> ndashChild = null;
		int count = 0;
		for(int i=0;i<ndash.pointers.length ; i++)
		{
			if(ndash.pointers[i] != null)
			{
				ndashChild = (Node<K,P>)ndash.pointers[i];
				break;
			}
			count++;
		}
		
		ndashChild.pointers[degree - 1] = null;
		// Increamenting the KeyCount.
		n.keyCount = n.keyCount + 1;
		System.out.println("Ndash Child is :: " + ndashChild);
		
		// pointing ndashChile from the n's last pointer.
		n.pointers[n.keyCount] = ndashChild;
		// Setting parent of ndashChile as n.
		ndashChild.setParent((NonLeafNode<K, P>) n);
		System.out.println("Kdash Val :: " + kdash + " Parent :: " + n.parent + " Ndash val :: " + ndash + " N val ::" + n);
		
	}
	
}
