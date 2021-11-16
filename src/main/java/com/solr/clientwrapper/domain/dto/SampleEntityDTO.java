package com.solr.clientwrapper.domain.dto;


import com.solr.clientwrapper.infrastructure.entity.SampleEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SampleEntityDTO {

    private Long id;

    private String name;

    private String password;

    private Integer age;

    private Integer phone;
    

 

    public SampleEntityDTO(SampleEntity sampleEntity) {
        this.id = sampleEntity.getId();
        this.name = sampleEntity.getName();
        this.password = sampleEntity.getPassword();
        this.age = sampleEntity.getAge();
        this.phone = sampleEntity.getPhone();
    }

}
