//
//  EHIDashboardSearchCell.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardSearchCell.h"
#import "EHIDashboardSearchViewModel.h"
#import "EHIDashboardSearchFieldBorder.h"
#import "EHIDashboardLayoutAttributes.h"
#import "EHIAnimatedShapeLayer.h"

@interface EHIDashboardSearchCell () <UITextFieldDelegate>
@property (strong, nonatomic) EHIDashboardSearchViewModel *viewModel;
@property (strong, nonatomic) UIView *borderView;
@property (weak  , nonatomic) IBOutlet EHITextField *searchField;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UIButton *scrollIndicatorButton;
@end

@implementation EHIDashboardSearchCell

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIDashboardSearchViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // search field styling; we'll use a custom border
    self.searchField.borderType       = EHITextFieldBorderNone;
    self.searchField.actionButtonType = EHIButtonTypeNearby;
    self.searchField.clipsToBounds    = NO;

    self.borderView = [[EHIDashboardSearchFieldBorder alloc] initWithFrame:self.searchField.bounds];
    self.borderView.autoresizingMask = UIViewAutoresizingFill;
    
    [self.searchField addSubview:self.borderView];
}

- (void)applyLayoutAttributes:(EHIDashboardLayoutAttributes *)attributes
{
    [super applyLayoutAttributes:attributes];
  
    // hardcoded length that the scroll indicator button will be alpha'd our over
    const CGFloat alphaDistance = 40.0f;
    self.scrollIndicatorButton.alpha = EHIClamp((alphaDistance - attributes.stickyOffset) / alphaDistance, 0.0f, 1.0f);
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.searchField.accessibilityIdentifier = EHIDashboardSearchInputKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardSearchViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title) : dest(self, .titleLabel.text),
        source(model.placeholder) : dest(self, .searchField.placeholder),
    });
}

# pragma mark - Actions

- (IBAction)didTapNearbyLocationButton:(EHITextField *)sender
{
    [self.viewModel searchNearby];
}

- (IBAction)didTapScrollButton:(UIButton *)button
{
    [self ehi_performAction:@selector(searchCellDidTapScrollButton:) withSender:button];
}

# pragma mark - UITextFieldDelegate

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField
{
    [self.viewModel searchLocations];
    
    return NO;
}

# pragma mark - Border

- (void)setBorderOpacity:(CGFloat)opacity
{
    self.borderView.alpha = opacity;
}

# pragma mark - EHILayoutable

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 149.0f };
    return metrics;
}

@end
