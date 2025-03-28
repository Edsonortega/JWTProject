package com.example.JWTProject.service;

import com.example.JWTProject.entity.Person;
import com.example.JWTProject.repository.PersonRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {
    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }
    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public Person updatePerson(Long id, Person s) {
        Optional<Person> optionalPerson = personRepository.findById(id);
        if (optionalPerson.isPresent()) {
            Person person = optionalPerson.get();
            person.setName(s.getName());
            person.setLastName(s.getLastName());
            person.setGender(s.getGender());
            return personRepository.save(person);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

}
