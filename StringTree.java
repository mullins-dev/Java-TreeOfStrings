import java.io.*;
import java.util.*;


/**
 * 
 * Ethan Mullins
 * 4-27-20
 *
 */

/*
 * Class that stores 6 letter strings efficiently. Has a private Node class, and the head
 * of the tree does not store any data. Any node in the tree will have up to 26 children 
 * (the number of English letters, all lowercase). At each level you will store a single letter of the word. 
 * If multiple words have the same starting letter(s) then you will store the 
 * similar letters once and then they will split into different sub-trees.
 * For example: if you added "amazon" and "amazin" it would look like this in theory:
 *                              _ 
 *                              a
 *                              |
 *                              m
 *                              |
 *                              a
 *                              |
 *                              z
 *                            /   \
 *                           o     i
 *                           |     |
 *                           n     n
 */
public class StringTree {

	//================================================================== Instance Variable (The head node)
	private Node root;

	//================================================================== Constructors

	/*
	 * Constructs a tree with no words
	 */
	public StringTree() {
		root = new Node(' '); // No data in the root
	}

	/*
	 * Reads all the words from a file and adds them to a new tree.
	 * If not the file is not found, handles the exception by printing
	 * "File not found. Starting with empty tree."
	 * @param filename the name of the file to be read from
	 */
	public StringTree(String filename) {
		try {
			root = new Node(' ');
			File file = new File(filename);
			Scanner sc = new Scanner(file);
			while (sc.hasNext()) {
				this.add(sc.nextLine());
			}
			sc.close();
		} catch (Exception e) {
			System.out.println(("File not found. Starting with empty tree."));
			root = new Node(' ');
		}
	}

	//================================================================== Methods

	/*
	 * Adds a single 6-letter String word to the tree
	 * @return true if the world was added, false otherwise/(if word is already in tree)
	 * @throws IllegalArgumentException if the string does not have a length of 6
	 */
	public boolean add(String str) {
		if (str.length() != 6) {
			throw new IllegalArgumentException("Invalid string size");
		}
		if (this.contains(str)) {
			return false;
		}
		// if the child contains the character, move onto the next one, and if null, add the rest
		// Call recursive helper
		add(this.root, str);
		return true;
	}

	/*
	 * Private helper method to add, that loops through and figures out
	 * how to add a given word
	 */
	private void add(Node root, String str) {
		if (str.length() > 0) {
			if (getChild(root, str.charAt(0)) != null) {
				Node next = getChild(root, str.charAt(0));
				str = str.substring(1);	
				add(next, str);
			} else {
				Node nw = new Node(str.charAt(0));
				root.children.add(nw);
				str = str.substring(1);
				add(nw, str);
			}
		}
	}

	/*
	 * Determines if a child node's children contain a certain
	 * character
	 * @return true if contains, false otherwise
	 */
	private Node getChild(Node letter, char c) {
		if (letter.children != null) {
			for (Node n : letter.children) {
				if (n.data == c) {
					return n;
				}
			}
		}
		return null;
	}

	/*
	 * Determines if the tree contains a given string 
	 * @return true if the tree contains the string, false otherwise
	 */
	public boolean contains(String str)	{
		if (str.length() != 6) { return false; }
		return contains(this.root, str);
	}

	/*
	 * Private helper method to contains that uses the root node
	 * in order to try and find the word
	 */
	private boolean contains(Node root, String str) {
		if (str.length() > 0) {
			if (getChild(root, str.charAt(0)) == null) {
				return false;
			}
			Node nw = getChild(root, str.charAt(0));
			str = str.substring(1);
			return contains(nw, str);
		} else {
			return true;
		}
	}

	/*
	 * Removes a given word from the tree
	 * @return true if the string existed, false otherwise, false if word is not correct length
	 */
	public boolean remove(String str) {
		if (str.length() != 6 || !(this.contains(str))) { return false; }
		remove(this.root, str);
		return true;
	}

	/*
	 * Helper method that works with remove
	 * @return true once removed
	 */
	private void remove(Node root, String str) {
		// Example of inefficient way to write a method
		Node let1 = getChild(root, str.charAt(0));
		Node let2 = getChild(let1, str.charAt(1));
		Node let3 = getChild(let2, str.charAt(2));
		Node let4 = getChild(let3, str.charAt(3));
		Node let5 = getChild(let4, str.charAt(4));
		Node let6 = getChild(let5, str.charAt(5));
		Stack<Node> stk = new Stack<Node>();
		stk.push(let1);
		stk.push(let2);
		stk.push(let3);
		stk.push(let4);
		stk.push(let5);
		stk.push(let6);
		while (!(stk.isEmpty())) {
			Node temp = stk.pop();
			if (isLeaf(temp)) {
				stk.peek().children.remove(temp);
			}
		}
	}


	/*
	 * Finds and returns a list that contains all the Strings
	 * starting with the given substring, if the substring is empty, "", 
	 * return all words
	 * @return a list with all strings starting with given substring, if "", all words
	 */
	public List<String> allStartWith(String sub) {
		List<String> result = new LinkedList<String>();
		if (root.children.isEmpty()) { return result; }
		List<String> removes = new LinkedList<String>();
		buildWordSet(this.root, "", result);
		for (String s : result) {
			if (!(sub.equals(s.substring(0, sub.length())))) {
				removes.add(s);
			}
		}
		result.removeAll(removes);
		return result;
	}

	/*
	 * (void) Recursive helper that builds up the set for allStartWith
	 */
	private void buildWordSet(Node root, String soFar, List<String> result) {
		if (isLeaf(root)) {
			result.add(soFar);
		}
		for (Node n : root.children) {
			buildWordSet(n, soFar + n.data, result);
		}
	}

	/*
	 * Creates map with a <Letter, <Set of all words>
	 * @return a map where the key is a letter of the alphabet, and the value is the 
	 * set of all words in that tree beginning with that letter
	 */
	public Map<Character, Set<String>> wordsByFirstLetter() {
		List<String> strs = allStartWith("");
		Map<Character, Set<String>> result = new TreeMap<Character, Set<String>>();
		for (String s : strs) {
			result.put(s.charAt(0), new TreeSet<String>());
		}
		for (String s : strs) {
			for (char c : result.keySet()) {
				if (s.charAt(0) == c) {
					result.get(c).add(s);
				}
			}
		}
		return result;
	}

	/*
	 * Returns the number of words in the tree
	 * @return the number of words in the tree
	 */
	public int numWords() {
		if (isLeaf(this.root)) { return 0; }
		return leafCount(this.root);
	}

	/*
	 * Private helper to numWords
	 * @return the number of words in the StringTree
	 */
	private int leafCount(Node root) {
		int count = 0;
		if (isLeaf(root)) { return 1; }
		for (Node child : root.children) {
			count += leafCount(child);
		}
		return count;
	}

	/*
	 * Determines if a node is a leaf of not
	 * @return true if leaf, false if not a leaf
	 */
	private boolean isLeaf(Node root) {
		if (root.children.isEmpty()) {
			return true;
		}
		return false;
	}

	/*
	 * Returns the "compression" of the tree, which is a measure of how much
	 * space was saved by storing words in the tree. 
	 * For example: 6 letter words are stored in the tree, totaling 36 letters. 
	 * But, we managed to store them using only 26 letter nodes: savings of 10 letters
	 * So, compression() would return 10.0/36.0 == 0.2778, indicating that we saved 27.78%
	 * by storing words in a tree. 
	 * @returns the space saved by using a tree
	 */
	public double compression() {
		int numNodes = numNodes();
		int numWords = numWords();
		double shouldBe = numWords * 6;
		double diff = shouldBe - numNodes;
		return (diff / shouldBe);
	}

	/*
	 * Returns the number of nodes in the tree
	 * @return the number of words in the tree
	 */
	private int numNodes() {
		if (isLeaf(this.root)) { return 0; }
		return numNodes(this.root);
	}

	/*
	 * Private helper to numWords
	 * @return the number of words in the StringTree
	 */
	private int numNodes(Node root) {
		int count = 0;
		for (Node child : root.children) {
			count++;
			count += numNodes(child);
		}
		return count;
	}

	//================================================================== Node inner class
	private class Node {
		private char data;
		private ArrayList<Node> children;
		private Node(char c) {
			this.data = c;
			this.children = new ArrayList<Node>();
		}
	}

}
