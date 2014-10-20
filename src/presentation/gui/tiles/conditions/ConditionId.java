package presentation.gui.tiles.conditions;

import presentation.gui.tiles.Graphic;
import presentation.objects.Orientation;

public class ConditionId extends Condition {
	
	private byte matchId;
	
	public ConditionId(byte matchId) {
		super();
		
		this.matchId = matchId;
	}
	
	public void setMatchId(byte matchId) {
		this.matchId = matchId;
	}
	
	@Override
	public boolean eval(Graphic g, byte id, byte data, UsedMask usedMask, Orientation orientation, byte custom) {
		
		if (id == matchId) {

			super.eval(g, id, data, usedMask, orientation, custom);
			
			return true;
		}
		
		return false;
	}

}
