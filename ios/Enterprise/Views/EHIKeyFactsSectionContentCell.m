
//
//  EHIKeyFactsSectionContentCell.m
//  Enterprise
//
//  Created by fhu on 11/4/15.
//  Copyright © 2015 Enterprise. All rights reserved.
//

#import "EHIKeyFactsSectionContentCell.h"
#import "EHIKeyFactsSectionContentViewModel.h"
#import "EHIKeyFactsContentCell.h"
#import "EHIKeyFactsContentViewModel.h"
#import "EHIListCollectionView.h"
#import "EHIRestorableConstraint.h"

@interface EHIKeyFactsSectionContentCell() <EHIListCollectionViewDelegate>

@property (strong, nonatomic) EHIKeyFactsSectionContentViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet EHIListCollectionView *collectionView;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *topDividerHeight;
@property (weak  , nonatomic) IBOutlet EHIRestorableConstraint *bottomDividerHeight;
@property (weak  , nonatomic) IBOutlet UILabel *headerLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subHeaderLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subHeaderDetailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *contentLabel;
@property (weak  , nonatomic) IBOutlet UIImageView *arrowImage;
@property (weak  , nonatomic) IBOutlet UIView *mainContainer;
@property (weak  , nonatomic) IBOutlet UIView *headerContainer;
@property (weak  , nonatomic) IBOutlet UIView *subHeaderContainer;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;

@end

@implementation EHIKeyFactsSectionContentCell

- (void)updateWithModel:(EHIKeyFactsSectionContentViewModel *)model metrics:(EHILayoutMetrics *)metrics
{
    [self.collectionView prepareForReuse];

    [super updateWithModel:model metrics:metrics];
    
    EHIListDataSourceSection *content = self.collectionView.section;
    content.klass = EHIKeyFactsContentCell.class;
    content.isDynamicallySized = YES;
    content.models = model.contentList;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIKeyFactsSectionContentViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.hidesTopThickDivider)      : dest(self, .topDividerHeight.isDisabled),
        source(model.hidesBottomThickDivider)   : dest(self, .bottomDividerHeight.isDisabled),
        source(model.headerText)                : dest(self, .headerLabel.text),
        source(model.subHeaderText)             : dest(self, .subHeaderLabel.text),
        source(model.subHeaderDetailsText)      : dest(self, .subHeaderDetailsLabel.text),
        source(model.contentAttributedText)     : dest(self, .contentLabel.attributedText),
    });
    
    [MTRReactor autorun:self action:@selector(updateContent:)];
    [MTRReactor autorun:self action:@selector(invalidateExpansion:)];
}

- (void)invalidateExpansion:(MTRComputation *)computation
{
    CGFloat angle = self.viewModel.isSelected ? M_PI : 0;
    
    UIView.animate(!computation.isFirstRun).duration(0.3).transform(^{
        self.arrowImage.layer.transform = CATransform3DMakeRotation(angle, 0.0, 0.0, 1.0);
    }).start(nil);
}

- (void)updateContent:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.headerText ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.headerContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.subHeaderText ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.subHeaderContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
    
    constraintPriority = self.viewModel.contentAttributedText.length ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.contentContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

# pragma mark - Actions

- (IBAction)didTapHeader:(id)sender
{
    self.viewModel.isSelected = !self.viewModel.isSelected;
    
    [self ehi_performAction:@selector(didTapSectionContentHeader:) withSender:self];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    BOOL showAllContent = self.viewModel.isSelected || !self.headerLabel.text.length;
    UIView *bottomView  = showAllContent ? self.mainContainer : self.headerContainer;
    
    CGFloat padding = showAllContent ? 0 : self.bottomDividerHeight.constant;
    CGFloat height  = CGRectGetMaxY(bottomView.frame) + padding;
    
    // if showing collection view, ignore it's frame and size based on content
    if(showAllContent) {
        height += self.collectionView.contentSize.height - self.collectionView.frame.size.height;
    }
    
    return (CGSize){
        .width = EHILayoutValueNil,
        .height = height
    };
}

@end
