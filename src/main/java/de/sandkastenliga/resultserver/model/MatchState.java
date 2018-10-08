package de.sandkastenliga.resultserver.model;

public enum MatchState {

	scheduled, ready, running, finished, canceled, postponed;

	public static MatchState[] getUnfinishedStates() {
		return new MatchState[] { scheduled, ready, running };
	}

	public static boolean isFinishedState(MatchState matchState) {
		for(MatchState ms : getUnfinishedStates()){
			if(ms == matchState)
				return false;
		}
		return true;
	}

	public int getIntValue(){
		return this.ordinal();
	}

}
