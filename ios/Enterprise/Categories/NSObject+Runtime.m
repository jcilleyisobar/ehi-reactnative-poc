//
//  NSObject+Runtime.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import ObjectiveC;

#import "NSObject+Runtime.h"

@implementation NSObject (Runtime)

+ (void)implementClassMethodNamed:(SEL)name imp:(IMP)imp types:(const char *)types forClassesConformingToProtocol:(Protocol *)protocol
{
    Class *classes; uint count;
    
    classes = objc_copyClassList(&count);
    for(int index=0 ; index<count ; index++) {
        Class class = classes[index];
        if(class_conformsToProtocol(class, protocol)) {
            // check if we have a class method
            Method method = class_getClassMethod(class, name);
            
            // if not, get the class' metaclass and add a method to it (class_addMethod adds instance methods,
            // and instance methods on the metaclass are class methods)
            if(method == NULL) {
                Class metaclass = object_getClass(class);
                class_addMethod(metaclass, name, imp, types);
            }
        }
    }
    
    free(classes);
}

+ (void)swizzleMethodNamed:(SEL)name type:(EHIMethodType)type withBlockGenerator:(id (^)(IMP, SEL))generator
{
    // get the correct method off this class
    Method method = type == EHIMethodTypeClass ? class_getClassMethod(self, name) : class_getInstanceMethod(self, name);
    NSAssert(method != NULL, @"%@ failed to find a method named: %s", self, sel_getName(name));
    
    // generate a block for the new implementation of this method
    id implementation = generator(method_getImplementation(method), name);
    // and update the implementation
    method_setImplementation(method, imp_implementationWithBlock(implementation));
}

@end
