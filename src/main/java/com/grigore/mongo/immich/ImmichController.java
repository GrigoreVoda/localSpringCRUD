package com.grigore.mongo.immich;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/immich")
@CrossOrigin(origins = "*")
public class ImmichController {

    private final ImmichService immichService;

    public ImmichController(ImmichService immichService) {
        this.immichService = immichService;
    }

    @GetMapping("/people")
    public List<ImmichPerson> listPeople() {
        return immichService.listPeople();
    }

    @GetMapping("/people/{id}")
    public ImmichPerson getPerson(@PathVariable String id) {
        return immichService.getPerson(id);
    }

    @PutMapping("/people/{id}")
    public ImmichPerson updatePerson(@PathVariable String id, @RequestBody ImmichPersonUpdate update) {
        return immichService.updatePerson(id, update);
    }

    @GetMapping("/people/{id}/thumbnail")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String id) {
        return immichService.getThumbnail(id);
    }
}
