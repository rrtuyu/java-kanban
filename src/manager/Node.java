package manager;

import task.Task;

class Node<T extends Task> {
    public T data;
    public Node<T> next;
    public Node<T> prev;

    public Node(T data, Node<T> prev, Node<T> next) {
        this.data = data;
        this.prev = prev;
        this.next = next;
    }
}
