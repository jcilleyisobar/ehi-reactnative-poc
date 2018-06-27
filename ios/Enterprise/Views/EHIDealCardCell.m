//
//  EHIDealCardCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealCardCell.h"
#import "EHIDealCardViewModel.h"
#import "EHINetworkImageView.h"
#import "EHIGradientView.h"
#import "EHIRestorableConstraint.h"

@interface EHIDealCardCell ()
@property (strong, nonatomic) EHIDealCardViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet EHINetworkImageView *imageView;
@property (weak  , nonatomic) IBOutlet EHIGradientView *gradientView;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;

@property (weak  , nonatomic) IBOutlet UIView *subtitleContainerView;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *termsLabel;

@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *dividerHeightConstraint;
@end

@implementation EHIDealCardCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIDealCardViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    self.gradientView.colors = @[UIColor.clearColor, UIColor.blackColor];
    self.gradientView.alpha  = 0.6f;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDealCardViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateSubtitleContainer:)];
    [MTRReactor autorun:self action:@selector(invalidateImage:)];
    
    model.bind.map(@{
        source(model.title)       : dest(self, .titleLabel.attributedText),
        source(model.subtitle)    : dest(self, .subtitleLabel.text),
        source(model.terms)       : dest(self, .termsLabel.text),
        source(model.hideDivider) : dest(self, .dividerHeightConstraint.isDisabled),
    });
}

- (void)invalidateSubtitleContainer:(MTRComputation *)computation
{
    BOOL show = self.viewModel.subtitle != nil;
    MASLayoutPriority priority = show ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.subtitleContainerView mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0).priority(priority);
    }];
}

- (void)invalidateImage:(MTRComputation *)computation
{
    [self.imageView prepareForReuse];
    
    EHIImage *imageModel = self.viewModel.imageModel;
    if(imageModel){
        self.imageView.imageModel = imageModel;
    }
    
    NSString *imageName  = self.viewModel.staticImageName;
    if(imageName) {
        self.imageView.image = [UIImage imageNamed:imageName];
    }
}

# pragma mark - Actions

- (IBAction)didTap:(UIControl *)sender
{
    [self.viewModel tapDeal];
}

# pragma mark - Layout

+ (EHILayoutMetrics *)metrics
{
    EHILayoutMetrics *metrics = [self.defaultMetrics copy];
    metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = 185 };
    
    return metrics;
}

@end
