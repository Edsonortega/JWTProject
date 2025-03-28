package com.example.JWTProject.controller;

import com.example.JWTProject.entity.Person;
import com.example.JWTProject.ratelimit.RateLimit;
import com.example.JWTProject.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/person")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping
    @RateLimit(capacity = 2, time = 60, skipForMembers = true)
    public ResponseEntity<List<Person>> getAllPersons() {
        return ResponseEntity.ok(personService.getAllPersons());
    }

    @GetMapping("/{id}")
    @RateLimit(capacity = 3, time = 60, skipForMembers = true)
    public ResponseEntity<Person> getPersonById(@PathVariable Long id) {
        Optional<Person> userOptional = personService.getPersonById(id);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person person) {
        return ResponseEntity.ok(personService.savePerson(person));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody Person personDetails) {
        Optional<Person> userOptional = personService.getPersonById(id);
        if(userOptional.isPresent()){
            Person personToUpdate = userOptional.get();
            personToUpdate.setName(personDetails.getName());
            personToUpdate.setLastName(personDetails.getLastName());
            personToUpdate.setGender(personDetails.getGender());
            return ResponseEntity.ok(personService.updatePerson(id, personToUpdate));
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        Optional<Person> userOptional = personService.getPersonById(id);
        if(userOptional.isPresent()){
            personService.deletePerson(id);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }
}
