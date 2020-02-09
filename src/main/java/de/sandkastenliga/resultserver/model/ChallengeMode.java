package de.sandkastenliga.resultserver.model;

public enum ChallengeMode {

	points, ko;

	public int getIntValue(){
		return this.ordinal();
	}

}
