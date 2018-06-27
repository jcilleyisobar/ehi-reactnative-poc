//
//  EHIRentalsPastRentalCell.m
//  Enterprise
//
//  Created by fhu on 4/16/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIRentalsPastRentalCell.h"
#import "EHIRentalsPastRentalViewModel.h"
#import "EHILabel.h"
#import "EHIActionButton.h"
#import "EHIRestorableConstraint.h"

@interface EHIRentalsPastRentalCell()
@property (strong, nonatomic) EHIRentalsPastRentalViewModel *viewModel;

@property (weak  , nonatomic) IBOutlet UIView *containerView;
@property (weak  , nonatomic) IBOutlet EHILabel *dateLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *locationLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *confirmationLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *vehicleLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *totalLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *creditCardNameLabel;
@property (weak  , nonatomic) IBOutlet EHILabel *creditCardNumberLabel;
@property (weak  , nonatomic) IBOutlet EHIActionButton *receiptButton;
@property (weak  , nonatomic) IBOutlet UIView *bottomContainer;

@end

@implementation EHIRentalsPastRentalCell

- (id)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIRentalsPastRentalViewModel new];
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    // hide the bottom container for now since we don't have all the informations
    [self.bottomContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@0.0).priority(MASLayoutPriorityRequired);
    }];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsPastRentalViewModel *)model
{
    [super registerReactions:model];

    model.bind.map(@{
        source(model.reservationTime)    : dest(self, .dateLabel.text),
        source(model.location)           : dest(self, .locationLabel.text),
        source(model.total)              : dest(self, .totalLabel.attributedText),
        source(model.receiptTitle)       : dest(self, .receiptButton.ehi_title)
    });
}

- (void)buildTabularContent
{
    [self assembleTabularContentForLabel:self.confirmationLabel usingText:self.viewModel.confirmationText];
    [self assembleTabularContentForLabel:self.vehicleLabel usingText:self.viewModel.vehicleText];
    
    NSAttributedString *totalText = EHIAttributedStringBuilder.new
        .appendText(self.viewModel.totalTitle)
        .appendText(@"\t")
        .append(self.viewModel.total)
        .string;
    
    [self assembleTabularContentForLabel:self.totalLabel usingAttributedText:totalText];
}

- (void)assembleTabularContentForLabel:(UILabel *)label usingText:(NSString *)text
{
    NSAttributedString *attributedText = EHIAttributedStringBuilder.new.appendText(text).string;
    [self assembleTabularContentForLabel:label usingAttributedText:attributedText];
}

- (void)assembleTabularContentForLabel:(UILabel *)label usingAttributedText:(NSAttributedString *)text
{
    NSMutableParagraphStyle *paragraph = NSMutableParagraphStyle.new;
    paragraph.alignment = NSTextAlignmentLeft;
    
    NSTextTab *tab = [[NSTextTab alloc] initWithTextAlignment:NSTextAlignmentRight
                                                     location:CGRectGetMaxX(self.confirmationLabel.frame)
                                                      options:@{}];
    paragraph.tabStops = @[tab];
    
    label.attributedText = EHIAttributedStringBuilder.new.append(text).paragraphStyle(paragraph).string;
}

#pragma mark - Actions

- (IBAction)pressedReceiptButton:(id)sender
{
    [self.viewModel displayInvoice];
}

# pragma mark - Layout

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    [self buildTabularContent];
}

- (CGSize)intrinsicContentSize
{
    return (CGSize) {
        .width = EHILayoutValueNil,
        .height = CGRectGetHeight(self.containerView.frame) + EHIMediumPadding
    };
}

@end
