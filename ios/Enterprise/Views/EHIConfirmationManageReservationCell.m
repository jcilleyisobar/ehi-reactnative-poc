//
//  EHIConfirmationManageReservationCellCollectionViewCell.m
//  Enterprise
//
//  Created by Bruno Fernandes Campos on 9/14/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIConfirmationManageReservationCell.h"
#import "EHIConfirmationManageReservationViewModel.h"
#import "EHIActivityButton.h"
#import "EHIBorderedView.h"

@interface EHIConfirmationManageReservationCell()

@property (strong, nonatomic) EHIConfirmationManageReservationViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIView *actionsContainer;
@property (weak, nonatomic) IBOutlet UIImageView *arrowView;
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *subtitleLabel;
@property (weak, nonatomic) IBOutlet UIView *bottomView;
@property (weak, nonatomic) IBOutlet EHIBorderedView *toggleView;
@property (weak, nonatomic) IBOutlet EHIActivityButton *modifyButton;
@property (weak, nonatomic) IBOutlet EHIActivityButton *cancelButton;
@property (weak, nonatomic) IBOutlet EHIActivityButton *addToCalendarButton;

@end

@implementation EHIConfirmationManageReservationCell

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    [self initialize];
}

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIConfirmationManageReservationViewModel new];
    }
    return self;
}

- (void)initialize
{
    self.modifyButton.isDisabledWhileLoading = YES;
    self.modifyButton.indicatorType = EHIActivityIndicatorTypeGreen;
    
    self.cancelButton.isDisabledWhileLoading = YES;
    self.cancelButton.indicatorType = EHIActivityIndicatorTypeGreen;
    
    self.actionsContainer.layer.borderColor = [UIColor ehi_grayColor1].CGColor;
    self.actionsContainer.layer.borderWidth = 1.0f;
}

- (void)registerReactions:(EHIConfirmationManageReservationViewModel *)model
{
    [super registerReactions:model];
    
    [MTRReactor autorun:self action:@selector(expandCollapseToggle)];
    [MTRReactor autorun:self action:@selector(invalidateIsLoading:)];
    
    model.bind.map(@{
        source(model.title)               : dest(self, .titleLabel.text),
        source(model.modifyButtonTitle)   : dest(self, .modifyButton.ehi_title),
        source(model.cancelButtonTitle)   : dest(self, .cancelButton.ehi_title),
        source(model.calendarButtonTitle) : dest(self, .addToCalendarButton.ehi_title),
    });
}

- (void)invalidateIsLoading:(MTRComputation *)computation
{
    BOOL isLoadingModify = self.viewModel.isModifyLoading;
    BOOL isLoadingCancel = self.viewModel.isCancelationLoading;
    BOOL isLoading       = isLoadingModify || isLoadingCancel;
    
    self.modifyButton.isLoading = isLoadingModify;
    self.modifyButton.enabled   = !isLoading;
    
    self.cancelButton.isLoading = isLoadingCancel;
    self.cancelButton.enabled   = !isLoading;
    
    self.addToCalendarButton.enabled = !isLoading;
}

- (void)expandCollapseToggle
{
    [self rotateArrow];
    [self updateActionsContainerLayout];
}

- (void)rotateArrow
{
    CGFloat degrees = self.viewModel.shouldExpand ? 90 : 0;
    CGFloat radians = degrees * (M_PI/180.0f);
    UIView.animate(YES).duration(0.3).transform(^{
        self.arrowView.layer.transform = CATransform3DMakeRotation(radians, 0.0, 0.0, 1.0);
    }).start(nil);
}

- (void)updateActionsContainerLayout
{
    MASLayoutPriority priority = self.viewModel.shouldExpand ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    [self.actionsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).priority(priority);
    }];
    
    [self ehi_performAction:@selector(didExpandManageReservationCell:) withSender:self];
}

#pragma mark - AutoLayout

- (void)updateConstraints
{
    [self updateActionsContainerLayout];
    [super updateConstraints];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width  = EHILayoutValueNil,
        .height = CGRectGetMaxY(self.bottomView.frame)
    };
}

#pragma mark - Actions

- (IBAction)didTapCancelButton:(id)sender {
    [self.viewModel cancelReservation];
}

- (IBAction)didTapModifyButton:(id)sender {
    [self.viewModel modifyReservation];
}

- (IBAction)didTapAddToCalendarButton:(id)sender {
    [self.viewModel addToCalendar];
}

- (IBAction)didTapToggle:(id)sender {
    [self.viewModel toggleViewClicked];
}

@end
