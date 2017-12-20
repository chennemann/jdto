package de.chennemann.jdto.processor;



import com.squareup.javapoet.JavaFile;
import de.chennemann.jdto.annotation.ToDTO;
import de.chennemann.jdto.processor.enriching.Enricher;
import de.chennemann.jdto.processor.enriching.TypeEnricher;
import de.chennemann.jdto.processor.exception.NotWritable;
import de.chennemann.jdto.processor.generation.ClassGenerator;
import de.chennemann.jdto.processor.generation.Generator;
import de.chennemann.jdto.processor.model.DtoSpecification;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Stream;


@SupportedAnnotationTypes("de.chennemann.jdto.annotation.ToDTO")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ToDtoProcessor extends AbstractProcessor {
    
    
    private Generator generator;
    private Enricher enricher;
    
    private Filer filer;
    private Messager messager;
    
    
    @Override
    public synchronized void init(final ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        messager = processingEnvironment.getMessager();
        
        generator = new ClassGenerator(messager);
        enricher = new TypeEnricher(messager, processingEnvironment.getElementUtils());
    }
    
    
    @Override
    public boolean process(
        final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv
    ) {
    
        messager.printMessage(Diagnostic.Kind.NOTE,"Start processing");
        try {
            findProcessingCandidates(roundEnv)
                .map(enricher::enrich)
                .map(generator::generate)
                .forEach(this::writeClass);
        } catch (final Exception unexpectedError) {
            messager.printMessage(Diagnostic.Kind.ERROR, unexpectedError.getMessage());
        }
        messager.printMessage(Diagnostic.Kind.NOTE,"Finished processing");
        
        return true;
    }
    
    
    private Stream<TypeElement> findProcessingCandidates(final RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,"Evaluating classes to process");
        return roundEnv.getElementsAnnotatedWith(ToDTO.class)
                       .stream()
                       .peek(candidate -> {
                           if (candidate.getKind() != ElementKind.CLASS)
                               messager.printMessage(Diagnostic.Kind.ERROR, ToDTO.class.getSimpleName() + " can only applied to classes");
                       })
                       .filter(candidate -> candidate.getKind() == ElementKind.CLASS)
                       .map(candidate -> (TypeElement) candidate);
    }
    
    
    private void writeClass(final DtoSpecification specification) {
        try {
            final String packageName = specification.getPackageName();
            messager.printMessage(Diagnostic.Kind.NOTE, "Write class to " + packageName);
            JavaFile.builder(packageName, specification.getContents()).build().writeTo(filer);
        } catch (IOException ioException) {
            throw new NotWritable(ioException);
        }
    }
}
