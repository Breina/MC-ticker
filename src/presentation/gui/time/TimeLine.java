package presentation.gui.time;

public class TimeLine<T> {
	
	private final Object[] items;
	private int start, index, end;
	
	public TimeLine(int size) {
		
		items = new Object[size];
	}
	
	public void init(T first) {
		start = 0;
		index = 0;
		end = 0;
		
		items[0] = first;
	}
	
	public boolean atStart() {
		return index == start;
	}
	
	public boolean atEnd() {
		return index == end;
	}
	
	@SuppressWarnings("unchecked")
	public T first() {
		return (T) items[index = start];
	}
	
	@SuppressWarnings("unchecked")
	public T prev() {
		if (index == start)
			throw new ArrayIndexOutOfBoundsException("prev");
		if (--index < 0)
			index = items.length - 1;
		return (T) items[index];		
	}
	
	@SuppressWarnings("unchecked")
	public T get() {
		return (T) items[index];
	}
	
	public void set(T tag) {
		items[index] = tag;
		end = index;
	}
	
	@SuppressWarnings("unchecked")
	public T next() {
		if (index == end)
			throw new ArrayIndexOutOfBoundsException("next");
		if (++index == items.length)
			index = 0;
		return (T) items[index];
	}
	
	@SuppressWarnings("unchecked")
	public T last() {
		return (T) items[index = end];
	}
	
	public void add(T tag) {
		if (index == end)
			if (++index == items.length)
				index = 0;
		
		if (++end == items.length)
			end = 0;
		
		items[end] = tag;
		
		if (end == start)
			if (++start == items.length)
				start = 0;
	}

}
