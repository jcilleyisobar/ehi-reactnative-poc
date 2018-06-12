package io.dwak.reactor.unbinder;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import io.dwak.reactor.unbinder.annotation.AutoUnbind;
import io.dwak.reactor.unbinder.annotation.AutoUnbindAll;
import io.dwak.reactor.unbinder.annotation.ExcludeUnbind;

public class UnbinderProcessor extends AbstractProcessor {
    public static final String CLASS_SUFFIX = "$$Unbinder";
    private Filer filer;
    private Messager messager;
    public Elements elementUtils;
    private boolean unbinderCreated;
    private final ClassName REACTOR_UNBINDER_CLASS_NAME = ClassName.get("io.dwak.reactor.unbinder", "ReactorUnbinder");

    @Override
    public synchronized void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationsSet = new HashSet<>(2);
        supportedAnnotationsSet.add(AutoUnbind.class.getCanonicalName());
        supportedAnnotationsSet.add(AutoUnbindAll.class.getCanonicalName());
        return supportedAnnotationsSet;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        Map<TypeElement, BindingClass> targetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(AutoUnbind.class)) {
            if (!isValidElement(element)) {
                error(element, AutoUnbind.class.getSimpleName() + " annotations can only be applied to fields!");
                return false;
            }
            processElement(targetClassMap, erasedTargetNames, element);
        }

        List<Element> enclosedReactorVars = new ArrayList<>();
        for(Element element : roundEnv.getElementsAnnotatedWith(AutoUnbindAll.class)){
            List<? extends Element> enclosedElements = element.getEnclosedElements();
            TypeMirror reactorVarMirror = this.processingEnv.getElementUtils()
                                                            .getTypeElement("io.dwak.reactor.ReactorVar")
                                                            .asType();

            Types typeUtils = this.processingEnv.getTypeUtils();
            for (Element enclosedElement : enclosedElements) {
                if (enclosedElement.getKind().isField()
                        && enclosedElement.getAnnotation(ExcludeUnbind.class) == null
                        && !enclosedElement.getModifiers().contains(Modifier.PRIVATE)
                        && typeUtils.isAssignable(typeUtils.erasure(enclosedElement.asType()), reactorVarMirror)) {
                    enclosedReactorVars.add(enclosedElement);
                }
            }

        }

        for (Element element : enclosedReactorVars) {
            processElement(targetClassMap, erasedTargetNames, element);
        }

        for (BindingClass bindingClass : targetClassMap.values()) {
            try {
                bindingClass.writeToFiler(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        if(!unbinderCreated) {
            try {
                createUnbinder(targetClassMap);
                unbinderCreated = true;
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return true;
    }

    private void createUnbinder(Map<TypeElement, BindingClass> targetClassMap) throws IOException{
        TypeSpec.Builder unbinderBuilder = TypeSpec.classBuilder(REACTOR_UNBINDER_CLASS_NAME.simpleName())
                .addJavadoc("This generated class can be used to unbind dependencies " +
                                    "\nin all classes annotated with {@link @$T} " +
                                    "\nor {@link @$T}" +
                                    "\n", AutoUnbindAll.class, AutoUnbind.class)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(generateUnbindMethod(targetClassMap));

        JavaFile.builder(REACTOR_UNBINDER_CLASS_NAME.packageName(), unbinderBuilder.build()).build().writeTo(filer);
    }

    private MethodSpec generateUnbindMethod(Map<TypeElement, BindingClass> targetClassMap) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("unbind")
                                               .addParameter(ParameterSpec.builder(TypeName.OBJECT, "target", Modifier.FINAL).build())
                                               .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                                               .returns(void.class);

        boolean first = true;
        for (TypeElement type : targetClassMap.keySet()) {
            ClassName viewModelClass = ClassName.get(type);
            builder.beginControlFlow("if(target instanceof $T)", viewModelClass);
            BindingClass binding = targetClassMap.get(type);
            ClassName unbinderClass = ClassName.get(binding.getClassPackage(), binding.getClassName());
            builder.addStatement("$T.unbind(($T)target)", unbinderClass, viewModelClass);
            builder.endControlFlow();
        }
        return builder.build();
    }

    private boolean isValidElement(Element element){
        return !element.getModifiers().contains(Modifier.PRIVATE)
                && element.getKind().equals(ElementKind.FIELD);
    }

    private void processElement(Map<TypeElement, BindingClass> targetClassMap, Set<String> erasedTargetNames, Element element) {
        TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
        BindingClass bindingClass = getOrCreateReactive(targetClassMap, enclosingElement, erasedTargetNames);
        bindingClass.createAndAddReactiveBinding(element);
    }

    private BindingClass getOrCreateReactive(Map<TypeElement, BindingClass> targetClassMap,
                                             TypeElement enclosingElement, Set<String> erasedTargetNames){
        BindingClass bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + CLASS_SUFFIX;
            bindingClass = new BindingClass(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, bindingClass);
            erasedTargetNames.add(enclosingElement.toString());
        }

        return bindingClass;
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
}
