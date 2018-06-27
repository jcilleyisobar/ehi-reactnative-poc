//
//  EHIDeliveryCollectionCell.m
//  Enterprise
//
//  Created by Alex Koller on 6/4/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIDeliveryCollectionCell.h"
#import "EHIDeliveryCollectionCellViewModel.h"
#import "EHIReservationViewStyle.h"
#import "EHIButton.h"
#import "EHILabel.h"

@interface EHIDeliveryCollectionCell ()
@property (strong, nonatomic) EHIDeliveryCollectionCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIView *contentContainer;
@property (weak  , nonatomic) IBOutlet UIView  *detailsContainer;
@property (weak  , nonatomic) IBOutlet UIView  *titleContainer;
@property (weak  , nonatomic) IBOutlet UILabel *titleLabel;
@property (weak  , nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *addressDetailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *phoneDetailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *commentDetailsLabel;
@property (weak  , nonatomic) IBOutlet UILabel *otherInfoLabel;
@property (weak  , nonatomic) IBOutlet UIView *accessoryContainer;
@property (weak  , nonatomic) IBOutlet UIImageView *accessoryImageView;
@property (weak  , nonatomic) IBOutlet UIView *buttonContainer;
@property (weak  , nonatomic) IBOutlet EHIButton *actionButton;
@property (weak  , nonatomic) IBOutlet UIView *topDivider;
@property (assign, nonatomic) BOOL isReview;
@end

@implementation EHIDeliveryCollectionCell

- (void)updateWithModel:(id)model metrics:(EHILayoutMetrics *)metrics
{
    [super updateWithModel:model metrics:metrics];
    
    self.isReview = metrics.tag == EHIReservationViewStyleReview;

    self.accessoryImageView.hidden = !self.isReview;
    
    MASLayoutPriority priority = !self.isReview ? MASLayoutPriorityRequired : MASLayoutPriorityDefaultLow;
    
    [self.titleContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0f).priority(priority);
    }];
}

-(void)awakeFromNib
{
    [super awakeFromNib];
    
    self.addressDetailsLabel.copyable = YES;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIDeliveryCollectionCellViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(invalidateViewType:)];
    [MTRReactor autorun:self action:@selector(invalidateCommentVisibility:)];
    [MTRReactor autorun:self action:@selector(invalidateTopDivider:)];
    
    model.bind.map(@{
        source(model.title)               : dest(self, .titleLabel.text),
        source(model.subtitle)            : dest(self, .subtitleLabel.text),
        source(model.addressDetails)      : dest(self, .addressDetailsLabel.attributedText),
        source(model.phoneDetails)        : dest(self, .phoneDetailsLabel.text),
        source(model.otherInfoTitle)      : dest(self, .otherInfoLabel.text),
        source(model.buttonTitle)         : dest(self, .actionButton.ehi_title),
        source(model.accessoryImageName)  : dest(self, .accessoryImageView.ehi_imageName),
    });
}

- (void)invalidateViewType:(MTRComputation *)computation
{
    EHIDeliveryCollectionCellType type = self.viewModel.type;
    BOOL isSame        = type == EHIDeliveryCollectionCellTypeSame;
    BOOL isButton      = type == EHIDeliveryCollectionCellTypeButton;
    BOOL isUnsupported = type == EHIDeliveryCollectionCellTypeDeliveryUnsupported || type == EHIDeliveryCollectionCellTypeCollectionUnsupported;
    
    self.buttonContainer.hidden    = !isButton;
    self.contentContainer.hidden   = isButton;
    self.accessoryContainer.hidden = isButton;
    self.detailsContainer.hidden   = isButton || isSame || isUnsupported;
    self.otherInfoLabel.hidden     = isButton || !(isUnsupported || isSame);
}

- (void)invalidateCommentVisibility:(MTRComputation *)computation
{
    BOOL showsComment = self.viewModel.showsComment;
    
    self.commentDetailsLabel.attributedText = showsComment ? self.viewModel.commentDetails : nil;
}

- (void)invalidateTopDivider:(MTRComputation *)computation
{
    BOOL show = self.viewModel.showTitle && self.isReview;
    
    self.topDivider.hidden = !show;
}

# pragma mark - Actions

- (IBAction)didTapActionButton:(id)sender
{
    [self ehi_performAction:@selector(didTapActionButtonForDeliveryCollectionCell:) withSender:sender];
}

# pragma mark - Layout

- (CGSize)intrinsicContentSize
{
    CGRect bottomFrame = [self.bottomView convertRect:self.bottomView.bounds toView:self];
    
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetMaxY(bottomFrame) + EHILightPadding
    };
}

//
// Helper
//

- (UIView *)bottomView
{
    // simply show button to use delivery and collection
    if(self.viewModel.type == EHIDeliveryCollectionCellTypeButton) {
        return self.buttonContainer;
    }
    // if showing content, hide the comment labels if no comment exists
    if(self.viewModel.type != EHIDeliveryCollectionCellTypeSame
    && self.viewModel.type != EHIDeliveryCollectionCellTypeCollectionUnsupported
    && self.viewModel.type != EHIDeliveryCollectionCellTypeDeliveryUnsupported) {
        return self.viewModel.showsComment ? self.detailsContainer : self.phoneDetailsLabel;
    }
    // otherwise, show 'same as delivery' content
    else {
        return self.otherInfoLabel;
    }
}

@end
