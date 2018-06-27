//
//  EHIPromotionDetailsImageCell.m
//  Enterprise
//
//  Created by Rafael Ramos on 3/28/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

#import "EHIPromotionDetailsImageCell.h"
#import "EHIPromotionDetailsImageCellViewModel.h"

@interface EHIPromotionDetailsImageCell ()
@property (strong, nonatomic) EHIPromotionDetailsImageCellViewModel *viewModel;
@property (weak  , nonatomic) IBOutlet UIImageView *promotionImageView;
@end

@implementation EHIPromotionDetailsImageCell

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIPromotionDetailsImageCellViewModel new];
    }

    return self;
}

# pragma mark - Reactions

- (void)registerReactions:(EHIPromotionDetailsImageCellViewModel *)model
{
    [super registerReactions:model];

    [MTRReactor autorun:self action:@selector(invalidateImage:)];
}

- (void)invalidateImage:(MTRComputation *)computation
{
    NSString *imageName = self.viewModel.imageName;
    
    self.promotionImageView.image = imageName ? [UIImage imageNamed:imageName] : nil;
}

# pragma mark - Layout

+ (EHILayoutMetrics *)defaultMetrics
{
    EHILayoutMetrics *metrics = [super defaultMetrics];
    metrics.fixedSize = (CGSize) { .width = EHILayoutValueNil, .height = 157.f };
    
    return metrics;
}

@end
