package com.inventory.manager.service;

import com.inventory.manager.dto.LocationDTO;
import com.inventory.manager.entity.Location;
import com.inventory.manager.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {
    
    private final LocationRepository locationRepository;
    
    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public LocationDTO getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return convertToDTO(location);
    }
    
    public LocationDTO getLocationByCode(String code) {
        Location location = locationRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Location not found with code: " + code));
        return convertToDTO(location);
    }
    
    public LocationDTO createLocation(LocationDTO locationDTO) {
        // Проверка на уникальность кода
        if (locationRepository.findByCode(locationDTO.getCode()).isPresent()) {
            throw new RuntimeException("Location with code " + locationDTO.getCode() + " already exists");
        }
        
        Location location = convertToEntity(locationDTO);
        Location savedLocation = locationRepository.save(location);
        return convertToDTO(savedLocation);
    }
    
    public LocationDTO updateLocation(Long id, LocationDTO locationDTO) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        
        // Проверка уникальности кода при обновлении
        locationRepository.findByCode(locationDTO.getCode())
                .ifPresent(existingLocation -> {
                    if (!existingLocation.getId().equals(id)) {
                        throw new RuntimeException("Location with code " + locationDTO.getCode() + " already exists");
                    }
                });
        
        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        location.setCode(locationDTO.getCode());
        location.setType(locationDTO.getType());
        
        Location updatedLocation = locationRepository.save(location);
        return convertToDTO(updatedLocation);
    }
    
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        
        // Проверка, что локация не содержит товары
        if (!location.getItems().isEmpty()) {
            throw new RuntimeException("Cannot delete location with existing items. Please relocate items first.");
        }
        
        locationRepository.deleteById(id);
    }
    
    private LocationDTO convertToDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .code(location.getCode())
                .type(location.getType())
                .itemCount(location.getItems() != null ? location.getItems().size() : 0)
                .createdAt(location.getCreatedAt())
                .build();
    }
    
    private Location convertToEntity(LocationDTO dto) {
        return Location.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .code(dto.getCode())
                .type(dto.getType() != null ? dto.getType() : Location.LocationType.WAREHOUSE)
                .build();
    }
}
