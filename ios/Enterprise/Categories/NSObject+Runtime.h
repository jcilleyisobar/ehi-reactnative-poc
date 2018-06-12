//
//  NSObject+Runtime.h
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

typedef NS_ENUM(NSInteger, EHIMethodType) {
    EHIMethodTypeClass,
    EHIMethodTypeInstance
};

@interface NSObject (Runtime)

/**
 @brief Supplies the implementation for any class conforming to the provided protocol
 
 If the class already has its own implementation, this method does nothing.
 
 @param name     The name of the method
 @param imp      The implementation of the method
 @param types    The types for this methods signature
 @param protocol The protocol to match against
*/

+ (void)implementClassMethodNamed:(SEL)name imp:(IMP)imp types:(const char *)types forClassesConformingToProtocol:(Protocol *)protocol;

/**
 @brief Swizzles the given method using a block that generates the implementation
 
 The @c generator block is passed the existing implementation, and it's expected to return a
 correctly typed block that can be used with @c imp_implementationWithBlock.
 
 @param name      The name of the instance method to swizzle
 @param type      The type of method to swizzle
 @param generator The block that generates the updated implementation
*/

+ (void)swizzleMethodNamed:(SEL)name type:(EHIMethodType)type withBlockGenerator:(id(^)(IMP existing, SEL name))generator;

@end
