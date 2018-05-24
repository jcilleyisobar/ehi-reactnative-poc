package com.isobar.android.newinstancer;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

public class BuilderBindingClass {
    protected enum Target {
        ACTIVITY, SERVICE, FRAGMENT;
    }

    protected final String classPackage;
    protected final String className;
    protected final String targetType;
    protected ProcessingEnvironment processingEnv;
    protected Messager messager;
    protected TypeElement enclosingElement;
    protected boolean isContext;
    protected Target target;
    protected final Map<String, ExtrasBinding> extrasBindings;
    private ClassName mTargetClassName;

    public BuilderBindingClass(final String classPackage, final String className, final String targetType, final ProcessingEnvironment processingEnv, Messager messager, TypeElement enclosingElement) {

        this.classPackage = classPackage;
        this.className = className;
        this.targetType = targetType;
        this.processingEnv = processingEnv;
        this.messager = messager;
        this.enclosingElement = enclosingElement;
        final TypeMirror activityTypeMirror = processingEnv.getElementUtils()
                .getTypeElement("android.app.Activity")
                .asType();

        final TypeMirror serviceTypeMirror = processingEnv.getElementUtils()
                .getTypeElement("android.app.Service")
                .asType();
        if (this.processingEnv.getTypeUtils().isAssignable(enclosingElement.asType(), activityTypeMirror)) {
            target = Target.ACTIVITY;
        } else if (this.processingEnv.getTypeUtils().isAssignable(enclosingElement.asType(), serviceTypeMirror)) {
            target = Target.SERVICE;
        } else {
            target = Target.FRAGMENT;
        }
        extrasBindings = new HashMap<>();
    }

    void createAndAddBinding(Element element) {
        ExtrasBinding binding = new ExtrasBinding(element);

        if (binding.hasArgs) {
            if (extrasBindings.containsKey(binding.methodName)) {
                throw new IllegalStateException(String.format("Duplicate attr assigned for field %s and method %s", binding.extraKey,
                        extrasBindings.get(binding.methodName).methodName));
            } else {
                extrasBindings.put(binding.methodName, binding);
            }
        }
    }

    public TypeSpec.Builder createTypeSpecBuilder() {
        mTargetClassName = ClassName.get(classPackage, targetType);

        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addJavadoc("Generated builder for {@link $L},\n", mTargetClassName.simpleName())
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);

        switch (target) {
            case ACTIVITY:
                builder.addJavadoc("use this to add expected arguments to the activity for instantiation\n");
                break;
            case SERVICE:
                builder.addJavadoc("use this to add expected arguments to the service for instantiation\n");
                break;
            case FRAGMENT:
                builder.addJavadoc("use this to add expected arguments to the fragment for instantiation\n");
                break;
        }

        ClassName requiredArray = null;

        if (extrasBindings.isEmpty()) {
            builder.addMethod(generateConstructor(null, null));
        } else {
            ClassName bundle = generateBundleBuilderField(builder, "builder");
            builder.addMethod(generateConstructor(bundle, requiredArray));
        }

        for (ExtrasBinding value : extrasBindings.values()) {
            builder.addMethod(generateBuilderMethod(value, mTargetClassName));
        }

        builder.addMethod(generateBuildMethod(mTargetClassName, extrasBindings.isEmpty()));


        return builder;
    }

    protected MethodSpec generateConstructor(final ClassName bundle, ClassName requiredArray) {
        MethodSpec.Builder builder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);

        if (bundle != null)
            builder.addStatement("this.$N = new $T()", "builder", bundle);
        return builder.build();

    }

    private ClassName generateRequiredArray(final TypeSpec.Builder builder) {
        ClassName hashMap = ClassName.get("java.util", "HashMap");
        ClassName string = ClassName.get("java.lang", "String");
        ClassName b = ClassName.get("java.lang", "Boolean");
        ParameterizedTypeName mapType = ParameterizedTypeName.get(hashMap, string, b);
        builder.addField(FieldSpec.builder(mapType, "requiredMap", Modifier.FINAL, Modifier.PRIVATE).build());
        return hashMap;

    }

    protected ClassName generateBundleBuilderField(final TypeSpec.Builder builder, final String fieldName) {
        ClassName bundle = ClassName.get("com.ehi.enterprise.android.utils.EHIBundle", "Builder");
        builder.addField(FieldSpec.builder(bundle, fieldName, Modifier.FINAL, Modifier.PRIVATE).build());
        return bundle;
    }

    private MethodSpec generateBuilderMethod(final ExtrasBinding extra, final ClassName mTargetClassName) {
        final TypeMirror typeMirror = extra.clazz;
        typeMirror.getKind();
        String enclosingHelperClass = mTargetClassName.simpleName().replace(mTargetClassName.packageName() + ".", "") + "Helper";
        MethodSpec.Builder builder = MethodSpec.methodBuilder(extra.methodName)
                .addJavadoc("Adds the $L extra to the $L being built\nREQUIRED: $L\n", extra.extraKey, mTargetClassName.simpleName(), extra.required)
                .addJavadoc("\n{@link $L#$L}\n", mTargetClassName.simpleName(), extra.extraKey)
                .addModifiers(Modifier.PUBLIC)
                .returns(ClassName.get(classPackage, enclosingHelperClass, className));

        TypeMirror stringTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.lang.String");
        TypeMirror ehiModelTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "com.ehi.enterprise.android.models.EHIModel");
        TypeMirror serializableTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.io.Serializable");
        TypeMirror listTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.util.List");
        TypeMirror bundleTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "android.os.Bundle");
        TypeMirror parcelableTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "android.os.Parcelable");
        TypeMirror integerTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.lang.Integer");
        TypeMirror arrayListTypeMirror = TypeMirrorUtils.getTypeMirror(processingEnv, "java.util.ArrayList");

        String builderStatement = null;
        if (TypeName.get(extra.clazz).isPrimitive()) {
            switch (extra.clazz.getKind()) {
                case BOOLEAN:
                    builderStatement = "builder.putBoolean($L.$L, value)";
                    break;
                case INT:
                    builderStatement = "builder.putInt($L.$L, value)";
                    break;
                case LONG:
                    builderStatement = "builder.putLong($L.$L, value)";
                    break;
                case DOUBLE:
                    builderStatement = "builder.putDouble($L.$L, value)";
                    break;
            }
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, arrayListTypeMirror)) {
            if (processingEnv.getTypeUtils().isAssignable(extra.typeClazz, integerTypeMirror)) {
                if (extra.required) {
                    builder.beginControlFlow("if(value == null)");
                    builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                    builder.endControlFlow();
                }
                final TypeName typeClassName = TypeName.get(extra.typeClazz);
                builderStatement = "builder.putIntegerArrayList($L.$L, value)";
                final ParameterizedTypeName returnTypeName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeClassName);
                builder.addParameter(ParameterSpec.builder(returnTypeName, "value", Modifier.FINAL).build());
            } else if (processingEnv.getTypeUtils().isAssignable(extra.typeClazz, stringTypeMirror)) {
                if (extra.required) {
                    builder.beginControlFlow("if(value == null)");
                    builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                    builder.endControlFlow();
                }
                final TypeName typeClassName = TypeName.get(extra.typeClazz);
                builderStatement = "builder.putStringArrayList($L.$L, value)";
                final ParameterizedTypeName returnTypeName = ParameterizedTypeName.get(ClassName.get(ArrayList.class), typeClassName);
                builder.addParameter(ParameterSpec.builder(returnTypeName, "value", Modifier.FINAL).build());
            }
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, listTypeMirror)) {
            if (processingEnv.getTypeUtils().isAssignable(extra.typeClazz, ehiModelTypeMirror)) {
                if (extra.required) {
                    builder.beginControlFlow("if(value == null)");
                    builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                    builder.endControlFlow();
                }
                final TypeName typeClassName = TypeName.get(extra.typeClazz);
                builderStatement = String.format("builder.putEHIModel($L.$L, value, %b)", extra.isLarge);
                final ParameterizedTypeName returnTypeName = ParameterizedTypeName.get(ClassName.get(List.class), typeClassName);
                builder.addParameter(returnTypeName, "value", Modifier.FINAL).build();
            }
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, stringTypeMirror)) {
            if (extra.required) {
                builder.beginControlFlow("if(value == null)");
                builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                builder.endControlFlow();
            }
            builderStatement = "builder.putString($L.$L, value)";
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, ehiModelTypeMirror)) {
            if (extra.required) {
                builder.beginControlFlow("if(value == null)");
                builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                builder.endControlFlow();
            }
            builderStatement = String.format("builder.putEHIModel($L.$L, value, %b)", extra.isLarge);
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, serializableTypeMirror)) {
            if (extra.required) {
                builder.beginControlFlow("if(value == null)");
                builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                builder.endControlFlow();
            }
            builderStatement = "builder.putSerializable($L.$L, value)";
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, bundleTypeMirror)) {
            if (extra.required) {
                builder.beginControlFlow("if(value == null)");
                builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                builder.endControlFlow();
            }
            builderStatement = "builder.putBundle($L.$L, value)";
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        } else if (processingEnv.getTypeUtils().isAssignable(extra.clazz, parcelableTypeMirror)) {
            if (extra.required) {
                builder.beginControlFlow("if(value == null)");
                builder.addStatement("throw new $T(\"$L cannot be null\")", IllegalStateException.class, extra.extraKey);
                builder.endControlFlow();
            }
            builderStatement = "builder.putParcelable($L.$L, value)";
            builder.addParameter(ParameterSpec.builder(TypeName.get(extra.clazz), "value", Modifier.FINAL).build());
        }

        if (builderStatement != null) {
            builder.addStatement(builderStatement, mTargetClassName.simpleName(), extra.extraKey);
        }
        builder.addStatement("return this");
        return builder.build();
    }

    private MethodSpec generateBuildMethod(final ClassName mTargetClassName, boolean empty) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("build")
                .addJavadoc("Creates a new $L with the added extras\n", mTargetClassName.simpleName())
                .addModifiers(Modifier.PUBLIC);
        if (target == Target.ACTIVITY || target == Target.SERVICE) {
            ClassName context = ClassName.get("android.content", "Context");
            builder.addJavadoc("@param context:  context to launch the intent from\n");
            builder.addJavadoc("@return the intent to use to launch this activity with the expected extras\n");
            builder.addParameter(ParameterSpec.builder(context, "context").build());
            ClassName intent = ClassName.get("android.content", "Intent");
            builder.addStatement("$T intent = new $T(context, $N.class)", intent, intent, enclosingElement.getSimpleName());
            builder.returns(intent);
        } else {
            builder.addStatement("$N fragment = new $N()", mTargetClassName.simpleName(), mTargetClassName.simpleName());
            builder.returns(mTargetClassName);
        }

        if (target == Target.ACTIVITY || target == Target.SERVICE) {
            if (!empty)
                builder.addStatement("intent.putExtras(builder.createBundle())");
            builder.addStatement("return intent");
        } else {
            if (!empty) builder.addStatement("fragment.setArguments(builder.createBundle())");
            builder.addStatement("return fragment");
        }
        return builder.build();
    }

    private void note(String message) {
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

}
