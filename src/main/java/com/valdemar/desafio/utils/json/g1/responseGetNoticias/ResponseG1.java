package com.valdemar.desafio.utils.json.g1.responseGetNoticias;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseG1 {
	private List<Item> items;

	
	public ResponseG1() {}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
	
	
}
