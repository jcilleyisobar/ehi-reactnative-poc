//
//  EHITerminalDirectionsViewModel.m
//  Enterprise
//
//  Created by Pawel Bragoszewski on 02.03.2015.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationWayfindingViewModel.h"
#import "EHILocationWayfinding.h"

@interface EHILocationWayfindingViewModel ()
@property (strong, nonatomic) NSArray *wayfindings;
@end

@implementation EHILocationWayfindingViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"terminal_directions_title", @"TERMINAL DIRECTIONS", @"Title for the Terminal Directions screen");
    }
    
    return self;
}

- (void)updateWithModel:(NSArray *)wayfindings
{
    [super updateWithModel:wayfindings];
   
    // filter out empty steps
    self.wayfindings = (wayfindings ?: @[]).select(^(EHILocationWayfinding *wayfinding) {
        return wayfinding.text.length != 0;
    });
}

@end
