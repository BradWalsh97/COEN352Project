package com.BradCoen352;

public class repeatedPriorityQueue {
    //each element in the pq will consist of a 'node' which is a key/value pair.
    //The PQ will sort everything based on the node's value (the frequency of the word occurring in the text)
    //The key will be the word. This word will be used to create the outputs of the project
    public class Node {
        int value; //Frequency of a dictionary word in the text
        String key; //The dictionary word found in the text

        public int getValue() {
            return this.value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setKeyAndValue(String key, int value) {
            setValue(value);
            setKey(key);
        }

        public Node() {
            int value = 0;
            String key = null;
        }

        public Node(int value, String key) {
            this.value = value;
            this.key = key;
        }
    }


    private Node[] elements = new Node[250]; //elements from 1 to N
    private int n; //number of elements in the priority queue
    private int size; //size of elements array

    //make PQ with provided capacity
    public repeatedPriorityQueue(int initialCapacity) {
        n = 0;
        //elements[] = new Node[300];
        //elements[] = new Node[initialCapacity];
        size = initialCapacity;
        for (int i = 0; i < initialCapacity; i++) {
            elements[i] = new Node();
        }
        //Node elements[] = new Node[initialCapacity];
    }

    //make empty PQ
    // public MySpecialPriorityQueue(){this(1);}
    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        return n;
    }

    public int getSizeOfArray() {
        return size;
    }

    public void setSizeOfArray(int size) {
        this.size = size;
    }

    public int getMinimumWordFrequency() {
        return elements[1].getValue();
    }

    public String getMinimumWord() {
        return elements[1].getKey();
    }

    public Node popMin() {
        if (isEmpty()) {
            System.out.println("Queue, is empty. Please fill it");
            return null;
        }
        Node min = elements[1];
        exchange(1, n--);
        sink(1);
        elements[n + 1] = null;
        //if needed, shrink heap
        if ((n > 0) && (n == (elements.length - 1) / 4))
            resize(elements.length / 2);
        return min;
    }

    private void resize(int capacity) {
        Node[] tmp = new Node[capacity]; //fix the assignment of this array
        for (int i = 0; i <= n; i++) {
            Node tmpNode = new Node(elements[i].getValue(), elements[i].getKey());
            tmp[i] = tmpNode;
        }
        for (int i = n + 1; i < capacity; i++) {
            Node tmpNode = new Node();
            tmp[i] = tmpNode;

        }
        setSizeOfArray(capacity);
//        for (int i = 0; i < n; i++) {
//            tmp[i].setKey(elements[i].getKey());
//            tmp[i].setValue(elements[i].getValue());
//        }
        elements = tmp;
    }

    public void insert(String key, int value) {
        //check if current array size is large enough. If not, double size
        //       System.out.println("Size: " + size());
        if (n == getSizeOfArray() - 1)
            resize(2 * (getSizeOfArray()));
        elements[++n].setKeyAndValue(key, value);
        swim(n);
    }

    private void swim(int n) {
        while (n > 1 && greater(n / 2, n)) {
            exchange(n, n / 2);
            n = n / 2;
        }
    }

    private boolean greater(int i, int j) {
        //return elements[i].getValue()>elements[j].getValue() || (elements[i].getValue()==elements[j].getValue() && elements[i].getKey().compareTo(elements[j].getKey())>0);
        return (elements[i].getKey().compareTo(elements[j].getKey()) > 0);
    }

    private void exchange(int i, int j) {
        Node tmp = elements[i];
        elements[i] = elements[j];
        elements[j] = tmp;
    }

    private void sink(int k) {
        while (2 * k <= n) {
            int i = 2 * k;
            if (i < n && greater(i, i + 1)) i++;
            if (!greater(k, i)) break;
            exchange(k, i);
            k = i;
        }
    }
}


