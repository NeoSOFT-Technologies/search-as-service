package com.searchservice.app.domain.dto.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode
public class SingleCoreDTO {

    private String coreName;

    public SingleCoreDTO(String coreName) {
        this.coreName = coreName;
    }

}
