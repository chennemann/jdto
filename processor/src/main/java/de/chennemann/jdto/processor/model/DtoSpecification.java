package de.chennemann.jdto.processor.model;



import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.TypeElement;
import java.util.Collections;
import java.util.List;



public class DtoSpecification {
    
    private final String packageName;
    private final String className;
    private final TypeElement underlyingElement;
    private final TypeSpec contents;
    private final List<String> excludedFields;
    
    
    public DtoSpecification(
        final String packageName,
        final String className,
        final List<String> excludedFields,
        final TypeElement underlyingElement,
        final TypeSpec contents
    ) {
        this.packageName = packageName;
        this.className = className;
        this.excludedFields = excludedFields != null ? excludedFields : Collections.EMPTY_LIST;
        this.underlyingElement = underlyingElement;
        this.contents = contents;
    }
    
    
    public String getPackageName() {
        return packageName;
    }
    
    
    public String getClassName() {
        return className;
    }
    
    
    public List<String> getExcludedFields() {
        return excludedFields;
    }
    
    
    public TypeElement getUnderlyingElement() {
        return underlyingElement;
    }
    
    
    public TypeSpec getContents() {
        return contents;
    }
    
    
    public static Builder from(DtoSpecification specification) {
        return new Builder()
            .withPackageName(specification.getPackageName())
            .withClassName(specification.getClassName())
            .with(specification.getUnderlyingElement())
            .with(specification.getContents());
    }
    
    
    public static class Builder {
        private String packageName;
        private String className;
        private TypeElement underlyingElement;
        private TypeSpec contents;
        private List<String> excludedFields;
        
        
        private Builder() {
        }
        
        
        public Builder withPackageName(final String packageName) {
            this.packageName = packageName;
            return this;
        }
        
        
        public Builder withClassName(final String className) {
            this.className = className;
            return this;
        }
        
        
        public Builder with(final TypeElement underlyingElement) {
            this.underlyingElement = underlyingElement;
            return this;
        }
        
        
        public Builder with(final TypeSpec contents) {
            this.contents = contents;
            return this;
        }
        
        
        public Builder with(final List<String> excludedFields) {
            this.excludedFields = excludedFields != null ? excludedFields : Collections.EMPTY_LIST;
            return this;
        }
        
        
        public DtoSpecification build() {
            return new DtoSpecification(
                packageName,
                className,
                excludedFields,
                underlyingElement,
                contents
            );
        }
    }
}
