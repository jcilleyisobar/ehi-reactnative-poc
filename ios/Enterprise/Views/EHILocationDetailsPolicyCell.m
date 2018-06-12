//
//  EHILocationDetailsPolicyCell.m
//  Enterprise
//
//  Created by Ty Cobb on 2/26/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHILocationDetailsPolicyCell.h"
#import "EHILocationDetailsPolicyViewModel.h"

@interface EHILocationDetailsPolicyCell ()
@property (strong, nonatomic) EHILocationDetailsPolicyViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@end

@implementation EHILocationDetailsPolicyCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHILocationDetailsPolicyViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHILocationDetailsPolicyViewModel *)model
{
    [super registerReactions:model];
   
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text)
    });
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 50.0f };
    return metrics;
}

@end
