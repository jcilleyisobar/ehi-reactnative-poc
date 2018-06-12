package io.dwak.reactor.unbinder;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public class BindingClass {
    private final String mClassPackage;
    private final String mClassName;
    private final String mTargetType;
    private final Map<String, ReactiveBinding> reactiveBindings;

    public BindingClass(final String classPackage, final String className, final String targetType) {

        mClassPackage = classPackage;
        mClassName = className;
        mTargetType = targetType;
        reactiveBindings = new HashMap<>();
    }

    void createAndAddReactiveBinding(Element element) {
        ReactiveBinding binding = new ReactiveBinding(element);
        if (reactiveBindings.containsKey(binding.name)) {
            throw new IllegalStateException(String.format("Duplicate attr assigned for field %s and %s", binding.name,
                                                          reactiveBindings.get(binding.name).name));
        }
        else {
            reactiveBindings.put(binding.name, binding);
        }
    }

    public void writeToFiler(final Filer filer) throws IOException {
        ClassName targetClassName = ClassName.get(mClassPackage, mTargetType);
        TypeSpec.Builder reactive = TypeSpec.classBuilder(mClassName)
                                            .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "unused").build())
                                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                                            .addMethod(generateUnbindMethod(targetClassName));

        JavaFile javaFile = JavaFile.builder(mClassPackage, reactive.build()).build();
        javaFile.writeTo(filer);
    }

    private MethodSpec generateUnbindMethod(final ClassName targetClassName) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("unbind")
                                               .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                                               .returns(void.class)
                                               .addParameter(ParameterSpec.builder(targetClassName, "target", Modifier.FINAL).build());

        if (!reactiveBindings.isEmpty()) {
            builder.beginControlFlow("if(target != null)");
            for (ReactiveBinding binding : reactiveBindings.values()) {
                builder.beginControlFlow("if (target.$L != null)", binding.name);
                builder.addStatement("target.$L.unbindDependency()", binding.name);
                builder.endControlFlow();
            }
            builder.endControlFlow();
        }
        return builder.build();
    }

    public String getClassPackage() {
        return mClassPackage;
    }

    public String getClassName() {
        return mClassName;
    }

    public Map<String, ReactiveBinding> getReactiveBindings() {
        return reactiveBindings;
    }


    private class ReactiveBinding {
        private String name;

        public ReactiveBinding(final Element element) {
            name = element.getSimpleName().toString();
        }
    }
}
