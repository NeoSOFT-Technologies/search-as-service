package com.searchservice.app.domain.utils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.searchservice.app.infrastructure.adaptor.versioning.MapperVersioningUtil;

public class StringMatcherRegexUtil {
	
	private StringMatcherRegexUtil() {}
	
	public static String getMatchedSaasVersion(String targetString) {
		List<String> availableVersions = MapperVersioningUtil.getAllAvailableSaasVersions();
        StringBuilder versionsString = new StringBuilder();
        for(String ver: availableVersions)
        	versionsString.append(", "+ver);
        
		Pattern pattern = Pattern.compile("v[0-9]");
        Matcher matcher = pattern.matcher(targetString);
		
        if(matcher.find())
        	return matcher.group(0);
        
		return null;
	}
}
