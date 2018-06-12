//
//  EHIFlightDetailsSearchCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 6/23/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIFlightDetailsSearchCell.h"
#import "EHIFlightDetailsSearchViewModel.h"

@interface EHIFlightDetailsSearchCell () <UITextFieldDelegate>
@property (strong, nonatomic) EHIFlightDetailsSearchViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHITextField *searchField;
@end

@implementation EHIFlightDetailsSearchCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIFlightDetailsSearchViewModel new];
    }

    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.searchField.borderType  = EHITextFieldBorderField;
    self.searchField.borderColor = [UIColor ehi_grayColor2];
    self.searchField.actionButtonType = EHIButtonTypeSearch;
    
    [self.searchField.actionButton addTarget:self action:@selector(searchFieldTapped) forControlEvents:UIControlEventTouchUpInside];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIFlightDetailsSearchViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.searchPlaceholder) : dest(self, .searchField.placeholder),
        source(model.airlineName)       : dest(self, .searchField.text),
        source(model.airlineTitle)      : dest(self, .titleLabel.text)
    });
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    [self searchFieldTapped];
    
    return NO;
}

# pragma mark - Actions

- (void)searchFieldTapped
{
    [self ehi_performAction:@selector(searchCellDidTap) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame) + EHIMediumPadding
    };
}

@end
