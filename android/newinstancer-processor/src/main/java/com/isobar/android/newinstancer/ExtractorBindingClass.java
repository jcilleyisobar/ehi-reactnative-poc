package com.isobar.android.newinstancer;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class ExtractorBindingClass extends BuilderBindingClass {
    public ExtractorBindingClass(final String classPackage, final String className, final String targetType, final ProcessingEnvironment processingEnv, final Messager messager, final TypeElement enclosingElement) {
        super(classPackage, className, targetType, processingEnv, messager, enclosingElement);
    }

    @Override
    public TypeSpec.Builder createTypeSpecBuilder() {
        ClassName targetClassName = ClassName.get(classPackage, targetType);
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addJavadoc("Generated extractor for $L,\n", targetClassName.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        if (!extrasBindings.isEmpty()) {
            ClassName bundle = generateBundleBuilderField(builder, "bundle");
            builder.addMethod(generateConstructor(targetClassName, null));

            for (ExtrasBinding binding : extrasBindings.values()) {
                builder.addMethod(generateExtractionMethod(binding, targetClassName));
            }
        }

        return builder;
    }

    @Override
    protected ClassName generateBundleBuilderField(final TypeSpec.Builder builder, final String fieldName) {
        ClassName bundle = ClassName.get("com.ehi.enterprise.android.utils", "EHIBundle");
        builder.addField(FieldSpec.builder(bundle, fieldName, Modifier.FINAL, Modifier.PRIVATE).build());
        return bundle;
    }

    @Override
    protected MethodSpec generateConstructor(final ClassName targetClassName, final ClassName requiredArray) {
        ClassName intentClass = ClassName.get("android.content", "Intent");

        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        if (target == Target.SERVICE) {
            builder.addParameter(ParameterSpec.builder(intentClass, "intent", Modifier.FINAL).build());
        } else {
            builder.addParameter(ParameterSpec.builder(targetClassName, "target", Modifier.FINAL).build());
        }

        if (targetClassName != null) {
            switch (target) {
                case SERVICE:
                    builder.addStatement("this.bundle = EHIBundle.fromBundle(intent.getExtras())");
                    break;
                case ACTIVITY:
                    builder.addStatement("this.bundle = EHIBundle.fromBundle(target.getIntent().getExtras())");
                    break;
                default:
                    builder.addStatement("this.bundle = EHIBundle.fromBundle(target.getArguments())");
                    break;
            }
        }
        return builder.build();
    }

    private MethodSpec generateExtractionMethod(final ExtrasBinding binding, final ClassName targetClassName) {
        final TypeMirror typeMirror = binding.clazz;
        typeMirror.getKind();
        MethodSpec.Builder builder = MethodSpec.methodBuilder(binding.methodName)
                .addJavadoc("Get the $L extra to the $L being built\nREQUIRED: $L\n",
                        binding.extraKey,
                        targetClassName.simpleName(), binding.required)
                .addJavadoc("\n{@link $L#$L}\n", targetClassName.simpleName(), binding.extraKey)
                .addModifiers(Modifier.PUBLIC);

        if (!binding.required) {
            ClassName nullableAnnotation = ClassName.get("android.support.annotation", "Nullable");
            builder.addAnnotation(AnnotationSpec.builder(nullableAnnotation).build())
                    .addJavadoc("\nNote, this will return <code> null </code> if the bundle doesn't contain the key!\n");
        } else {
            ClassName nonNull = ClassName.get("android.support.annotation", "NonNull");
            builder.addAnnotation(AnnotationSpec.builder(nonNull).build());
        }

        TypeMirror stringTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.lang.String");
        TypeMirror ehiModelTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "com.ehi.enterprise.android.models.EHIModel");
        TypeMirror serializableTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.io.Serializable");
        TypeMirror listTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.util.List");
        TypeMirror bundleTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "android.os.Bundle");
        TypeMirror parcelableTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "android.os.Parcelable");
        TypeMirror integerTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.lang.Integer");
        TypeMirror arrayListTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.util.ArrayList");

        String builderStatement = null;
        if (TypeName.get(binding.clazz).isPrimitive()) {
            switch (binding.clazz.getKind()) {
                case BOOLEAN:
                    builder.returns(Boolean.class);
                    addCheckContainsKeyStatement(binding, targetClassName, builder);
                    builderStatement = "bundle.getBoolean($L.$L)";
                    break;
                case INT:
                    builder.returns(Integer.class);
                    addCheckContainsKeyStatement(binding, targetClassName, builder);
                    builderStatement = "bundle.getInt($L.$L)";
                    break;
                case LONG:
                    builder.returns(Long.class);
                    addCheckContainsKeyStatement(binding, targetClassName, builder);
                    builderStatement = "bundle.getLong($L.$L)";
                    break;
                case DOUBLE:
                    builder.returns(Double.class);
                    addCheckContainsKeyStatement(binding, targetClassName, builder);
                    builderStatement = "bundle.getDouble($L.$L)";
                    break;
            }
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, arrayListTypeMirror)) {
            if (processingEnv.getTypeUtils().isAssignable(binding.typeClazz, integerTypeMirror)) {
                final TypeName typeClassName = TypeName.get(binding.typeClazz);
                final ClassName listClass = ClassName.get(ArrayList.class);
                final ParameterizedTypeName returnType = ParameterizedTypeName.get(listClass, typeClassName);
                addCheckContainsKeyStatement(binding, targetClassName, builder);
                builder.addStatement("return bundle.getIntegerArrayList($L.$L)", targetClassName.simpleName(), binding.extraKey);
                builder.returns(returnType);
                return builder.build();
            } else if (processingEnv.getTypeUtils().isAssignable(binding.typeClazz, stringTypeMirror)) {
                final TypeName typeClassName = TypeName.get(binding.typeClazz);
                final ClassName listClass = ClassName.get(ArrayList.class);
                final ParameterizedTypeName returnType = ParameterizedTypeName.get(listClass, typeClassName);
                addCheckContainsKeyStatement(binding, targetClassName, builder);
                builder.addStatement("return bundle.getStringArrayList($L.$L)", targetClassName.simpleName(), binding.extraKey);
                builder.returns(returnType);
                return builder.build();
            }
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, listTypeMirror)) {
            if (processingEnv.getTypeUtils().isAssignable(binding.typeClazz, ehiModelTypeMirror)) {
                final TypeName typeClassName = TypeName.get(binding.typeClazz);
                final ClassName listClass = ClassName.get(List.class);
                final ParameterizedTypeName listOfType = ParameterizedTypeName.get(listClass, typeClassName);
                final ClassName typeTokenClassName = ClassName.get("com.google.gson.reflect", "TypeToken");
                final ParameterizedTypeName parameterizedTypeToken = ParameterizedTypeName.get(typeTokenClassName, listOfType);
                final ParameterizedTypeName returnTypeName = ParameterizedTypeName.get(ClassName.get(List.class), typeClassName);
                addCheckContainsKeyStatement(binding, targetClassName, builder);
                builderStatement = "return bundle.getEHIModel($L.$L, \nnew $T(){}.getType())";
                builder.addStatement(builderStatement, targetClassName.simpleName(), binding.extraKey, parameterizedTypeToken);
                builder.returns(returnTypeName);
                return builder.build();
            }
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, stringTypeMirror)) {
            addCheckContainsKeyStatement(binding, targetClassName, builder);
            builder.returns(String.class);
            builderStatement = "bundle.getString($L.$L)";
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, ehiModelTypeMirror)) {
            final TypeName returnType = TypeName.get(binding.clazz);
            addCheckContainsKeyStatement(binding, targetClassName, builder);
            builderStatement = "return bundle.getEHIModel($L.$L, $L.class)";
            builder.addStatement(builderStatement, targetClassName.simpleName(), binding.extraKey, returnType);
            builder.returns(returnType);
            return builder.build();
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, serializableTypeMirror)) {
            final TypeName returnType = TypeName.get(binding.clazz);
            addCheckContainsKeyStatement(binding, targetClassName, builder);
            builder.addStatement("return ($T) bundle.getSerializable($L.$L)", returnType, targetClassName.simpleName(), binding.extraKey);
            builder.returns(returnType);
            return builder.build();
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, bundleTypeMirror)) {
            final TypeName returnType = TypeName.get(binding.clazz);
            addCheckContainsKeyStatement(binding, targetClassName, builder);
            builder.addStatement("return ($T) bundle.getBundle($L.$L)", returnType, targetClassName.simpleName(), binding.extraKey);
            builder.returns(returnType);
            return builder.build();
        } else if (processingEnv.getTypeUtils().isAssignable(binding.clazz, parcelableTypeMirror)) {
            final TypeName returnType = TypeName.get(binding.clazz);
            addCheckContainsKeyStatement(binding, targetClassName, builder);
            builder.addStatement("return ($T) bundle.getParcelable($L.$L)", returnType, targetClassName.simpleName(), binding.extraKey);
            builder.returns(returnType);
            return builder.build();
        } else {
            error("Can't create Extractor for " + targetClassName.simpleName() + binding.extraKey + " it's unsupported!");
        }

        builder.addStatement("return " + builderStatement, targetClassName.simpleName(), binding.extraKey);
        return builder.build();
    }

    private void addCheckContainsKeyStatement(final ExtrasBinding binding, final ClassName targetClassName, final MethodSpec.Builder builder) {
        if (!binding.required) {
            builder.beginControlFlow("if (!bundle.containsKey($L.$L))", targetClassName.simpleName(), binding.extraKey);
            builder.addStatement("return null");
            builder.endControlFlow();
        }
    }

    private void error(String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void note(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

}
