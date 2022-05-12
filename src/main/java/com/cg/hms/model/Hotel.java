package com.cg.hms.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@ToString
@Document(collection = "HOTELS")
public class Hotel {
	
	@Id
	private String id;
	@NonNull
	private String name;
	
	@Indexed(direction = IndexDirection.ASCENDING)
	@NonNull
	private int pricePerNight;
	@NonNull
	private Address address;
	@NonNull
	private List<Review> reviews;

}
