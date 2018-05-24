//
//  EHILocationDetailsConflictViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/7/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHILocationDetailsConflictViewModel.h"
#import "EHILocationConflictDataProvider.h"

@interface EHILocationDetailsConflictViewModel ()
@property (strong, nonatomic) EHILocationConflictDataProvider *conflictProvider;
@end

@implementation EHILocationDetailsConflictViewModel

- (instancetype)initWithModel:(EHILocation *)model
{
    if(self = [super initWithModel:model]) {
        self.conflictProvider = EHILocationConflictDataProvider.new.location(model);
    }
    
    return self;
}

- (NSString *)title
{
	return self.conflictProvider.title;
}

- (NSString *)openHours
{
	return self.conflictProvider.openHours;
}

@end
