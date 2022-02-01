package com.searchservice.app.infrastructure.adaptor.versioning;

public interface Versioned {
    Versioned toVersion(int version);
}
