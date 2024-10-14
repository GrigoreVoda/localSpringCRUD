package com.grigore.mongo.controller;

import com.grigore.mongo.model.Item;
import com.grigore.mongo.model.Location;
import com.grigore.mongo.model.Place;
import com.grigore.mongo.service.HomeManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HomeManagementController {
    @Autowired
    private HomeManagementService homeManagementService;


    // Location-related endpoints
    @GetMapping("/locations")
    public List<Location> getAllLocations() {
        return homeManagementService.getAllLocations();
    }

    @GetMapping("/locations/{id}")
    public Location getLocation(@PathVariable String id) {
        return homeManagementService.getLocationById(id);
    }

    @PostMapping("/locations")
    public Location createLocation(@RequestBody Location location) {
        return homeManagementService.saveLocation(location);
    }

    @DeleteMapping("/locations/{id}")
    public void deleteLocation(@PathVariable String id) {
        homeManagementService.deleteLocation(id);
    }

    // Place-related endpoints
    @GetMapping("/places")
    public List<Place> getAllPlaces() {
        return homeManagementService.getAllPlaces();
    }

    @GetMapping("/places/{id}")
    public Place getPlace(@PathVariable String id) {
        return homeManagementService.getPlaceById(id);
    }

    @PostMapping("/places")
    public Place createPlace(@RequestBody Place place) {
        return homeManagementService.savePlace(place);
    }

    @DeleteMapping("/places/{id}")
    public void deletePlace(@PathVariable String id) {
        homeManagementService.deletePlace(id);
    }

    // Item-related endpoints
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return homeManagementService.getAllItems();
    }

    @GetMapping("/items/{id}")
    public Item getItem(@PathVariable String id) {
        return homeManagementService.getItemById(id);
    }

    @PostMapping("/items")
    public Item createItem(@RequestBody Item item) {
        return homeManagementService.saveItem(item);
    }

    @DeleteMapping("/items/{id}")
    public void deleteItem(@PathVariable String id) {
        homeManagementService.deleteItem(id);
    }
    @PutMapping("/locations")
    public Location updateLocation( @RequestBody Location location) {
        return homeManagementService.updateLocation(location);
    }

    // Place-related update endpoint
    @PutMapping("/places")
    public Place updatePlace( @RequestBody Place place) {
        return homeManagementService.updatePlace( place);
    }

    // Item-related update endpoint
    @PutMapping("/items")
    public Item updateItem( @RequestBody Item item) {
        return homeManagementService.updateItem( item);
    }
}
