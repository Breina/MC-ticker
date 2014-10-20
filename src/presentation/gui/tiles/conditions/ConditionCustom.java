package presentation.gui.tiles.conditions;

import presentation.gui.tiles.Graphic;
import presentation.objects.Orientation;

public class ConditionCustom extends Condition {
	
	private byte matchCustom, matchMask;
	
	public ConditionCustom(byte matchCustom, byte matchMask) {
		super();
		
		this.matchCustom = matchCustom;
		this.matchMask = matchMask;
	}

	public void setMatchCustom(byte matchCustom) {
		this.matchCustom = matchCustom;
	}

	public void setMatchMask(byte matchMask) {
		this.matchMask = matchMask;
	}

	@Override
	public boolean eval(Graphic g, byte id, byte data, UsedMask usedMask, Orientation orientation, byte custom) {
		
		if (matchCustom == (custom & matchMask)) {

			super.eval(g, id, data, usedMask, orientation, custom);
			
			return true;
		}
		
		return false;
	}

}
