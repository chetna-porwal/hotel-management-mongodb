package com.cg.hms.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import com.cg.hms.model.Hotel;
import com.cg.hms.model.QHotel;
import com.cg.hms.repository.HotelRepository;
import com.querydsl.core.types.dsl.BooleanExpression;

@RestController
@RequestMapping("/hotels")
public class HotelController {
	
	private static final String kafka_topic = "first-kafka-topic";
	
	@Autowired
	KafkaTemplate<String, Hotel> kafkaTemplate;

	@Autowired
	private HotelRepository hotelRepository;

	@GetMapping("/all")
	public List<Hotel> getAll() {
		return hotelRepository.findAll();
	}

	@GetMapping("/all/page")
	public Map<String, Object> getAllEmployeesInPage(@RequestParam(name = "pageno", defaultValue = "0") int pageNo,
			@RequestParam(name = "pagesize", defaultValue = "2") int pageSize,
			@RequestParam(name = "sortby", defaultValue = "id") String sortBy) {
		Map<String, Object> response = new HashMap<String, Object>();
		Sort sort = Sort.by(sortBy);
		Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
		Page<Hotel> hotelPage = hotelRepository.findAll(pageable);
		response.put("data", hotelPage.getContent());
		response.put("total no. of page", hotelPage.getTotalPages());
		response.put("total no. of elements", hotelPage.getTotalElements());
		response.put("Current page no.", hotelPage.getNumber());

		return response;
	}

	@PutMapping
	public void addHotel(@RequestBody Hotel hotel) {
		hotelRepository.insert(hotel);
	}

	@PostMapping
	public void updateHotel(@RequestBody Hotel hotel) {
		hotelRepository.save(hotel);
		try {
			kafkaTemplate.send(kafka_topic, hotel);
			
		} catch (Exception e) {
			System.out.println("error in publishing data to kafka");
		}
		
		System.out.println(hotel.toString() +"published successfully to Kafka");
	}
	
	@KafkaListener(id="kid", topics=kafka_topic)
	public void listenToKafkaTopic(Hotel hotel)
	{
		System.out.println(hotel.toString());
	}

	@DeleteMapping("/delete/{id}")
	public void deleteHotel(@PathVariable("id") String id) {
		hotelRepository.deleteById(id);
	}

	@GetMapping("/{id}")
	public Hotel getById(@PathVariable("id") String id) {
		Hotel hotel = hotelRepository.findById(id).get();
		return hotel;
	}

	@GetMapping("/price/{max}")
	public List<Hotel> getById(@PathVariable("max") int max) {
		List<Hotel> hotels = hotelRepository.findByPricePerNightLessThan(max);
		return hotels;
	}

	@GetMapping("/address/{city}")
	public List<Hotel> getByCity(@PathVariable("city") String city) {
		List<Hotel> hotels = hotelRepository.findByCity(city);

		return hotels;
	}

	@GetMapping("/address/{country}")
	public List<Hotel> getByCountry(@PathVariable("country") String country) {
		QHotel qhotel = new QHotel("hotel");
		BooleanExpression filterByCountry = qhotel.address.country.eq(country);

		List<Hotel> hotels = (List<Hotel>) hotelRepository.findAll(filterByCountry);
		return hotels;
	}

	@GetMapping("/recommended")
	public List<Hotel> getRecommended() {
		final int maxPrice = 100;
		final int minRating = 5;

		QHotel qhotel = new QHotel("hotel");

		BooleanExpression filterByPrice = qhotel.pricePerNight.lt(maxPrice);
		BooleanExpression filterByRating = qhotel.reviews.any().rating.gt(minRating);

		List<Hotel> hotels = (List<Hotel>) hotelRepository.findAll(filterByPrice.and(filterByRating));

		return hotels;
	}
}
