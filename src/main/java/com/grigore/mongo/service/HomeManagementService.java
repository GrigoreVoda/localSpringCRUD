package com.grigore.mongo.service;

import com.grigore.mongo.exception.UserNotFoundException;
import com.grigore.mongo.model.Item;
import com.grigore.mongo.model.Location;
import com.grigore.mongo.model.Place;
import com.grigore.mongo.repository.ItemRepository;
import com.grigore.mongo.repository.LocationRepository;
import com.grigore.mongo.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class HomeManagementService {
    private final LocationRepository locationRepository;
    private final PlaceRepository placeRepository;
    private final ItemRepository itemRepository;

    public HomeManagementService(LocationRepository locationRepository, PlaceRepository placeRepository, ItemRepository itemRepository) {
        this.locationRepository = locationRepository;
        this.placeRepository = placeRepository;
        this.itemRepository = itemRepository;
    }

    // Location Methods
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location saveLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location getLocationById(String id) {
        return locationRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Location by id " + id + " not found"));
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
        return placeRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Place by id " + id + " not found"));
    }

    public void deletePlace(String id) {
        placeRepository.deleteById(id);
    }

    // Item Methods
    public List<Item> getAllItems() {

        return itemRepository.findAll().stream().
                sorted(Comparator.comparing(Item::getExpireDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

                //.stream().
              //  sorted((o1,o2) ->o1.getExpireDate().compareTo(o2.getExpireDate())).toList();
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item getItemById(String id) {
        return itemRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Item by id " + id + " not found"));
    }

    public void deleteItem(String id) {
        itemRepository.deleteById(id);
    }

    public Location updateLocation(Location updatedLocation) {
        return locationRepository.save(updatedLocation);
    }
    public Place updatePlace( Place updatedPlace) {
        return placeRepository.save(updatedPlace);

    }
    public Item updateItem( Item item) {
     itemRepository.save(item);
        return item;
    }
}
