package de.sandkastenliga.resultserver.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "team")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Team {

	@Id
	private String name;
	@ManyToOne
	private Challenge mainChallenge;
	private int mainChallengePos;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Challenge getMainChallenge() {
		return mainChallenge;
	}

	public void setMainChallenge(Challenge mainChallenge) {
		this.mainChallenge = mainChallenge;
	}

	public int getMainChallengePos() {
		return mainChallengePos;
	}

	public void setMainChallengePos(int mainChallengePos) {
		this.mainChallengePos = mainChallengePos;
	}

}
