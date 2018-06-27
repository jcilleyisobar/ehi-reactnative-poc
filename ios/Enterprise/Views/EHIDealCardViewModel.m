//
//  EHIDealCardViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 08/06/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIDealCardViewModel.h"

@interface EHIDealCardViewModel ()
@property (assign, nonatomic) EHIDealLayout layout;
@property (strong, nonatomic) id<EHIPromotionRenderable> renderable;
@end

@implementation EHIDealCardViewModel
@synthesize delegate;
+ (instancetype)modelWithRenderable:(id<EHIPromotionRenderable>)renderable layout:(EHIDealLayout)layout
{
    EHIDealCardViewModel *model = EHIDealCardViewModel.new;
    model.layout     = layout;
    model.renderable = renderable;
    
    return model;
}

# pragma mark - EHIDealActionTap

- (void)tapDeal
{
    [self.delegate show:self.renderable];
}

# pragma mark - Accessors

- (NSString *)terms
{
    return self.layout == EHIDealLayoutList
        ? EHILocalizedString(@"deals_termsapply", @"Terms Apply", @"")
        : nil;
}

- (BOOL)hideDivider
{
    return self.layout == EHIDealLayoutDetail;
}

- (void)setRenderable:(id<EHIPromotionRenderable>)renderable
{
    _renderable = renderable;
    _title      = [self titleForDeal:renderable];
    _subtitle   = [self subtitleForDeal:renderable];

    if([renderable respondsToSelector:@selector(imageModel)]){
        _imageModel = renderable.imageModel;
    }
    
    if([renderable respondsToSelector:@selector(imageName)]){
        _staticImageName = renderable.imageName;
    }
}

//
// Helpers
//

- (NSAttributedString *)titleForDeal:(id<EHIPromotionRenderable>)renderable
{
    return EHIAttributedStringBuilder.new
        .appendText(renderable.title)
        .fontStyle(EHIFontStyleBold, self.fontSize)
        .color(UIColor.whiteColor)
        .string;
}

- (NSString *)subtitleForDeal:(id<EHIPromotionRenderable>)renderable
{
    return self.layout == EHIDealLayoutList
        ? renderable.subtitle
        : nil;
}

- (CGFloat)fontSize
{
    return self.layout == EHIDealLayoutList ? 18.f : 22.f;
}

@end
