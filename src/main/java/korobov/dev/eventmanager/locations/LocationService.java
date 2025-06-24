package korobov.dev.eventmanager.locations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repository;
    private final LocationEntityMapper entityMapper;

    public LocationService(
            LocationRepository repository,
            LocationEntityMapper entityMapper
    ) {
        this.repository = repository;
        this.entityMapper = entityMapper;
    }

    public List<Location> getAllLocations() {
        return repository.findAll()
                .stream()
                .map(entityMapper::toDomain)
                .toList();
    }

    public Location createLocation(Location locationToCreate) {
        if (locationToCreate.id() != null) {
            throw new IllegalArgumentException("Can not create location with provided id. Id Must be empty");
        }
        var createdLocation = repository.save(entityMapper.toEntity(locationToCreate));
        return entityMapper.toDomain(createdLocation);
    }

    public Location updateLocation(
            Location locationToUpdate,
            Long locationId
    ) {
        if (locationToUpdate.id() != null) {
            throw new IllegalArgumentException("Can not update location with provided id. Id Must be empty");
        }
        var entityToUpdate = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s"
                        .formatted(locationId)));
        entityToUpdate.setAddress(locationToUpdate.address());
        entityToUpdate.setCapacity(locationToUpdate.capacity());
        entityToUpdate.setName(locationToUpdate.name());
        entityToUpdate.setDescription(locationToUpdate.description());

        var updatedEntity = repository.save(entityToUpdate);

        return entityMapper.toDomain(updatedEntity);
    }

    public Location deleteLocation(Long locationId) {
        var entityToDelete = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s"
                        .formatted(locationId)));
        repository.deleteById(locationId);
        return entityMapper.toDomain(entityToDelete);
    }

    public Location getLocationById(Long locationId) {
        var foundEntity = repository.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Location entity wasn't found id=%s"
                        .formatted(locationId)));
        return entityMapper.toDomain(foundEntity);
    }

}
