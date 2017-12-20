package de.chennemann.jdto.processor.enriching;



import de.chennemann.jdto.annotation.ToDTO;
import de.chennemann.jdto.processor.model.DtoSpecification;
import de.chennemann.jdto.processor.util.Utils;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;



public class TypeEnricher implements Enricher {
    
    
    private static final String DTO_SUFFIX = "DTO";
    private final Messager messager;
    private final Elements elementUtils;
    
    
    public TypeEnricher(final Messager messager, final Elements elementUtils) {
        this.messager = messager;
        this.elementUtils = elementUtils;
    }
    
    
    public DtoSpecification enrich(final TypeElement typeElement) {
        messager.printMessage(Diagnostic.Kind.NOTE, "Build " + DtoSpecification.class.getSimpleName());
    
        final ToDTO elementAnnotation = typeElement.getAnnotation(ToDTO.class);
    
        final String configuredPackage = elementAnnotation.targetPackage();
        final String defaultPackage = Utils.packageNameOfElement(elementUtils, typeElement);
        
        final String configuredName = elementAnnotation.name();
        final String defaultName = typeElement.getSimpleName() + DTO_SUFFIX;
        
        final String[] configuredExcludedFields = elementAnnotation.excludedFields();
        
        final String targetPackage = !configuredPackage.isEmpty() ? configuredPackage : defaultPackage;
        final String targetName = !configuredName.isEmpty() ? configuredName : defaultName;
        final List<String> targetExcludedFields = configuredExcludedFields.length > 0 ? Arrays.asList(configuredExcludedFields) : Collections.EMPTY_LIST;
        
        messager.printMessage(Diagnostic.Kind.NOTE, "Target Package: " + targetPackage);
        messager.printMessage(Diagnostic.Kind.NOTE, "Target Name: " + targetName);
        messager.printMessage(Diagnostic.Kind.NOTE, "Target Excluded Fields: " + targetExcludedFields);
        
        return new DtoSpecification(
            targetPackage,
            targetName,
            targetExcludedFields,
            typeElement,
            null);
    }
}
