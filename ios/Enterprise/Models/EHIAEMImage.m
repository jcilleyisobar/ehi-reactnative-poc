//
//  EHIAEMImage.m
//  Enterprise
//
//  Created by Rafael Ramos on 06/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIModel_Subclass.h"
#import "EHIAEMImage.h"

@interface EHIInternalImage : EHIModel
@property (assign, nonatomic) NSInteger width;
@property (copy  , nonatomic) NSString* path;
@end

@implementation EHIInternalImage
@end

@interface EHIAEMImage ()
@property (copy, nonatomic) NSArray<EHIInternalImage *> *imagesStorage;
@end

@implementation EHIAEMImage

- (void)updateWithDictionary:(NSDictionary *)dictionary forceDeletions:(BOOL)forceDeletions
{
    [super updateWithDictionary:dictionary forceDeletions:forceDeletions];
    
    self.imagesStorage = (dictionary ?: @{}).map(^(NSString *key, NSString *path){
        EHIInternalImage *image = EHIInternalImage.new;
        image.width = [key integerValue];
        image.path  = path;
        
        return image;
    }).sortBy(^(EHIInternalImage *image){
        return image.width;
    });
}

# pragma mark - EHIImage

- (NSString *)pathForWidth:(NSInteger)width quality:(EHIImageQuality)quality
{
    EHIInternalImage *image = self.imagesStorage.find(^(EHIInternalImage *image){
        return image.width >= width;
    });
    
    return image.path ?: self.imagesStorage.lastObject.path;
}

@end
