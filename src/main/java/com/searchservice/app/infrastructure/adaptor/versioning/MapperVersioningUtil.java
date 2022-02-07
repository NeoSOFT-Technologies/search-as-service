package com.searchservice.app.infrastructure.adaptor.versioning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.searchservice.app.rest.VersionedManageTableResource;

import lombok.Data;

@Data
public class MapperVersioningUtil {
	private MapperVersioningUtil() {}
	
	// All available SAAS versions
	private static List<String> availableVersions = new ArrayList<>(
			Arrays.asList(
					"v1", 
					"v2"));
	public static List<String> getAllAvailableSaasVersions() {
		return availableVersions;
	}
	
	// Base urls
	@Value("${base-url.api-endpoint.manage-table}")
	public static String BASE_URL_MANAGE_TABLE;
	
	// VersionedResources
	private static List<Class<?>> versioned = new ArrayList<>();
	public static List<Class<?>> getAllVersionedResources() {
		versioned.addAll(
				Arrays.asList(
						VersionedManageTableResource.class));
		
		return versioned;
	}
}
