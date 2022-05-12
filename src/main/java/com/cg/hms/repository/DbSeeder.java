package com.cg.hms.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.cg.hms.model.Address;
import com.cg.hms.model.Hotel;
import com.cg.hms.model.Review;

@Component
public class DbSeeder implements CommandLineRunner {
    private HotelRepository hotelRepository;

    public DbSeeder(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public void run(String... strings) throws Exception {
        Hotel hotel1 = new Hotel(
                "hotel1",
                130,
                new Address("Paris", "France"),
                Arrays.asList(
                        new Review("John", 8, false),
                        new Review("Mary", 7, true)
                )
        );

        Hotel hotel2 = new Hotel(
                "hotel2",
                90,
                new Address("Bucharest", "Romania"),
                Arrays.asList(
                        new Review("Teddy", 9, true)
                )
        );

        Hotel hotel3 = new Hotel(
                "hotel3",
                200,
                new Address("Rome", "Italy"),
                new ArrayList<>()
        );

        // drop all hotels
        this.hotelRepository.deleteAll();

        //add our hotels to the database
        List<Hotel> hotels = Arrays.asList(hotel1, hotel2, hotel3);
        this.hotelRepository.saveAll(hotels);
    }
}