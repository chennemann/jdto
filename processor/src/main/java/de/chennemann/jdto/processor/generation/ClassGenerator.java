package de.chennemann.jdto.processor.generation;



import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import de.chennemann.jdto.processor.model.DtoSpecification;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.stream.Collectors;

import static de.chennemann.jdto.processor.util.Utils.capitalize;



public class ClassGenerator implements Generator {
    
    private static final String IS_ASSIGNED_SUFFIX = "IsAssigned";
    private final Messager messager;
    
    
    public ClassGenerator(final Messager messager) {
        this.messager = messager;
    }
    
    
    public DtoSpecification generate(final DtoSpecification specification) {
        
        messager.printMessage(Diagnostic.Kind.NOTE, "Generating class for " + specification.getClassName());
        
        final TypeElement element = specification.getUnderlyingElement();
        final List<Element> fields = extractFields(element);
        
        final TypeSpec.Builder classBuilder = TypeSpec
            .classBuilder(specification.getClassName())
            .addMethod(
                MethodSpec
                    .constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .build()
            )
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL);
        
        final MethodSpec.Builder allArgsConstructorBuilder = MethodSpec.constructorBuilder();
        
        fields
            .stream()
            .filter(field -> !specification.getExcludedFields().contains(field.getSimpleName().toString()))
            .forEach(field -> {
                
                final String fieldName = field.getSimpleName().toString();
                final String assignedMarkerName = fieldName + IS_ASSIGNED_SUFFIX;
                final TypeName fieldType = ClassName.get(field.asType());
                
                allArgsConstructorBuilder
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(fieldType, fieldName, Modifier.FINAL)
                    .addStatement(buildAssignInvocation(fieldName));
                
                classBuilder
                    .addField(fieldType, fieldName, Modifier.PRIVATE)
                    .addField(TypeName.BOOLEAN, assignedMarkerName, Modifier.PRIVATE)
                    .addMethod(buildSetter(fieldName, fieldType))
                    .addMethod(buildGetter(fieldName, fieldType))
                    .addMethod(buildAssignment(fieldName, assignedMarkerName, fieldType));
            });
        
        classBuilder.addMethod(allArgsConstructorBuilder.build());
        
        return DtoSpecification.from(specification).with(classBuilder.build()).build();
    }
    
    
    private MethodSpec buildSetter(final String fieldName, final TypeName fieldType) {
        return MethodSpec
            .methodBuilder("set" + capitalize(fieldName))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addParameter(fieldType, fieldName, Modifier.FINAL)
            .addStatement(buildAssignInvocation(fieldName))
            .returns(TypeName.VOID)
            .build();
    }
    
    
    private MethodSpec buildGetter(final String fieldName, final TypeName fieldType) {
        return MethodSpec
            .methodBuilder("get" + capitalize(fieldName))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addStatement("return this." + fieldName)
            .returns(fieldType)
            .build();
    }
    
    
    private MethodSpec buildAssignment(
        final String fieldName,
        final String assignedMarkerName,
        final TypeName fieldType
    ) {
        return MethodSpec
            .methodBuilder("assign" + capitalize(fieldName))
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addParameter(fieldType, fieldName, Modifier.FINAL)
            .addStatement(
                "if (this." + assignedMarkerName + ") { throw new IllegalArgumentException(\"The field '"
                    + fieldName + "' already has a value assigned and cannot be changed again.\");}"
            )
            .addStatement("this." + assignedMarkerName + " = true")
            .addStatement("this." + fieldName + " = " + fieldName)
            .returns(TypeName.VOID)
            .build();
    }
    
    
    private String buildAssignInvocation(final String fieldName) {
        return "assign" + capitalize(fieldName) + "(" + fieldName + ")";
    }
    
    
    private List<Element> extractFields(final TypeElement element) {
        return element
            .getEnclosedElements()
            .stream()
            .filter(fieldCandidate -> fieldCandidate.getKind().isField())
            .map(field -> (Element) field)
            .collect(Collectors.toList());
    }
}
