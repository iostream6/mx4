/*
 * 2020.04.02 - Created | Base functionality QC'ed
 * 2020.05.24 - Implemented Admin CRUD endpoints for Instruments. Add minor improvements to Entity endpoint implementation
 *
 * // TODO when an entity is deleted, all associated instruments ought to be deleted as well. When an instrument is deleted, it ought to be deleted from all portfolio transactions
 * // Simple solution for now may be to preclude delete of Entity that has one or more instruments. Ditto for instruments that have one or more transactions
 */
package mx4.springboot.services;

import java.util.List;
import java.util.Optional;
import mx4.springboot.model.Entity;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.Sector;
import mx4.springboot.persistence.EntityRepository;
import mx4.springboot.persistence.InstrumentRepository;
import mx4.springboot.persistence.SectorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Ilamah, Osho
 */
@RestController

public class EntityService {

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private SectorRepository sectorRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    @PostMapping("/admin/entities") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
    public Entity createEntity(@RequestBody Entity e) {
        return entityRepository.save(e);
    }

    @GetMapping("/api/entities")
    public List<Entity> readAllEntities() {
        return entityRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @GetMapping("/api/entities/{id}")
    public ResponseEntity<?> readOneEntity(@PathVariable String id) {
        final Optional<Entity> optional = entityRepository.findById(id);
        if (optional.isPresent()) {
            return ResponseEntity.ok(optional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping("/admin/entities/{id}")
    public ResponseEntity<?> updateEntity(@RequestBody Entity e, @PathVariable String id) {
        final Optional<Entity> optional = entityRepository.findById(id);
        if (optional.isPresent()) {
            final Entity entity = optional.get();
            entity.setName(e.getName());
            entity.setDescription(e.getDescription());
            return ResponseEntity.ok(entityRepository.save(entity));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/admin/entities/{id}")
    public void deleteEntity(@PathVariable String id) {
        entityRepository.deleteById(id);
        //TODO when an entity is deleted, all associated instruments ought to be deleted as well
    }
//
// -- Sector objects    
//    

    @GetMapping("/api/sectors")
    public List<Sector> readSectors() {
        return sectorRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

// -- Instruments 
    @PostMapping("/admin/instruments") //Might also use ResponseEntity return type so we can set location. See Gutierez pp 101 & https://stackoverflow.com/a/51916728
    public ResponseEntity<?> createInstrument(@RequestBody Instrument i) {
        final List<Instrument> existing = instrumentRepository.findByCode(i.getCode());
        if (existing.isEmpty()) {
            return ResponseEntity.ok(instrumentRepository.save(i));
        }
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
    }

    @GetMapping("/admin/instruments")
    public List<Instrument> readAllInstruments() {
        return instrumentRepository.findAll(Sort.by(Sort.Direction.ASC, "code"));
    }

    @PutMapping("/admin/instrument/{id}")
    public ResponseEntity<?> updateInstrument(@RequestBody Instrument i, @PathVariable String id) {
        final Optional<Instrument> optional = instrumentRepository.findById(id);
        if (optional.isPresent()) {
            final Instrument ii = optional.get();
            ii.setType(i.getType());
            ii.setCurrencyId(i.getCurrencyId());
            ii.setCode(i.getCode());
            ii.setSectorId(i.getSectorId());
            ii.setEntityId(i.getEntityId());
            ii.setDescription(i.getDescription());
            ii.setCountryId(i.getCountryId());
            return ResponseEntity.ok(instrumentRepository.save(ii));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/admin/instrument/{id}")
    public void deleteInstrument(@PathVariable String id) {
        instrumentRepository.deleteById(id);
        //TODO when an instrument is deleted, it ought to be deleted from all portfolio transactions
    }

}
