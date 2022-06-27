package com.searchservice.app.domain.service.security;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.searchservice.app.config.UserPermissionConfigProperties;
import com.searchservice.app.rest.errors.CustomException;
import com.searchservice.app.rest.errors.HttpStatusCode;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class KeycloakUserPermission {

	private static final String FROM_CACHE = "[{}] is being fetched from cache";

	private static final String USER_PERMISSIONS = "userPermissions";

	private final Logger log = LoggerFactory.getLogger(KeycloakUserPermission.class);
	
	@Autowired 
	private UserPermissionConfigProperties userPermissionConfigProperties;
	
	@Autowired
	private CacheManager cacheManager;
	
	@Nullable
	Cache cache;
	
	private boolean isViewPermissionEnabled;
	private boolean isCreatePermissionEnabled;
	private boolean isEditPermissionEnabled;
	private boolean isDeletePermissionEnabled;

	
	public boolean isViewPermissionEnabled() {
		if(checkIfUserPermissionExistsInCache(userPermissionConfigProperties.getView())) {
			log.info(FROM_CACHE, userPermissionConfigProperties.getView());
			return getUserPermissionFromCache(userPermissionConfigProperties.getView());
		}
		return isViewPermissionEnabled;
	}
	@Cacheable(cacheNames = USER_PERMISSIONS, key = "#authority", condition = "#authority!=null")
	public boolean setViewPermissionEnabled(String authority, boolean viewPermission) {
		this.isViewPermissionEnabled = viewPermission;
		return viewPermission;
	}
	
	public boolean isCreatePermissionEnabled() {
		if(checkIfUserPermissionExistsInCache(userPermissionConfigProperties.getCreate())) {
			log.info(FROM_CACHE, userPermissionConfigProperties.getCreate());
			return getUserPermissionFromCache(userPermissionConfigProperties.getCreate());
		}
		return isCreatePermissionEnabled;
	}
	@Cacheable(cacheNames = {USER_PERMISSIONS}, key = "#authority", condition = "#authority!=null")
	public boolean setCreatePermissionEnabled(String authority, boolean createPermission) {
		this.isCreatePermissionEnabled = createPermission;
		return createPermission;
	}
	
	public boolean isEditPermissionEnabled() {
		if(checkIfUserPermissionExistsInCache(userPermissionConfigProperties.getEdit())) {
			log.info(FROM_CACHE, userPermissionConfigProperties.getEdit());
			return getUserPermissionFromCache(userPermissionConfigProperties.getEdit());
		}
		return isEditPermissionEnabled;
	}
	@Cacheable(cacheNames = {USER_PERMISSIONS}, key = "#authority", condition = "#authority!=null")
	public boolean setEditPermissionEnabled(String authority, boolean editPermission) {
		this.isEditPermissionEnabled = editPermission;
		return editPermission;
	}
	
	public boolean isDeletePermissionEnabled() {
		if(checkIfUserPermissionExistsInCache(userPermissionConfigProperties.getDelete())) {
			log.info(FROM_CACHE, userPermissionConfigProperties.getDelete());
			return getUserPermissionFromCache(userPermissionConfigProperties.getDelete());
		}
		return isDeletePermissionEnabled;
	}
	@Cacheable(cacheNames = {USER_PERMISSIONS}, key = "#authority", condition = "#authority!=null")
	public boolean setDeletePermissionEnabled(String authority, boolean deletePermission) {
		this.isDeletePermissionEnabled = deletePermission;
		return deletePermission;
	}
	
	
	public boolean getUserPermissionFromCache(String permission) {
	    cache = cacheManager.getCache(userPermissionConfigProperties.getKey());
	    if(cache == null)
	    	throw new CustomException(
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	    else {
		    Optional<ValueWrapper> permissionValueWrapper = Optional.ofNullable(cache.get(permission));

		    if(permissionValueWrapper.isEmpty() || permissionValueWrapper.get().get() == null)
		    	throw new CustomException(
		    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
		    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
		    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
		    else
		    	return (boolean)permissionValueWrapper.get().get();
	    }
	}
	
	public boolean checkIfUserPermissionExistsInCache(String permission) {
	    cache = cacheManager.getCache(userPermissionConfigProperties.getKey());
	    if(cache == null)
	    	throw new CustomException(
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getCode(), 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION, 
	    			HttpStatusCode.NULL_POINTER_EXCEPTION.getMessage());
	    else {
	    	return (cache.get(permission)!=null);
	    }
	}
	
}
