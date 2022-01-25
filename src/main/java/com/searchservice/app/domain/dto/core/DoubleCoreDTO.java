package com.searchservice.app.domain.dto.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class DoubleCoreDTO {

    private String coreOne;
    private String coreTwo;

    public DoubleCoreDTO(String coreOne, String coreTwo) {
        this.coreOne = coreOne;
        this.coreTwo = coreTwo;
    }

}
