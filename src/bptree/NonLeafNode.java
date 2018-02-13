package bptree;

/**
 * The {@code NonLeafNode} class implements non-leaf nodes in a B+-tree.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public class NonLeafNode<K extends Comparable<K>, P> extends Node<K, P> {

	/**
	 * Constructs a {@code NonLeafNode}.
	 * 
	 * @param degree
	 *            the degree of the {@code NonLeafNode}
	 */
	public NonLeafNode(int degree) {
		super(degree);
	}

	/**
	 * Constructs a {@code NonLeafNode} while adding the specified key and registering the specified {@code Node}s as
	 * children.
	 * 
	 * @param degree
	 *            the degree of the {@code NonLeafNode}
	 * @param n
	 *            a {@code Node}
	 * @param key
	 *            a key
	 * @param nn
	 *            a {@code Node}
	 */
	public NonLeafNode(int degree, Node<K, P> n, K key, Node<K, P> nn) {
		this(degree);
		pointers[0] = n;
		n.setParent(this);
		keys[0] = key;
		pointers[1] = nn;
		nn.setParent(this);
		keyCount = 1;
	}

	BPlusTree<K, P> bt;
	
	/**
	 * Copy-constructs a {@code NonLeafNode}.
	 * 
	 * @param parent
	 *            the parent {@code Node} of this {@code NonLeafNode}
	 * @param node
	 *            the other {@code NonLeafNode} to copy from
	 */
	public NonLeafNode(NonLeafNode<K, P> parent, NonLeafNode<K, P> node) {
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
	public Node<K, P> pointer(int i) {
		return (Node<K, P>) pointers[i];
	}

	/**
	 * Inserts the specified key and {@code Node} after the specified child {@code Node}.
	 * 
	 * @param key
	 *            the key to insert
	 * @param node
	 *            the {@code Node} to insert
	 * @param child
	 *            the {@code Node} after which the key and the specified {@code Node} will be inserted
	 */
	public void insertAfter(K key, Node<K, P> node, Node<K, P> child) {
		int i = keyCount;
		System.out.println("Value of I :: " + i);
		while (pointers[i] != child) {
			System.out.println("Keys :: " + i + " "+ keys[i - 1]);
			keys[i] = keys[i - 1];
			pointers[i + 1] = pointers[i];
			i--;
		}
		System.out.println("After While");
		keys[i] = key;
		pointers[i + 1] = node;
		node.setParent(this);
		keyCount++;
	}

	/**
	 * Returns the number of children that this {@code NonLeafNode} has.
	 * 
	 * @return the number of children that this {@code NonLeafNode} has
	 */
	public int childCount() {
		return keyCount + 1;
	}

	public int pointerCount()
	{
		int count=0;
		for(int i= 0; i < pointers.length ; i++)
		{
			if(pointers[i] != null)
			{
				count++;
			}
			else
				continue;
		}
		return count;
	}
	/**
	 * Copies the specified keys and their pointers of the specified {@code NonLeafNode} into this {@code NonLeafNode}.
	 * 
	 * @param node
	 *            a {@code NonLeafNode}
	 * @param beginIndex
	 *            the beginning index of the keys, inclusive
	 * @param endIndex
	 *            the ending index of the pointers, inclusive
	 */
	public void copy(NonLeafNode<K, P> node, int beginIndex, int endIndex) {
		clear();
		super.append(node, beginIndex, endIndex - 1);
		this.pointers[keyCount] = node.pointers[keyCount + beginIndex];
		for (int i = 0; i <= keyCount; i++)
			pointer(i).setParent(this);
	}

	/**
	 * Inserts a key and pointer at the specified indices.
	 * 
	 * @param k
	 *            a key
	 * @param iK
	 *            the index at which the key is inserted
	 * @param p
	 *            a pointer
	 * @param iP
	 *            the index at which the pointer is inserted
	 */
	public void insert(K k, int iK, Node<K, P> p, int iP) {
		for (int i = keyCount; i > iK; i--)
			keys[i] = keys[i - 1];
		keys[iK] = k;
		for (int i = keyCount + 1; i > iP; i--)
			pointers[i] = pointers[i - 1];
		pointers[iP] = p;
		p.setParent(this);
		keyCount++;
	}

	/**
	 * Removes the key and pointer at the specified indices.
	 * 
	 * @param iK
	 *            the index at which the key is deleted
	 * @param iP
	 *            the index at which the pointer is deleted
	 */
	public void delete(int iK, int iP) {
		for (int i = iK; i < keyCount - 1; i++)
			keys[i] = keys[i + 1];
		for (int i = iP; i < keyCount; i++)
			pointers[i] = pointers[i + 1];
		keys[keyCount - 1] = null;
		pointers[keyCount] = null;
		keyCount--;
	}

	/**
	 * Removes the specified key and pointer from this {@code NonLeafNode}.
	 * 
	 * @param key
	 *            a key
	 * @param pointer
	 *            a pointer
	 */
	@Override
	public void delete(K key, Object pointer) {
		for (int i = 0; i < keyCount; i++) {
			if (keys[i].compareTo(key) == 0 && pointers[i + 1].equals(pointer)) {
				for (int j = i; j < keyCount - 1; j++) {
					keys[j] = keys[j + 1];
					pointers[j + 1] = pointers[j + 2];
				}
				break;
			}
		}
		keyCount--;
		keys[keyCount] = null;
		pointers[keyCount + 1] = null;
	}

	/**
	 * Changes the key between the specified {@code Node}s.
	 * 
	 * @param p
	 *            a {@code Node}
	 * @param n
	 *            a {@code Node}
	 * @param k
	 *            the new key
	 */
	public void changeKey(Node<K, P> p, Node<K, P> n, K k) {
		for (int i = 0; i < keyCount; i++)
			if (pointers[i] == p && pointers[i + 1] == n) {
				keys[i] = k;
				return;
			}
		throw new UnsupportedOperationException("There must be a bug in the code. This case must not happen!");
	}

	/**
	 * Finds, starting from this {@code NonLeafNode}, the {@code LeafNode} that is responsible for the specified key.
	 * 
	 * @param k
	 *            a key
	 * @return the {@code LeafNode} that is responsible for the specified key
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LeafNode<K, P> find(K k) {
		int i = 0;
		for (; i < keyCount && k.compareTo(keys[i]) > 0; i++)
			;
		return ((Node<K, P>) pointers[i]).find(k);
	}

	
	@SuppressWarnings("unchecked")
	@Override
	public LeafNode<K, P> find(K k, P p) {
		// TODO Auto-generated method stub
		System.out.println("Inside find method of NonLeafNode.java");
		int i = 0;
		for (; i < keyCount && k.compareTo(keys[i]) > 0; i++)
			;
		return ((Node<K, P>) pointers[i]).find(k);
		}
	
	
	/**
	 * Determines whether or not this {@code NonLeafNode} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code NonLeafNode} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	@Override
	public boolean isUnderUtilized(int degree) {
		System.out.println("Inside UnderUtilized of NonLeafNode.");
		float degree1 = (float) degree;
		System.out.println("BPluse Tree Degree :: " + degree1);
		int deg =(int) Math.ceil((degree1 ) /2) ;
		System.out.println("UnderUtilize Degree :: Pointer Count :: " +this.pointerCount() + " Degree Limit :: " + deg);
		if(this.pointerCount() < deg)
			return true;
		return false;
		//throw new UnsupportedOperationException();
	}

	/**
	 * Determines whether or not this {@code NonLeafNode} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code NonLeafNode} can be merged with the specified {@code Node}; {@code false}
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
		// throw new UnsupportedException();
	}

	protected int findIndexL(K key) {
		for (int i = keyCount - 1; i >= 0; i--) {
			if (keys[i].compareTo(key) < 0)
				return i;
		}
		return -1;
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
	
}
