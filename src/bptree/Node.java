package bptree;

import java.util.Arrays;

/**
 * The {@code Node} class implements nodes that constitute a B+-tree.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 * @param <K>
 *            the type of keys
 * @param <P>
 *            the type of pointers
 */
public abstract class Node<K extends Comparable<K>, P> {

	/**
	 * The number of keys that this {@code Node} currently maintains.
	 */
	protected int keyCount;

	/**
	 * The keys that this {@code Node} maintains.
	 */
	protected K[] keys;

	/**
	 * The pointers that this {@code Node} maintains.
	 */
	protected Object[] pointers;

	/**
	 * The parent {@code Node} of this {@code Node}.
	 */
	protected NonLeafNode<K, P> parent = null;

	/**
	 * Constructs a {@code Node}.
	 * 
	 * @param degree
	 *            the degree of the {@code Node}
	 */
	@SuppressWarnings("unchecked")
	public Node(int degree) {
		keyCount = 0;
		keys = (K[]) new Comparable[degree - 1];
		pointers = new Object[degree];
	}

	/**
	 * Copy-constructs a {@code Node}.
	 * 
	 * @param parent
	 *            the parent {@code Node} of this {@code Node}
	 * @param node
	 *            the other {@code Node} to copy from
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Node(NonLeafNode<K, P> parent, Node<K, P> node) {
		this.parent = parent;
		this.keyCount = node.keyCount;
		keys = (K[]) new Comparable[node.keys.length];
		System.arraycopy(node.keys, 0, keys, 0, node.keys.length);
		pointers = new Object[node.pointers.length];
		for (int i = 0; i < node.pointers.length; i++) {
			Object pointer = node.pointers[i];
			if (pointer instanceof LeafNode)
				pointers[i] = new LeafNode(this instanceof NonLeafNode ? (NonLeafNode) this : null, (LeafNode) pointer);
			else if (pointer instanceof NonLeafNode)
				pointers[i] = new NonLeafNode((NonLeafNode) this, (NonLeafNode) pointer); // copy construct the node.
			else
				pointers[i] = pointer;
		}
	}

	/**
	 * Registers a {@code NonLeafNode} as the parent {@code Node} of this {@code Node}.
	 * 
	 * @param parent
	 *            a {@code NonLeafNode}
	 */
	public void setParent(NonLeafNode<K, P> parent) {
		this.parent = parent;
	}

	/**
	 * Returns the parent {@code Node} of this {@code Node}.
	 * 
	 * @return the parent {@code Node} of this {@code Node}
	 */
	public NonLeafNode<K, P> parent() {
		return parent;
	}

	/**
	 * Returns a string representation of this {@code Node}.
	 */
	public String toString() {
		return keyCount + " " + Arrays.toString(keys);
	}

	/**
	 * Returns the number of keys in this {@code Node}.
	 * 
	 * @return the number of keys in this {@code Node}
	 */
	public int keyCount() {
		return keyCount;
	}

	/**
	 * Returns the key at the specified index.
	 * 
	 * @param i
	 *            the index of the key
	 * @return the key at the specified index
	 */
	public K key(int i) {
		return keys[i];
	}

	/**
	 * Returns the pointer at the specified index.
	 * 
	 * @param i
	 *            the index of the key
	 * @return the pointer at the specified index
	 */
	public Object pointer(int i) {
		return pointers[i];
	}

	/**
	 * Appends the specified keys and their pointers of the specified {@code Node} into this {@code Node}.
	 * 
	 * @param node
	 *            a {@code Node}.
	 * @param beginIndex
	 *            the beginning index of the keys, inclusive
	 * @param endIndex
	 *            the ending index of the keys, inclusive
	 */
	public void append(Node<K, P> node, int beginIndex, int endIndex) {
		for (int i = 0; i <= endIndex - beginIndex; i++) {
			this.keys[keyCount] = node.keys[i + beginIndex];
			this.pointers[keyCount] = node.pointers[i + beginIndex];
			keyCount++;
		}
	}

	/**
	 * Clears this {@code Node}.
	 */
	public void clear() {
		keyCount = 0;
		for (int i = 0; i < keys.length; i++)
			keys[i] = null;
		for (int i = 0; i < pointers.length; i++)
			pointers[i] = null;
	}

	/**
	 * Determines whether or not this {@code Node} is full and thus cannot contain more keys.
	 * 
	 * @return {@code true} if this {@code Node} is full and thus cannot contain more keys; {@code false} otherwise
	 */
	public boolean isFull() {
		return keyCount >= keys.length;
	}

	/**
	 * Finds, starting from this {@code Node}, the {@code LeafNode} that is responsible for the specified key.
	 * 
	 * @param k
	 *            a key
	 * @return the {@code LeafNode} that is responsible for the specified key
	 */
	public abstract LeafNode<K, P> find(K k);
	
	/**
	 * Finds, starting from this {@code Node}, the {@code LeafNode} that is responsible for the specified key.
	 * 
	 * @param k - a key , p - pointer
	 *            
	 * @return the {@code LeafNode} that is responsible for the specified key
	 */ 
	public abstract LeafNode<K, P> find(K k, P p);

	/**
	 * Removes the specified key and pointer from this {@code Node}.
	 * 
	 * @param key
	 *            a key
	 * @param pointer
	 *            a pointer
	 */
	public abstract void delete(K key, Object pointer);

	/**
	 * Determines whether or not this {@code Node} is under-utilized and thus some action such as merging or
	 * redistribution is needed.
	 * 
	 * @return {@code true} if this {@code Node} is under-utilized and thus some action such as merging or
	 *         redistribution is needed; {@code false} otherwise
	 */
	public abstract boolean isUnderUtilized(int degree);

	/**
	 * Determines whether or not this {@code Node} can be merged with the specified {@code Node}.
	 * 
	 * @param other
	 *            another {@code Node}
	 * @return {@code true} if this {@code Node} can be merged with the specified {@code Node}; {@code false} otherwise
	 */
	public abstract boolean mergeable(Node<K, P> other);

	
	// Method to count the available pointers in the node.
	public int pointerCount()
	{
		int count=0;
		for(int i= 0; i < this.pointers.length ; i++)
		{
			if(pointers[i] == null)
			{
				break;
			}
			count++;
		}
		return count;
	}
}
