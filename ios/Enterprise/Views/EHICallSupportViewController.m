//
//  EHICallSupportViewController.m
//  Enterprise
//
//  Created by mplace on 6/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHICallSupportViewController.h"
#import "EHICallSupportViewModel.h"
#import "EHIListCollectionView.h"
#import "EHICollectionButtonCell.h"

@interface EHICallSupportViewController ()
@property (strong, nonatomic) EHICallSupportViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet UIView *subtitleContainer;
@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@end

@implementation EHICallSupportViewController

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHICallSupportViewModel new];
    }
    
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    EHIListDataSourceSection *phone = self.collectionView.sections[EHICallSupportSectionsPhoneNumbers];
    phone.klass = [EHICollectionButtonCell class];
    phone.isDynamicallySized = YES;
}

- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

# pragma mark - Reactions

- (void)registerReactions:(EHICallSupportViewModel *)model
{
    [super registerReactions:model];
    
    EHIListDataSourceSection *phone = self.collectionView.sections[EHICallSupportSectionsPhoneNumbers];
    
    model.bind.map(@{
        source(model.title)    : dest(self, .titleLabel.text),
        source(model.subtitle) : dest(self, .subtitleLabel.text),
        source(model.models)   : dest(phone, .models)
    });
    
    [MTRReactor autorun:self action:@selector(updateSubtitleView:)];
}

- (void)updateSubtitleView:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.subtitle.length ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.subtitleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

- (void)willBecomeReady
{
    [super willBecomeReady];
    
    [self.view setNeedsUpdateConstraints];
    [self.view layoutIfNeeded];
    
    [self.view mas_updateConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(self.preferredContentSize.height)).with.priorityMedium();
    }];
}

# pragma mark - Actions

- (IBAction)didTapDismissButton:(id)sender
{
    [self.viewModel dismiss];
}

# pragma mark - Layout

- (CGSize)preferredContentSize
{
    CGSize result  = self.view.bounds.size;
    
    result.height -= self.collectionView.bounds.size.height;
    result.height += self.collectionView.contentSize.height;
    
    return result;
}

# pragma mark - EHIViewController

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenCallSupport;
}

@end
