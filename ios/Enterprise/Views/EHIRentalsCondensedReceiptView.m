//
//  EHIRentalsCondensedReceiptView.m
//  Enterprise
//
//  Created by cgross on 7/19/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIRentalsCondensedReceiptView.h"
#import "EHIRentalsCondensedReceiptViewModel.h"
#import "EHILabel.h"
#import "EHIToastManager.h"

@interface EHIRentalsCondensedReceiptView ()
@property (strong, nonatomic) EHIRentalsCondensedReceiptViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIView *contentContainer;

@property (weak, nonatomic) IBOutlet EHILabel *rentalAgreementNumberLabel;
@property (weak, nonatomic) IBOutlet UIView *contractContainer;
@property (weak, nonatomic) IBOutlet EHILabel *contractLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupDateLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupLocationLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pickupCityLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnDateLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnLocationLabel;
@property (weak, nonatomic) IBOutlet EHILabel *returnCityLabel;
@property (weak, nonatomic) IBOutlet EHILabel *totalTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *totalLabel;
@property (weak, nonatomic) IBOutlet UIView *pointsContainer;
@property (weak, nonatomic) IBOutlet EHILabel *pointsTitleLabel;
@property (weak, nonatomic) IBOutlet EHILabel *pointsLabel;
@end

@implementation EHIRentalsCondensedReceiptView

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        self.viewModel = [EHIRentalsCondensedReceiptViewModel new];
    }
    
    return self;
}

+ (void)captureReceiptWithRental:(EHIUserRental *)rental
{
    // create instance
    EHIRentalsCondensedReceiptView *view = [EHIRentalsCondensedReceiptView ehi_instanceFromNib];
    [view updateWithModel:rental];
    
    // resize
    UIView *container = [UIApplication sharedApplication].keyWindow;
    [view resizeDynamicallyForContainer:container.bounds.size model:rental];
    
    // add
    [container insertSubview:view atIndex:0];
    [view mas_makeConstraints:^(MASConstraintMaker *make) {
        make.metrics([view.class metrics]);
    }];
    
    // capture
    BOOL resizeToContent = NO;
    UIView *contentView = resizeToContent ? view.contentContainer : view;
    UIGraphicsBeginImageContextWithOptions(contentView.bounds.size, contentView.opaque, 0.0f);
    [contentView drawViewHierarchyInRect:contentView.bounds afterScreenUpdates:YES];
    UIImage *snapshotImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    
    // save
    [snapshotImage saveImageToPhotoLibraryWithHandler:^(BOOL success, NSError *error) {
        if (success) {
            dispatch_async(dispatch_get_main_queue(), ^{
                [EHIToastManager showMessage:EHILocalizedString(@"invoice_saved_photo", @"A copy of your trip summary has been saved to your photos.", @"")];
            });
        }
    }];
    
    // remove
    [view removeFromSuperview];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIRentalsCondensedReceiptViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateContract:)];
    [MTRReactor autorun:self action:@selector(invalidatePoints:)];
    
    model.bind.map(@{
                     source(model.rentalAgreementNumber) : dest(self, .rentalAgreementNumberLabel.text),
                     source(model.contract)         : dest(self, .contractLabel.text),

                     source(model.pickupTitle)      : dest(self, .pickupTitleLabel.text),
                     source(model.pickupDate)       : dest(self, .pickupDateLabel.text),
                     source(model.pickupLocation)   : dest(self, .pickupLocationLabel.text),
                     source(model.pickupCity)       : dest(self, .pickupCityLabel.text),
                     source(model.returnTitle)      : dest(self, .returnTitleLabel.text),
                     source(model.returnDate)       : dest(self, .returnDateLabel.text),
                     source(model.returnLocation)   : dest(self, .returnLocationLabel.text),
                     source(model.returnCity)       : dest(self, .returnCityLabel.text),
                     source(model.totalTitle)       : dest(self, .totalTitleLabel.text),
                     source(model.totalPrice)       : dest(self, .totalLabel.text),
                     source(model.pointsTitle)      : dest(self, .pointsTitleLabel.text),
                     source(model.points)           : dest(self, .pointsLabel.text),
                    });
}

- (void)invalidateContract:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.showContract ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.contractContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}


- (void)invalidatePoints:(MTRComputation *)computation
{
    MASLayoutPriority constraintPriority = self.viewModel.showPoints ? MASLayoutPriorityDefaultLow : MASLayoutPriorityRequired;
    
    [self.pointsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        make.height.equalTo(@(0.0f)).with.priority(constraintPriority);
    }];
}

@end
