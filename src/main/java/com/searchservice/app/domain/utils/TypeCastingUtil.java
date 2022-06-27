package com.searchservice.app.domain.utils;

import java.util.ArrayList;
import java.util.List;

public class TypeCastingUtil {
    private TypeCastingUtil() {
    }

    public static List<String> castToListOfStrings(Object obj, int tenantId) {
        List<String> resultList = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj)
                if (String.class.cast(o).endsWith("_" + tenantId))
                    resultList.add(String.class.cast(o));
        }
        return resultList;
    }

    public static List<String> castToListOfStrings(Object obj) {
        List<String> resultList = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj)
                resultList.add(String.class.cast(o));
        }
        return resultList;
    }


}
