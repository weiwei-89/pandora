package org.edward.pandora.turbosnail.xml.model.property;

import java.util.Arrays;

public class Property {
    private static final String FORMULA_PREFIX = "$";
    private static final String REFERENCE_PREFIX = "@";

    private String value;

    public String getValue() {
        return this.value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public static boolean isFormula(String value) {
        if(value.startsWith(FORMULA_PREFIX)) {
            return true;
        }
        return false;
    }

    public static String extractFormula(String value) {
        return value.substring(2, value.length()-1);
    }

    public static String[] extractOperators(String formula) {
        String[] operators = formula.split("[+\\-*/()]");
        return Arrays.stream(operators)
                .filter(p->!p.isEmpty())
                .toArray(String[]::new);
    }

    public static boolean isReference(String value) {
        if(value.startsWith(REFERENCE_PREFIX)) {
            return true;
        }
        return false;
    }

    public static String extractReference(String value) {
        return value.substring(2, value.length()-1);
    }
}