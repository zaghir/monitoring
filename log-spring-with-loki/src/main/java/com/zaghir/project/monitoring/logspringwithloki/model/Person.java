package com.zaghir.project.monitoring.logspringwithloki.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class Person {

    private Long id ;
    private String firstName ;
    private String lastName ;

}
