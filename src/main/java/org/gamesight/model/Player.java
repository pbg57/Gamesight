package org.gamesight.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

// TODO: Add @Temporal timestamps to mark entity create and updated timestamps
@Entity
@Table(name = "players")
public class Player extends UserTemporal implements Serializable {

	/*
	A Player entity holds the data describing a player and holds references to all
	 games this player is using. A Player is also a Gamesight User.
	 */

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Size(max = 100)
	private String name;

	/*
		Configure data relationship. A Player may have many Games they are playing.
		The same Game may also be used by multiple Players. We have a many-to-many
		relationship.
		The Player entity specifies the JoinTable so it "owns" the relationship.
	 */
	@JsonManagedReference
	@ManyToMany(fetch = FetchType.LAZY,
			cascade = {
					CascadeType.PERSIST,
					CascadeType.MERGE
			})
	@JoinTable(name = "player_game",
			joinColumns = { @JoinColumn(name = "player_id") },
			inverseJoinColumns = { @JoinColumn(name = "game_id") })
	private Set<Game> games = new HashSet<>();

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;


	// No-args constructor required for spring-rest access
	public Player() {
	}

	public Player(@JsonProperty("name") String name) {

		this.name = name;
	}


	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String toString() {
		return "Player{" +
				"id=" + id +
				", name='" + name + "}";
	}

	public Set<Game> getGames() {

		return games;
	}

	public User getUser() {

		return user;
	}

	public void setUser(User user) {

		this.user = user;
	}
}
