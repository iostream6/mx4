/*
 * 2020.06.17 - Created
 * 2020.07.01 - Fixed - createValuesRecordsLegacy now skips and logs unrecognized instrument symbols. readLastValuesRecords now returns HTTP/OK even if no record found
 */
package mx4.springboot.services;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import mx4.springboot.model.Instrument;
import mx4.springboot.model.Values;
import mx4.springboot.persistence.InstrumentRepository;
import mx4.springboot.persistence.ValuesRepository;
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
public class ValuesService {

    @Autowired
    private ValuesRepository valuesRepository;

    @Autowired
    private InstrumentRepository instrumentRepository;

    /**
     * Creates a new Values record
     *
     * @param vs the Values data model
     * @return A response entity with the created values record, including newly assigned id, in the response body
     */
    @PostMapping("/admin/values")
    public Values createValuesRecord(@PathVariable Values vs) {
        return valuesRepository.save(vs);
    }

    /**
     * Reads all values record from the database
     *
     * @return all values record from the database
     */
    @GetMapping("/api/values")
    public List<Values> readValuesRecords() {
        return valuesRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));
    }

    /**
     * Reads last date values record from the database
     *
     * @return all values record from the database
     */
    @GetMapping("/api/values/last")
    public ResponseEntity<?> readLastValuesRecords() {
        List<Values> vs = valuesRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
        if (vs.isEmpty()){
            return ResponseEntity.ok().build();
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }        
        return ResponseEntity.ok(vs.get(0));
    }

    /**
     * Updates a given values record
     *
     * @param vs the Values data model
     * @param id the id of the values record that is to be updated
     * @return the updated values record
     */
    @PutMapping("/admin/value/{id}")
    public ResponseEntity<?> updateValuesRecord(@RequestBody Values vs, @PathVariable String id) {
        final Optional<Values> optional = valuesRepository.findById(id);
        if (optional.isPresent()) {
            final Values vv = optional.get();
            vv.setDate(vs.getDate());
            vv.setRecords(vs.getRecords());
            return ResponseEntity.ok(valuesRepository.save(vv));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    /**
     * Deletes a values record from the backing data store, iff the values records exists.
     *
     * @param id the id of the values record to delete
     */
    @DeleteMapping("/admin/value/{id}")
    public void deleteValuesRecord(@PathVariable String id) {
        valuesRepository.deleteById(id);
        //TODO when an entity is deleted, all associated instruments ought to be deleted as well
    }

    /**
     * Deletes one or more values record from the backing data store, iff the values records exists.
     *
     * @param vids a comma-separated list of values records id's to be deleted
     */
    @DeleteMapping("/admin/values/{vids}")
    public void deleteValuesRecords(@PathVariable String vids) {
        final String[] ids = vids.split(",");
        for (final String vid : ids) {
            valuesRepository.deleteById(vid);
        }
    }
   
   /**
     * Creates multiple values records (from legacy JSON strings to allow easy migration
     *
     * @param data
     * @return
     * @throws java.lang.Exception
     */
    @PostMapping("/admin/v2/values")
    public List<Values> createValuesRecordsLegacy(@RequestBody String data) throws Exception {
        final int DATE_INDEX = 0, CODE_INDEX = 1, VALUE_INDEX = 2;
        //final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final DecimalFormat nf = new DecimalFormat("#0.00##");
        final Scanner lineScanner = new Scanner(data);
        final List<Instrument> instruments = instrumentRepository.findAll();
        Map<LocalDate, Set<Values.Record>> m = new HashMap<>();
        while (lineScanner.hasNextLine()) {
            final String[] lineData = lineScanner.nextLine().split("\t");
            final LocalDate d = LocalDate.parse(lineData[DATE_INDEX], dtf);
            Set<Values.Record> records = m.get(d);
            if (records == null) {
                records = new HashSet<>();
                m.put(d, records);
            }
            boolean found = false;
            for (Instrument i : instruments) {
                if (i.getCode().equals(lineData[CODE_INDEX])) {
                    final Values.Record record = new Values.Record();
                    record.setValue(nf.parse(lineData[VALUE_INDEX]).doubleValue());
                    record.setInstrumentId(i.getId());
                    records.add(record);
                    found = true;
                    System.out.println("OK " + lineData[CODE_INDEX]);
                    break;
                }
            }
            if(!found){
                System.out.println("Could not find " + lineData[CODE_INDEX]);
            }
        }
        final List<Values> vs = new ArrayList<>();
        for (final LocalDate d : m.keySet()) {
            final Set<Values.Record> records = m.get(d);
            final Values v = new Values();
            v.setDate(d);
            v.setRecords(records);
            valuesRepository.save(v);
            vs.add(v);
        }
        return vs;
    }

}
