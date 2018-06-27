//
//  EHIDashboardLoyaltyCell.m
//  Enterprise
//
//  Created by mplace on 5/18/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDashboardLoyaltyHeader.h"
#import "EHIDashboardLoyaltyViewModel.h"
#import "EHILabel.h"
#import "EHIButton.h"

@interface EHIDashboardLoyaltyHeader ()
@property (strong, nonatomic) EHIDashboardLoyaltyViewModel *viewModel;
@property (strong, nonatomic) UIColor *patternColor;
// unauthenticated view
@property (weak  , nonatomic) IBOutlet UIView *unauthenticatedContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet EHIButton *signInButton;
// authenticated view
@property (weak  , nonatomic) IBOutlet UIView *authenticatedContainer;
@property (weak  , nonatomic) IBOutlet EHILabel *greetingTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *greetingSubtitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *pointsTitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *pointsSubtitleLabel;
@end

@implementation EHIDashboardLoyaltyHeader

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDashboardLoyaltyViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // tile the background
    UIImage *tileImage = [UIImage imageNamed:@"eplus_tilepattern"];
    self.patternColor  = [UIColor colorWithPatternImage:tileImage];
}

- (void)applyLayoutAttributes:(UICollectionViewLayoutAttributes *)layoutAttributes
{
    [super applyLayoutAttributes:layoutAttributes];
    
    // the refresh control needs a 1px peek so that flow layout actually draws is, which
    // results the in 1px check here to check our top value
    
    // hide the pattern background until we start scrolling into the content
    self.unauthenticatedContainer.backgroundColor = layoutAttributes.frame.origin.y > 1.0f ? self.patternColor : [UIColor clearColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDashboardLoyaltyViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.title)             : dest(self, .titleLabel.text),
        source(model.signInButtonTitle) : dest(self, .signInButton.ehi_title),
        source(model.greetingTitle)     : dest(self, .greetingTitleLabel.text),
        source(model.greetingSubtitle)  : dest(self, .greetingSubtitleLabel.text),
        source(model.pointsTitle)       : dest(self, .pointsTitleLabel.text),
        source(model.pointsSubtitle)    : dest(self, .pointsSubtitleLabel.text),
        source(model.isAuthenticated)   : ^(NSNumber *isAuthenticated) {
            self.unauthenticatedContainer.hidden = isAuthenticated.boolValue;
            self.authenticatedContainer.hidden   = !isAuthenticated.boolValue;
        }
    });
}

# pragma mark - Actions

- (IBAction)didTapSignInButton:(id)sender
{
    [self.viewModel presentSignIn];
}

- (IBAction)didTapProfileName:(UIControl *)sender
{
    [self.viewModel pushProfile];
}

- (IBAction)didTapPoints:(UIControl *)sender
{
    [self.viewModel pushRewards];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize){ .width = EHILayoutValueNil, .height = 64.0f };
    return metrics;
}

@end
