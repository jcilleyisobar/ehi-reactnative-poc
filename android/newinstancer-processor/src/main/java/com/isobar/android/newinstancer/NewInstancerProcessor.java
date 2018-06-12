package com.isobar.android.newinstancer;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

public class NewInstancerProcessor extends AbstractProcessor{
    public static final String BUILDER_CLASS_SUFFIX = "Builder";
    public static final String EXTRACTOR_CLASS_SUFFIX = "Extractor";
    private Filer filer;
    private Messager messager;
    public Elements elementUtils;
    private boolean helpersGenerated = false;
    private int noArgsHelperCount;

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>(2);
        supportedAnnotationTypes.add(Extra.class.getCanonicalName());
        supportedAnnotationTypes.add(NoExtras.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Map<TypeElement, BuilderBindingClass> builderTargetClassMap = new LinkedHashMap<>();
        Map<TypeElement, ExtractorBindingClass> extractorTargetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        for (Element element: roundEnv.getElementsAnnotatedWith(Extra.class)) {
            if(element.getKind() != ElementKind.FIELD
                    && !element.getModifiers().contains(Modifier.STATIC)
                    && !element.getModifiers().contains(Modifier.FINAL)){
                error(element, Extra.class.getSimpleName() + " annotations can only be applied to fields!");
                return false;
            }


            createBuilder(builderTargetClassMap, erasedTargetNames, element);
            createExtracter(extractorTargetClassMap, erasedTargetNames, element);
        }

        if(!helpersGenerated) {
            for (TypeElement element : builderTargetClassMap.keySet()) {
                MethodSpec.Builder privateConstructor = MethodSpec.constructorBuilder()
                                                                  .addModifiers(Modifier.PRIVATE);
                TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(getClassName(element, getPackageName(element)) + "Helper")
                                                         .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                                         .addMethod(privateConstructor.build())
                                                         .addType(builderTargetClassMap.get(element).createTypeSpecBuilder().build())
                                                         .addType(extractorTargetClassMap.get(element).createTypeSpecBuilder().build());
                try {
                    JavaFile javaFile = JavaFile.builder(builderTargetClassMap.get(element).classPackage, helperBuilder.build()).build();
                    javaFile.writeTo(filer);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
                }
            }
            helpersGenerated = true;
        }

        noArgsHelperCount = roundEnv.getElementsAnnotatedWith(NoExtras.class).size();
        for (Element element : roundEnv.getElementsAnnotatedWith(NoExtras.class)) {
            if(noArgsHelperCount == 0){
                break;
            }
            if(element.getKind() != ElementKind.CLASS){
                error(element, NoExtras.class.getSimpleName() + " annotation can only be applied to a class!");
                return false;
            }

            final BuilderBindingClass builder = createBuilder(builderTargetClassMap, erasedTargetNames, element);
            final TypeElement enclosingElement = getEnclosingElement(element);
            TypeSpec.Builder helperBuilder = TypeSpec.classBuilder(getClassName(enclosingElement, getPackageName(enclosingElement)) + "Helper")
                                                     .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                                     .addType(builder.createTypeSpecBuilder().build());
            try {
                JavaFile javaFile = JavaFile.builder(builder.classPackage, helperBuilder.build()).build();
                javaFile.writeTo(filer);
                noArgsHelperCount--;
            } catch (IOException e){
                messager.printMessage(Diagnostic.Kind.ERROR, e.toString());
            }
        }
        return true;
    }

    private BuilderBindingClass createBuilder(Map<TypeElement, BuilderBindingClass> targetClassMap, Set<String> erasedTargetNames, Element element) {
        TypeElement enclosingElement;
        enclosingElement = getEnclosingElement(element);

        BuilderBindingClass builderBindingClass = getOrCreateBuilder(targetClassMap, enclosingElement, erasedTargetNames);
        builderBindingClass.createAndAddBinding(element);

        return builderBindingClass;
    }

    private TypeElement getEnclosingElement(final Element element) {
        final TypeElement enclosingElement;
        if(element.getKind() == ElementKind.FIELD) {
            enclosingElement = (TypeElement) element.getEnclosingElement();
        }
        else {
            enclosingElement = (TypeElement) element;
        }
        return enclosingElement;
    }

    private ExtractorBindingClass createExtracter(Map<TypeElement, ExtractorBindingClass> targetClassMap, Set<String> erasedTargetNames, Element element) {
        TypeElement enclosingElement;
        enclosingElement = getEnclosingElement(element);

        ExtractorBindingClass extractorBindingClass = getOrCreateExtracter(targetClassMap, enclosingElement, erasedTargetNames);
        extractorBindingClass.createAndAddBinding(element);
        return extractorBindingClass;
    }

    private BuilderBindingClass getOrCreateBuilder(Map<TypeElement, BuilderBindingClass> targetClassMap,
                                            TypeElement enclosingElement,
                                            Set<String> erasedTargetNames){

        BuilderBindingClass builderBindingClass = targetClassMap.get(enclosingElement);
        if (builderBindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = BUILDER_CLASS_SUFFIX;

            builderBindingClass = new BuilderBindingClass(classPackage,
                                            className,
                                            targetType,
                                            this.processingEnv,
                                            this.messager,
                                            enclosingElement);
            targetClassMap.put(enclosingElement, builderBindingClass);
            erasedTargetNames.add(enclosingElement.toString());
        }
        return builderBindingClass;
    }

    private ExtractorBindingClass getOrCreateExtracter(Map<TypeElement, ExtractorBindingClass> targetClassMap,
                                                       TypeElement enclosingElement,
                                                       Set<String> erasedTargetNames){

        ExtractorBindingClass extractorBindingClass = targetClassMap.get(enclosingElement);
        if (extractorBindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = EXTRACTOR_CLASS_SUFFIX;

            extractorBindingClass = new ExtractorBindingClass(classPackage,
                                                          className,
                                                          targetType,
                                                          this.processingEnv,
                                                          this.messager,
                                                          enclosingElement);
            targetClassMap.put(enclosingElement, extractorBindingClass);
            erasedTargetNames.add(enclosingElement.toString());
        }
        return extractorBindingClass;
    }

    private void note(String message){
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private void debug(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.NOTE, String.format(msg, args), e);
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }

    private class BuilderExtractorPair {
        BuilderBindingClass builder;
        ExtractorBindingClass extractor;

        public BuilderExtractorPair(final BuilderBindingClass builder, final ExtractorBindingClass extractor) {
            this.builder = builder;
            this.extractor = extractor;
        }
    }
}
