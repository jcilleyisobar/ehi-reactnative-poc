package com.isobar.android.newinstancer;

import java.util.StringTokenizer;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

class ExtrasBinding {
    String extraKey;
    String methodName;
    boolean required;
    TypeMirror clazz;
    TypeMirror typeClazz;
    boolean hasArgs;
    boolean isLarge;

    public ExtrasBinding(Element element) {
        if (element.getKind() == ElementKind.CLASS) {
            return;
        }

        hasArgs = true;
        Extra instance = element.getAnnotation(Extra.class);
        isLarge = instance.large();
        clazz = getTypeMirror(instance);
        typeClazz = getTypeMirrorForType(instance);
        extraKey = element.getSimpleName().toString();
        if (instance.name().length() == 0) {
            this.methodName = toCamelCase(element.getSimpleName().toString());
        }
        else {
            this.methodName = instance.name();
        }

        this.required = instance.required();
    }

    String toCamelCase(String string) {
        String newString = null;
        StringTokenizer stringTokenizer = new StringTokenizer(string.toLowerCase(), "_");
//
        boolean isFirst = true;
        while (stringTokenizer.hasMoreTokens()) {
            if (isFirst) {
                newString = stringTokenizer.nextToken();
                isFirst = false;
            }
            else {
                String temp = stringTokenizer.nextToken();
                char upperCase = Character.toUpperCase(temp.charAt(0));
                temp = upperCase + temp.substring(1, temp.length());
                newString += temp;
            }
        }
//
        return newString;
    }

    private TypeMirror getTypeMirror(Extra annotation) {
        try {
            annotation.value(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }

    private TypeMirror getTypeMirrorForType(Extra annotation) {
        try {
            annotation.type(); // this should throw
        } catch (MirroredTypeException mte) {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }
}
