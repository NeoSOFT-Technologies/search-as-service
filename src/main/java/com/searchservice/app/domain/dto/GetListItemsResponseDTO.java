package com.searchservice.app.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

import com.searchservice.app.infrastructure.adaptor.versioning.VersionedObjectMapper;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GetListItemsResponseDTO implements VersionedObjectMapper {
    private int statusCode;
    private String message;
    private List<String> items;
	@Override
	public VersionedObjectMapper toVersion(int version) {
		return this;
	}
}
