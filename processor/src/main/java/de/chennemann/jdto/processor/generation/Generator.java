package de.chennemann.jdto.processor.generation;



import de.chennemann.jdto.processor.model.DtoSpecification;



public interface Generator {
    
    DtoSpecification generate(final DtoSpecification specification);
    
}
