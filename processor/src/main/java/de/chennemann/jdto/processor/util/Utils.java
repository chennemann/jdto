package de.chennemann.jdto.processor.util;



import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;



public class Utils {
    public static String packageNameOfElement(final Elements elementUtils, final TypeElement typeElement) {
        return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
    }
    
    
    public static String capitalize(final String string) {
        return Character.toUpperCase(string.charAt(0)) + string.substring(1);
    }
}
