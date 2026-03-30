package nl.han.ica.datastructures;

import java.util.NoSuchElementException;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    private class Node {
        T value;
        Node next;

        public Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node head;
    private int size;

    public HANLinkedList() {
        head = null;
        size = 0;
    }

    @Override
    public void addFirst(T value) {
        Node newNode = new Node(value);
        newNode.next = head;
        head = newNode;
        size++;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public void insert(int index, T value) {
        if (index < 0 || index > size) throw new IndexOutOfBoundsException();

        if (index == 0) {
            addFirst(value);
            return;
        }

        Node current = head;
        for (int i = 0; i < index - 1; i++) {
            current = current.next;
        }

        Node newNode = new Node(value);
        newNode.next = current.next;
        current.next = newNode;
        size++;
    }

    @Override
    public void delete(int pos) {
        if (pos < 0 || pos >= size) throw new IndexOutOfBoundsException();

        if (pos == 0) {
            head = head.next;
            size--;
            return;
        }

        Node current = head;
        for (int i = 0; i < pos - 1; i++) {
            current = current.next;
        }

        current.next = current.next.next;
        size--;
    }

    @Override
    public T get(int pos) {
        if (pos < 0 || pos >= size) throw new IndexOutOfBoundsException();

        Node current = head;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }
        return current.value;
    }

    @Override
    public void removeFirst() {
        if (head == null) throw new NoSuchElementException();
        head = head.next;
        size--;
    }

    @Override
    public T getFirst() {
        if (head == null) throw new NoSuchElementException();
        return head.value;
    }

    @Override
    public int getSize() {
        return size;
    }
}
