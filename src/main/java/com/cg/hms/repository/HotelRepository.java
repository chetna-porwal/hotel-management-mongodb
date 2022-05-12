package com.cg.hms.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import com.cg.hms.model.Hotel;

@Repository
public interface HotelRepository extends MongoRepository<Hotel, String>,QuerydslPredicateExecutor<Hotel>{
	
	//list of supported query methods in mongodb
	//https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mongo.repositories
	List<Hotel> findByPricePerNightLessThan(int max);
	
	@Query(value = "{'address.city':?0}")
    List<Hotel> findByCity(String city);

}
