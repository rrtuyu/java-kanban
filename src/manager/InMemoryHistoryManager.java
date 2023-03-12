package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<Task> historyList;
    private final Map<Integer, Node<Task>> historyMap;

    public InMemoryHistoryManager() {
        historyList = new CustomLinkedList<>();
        historyMap = new HashMap<>();
    }

    @Override
    public void add(Task task) {
        int id = task.getId();

        if (historyMap.containsKey(id))
            remove(id);

        historyMap.put(id, historyList.linkLast(task));
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    @Override
    public void remove(int id) {
        Node<Task> node = historyMap.remove(id);
        if (node != null)
            historyList.removeNode(node);
    }
}

class CustomLinkedList<T extends Task> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    CustomLinkedList() {
        size = 0;
        head = null;
        tail = null;
    }

    Node<T> linkLast(T data) {
        Node<T> t = tail;
        Node<T> newNode = new Node<>(data, t, null);
        tail = newNode;
        if (t == null)
            head = newNode;
        else
            t.next = newNode;
        size++;
        return newNode;
    }

    void removeNode(Node<T> node) {
        Node<T> prev = node.prev;
        Node<T> next = node.next;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            node.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            node.next = null;
        }

        size--;
    }

    List<T> getTasks() {
        List<T> hList = new ArrayList<>();
        for (Node<T> n = head; n != null; n = n.next)
            hList.add(n.data);
        return hList;
    }
}
