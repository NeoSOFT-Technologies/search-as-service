package com.searchservice.app.domain.port.api;

import com.searchservice.app.domain.dto.ResponseDTO;

public interface CoreServicePort {

    ResponseDTO create(String coreName);

    ResponseDTO rename(String coreName, String newName);

    ResponseDTO delete(String coreName);

    ResponseDTO swap(String coreOne, String coreTwo);

    ResponseDTO reload(String coreName);

    String status(String coreName);

}
