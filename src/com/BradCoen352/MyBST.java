package com.BradCoen352;

public class MyBST<Key extends Comparable<Key>, Value> {
    private Node root;
    private class Node{
        private Key key;
        private Value value;
        private Node left, right;
        private int size;

        public Node(Key key, Value value, int size){
            this.key = key; //the word
            this.value = value; //the frequency of a word appearing in a text
            this.size = size;
        }
    }

    //init empty tree
    public MyBST(){}
    public boolean isEmpty(){return size(root) == 0;}
    public int size(Node n){
        if(n == null) return 0;
        else return n.size;
    }
    //Return true if contains, false if else. Throw exception if argument is null
    public boolean contains(Key key){
        if(key == null) throw new IllegalArgumentException("You passed a null key, thats not allowed!");
        return get(key) != null;
    }
    public Value get(Key key) {
        return get(root, key);
    }
    /*this implementation of get allows the recursive call after the compare as we can specify what the
    * 'root' of the method call will be. Thus we can specify if we want to call get on the right or
    * left node of the tree.
    */
    private Value get(Node n, Key key){
        if(n == null) return null; //make sure root of get isnt null (tree empty)
        if(key == null) throw new IllegalArgumentException("Can't pass a null key!"); //make sure arg isnt null

        int compare = key.compareTo(n.key);
        if      (compare < 0) return get(n.left, key);
        else if (compare > 0) return get(n.right, key);
        else              return n.value;
    }
    public void put(Key key, Value value) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        if (value == null) {
            //delete(key);
            return;
        }
        root = put(root, key, value);
    }
    private Node put(Node n, Key key, Value value) {
        if (n == null) return new Node(key, value, 1);
        int compare = key.compareTo(n.key);
        if      (compare < 0) n.left  = put(n.left,  key, value);
        else if (compare > 0) n.right = put(n.right, key, value);
        else              n.value   = value;
        n.size = 1 + size(n.left) + size(n.right);
        return n;
    }

}

