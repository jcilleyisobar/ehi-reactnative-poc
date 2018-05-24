//
//  EHITermsAndConditionsViewController.m
//  Enterprise
//
//  Created by frhoads on 10/21/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHITermsAndConditionsViewController.h"
#import "EHITermsAndConditionsViewModel.h"
#import "EHILabel.h"
#import "EHIButton.h"
#import "EHIListCollectionView.h"
#import "EHIFormFieldDropdownCell.h"
#import "EHIActivityIndicator.h"

@interface EHITermsAndConditionsViewController () <EHIFormFieldCellActions>
@property (strong, nonatomic) EHITermsAndConditionsViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *dropdownPlaceHolder;
@property (weak  , nonatomic) IBOutlet UIWebView *webView;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *dismissButton;
@property (weak  , nonatomic) IBOutlet EHIActivityIndicator *loadingIndicator;
@end

@implementation EHITermsAndConditionsViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if (self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHITermsAndConditionsViewModel new];
    }
    
    return self;
}

# pragma mark - View Lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];

    self.dismissButton.type = EHIButtonTypeClose;
    [self insertDropdownPlaceholder];
}

- (void)insertDropdownPlaceholder
{
    // placeholder variable
    EHIViewModel *model = self.viewModel.dropDownModel;

    // create placeholder cell
    EHIFormFieldDropdownCell *placeholderCell = [EHIFormFieldDropdownCell ehi_instanceFromNib];
    placeholderCell.contentView.backgroundColor = [UIColor clearColor];
    [placeholderCell updateWithModel:model];
    [self.dropdownPlaceHolder addSubview:placeholderCell];

    // constrain into our placeholder container
    [placeholderCell mas_makeConstraints:^(MASConstraintMaker *make) {
        make.top.leading.trailing.equalTo(self.dropdownPlaceHolder);
    }];

    NSNumber *height = @(CGRectGetHeight(self.dropdownPlaceHolder.frame));
    [self.dropdownPlaceHolder mas_makeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(height);
    }];

    // rerun reactions with properly sized cell
    [placeholderCell registerReactions:model];
}

- (void)registerReactions:(EHITermsAndConditionsViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)         : ^(NSString *title) {
                                       self.title           = title;
                                       self.titleLabel.text = title;
                                    },
        source(model.title)         : dest(self, .titleLabel.text),
        source(model.isLoading)     : dest(self, .loadingIndicator.isAnimating),
        source(model.htmlString)    : ^(NSString *htmlString) {
                                        [self.webView loadHTMLString:htmlString baseURL:nil];
                                    },
    });
}

- (void)didBeginEditingPrimaryInputForCell:(EHIFormFieldCell *)sender
{
    [self.viewModel didShowDropdown];
}

# pragma mark - Actions

- (IBAction)didTapCloseButton:(UIButton *)closeButton
{
    [self.viewModel dismiss];
}

# pragma mark - Analytics

- (void)prepareToUpdateAnalyticsContext
{
    [EHIAnalytics changeScreen:EHIScreenTermsAndConditions state:EHIScreenTermsAndConditions];
}

- (void)didUpdateAnalyticsContext
{
    [EHIAnalytics trackState:nil];
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenTermsAndConditions;
}

@end
