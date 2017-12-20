package de.chennemann.jdto.processor.enriching;



import de.chennemann.jdto.processor.model.DtoSpecification;

import javax.lang.model.element.TypeElement;



public interface Enricher {
    
    DtoSpecification enrich(final TypeElement typeElement);
    
}
