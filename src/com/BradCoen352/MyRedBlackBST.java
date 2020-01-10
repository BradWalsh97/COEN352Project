package com.BradCoen352;

//todo: when insert, put lower case
//todo: determine if deleting stuff is needed
//todo: Deletion necessary?
//todo: if no dictionary provided, use the default dictionary

/*This implementation of a red-black tree is inspired by the one outline in the slides as well as
 *this class's textbook: Algorithms, 4th Edition by Robert Sedgewick and Kevin Wayne
 *
 * The way it works is by simply looking up if the key is there or not. If it is, it will increment the value
 * which holds the amount of times the key is found in the give text
 *
 * The tree is built using the complete word as the key as for part 1 we can expect the texts to be without
 * spaces or mutations. This technique may not be used for part 2
 */

public class MyRedBlackBST<Key extends Comparable<Key>, Value> {
    //using this will ease keeping track throughout code of the type of link between a parent and child
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private Node root; //root of BST
    private class Node{
        private Key key; //the word from the dictionary
        private Value value; //amount of times the key is found the the provided text todo: does this need to be a Value or can it maybe just be an int?
        private Node left, right; //links to the left and right children (subtrees) of the node
        private boolean colour; //colour of the link between this node and its parent
        private int size; //amount of nodes in the subtree

        //constructor for the node
        public Node(Key key, Value value, boolean colour, int size){
            this.key = key;
            this.value = value;
            this.colour = colour;
            this.size = size;
        }
    }

    public MyRedBlackBST(){}; //Initialize empty RBBST (Red-Black Balanced Search Tree)

    /*Below are the methods to use the RBBST*/

    private int size(Node n){//Returns the size of the node starting at Node n
        if(n == null) return 0;
        else return n.size;
    }
    public boolean isRed(Node n) {//returns false if black or null, true if red
        if(n == null) return false;
        return n.colour == RED;
    }
    public int size(){return size(root);} //returns total size of the RBBST
    public boolean isEmpty(){return root == null;} //check if empty. Return true if empty, false if not
    public void incrementValue(Key key){ //todo: make this more efficient because its TRASH
        Value val = get(key); //get current value of searched key
        int value = Integer.parseInt(val.toString()) + 1;
        String newVal = String.valueOf(value);
        Value newValue = (Value)newVal;
        put(key, newValue); //re-add the key/value pair with an incremented value
        //todo: test if delete key?
    }

    public Value get(Key key){//get  value based on key. Return null if its not found or if key invalid
        if(key == null ) return null; //todo: Throw exception?
        Node n = root; //want to work with the root without messing it up
        while(n != null){
            int compare = key.compareTo(n.key);
            if(compare < 0)         n = n.left;
            else if(compare > 0)    n = n.right;
            else                    return n.value;
        }
        return null; //if not found
    }
    public boolean contains(Key key){return get(key) != null;} //check if it contains the key. If yes, true. Else false

    /** Due to the nature of creating the dictionary, by default value does not need to be passed although
     * I have decided to code is as such in case I might need to pass value in the future. Thus, for the time being
     * when put is called for creating the dictionary I will simply pass zero.
     * @param key the key to be added
     * @param value key's associated value
     **/
    public void put(Key key, Value value){//insert key/value pair into RBBST
        if(value == null || key == null){ //if either values are null we just return since that invalid. //TODO: throw exception? is it worth coding exceptions?
            //delete(key); todo: Add this if needed?
            return;
        }
        root = put(root, key, value);
        root.colour = BLACK;
    }
    private Node put(Node miniRoot, Key key, Value value){//this allows insertion at Node n as root.
        if(miniRoot == null)//if the tree is empty, root will be null.
            return new Node(key, value, RED, 1);//Thus we make the root as what has just been passed with size 1

        int compare = key.compareTo(miniRoot.key); //insert it in the correct location based on if bigger or not
        if      (compare < 0) miniRoot.left  = put(miniRoot.left,  key, value);
        else if (compare > 0) miniRoot.right = put(miniRoot.right, key, value);
        else                  miniRoot.value = value;

        //now that its been inserted, we check for any bad (right-learning) red links
        if (isRed(miniRoot.right) && !isRed(miniRoot.left))      miniRoot = rotateLeft(miniRoot);
        if (isRed(miniRoot.left)  &&  isRed(miniRoot.left.left)) miniRoot = rotateRight(miniRoot);
        if (isRed(miniRoot.left)  &&  isRed(miniRoot.right))     flipColours(miniRoot);
        miniRoot.size = size(miniRoot.left) + size(miniRoot.right) + 1; //size is all children + 1 (itself)

        return miniRoot;
    }

    /* RBBST manipulation methods (rotate, switch, swap, balance, etc) */
    private Node rotateRight(Node n){//rotate right (code from book)
        Node tmp = n.left;
        n.left = tmp.right;
        tmp.right = n;
        tmp.colour = tmp.right.colour;
        tmp.right.colour = RED;
        tmp.size = n.size;
        n.size = size(n.left) + size(n.right) + 1;
        return tmp;
    }
    private Node rotateLeft(Node n){//rotate left (code from book)
        Node x = n.right;
        n.right = x.left;
        x.left = n;
        x.colour = x.left.colour;
        x.left.colour = RED;
        x.size = n.size;
        n.size = size(n.left) + size(n.right) + 1;
        return x;
    }
    void flipColours(Node n){
        n.colour = !n.colour;
        n.left.colour = !n.left.colour;
        n.right.colour = !n.right.colour;
    }
    private Node moveRedLeft(Node n) {
        flipColours(n);
        if (isRed(n.right.left)) {
            n.right = rotateRight(n.right);
            n = rotateLeft(n);
            flipColours(n);
        }
        return n;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    private Node moveRedRight(Node n) {
        flipColours(n);
        if (isRed(n.left.left)) {
            n = rotateRight(n);
            flipColours(n);
        }
        return n;
    }

    // restore red-black tree invariant
    private Node balance(Node n) {
        if (isRed(n.right))                      n = rotateLeft(n);
        if (isRed(n.left) && isRed(n.left.left)) n = rotateRight(n);
        if (isRed(n.left) && isRed(n.right))     flipColours(n);

        n.size = size(n.left) + size(n.right) + 1;
        return n;
    }

    public Key select(int k) {
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + k);
        }
        Node x = select(root, k);
        return x.key;
    }

    // the key of rank k in the subtree rooted at x
    private Node select(Node x, int k) {
        // assert x != null;
        // assert k >= 0 && k < size(x);
        int t = size(x.left);
        if      (t > k) return select(x.left,  k);
        else if (t < k) return select(x.right, k-t-1);
        else            return x;
    }










}
