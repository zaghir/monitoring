package com.zaghir.project.monitoring.logspringwithloki.controller;

import com.github.loki4j.slf4j.marker.LabelMarker;
import com.zaghir.project.monitoring.logspringwithloki.model.Person;
import lombok.With;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/persons")
public class PersonController {

    /**
     *
    Besides the static labels, we may send dynamic data, e.g. something specific just for the current request.
    Assuming we have a service that manages persons, we want to log the id of the target person from the request.
    As I mentioned before, with Loki4j we can use Logback markers for that.
    In classic Logback, markers are mostly used to filter log records.
    With Loki, we just need to define the LabelMarker object containing the key/value Map of dynamic fields (1).
    Then we pass the object to the current log line (2).
     */

    private final List<Person> persons = new ArrayList<>();

    @GetMapping
    public List<Person> findAll() {
        return persons;
    }

    @GetMapping("/{id}")
    public Person findById(@PathVariable("id") Long id) {
        Person p = persons.stream().filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
//        LabelMarker marker = LabelMarker.of("personId", () ->
//                String.valueOf(p.getId())); // (1)
        LabelMarker marker = LabelMarker.of(() -> Map.of("audit", "true",
                "X-Request-ID", MDC.get("X-Request-ID"),
                "X-Correlation-ID", MDC.get("X-Correlation-ID")));
        log.info(marker, "Person successfully found"); // (2)
        return p;
    }

    @GetMapping("/name/{firstName}/{lastName}")
    public List<Person> findByName(
            @PathVariable("firstName") String firstName,
            @PathVariable("lastName") String lastName) {

        return persons.stream()
                .filter(it -> it.getFirstName().equals(firstName)
                        && it.getLastName().equals(lastName))
                .toList();
    }

    @PostMapping
    public Person add(@RequestBody Person p) {
        p.setId((long) (persons.size() + 1));
        LabelMarker marker = LabelMarker.of("personId", () ->
                String.valueOf(p.getId()));
        log.info(marker, "New person successfully added");
        persons.add(p);
        return p;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        Person p = persons.stream()
                .filter(it -> it.getId().equals(id))
                .findFirst()
                .orElseThrow();
        persons.remove(p);
        LabelMarker marker = LabelMarker.of("personId", () ->
                String.valueOf(id));
        log.info(marker, "Person successfully removed");
    }

    @PutMapping
    public void update(@RequestBody Person p) {
        Person person = persons.stream()
                .filter(it -> it.getId().equals(p.getId()))
                .findFirst()
                .orElseThrow();
        persons.set(persons.indexOf(person), p);
        LabelMarker marker = LabelMarker.of("personId", () ->
                String.valueOf(p.getId()));
        log.info(marker, "Person successfully updated");
    }

}