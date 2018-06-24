package ftn.bsep9.controller;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.model.Log;
import ftn.bsep9.service.AlarmStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    @Autowired
    private AlarmStorageService alarmStorageService;

    @PostMapping()
    public ResponseEntity<String> create(@RequestBody AlarmFile alarmFile) {
        System.out.println(alarmFile);
        alarmStorageService.create(alarmFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<String>> getAll(@RequestParam Integer page,
                                            @RequestParam Integer size,
                                               @RequestParam String sortDirection) {
        // Page<String> fileNames = ...
        alarmStorageService.findAllWithPages(page, size, sortDirection);
//        if (fileNames == null) {
//            return ResponseEntity.badRequest().build();
//        }
        return new ResponseEntity<>(HttpStatus.OK); // FALI OBJEKAT U ODGOVORU !!!
    }

    @GetMapping("/{alarmName}")
    public ResponseEntity<AlarmFile> get(@PathVariable String alarmName) {
        System.out.println("Getting: " + alarmName);
        AlarmFile alarmFile = alarmStorageService.get(alarmName);
        if (alarmFile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alarmFile);
    }

    @PutMapping("/{alarmName}")
    public ResponseEntity<AlarmFile> update(@PathVariable String alarmName,
                                            @RequestBody AlarmFile alarmFile) {
        System.out.println("Updating: " + alarmName);
        AlarmFile updated = alarmStorageService.update(alarmName, alarmFile);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{alarmName}")
    public ResponseEntity<Boolean> delete(@PathVariable String alarmName) {
        System.out.println("Deleting: " + alarmName);
        return ResponseEntity.ok(alarmStorageService.delete(alarmName));
    }
}
