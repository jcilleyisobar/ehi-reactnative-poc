//
//  EHIItineraryPickupLocationView.m
//  Enterprise
//
//  Created by Michael Place on 3/10/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIItineraryPickupLocationView.h"
#import "EHIItineraryPickupLocationViewModel.h"
#import "EHIRestorableConstraint.h"
#import "EHILabel.h"

@interface EHIItineraryPickupLocationView ()
@property (strong, nonatomic) EHIItineraryPickupLocationViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIImageView *iconImageView;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *iconContainerWidthConstraint;
@property (weak  , nonatomic) IBOutlet UIView *lockIconContainer;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;

@end

@implementation EHIItineraryPickupLocationView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        // Initialize view model
        self.viewModel = [EHIItineraryPickupLocationViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.iconImageView.tintColor = [UIColor blackColor];
}

# pragma mark - Accessibility

- (void)registerAccessibilityIdentifiers
{
    [super registerAccessibilityIdentifiers];
    
    self.accessibilityIdentifier = EHIItinerarySelectPickupDateKey;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIItineraryPickupLocationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateLockIcon:)];
    [MTRReactor autorun:self action:@selector(invalidateIconImage:)];
    
    model.bind.map(@{
        source(model.title)          : dest(self, .titleLabel.text),
        source(model.shouldHideIcon) : dest(self, .iconContainerWidthConstraint.isDisabled),
    });
}

- (void)invalidateLockIcon:(MTRComputation *)computation
{
    BOOL shouldHideLockIcon = self.viewModel.shouldHideLockIcon;
    
    MASLayoutPriority priority = shouldHideLockIcon ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    [self.lockIconContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.width.equalTo(@0.0).priority(priority);
    }];
}

- (void)invalidateIconImage:(MTRComputation *)computation
{
    // render as template so we can tint it
    UIImage *iconImage = [[UIImage imageNamed:self.viewModel.iconImageName] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    self.iconImageView.image = iconImage;
}

# pragma mark - Actions

- (IBAction)didTapPickupLocationView:(id)sender
{
    // let the view model handle the navigation
    [self.viewModel searchForPickupLocation];
}

@end
