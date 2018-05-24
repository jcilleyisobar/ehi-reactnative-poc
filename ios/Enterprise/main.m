//
//  main.m
//  Enterprise
//
//  Created by Ty Cobb on 1/5/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

@import UIKit;

#import "EHIAppDelegate.h"
#import <dlfcn.h>
#import <sys/types.h>

typedef int (*ptrace_ptr_t)(int _request, pid_t _pid, caddr_t _addr, int _data);
#define PT_DENY_ATTACH 31

// deny attach from debugger
void denyAttach() {
    // The dlopen() function shall make an executable object file specified by file available to the calling program.
    // For more: http://pubs.opengroup.org/onlinepubs/009695399/functions/dlopen.html
    // RTLD_GLOBAL: The object's symbols shall be made available for the relocation processing of any other object. In addition, symbol lookup using dlopen(0, mode) and an associated dlsym() allows objects loaded with this mode to be searched.
    // RTLD_NOW: All necessary relocations shall be performed when the object is first loaded. This may waste some processing if relocations are performed for functions that are never referenced. This behavior may be useful for applications that need to know as soon as an object is loaded that all symbols referenced during execution are available.
    void* handle = dlopen(0, RTLD_GLOBAL | RTLD_NOW);
    
    // The dlsym() function shall obtain the address of a symbol defined within an object made accessible through a dlopen() call.
    // For more: http://pubs.opengroup.org/onlinepubs/009695399/functions/dlsym.html
    ptrace_ptr_t ptrace_ptr = dlsym(handle, "ptrace");
    ptrace_ptr(PT_DENY_ATTACH, 0, 0, 0);
    
    dlclose(handle);
}

int main(int argc, char * argv[]) {

#if !(defined(DEBUG))
    denyAttach();
#endif

    @autoreleasepool {
        return UIApplicationMain(argc, argv, nil, NSStringFromClass([EHIAppDelegate class]));
    }
}
