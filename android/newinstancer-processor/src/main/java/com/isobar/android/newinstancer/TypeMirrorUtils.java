package com.isobar.android.newinstancer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.TypeMirror;

public class TypeMirrorUtils {
    public static TypeMirror getTypeMirror(ProcessingEnvironment processingEnvironment, String qualifiedName){
        return processingEnvironment.getElementUtils().getTypeElement(qualifiedName).asType();
    }
}
