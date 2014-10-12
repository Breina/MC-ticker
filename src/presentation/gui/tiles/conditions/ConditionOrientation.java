package presentation.gui.tiles.conditions;

import presentation.gui.tiles.Graphic;
import presentation.objects.Orientation;

public class ConditionOrientation extends Condition {

	private Orientation matchOrientation;
	
	public ConditionOrientation(Orientation matchOrientation) {
		super();
		
		this.matchOrientation = matchOrientation;
	}
	
	public void setMatchOrientation(Orientation matchOrientation) {
		this.matchOrientation = matchOrientation;
	}
	
	@Override
	public boolean eval(Graphic g, byte id, byte data, UsedMask usedMask, Orientation orientation, byte custom) {
		
		if (orientation == matchOrientation) {
			
			super.eval(g, id, data, usedMask, orientation, custom);
			
			return true;
		}
		
		return false;
	}

}
