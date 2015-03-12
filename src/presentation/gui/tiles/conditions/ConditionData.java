package presentation.gui.tiles.conditions;

import presentation.gui.tiles.Graphic;
import presentation.objects.Orientation;

public class ConditionData extends Condition {
	
	private byte matchData, matchMask;
	
	public ConditionData(byte matchData) {
		this(matchData, (byte) 0b1111111);
	}
	
	private ConditionData(byte matchData, byte matchMask) {
		super();
		
		this.matchData = matchData;
		this.matchMask = matchMask;
	}
	
	public void setData(byte matchData) {
		this.matchData = matchMask;
	}
	
	public void setMask(byte matchMask) {
		this.matchMask = matchMask;
	}

	@Override
	public boolean eval(Graphic g, byte id, byte data, UsedMask usedMask, Orientation orientation, byte custom) {
		
		// TODO rather than checking every mask like this, find a way to only check what is required
		if ((matchMask & usedMask.mask) != 0) {
			return false;
		}
		
		if ((data & matchMask) == matchData) {
			
			usedMask.mask |= matchMask;
			
			super.eval(g, id, data, usedMask, orientation, custom);
			
			return true;
		}
		
		return false;
	}

}
