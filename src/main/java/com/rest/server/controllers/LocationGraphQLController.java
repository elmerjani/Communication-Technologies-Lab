package com.rest.server.controllers;

import com.rest.server.models.Location;
import com.rest.server.services.LocationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


/**
 * @author : El-Merjani Mohamed
 * Date : 2/22/2025
 */
@Controller
public class LocationGraphQLController {

    private final LocationService locationService;

    public LocationGraphQLController(LocationService locationService) {
        this.locationService = locationService;
    }

    @QueryMapping
    public Page<Location> getAllLocations(@Argument int page, @Argument int size) {
        Pageable pageable = PageRequest.of(page, size);
        return locationService.allLocations(pageable);
    }

    @QueryMapping
    public Location getSingleLocation(@Argument String locationId) {
        try{
            return locationService.singleLocation(locationId).orElse(null);
        } catch (Exception e) {
            return null;
        }

    }

    @MutationMapping
    public Location createLocation(@Argument String locationStreet, @Argument String locationCity,
                                   @Argument String locationState, @Argument String locationCountry,
                                   @Argument String locationTimezone) {
        Location newLocation = new Location(null, locationStreet, locationCity, locationState, locationCountry, locationTimezone);
        return locationService.createLocation(newLocation);

    }


    @MutationMapping
    public Location updateLocation(@Argument String locationId, @Argument String locationStreet,
                                   @Argument String locationCity, @Argument String locationState,
                                   @Argument String locationCountry, @Argument String locationTimezone) {
        Location updatedLocation = new Location(null, locationStreet, locationCity, locationState, locationCountry, locationTimezone);
        try{
            return locationService.updateLocation(locationId, updatedLocation);
        }catch (Exception e){
            return null;
        }
    }


    @MutationMapping
    public boolean deleteLocation(@Argument String locationId) {
       if(locationService.existsLocation(locationId)){
           locationService.deleteLocation(locationId);
           return true;
       }
       return false;

    }


}