package com.grigore.mongo.service;

import com.grigore.mongo.model.Item;
import com.grigore.mongo.model.Location;
import com.grigore.mongo.model.Place;
import com.grigore.mongo.repository.ItemRepository;
import com.grigore.mongo.repository.LocationRepository;
import com.grigore.mongo.repository.PlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HomeManagementService {
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ItemRepository itemRepository;

    // Location Methods
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(String id) {
        return locationRepository.findById(id).orElse(null);
    }

    public void deleteLocation(String id) {
        locationRepository.deleteById(id);
    }

    // Place Methods
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Place savePlace(Place place) {
        return placeRepository.save(place);
    }

    public Place getPlaceById(String id) {
        return placeRepository.findById(id).orElse(null);
    }

    public void deletePlace(String id) {
        placeRepository.deleteById(id);
    }

    // Item Methods
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(String id) {
        return itemRepository.findById(id).orElse(null);
    }

    public void deleteItem(String id) {
        itemRepository.deleteById(id);
    }

    public Location updateLocation(String id, Location updatedLocation) {
        return locationRepository.findById(id)
                .map(location -> {
                    // Update fields as necessary
                    location.setName(updatedLocation.getName());
                    location.setDescription(updatedLocation.getDescription());
                    // Add other fields as needed
                    return locationRepository.save(location);
                })
                .orElse(null); // Return null if location not found
    }
    public Place updatePlace(String id, Place updatedPlace) {
        return placeRepository.findById(id)
                .map(place -> {
                    // Update fields as necessary
                    place.setPlaceName(updatedPlace.getPlaceName());
                    place.setDescription(updatedPlace.getDescription());
                    // Add other fields as needed
                    return placeRepository.save(place);
                })
                .orElse(null); // Return null if place not found
    }
    public Item updateItem(String id, Item updatedItem) {
        return itemRepository.findById(id)
                .map(item -> {
                    // Update fields as necessary
                    item.setItemName(updatedItem.getItemName());
                    item.setDescription(updatedItem.getDescription());
                    // Add other fields as needed
                    return itemRepository.save(item);
                })
                .orElse(null); // Return null if item not found
    }
}
