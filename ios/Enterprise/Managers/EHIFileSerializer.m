//
//  EHIFileSerializer.m
//  Enterprise
//
//  Created by Ty Cobb on 2/23/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIFileSerializer.h"

#define EHIFileSerializerBytesPerMegabyte (1024 * 1024)

@interface EHIFileSerializer()
@property (copy  , nonatomic) NSString *basePath;
@property (strong, nonatomic) NSOperationQueue *queue;
@property (nonatomic, readonly) NSFileManager *fileManager;
@end

@implementation EHIFileSerializer

+ (instancetype)serializer
{
    static EHIFileSerializer *serializer;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        serializer = [self new];
    });
    
    return serializer;
}

- (instancetype)init
{
    return [self initWithDirectory:NSCachesDirectory];
}

- (instancetype)initWithDirectory:(NSSearchPathDirectory)directory
{
    if(self = [super init]) {
        _queue = [NSOperationQueue new];
        _queue.maxConcurrentOperationCount = 1;
        _basePath = NSSearchPathForDirectoriesInDomains(directory, NSUserDomainMask, YES).firstObject;
    }
    
    return self;
}

- (instancetype)initWithDirectory:(NSSearchPathDirectory)directory subdirectory:(NSString *)subdirectory
{
    if(self = [self initWithDirectory:directory]) {
        _basePath = [_basePath stringByAppendingPathComponent:subdirectory];
        [self ensureDirectoriesToPath:_basePath];
    }
    
    return self;
}

# pragma mark - Write

- (void)write:(id<NSCoding>)object toPath:(NSString *)path handler:(void (^)(BOOL))handler
{
    NSData *fileData = [NSKeyedArchiver archivedDataWithRootObject:object];
    [self.queue addOperationWithBlock:^{
        BOOL didWrite = [self writeData:fileData toRelativePath:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(didWrite);
        });
    }];
}

- (BOOL)write:(id<NSCoding>)object toRelativePath:(NSString *)relativePath
{
    NSData *fileData = [NSKeyedArchiver archivedDataWithRootObject:object];
    return [self writeData:fileData toRelativePath:relativePath];
}

- (BOOL)writeData:(NSData *)data toRelativePath:(NSString *)relativePath
{
    NSError *error;
    
    NSString *filePath = [self absolutePathForRelativePath:relativePath];
    BOOL success = [data writeToFile:filePath options:NSDataWritingAtomic error:&error];
    
    if(error) {
        EHIDomainError(EHILogDomainFiles, @"couldn't write: %@", error);
    }
    
#ifdef DEBUG
    NSDictionary *fileAttributes = [self.fileManager attributesOfItemAtPath:filePath error:nil];
    double fileSize = (double)fileAttributes.fileSize / EHIFileSerializerBytesPerMegabyte;
    
    if(success) {
        EHIDomainDebug(EHILogDomainFiles, @"saved: %@ size: %.2f MB", relativePath, fileSize);
    } else {
        EHIDomainError(EHILogDomainFiles, @"failed to save: %@ size: %.2f MB", relativePath, fileSize);
    }
#endif
    
    return success;
}

# pragma mark - Read

- (void)read:(NSString *)relativePath withHandler:(void(^)(id object))handler
{
    [self.queue addOperationWithBlock:^{
        id object = [self read:relativePath];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(object);
        });
    }];
}

- (id)read:(NSString *)relativePath
{
    // find the filepath
    NSString *filepath = [self absolutePathForRelativePath:relativePath];
    if(![self.fileManager fileExistsAtPath:filepath]) {
        return nil;
    }
  
    // log filesize info
    [self recordFilesizeForPath:filepath relativePath:relativePath];
    
    // try to unarchive the object and return it
    id unarchivedObject = [self unarchiveObjectAtPath:filepath];
    
    return unarchivedObject;
}

- (id)unarchiveObjectAtPath:(NSString *)path
{
    id unarchivedObject = nil;
    
    @try {
        unarchivedObject = [NSKeyedUnarchiver unarchiveObjectWithFile:path];
    } @catch (NSException *exception) {
        EHIDomainError(EHILogDomainFiles, @"couldn't unarchive: %@", exception);
    }
    
    return unarchivedObject;
}

# pragma mark - Read All

- (void)readAll:(NSString *)path handler:(void (^)(NSArray *))handler
{
    [self.queue addOperationWithBlock:^{
        NSArray *objects = [self readAll:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(objects);
        });
    }];
}

- (NSArray *)readAll:(NSString *)relativePath
{
    // find the filepath
    NSString *directoryPath = [self absolutePathForRelativePath:relativePath];
    if(![self.fileManager fileExistsAtPath:directoryPath]) {
        return nil;
    }
   
    // record the directory size
    [self recordFilesizeForPath:directoryPath relativePath:relativePath];
   
    NSError *error;
   
    // unarchive all the objects in this directory
    NSArray *contents = [self.fileManager contentsOfDirectoryAtPath:directoryPath error:&error];
    NSArray *objects = contents.map(^(NSString *path) {
        path = [directoryPath stringByAppendingFormat:@"/%@", path];
        return [self unarchiveObjectAtPath:path];
    });
   
    return objects;
}

# pragma mark - Remove

- (void)remove:(NSString *)path handler:(void(^)(BOOL didRemove))handler;
{
    [self.queue addOperationWithBlock:^{
        BOOL didRemove = [self removeFile:path];
        dispatch_async(dispatch_get_main_queue(), ^{
            ehi_call(handler)(didRemove);
        });
    }];
}

- (BOOL)removeFile:(NSString *)relativePath
{
    // check that the file actually exists
    NSString *absolutePath = [self absolutePathForRelativePath:relativePath];
   
    // check for directory globbing
    BOOL recreateDirectory = NO;
    if([absolutePath hasSuffix:@"/*"]) {
        absolutePath = [absolutePath substringToIndex:absolutePath.length - 2];
        // if we globbed the contents of a directory, we'll delete and re-create it
        recreateDirectory = YES;
    }
    
    if(![self.fileManager fileExistsAtPath:absolutePath]) {
        return NO;
    }
    
    NSError *error;
    // remove the file / directory
    [self.fileManager removeItemAtPath:absolutePath error:&error];
    // create the directory if necessary
    if(!error && recreateDirectory) {
        [self.fileManager createDirectoryAtPath:absolutePath withIntermediateDirectories:YES attributes:nil error:&error];
    }
    
    if(error) {
        EHIDomainError(EHILogDomainFiles, @"failed to delete file: %@", error);
        return NO;
    }
    
    return YES;
}

# pragma mark - Directories

- (void)ensureDirectoriesToPath:(NSString *)relativePath
{
    NSError *error;
   
    NSString *directoryPath = [self absolutePathForRelativePath:relativePath];
    if(![self.fileManager fileExistsAtPath:directoryPath]) {
        [self.fileManager createDirectoryAtPath:directoryPath withIntermediateDirectories:YES attributes:nil error:&error];
    }
    
    if(error) {
        EHIDomainError(EHILogDomainFiles, @"couldn't create directories: %@", error);
    }
}

# pragma mark - Descruction

- (void)deleteObjectAtRelativePath:(NSString *)relativePath
{
    NSError *error;
    
    NSString *absolutePath = relativePath ? [self absolutePathForRelativePath:relativePath] : self.basePath;
    [[NSFileManager defaultManager] removeItemAtPath:absolutePath error:&error];

    if(error) {
        EHIDomainError(EHILogDomainFiles, @"couldnt delete: %@", error);
    }
}

- (void)resetRootDirectory
{
    [self deleteObjectAtRelativePath:nil];
    [self ensureDirectoriesToPath:self.basePath];
}

# pragma mark - Helpers

- (void)recordFilesizeForPath:(NSString *)filepath relativePath:(NSString *)relativePath
{
#ifdef DEBUG
    NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfItemAtPath:filepath error:nil];
    CGFloat fileSize = (CGFloat)fileAttributes.fileSize / EHIFileSerializerBytesPerMegabyte;
    EHIDomainDebug(EHILogDomainFiles, @"file: %@ size: %.2f MB", relativePath, fileSize);
#endif
}

- (NSString *)absolutePathForRelativePath:(NSString *)relativePath
{
    return [self.basePath stringByAppendingPathComponent:relativePath];
}

# pragma mark - Accessors

- (NSFileManager *)fileManager
{
    return [NSFileManager defaultManager];
}

@end
