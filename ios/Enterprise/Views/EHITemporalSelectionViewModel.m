//
//  EHITemporalSelectionViewModel.m
//  Enterprise
//
//  Created by Rafael Ramos on 03/05/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHITemporalSelectionViewModel.h"

@interface EHITemporalSelectionViewModel ()
@property (copy  , nonatomic) NSAttributedString *valueString;
@property (assign, nonatomic) EHITemporalSelectionType type;
@property (assign, nonatomic) EHITemporalSelectionLayout layout;
@property (strong, nonatomic) NSParagraphStyle *paragraphStyle;
@end

@implementation EHITemporalSelectionViewModel

- (instancetype)initWithConfig:(EHITemporalSelectionConfig)config
{
    if(self = [super init]) {
        _layout = config.layout;
        _type   = config.type;
        _valueString = self.attributedEmptyValue;
    }
    
    return self;
}

- (void)updateWithModel:(id)model
{
    [super updateWithModel:model];
    
    self.valueString = self.currentValueString;
}

# pragma mark - Actions

- (void)didTapClearValue
{
    self.value = nil;
}

# pragma mark - Accessors

- (void)setValue:(NSDate *)value
{
    _value = value;
    
    self.valueString = self.currentValueString;
}

- (NSAttributedString *)currentValueString
{
    return self.value ? self.attributedValue : self.attributedEmptyValue;
}

- (BOOL)hideClear
{
    return self.layout == EHITemporalSelectionLayoutMap
        || self.value == nil;
}

- (BOOL)hasValue
{
    return self.value != nil;
}

//
// Helpers
//

- (NSAttributedString *)attributedEmptyValue
{
    NSString *text;
    switch(self.type) {
        case EHITemporalSelectionTypeDate:
            text = self.emptyDate;
            break;
        case EHITemporalSelectionTypeTime:
            text = self.emptyTime;
            break;
    }
    
    return EHIAttributedStringBuilder.new
        .appendText(text)
        .paragraphStyle(self.paragraphStyle)
        .color([UIColor ehi_greenColor])
        .fontStyle(self.emptyFontStyle, 15.f)
        .string;
}

- (EHIFontStyle)emptyFontStyle
{
    return self.layout == EHITemporalSelectionLayoutMap ? EHIFontStyleRegular : EHIFontStyleBold;
}

- (NSTextAlignment)textAlignment
{
    return self.layout == EHITemporalSelectionLayoutMap ? NSTextAlignmentLeft : NSTextAlignmentCenter;
}

- (NSString *)emptyDate
{
    return EHILocalizedString(@"locations_map_any_day_label", @"ANY DAY", @"");
}

- (NSString *)emptyTime
{
    return EHILocalizedString(@"locations_map_any_time_label", @"ANY TIME", @"");
}

- (NSAttributedString *)attributedValue
{
    NSString *text;
    switch(self.type) {
        case EHITemporalSelectionTypeDate:
            text = self.formattedDate;
            break;
        case EHITemporalSelectionTypeTime:
            text = self.formattedTime;
            break;
    }
    
    return EHIAttributedStringBuilder.new
        .appendText(text)
        .paragraphStyle(self.paragraphStyle)
        .color([UIColor ehi_greenColor])
        .fontStyle(self.dataFontStyle, self.dataFontSize)
        .string;
}

- (NSParagraphStyle *)paragraphStyle
{
    NSMutableParagraphStyle *paragraphStyle = [NSMutableParagraphStyle new];
    [paragraphStyle setLineBreakMode:NSLineBreakByTruncatingTail];
    [paragraphStyle setAlignment:self.textAlignment];
    
    return paragraphStyle;
}

- (EHIFontStyle)dataFontStyle
{
    return self.layout == EHITemporalSelectionLayoutMap ? EHIFontStyleRegular : EHIFontStyleLight;
}

- (CGFloat)dataFontSize
{
    return self.layout == EHITemporalSelectionLayoutMap ? 16.0f : 20.0f;
}

- (NSString *)formattedDate
{
    return [self.value ehi_stringForTemplate:@"MMM d"];
}

- (NSString *)formattedTime
{
    return [self.value ehi_localizedTimeString];
}

@end
