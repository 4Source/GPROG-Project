package ZombieGame;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;

public final class UniquePriorityQueue<T> implements Queue<T> {
    private final HashSet<T> set;
    private final PriorityQueue<T> queue;

    public UniquePriorityQueue() {
        this.set = new HashSet<>();
        this.queue = new PriorityQueue<>();
    }

    public UniquePriorityQueue(Comparator<T> comparator) {
        this.set = new HashSet<>();
        this.queue = new PriorityQueue<>(comparator);
    }

    @Override
    public boolean add(T value) {
        if (set.add(value)) {
            queue.add(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean offer(T value) {
        if (set.add(value)) {
            return queue.offer(value);
        }
        return false;
    }

    @Override
    public T remove() {
        T value = queue.remove();
        if (value != null) {
            set.remove(value);
        }
        return value;
    }

    @Override
    public T poll() {
        T value = queue.poll();
        if (value != null) {
            set.remove(value);
        }
        return value;
    }

    @Override
    public T element() {
        return queue.element();
    }

    @Override
    public T peek() {
        return queue.peek();
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object value) {
        return set.contains(value);
    }

    @Override
    public boolean remove(Object value) {
        if (set.remove(value)) {
            return queue.remove(value);
        }
        return false;
    }

    @Override
    public void clear() {
        set.clear();
        queue.clear();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private final Iterator<T> it = queue.iterator();
            private T last = null;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public T next() {
                return it.next();
            }

            @Override
            public void remove() {
                if (last == null) {
                    throw new IllegalStateException("remove() called before next()");
                }

                if (set.remove(last)) {
                    it.remove();
                    last = null;
                } else {
                    throw new IllegalStateException("Set of UniquePriorityQueue does not contain the last value");
                }
            }
        };
    }

    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @Override
    public <E> E[] toArray(E[] array) {
        return queue.toArray(array);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return set.containsAll(collection);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        boolean changed = false;
        for (T value : collection) {
            if (add(value)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        boolean changed = false;
        for (Object value : collection) {
            if (remove(value)) {
                changed = true;
            }
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        boolean changed = false;
        for (T value : this.queue) {
            if (!collection.contains(value)) {
                if (remove(value)) {
                    changed = true;
                }
            }
        }
        return changed;
    }
}
