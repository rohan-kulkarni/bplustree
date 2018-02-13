package bptree;

/**
 * The {@code LeafNode} class implements leaf nodes in a B+-tree. {@code LeafNode}s are chained so each {@code LeafNode}
 * except the last {@code LeafNode} has a successor.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * 
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class LeafNode<K extends Comparable<K>, P> extends Node<K, P> {

	/**
	 * Constructs a {@code LeafNode}.
	 * 
	 * @param degree
	 *            the degree of the {@code LeafNode}
	 */
	public LeafNode(int degree) {
		super(degree);
	}

	/**
	 * Copy-constructs a {@code LeafNode}.
	 * 
	 * @param parent
	 *            the parent {@code Node} of this {@code LeafNode}
	 * @param node
	 *            the other {@code LeafNode} to copy from
	 */
	public LeafNode(NonLeafNode<K, P> parent, LeafNode<K, P> node) {
		super(parent, node);
	}

	/**
	 * Returns the pointer at the specified index.
	 * 
	 * @param i
	 *            the index of the key
	 * @return the pointer at the specified index
	 */
	@SuppressWarnings("unchecked")
	public P pointer(int i) {
		return (P) pointers[i];
	}

	BPlusTree<K, P> bt;
	
	/**
	 * Inserts the specified key and pointer assuming that this {@code LeafNode} has room for them.
	 * 
	 * @param k
	 *            the key to insert
	 * @param p
	 *            the pointer to insert
	 */
	public void insert(K k, P p) {
		if (keyCount == 0 || k.compareTo(keys[0]) < 0)
			insert(0, k, p);
		else {
			int i = findIndexL(k);
			insert(i + 1, k, p);
		}
	}

	/**
	 * Inserts the specified key and pointer at the specified index.
	 * 
	 * @param i
	 *            the index at which the key and pointer are inserted
	 * @param k
	 *            a key
	 * @param p
	 *            a pointer
	 */
	public void insert(int i, K k, P p) {
		for (int j = keyCount; j > i; j--) {
			keys[j] = keys[j - 1];
			pointers[j] = pointers[j - 1];
		}
		keys[i] = k;
		pointers[i] = p;
		keyCount++;
	}

	/**
	 * Removes a key and a pointer at the specified index.
	 * 
	 * @param i
	 *            the index at which the key and pointer are deleted
	 */
	public void delete(int i) {
		for (int j = i; j < keyCount - 1; j++) {
			keys[j] = keys[j + 1];
			pointers[j] = pointers[j + 1];
		}
		keys[keyCount - 1] = null;
		pointers[keyCount - 1] = null;
		keyCount--;
	}

	/**
	 * Returns the largest index i such that keys[i] < the given key.
	 * 
	 * @param key
	 *            a key
	 * @return the largest index i such that keys[i] < the given key; -1 if there is no such i
	 */
	protected int findIndexL(K key) {
		for (int i = keyCount - 1; i >= 0; i--) {
			if (keys[i].compareTo(key) < 0)
				return i;
		}
		return -1;
	}

	/**
	 * Returns the successor of this {@code LeafNode}
	 * 
	 * @return the successor of this {@code LeafNode}
	 */
	@SuppressWarnings("unchecked")
	public LeafNode<K, P> successor() {
		return (LeafNode<K, P>) pointers[pointers.length - 1];
	}

	/**
	 * Sets the successor of this {@code LeafNode}.
	 * 
	 * @param successor
	 *            the new successor of this {@code LeafNode}
	 * @return the previous successor of this {@code LeafNode}
	 */
	public LeafNode<K, P> setSuccessor(LeafNode<K, P> successor) {
		@SuppressWarnings("unchecked")
		LeafNode<K, P> s = (LeafNode<K, P>) pointers[pointers.length - 1];
		pointers[pointers.length - 1] = successor;
		return s;
	}

	/**
	 * Determines whether or not the specified key and pointer are contained in this {@code LeafNode}.
	 * 
	 * @param k
	 *            a key
	 * @param p
	 *            a pointer
	 * @return {@code true} if the specified key and pointer are contained in this {@code LeafNode}; {@code false}
	 *         otherwise
	 */
	public boolean contains(K k, P p) {
		for (int i = 0; i < keyCount; i++)
			if (keys[i].compareTo(k) == 0 && pointers[i].equals(p))
				return true;
		return false;
	}

	/**
	 * Removes the specified key and pointer from this {@code LeafNode}.
	 * 
	 * @param k
	 *            a key
	 * @param p
	 *            a pointer
	 */
	@Override
	public void delete(K k, Object p) {
		for (int i = 0; i < keyCount; i++) {
			if (keys[i].compareTo(k) == 0 && pointers[i].equals(p)) {
				for (int j = i; j < keyCount - 1; j++) {
					keys[j] = keys[j + 1];
					pointers[j] = pointers[j + 1];
				}
				break;
			}
		}
		keyCount--;
		keys[keyCount] = null;
		pointers[keyCount] = null;
	}

	/**
	 * Finds, starting from this {@code LeafNode}, the {@code LeafNode} that is responsible for the specified key.
	 * 
	 * @param k
	 *            a key
	 * @return the {@code LeafNode} that is responsible for the specified key
	 */
	@Override
	public LeafNode<K, P> find(K k) {
		if (keyCount > 0 && keys[keyCount - 1].compareTo(k) < 0 && successor() != null)
			return successor();
		else
			return this;
	}

	/**
	 * Finds, starting from this {@code LeafNode}, the {@code LeafNode} containing the specified key and pointer.
	 * 
	 * @param k
	 *            a key
	 * @param p
	 *            a pointer
	 * @return the {@code LeafNode} containing the specified key and pointer
	 */
	public LeafNode<K, P> find(K k, P p) {
		System.out.println("Inside find method of LeafNode.java");
		for (int i = 0; i < keyCount; i++)
			if (keys[i].compareTo(k) == 0 && pointers[i].equals(p))
				return this;
		return null;
		
//	LeafNode<K,P> temp = find(k);
//		
//		if(temp.contains(k, p))
//			return temp;
//		
//		else
//		{
//			for(int i = 0; i <= temp.parent.pointers.length ; i++)
//			{
//				LeafNode<K,P> lf = (LeafNode<K,P>) temp.parent.pointer(i);
//				
//				if(lf != null && lf.contains(k, p) )
//				{
//					return (LeafNode<K, P>) temp.parent.pointer(i);
//				}
//			}
//			return null;
//		}
	}

	/**
	 * Determines whether or not this {@code LeafNode} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code LeafNode} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	@Override
	public boolean isUnderUtilized(int degree) {
		System.out.println("Inside UnderUtilized of LeafNode.");
		float degree1 = (float) degree;
		System.out.println("BPluse Tree Degree :: " + degree1);
		int deg =(int) Math.ceil((degree1 - 1) /2) ;
		System.out.println("UnderUtilize Degree :: Pointer Count :: " +this.pointerCount() + " Degree Limit :: " + deg);
		if(this.pointerCount() < deg)
			return true;
		return false;
		//throw new UnsupportedOperationException();
	}

	/**
	 * Determines whether or not this {@code LeafNode} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code LeafNode} can be merged with the specified {@code Node}; {@code false}
	 *         otherwise
	 */
	@Override
	public boolean mergeable(Node<K, P> other) {
		
		int keyOfCurrentNode = this.keyCount;
		int otherNode = other.keyCount;
		
		System.out.println("Current Node N's keyCount :: " + this.keyCount + " Ndash's KeyCount :: " + otherNode + "Keys array Length :: " + keys.length);
		if(keyOfCurrentNode + otherNode <= keys.length)
			return true;
		return false;
		
		//throw new UnsupportedOperationException();
	}
/*
	 * Returns the number of children that this {@code NonLeafNode} has.
	 * 
	 * @return the number of children that this {@code NonLeafNode} has
	 */
	public int childCount() {
		return keyCount + 1;
	}
}
