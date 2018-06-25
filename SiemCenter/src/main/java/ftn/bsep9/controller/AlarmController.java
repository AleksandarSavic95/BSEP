package ftn.bsep9.controller;

import ftn.bsep9.model.AlarmFile;
import ftn.bsep9.service.AlarmFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alarms")
public class AlarmController {

    @Autowired
    private AlarmFileService alarmFileService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody AlarmFile alarmFile) {
        System.out.println(alarmFile);
        alarmFileService.create(alarmFile);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<String>> getAll(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(defaultValue = "asc") String sortDirection) {

        Page<String> fileNames = alarmFileService.findAllWithPages(page, size, sortDirection);
        return new ResponseEntity<>(fileNames, HttpStatus.OK);
    }

    @GetMapping("/{alarmName}")
    public ResponseEntity<AlarmFile> get(@PathVariable String alarmName) {
        System.out.println("Getting: " + alarmName);
        AlarmFile alarmFile = alarmFileService.get(alarmName);
        if (alarmFile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alarmFile);
    }

    @PutMapping("/{alarmName}")
    public ResponseEntity<AlarmFile> update(@PathVariable String alarmName,
                                            @RequestBody AlarmFile alarmFile) {
        System.out.println("Updating: " + alarmName);
        AlarmFile updated = alarmFileService.update(alarmName, alarmFile);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{alarmName}")
    public ResponseEntity<Boolean> delete(@PathVariable String alarmName) {
        System.out.println("Deleting: " + alarmName);
        return ResponseEntity.ok(alarmFileService.delete(alarmName));
    }
}
