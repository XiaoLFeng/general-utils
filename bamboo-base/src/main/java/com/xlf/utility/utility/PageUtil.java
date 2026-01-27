package com.xlf.utility.utility;

import org.jetbrains.annotations.Contract;

@SuppressWarnings("unused")
public class PageUtil {

    @Contract(value = " -> fail", pure = true)
    private PageUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
}
