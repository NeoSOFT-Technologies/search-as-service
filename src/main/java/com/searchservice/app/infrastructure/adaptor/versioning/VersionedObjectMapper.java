package com.searchservice.app.infrastructure.adaptor.versioning;

public interface VersionedObjectMapper {
    VersionedObjectMapper toVersion(int version);
}
