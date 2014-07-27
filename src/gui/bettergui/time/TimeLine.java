package gui.bettergui.time;

import utils.Tag;

public class TimeLine {
	
	private Tag[] items;
	private int start, index, end;
	
	public TimeLine(int size) {
		
		items = new Tag[size];
	}
	
	public void init(Tag first) {
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
	
	public Tag first() {
		return items[index = start];
	}
	
	public Tag prev() {
		if (index == start)
			throw new ArrayIndexOutOfBoundsException("prev");
		if (--index < 0)
			index = items.length - 1;
		return items[index];		
	}
	
	public Tag get() {
		return items[index];
	}
	
	public void set(Tag tag) {
		items[index] = tag;
		end = index;
	}
	
	public Tag next() {
		if (index == end)
			throw new ArrayIndexOutOfBoundsException("next");
		if (++index == items.length)
			index = 0;
		return items[index];
	}
	
	public Tag last() {
		return items[index = end];
	}
	
	public void add(Tag tag) {
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
