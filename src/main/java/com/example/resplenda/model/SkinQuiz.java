package com.example.resplenda.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "skin_quiz")
public class SkinQuiz {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private String skinType; // e.g., "Oily", "Dry"

	@ElementCollection
	private Set<String> productPref = new HashSet<>();
	private String finish; // e.g., "matt", "dewy"
	@ElementCollection
	private Set<String> tagPref = new HashSet<>(); // e.g., "vegan", "cruelty-free"

	@OneToOne
	private User user;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSkinType() {
		return skinType;
	}

	public void setSkinType(String skinType) {
		this.skinType = skinType;
	}

	public Set<String> getProductPref() {
		return productPref;
	}

	public void setProductPref(Set<String> productPref) {
		this.productPref = productPref;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}

	public Set<String> getTagPref() {
		return tagPref;
	}

	public void setTagPref(Set<String> tagPref) {
		this.tagPref = tagPref;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
