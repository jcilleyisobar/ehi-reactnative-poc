//
//  EHIRedemptionTotalViewModel.m
//  Enterprise
//
//  Created by mplace on 8/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRedemptionTotalViewModel.h"
#import "EHIPriceFormatter.h"
#import "EHIPriceContext.h"
#import "EHIPlaceholder.h"

@interface EHIRedemptionTotalViewModel ()
@property (strong, nonatomic) id<EHIPriceContext> total;
@end

@implementation EHIRedemptionTotalViewModel

- (instancetype)initWithModel:(id)model
{
    if(self = [super initWithModel:model]) {
        _title = EHILocalizedString(@"redemption_total_cell_title", @"Original Total", @"title for the redemption total cell");
        self.showsLineItems = NO;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    if([model conformsToProtocol:@protocol(EHIPriceContext)]) {
        self.total = model;
    } else {
        self.value = nil;
    }
}

- (void)setTotal:(id<EHIPriceContext>)total
{
    _total = total;
    
    self.value = [EHIPriceFormatter format:total.viewPrice].attributedString;
}

# pragma mark - Setters

- (void)setShowsLineItems:(BOOL)showsLineItems
{
    _showsLineItems = showsLineItems;
    
    self.actionTitle = showsLineItems
    ? EHILocalizedString(@"redemption_total_cell_button_title_hide", @"HIDE DETAILS", @"action title for the redemption total cell")
    : EHILocalizedString(@"redemption_total_cell_button_title_show", @"SHOW DETAILS", @"action title for the redemption total cell");
}

@end
