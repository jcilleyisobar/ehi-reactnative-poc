//
//  EHIRequiredInfoFootnoteView.m
//  Enterprise
//
//  Created by Rafael Ramos on 07/05/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIRequiredInfoFootnoteView.h"
#import "EHIRequiredInfoFootnoteViewModel.h"

@interface EHIRequiredInfoFootnoteView ()
@property (strong, nonatomic) EHIRequiredInfoFootnoteViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UILabel *footnoteLabel;
@end

@implementation EHIRequiredInfoFootnoteView

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRequiredInfoFootnoteViewModel new];
    }
    
    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRequiredInfoFootnoteViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:^(MTRComputation *computation) {
        depend(self.viewModel.type);
        
        self.backgroundColor = model.type == EHIRequiredInfoFootnoteTypeReservation
            ? [UIColor clearColor]
            : [UIColor ehi_grayColor0];
    }];
    
    model.bind.map(@{
        source(model.note) : dest(self, .footnoteLabel.attributedText)
    });
}

# pragma mark - Replaceable

+ (BOOL)isReplaceable
{
    return YES;
}

@end
