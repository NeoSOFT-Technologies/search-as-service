package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class TypeCastingUtil {
	private TypeCastingUtil() {}
	public static List<String> castToListOfStrings(Object obj) {
		List<String> resultList = new ArrayList<>();
		if (obj instanceof List<?>) {
			for (Object o : (List<?>) obj)
				resultList.add(String.class.cast(o));
		}
		return resultList;
	}	

}
