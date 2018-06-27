//
//  EHIRequiredInfoView.m
//  Enterprise
//
//  Created by Rafael Ramos on 02/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoView.h"
#import "EHIRequiredInfoViewModel.h"

@interface EHIRequiredInfoView ()
@property (strong, nonatomic) EHIRequiredInfoViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *requiredLabel;
@end

@implementation EHIRequiredInfoView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRequiredInfoViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRequiredInfoViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .requiredLabel.text)
    });
}

# pragma mark - Replaceable

+ (BOOL)isReplaceable
{
    return YES;
}

@end
