//
//  EHIClassSelectFilterPickerCellViewModel.m
//  Enterprise
//
//  Created by mplace on 4/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIClassSelectFilterPickerCellViewModel.h"
#import "EHIFilters.h"

@interface EHIClassSelectFilterPickerCellViewModel ()
@property (strong, nonatomic) EHIFilters *filter;
@end

@implementation EHIClassSelectFilterPickerCellViewModel

- (void)updateWithModel:(EHIFilters *)model
{
    [super updateWithModel:model];
    
    if([model isKindOfClass:[EHIFilters class]]) {
        self.filter = model;
    }
}

# pragma mark - Actions

- (void)selectFilterValueAtIndex:(NSInteger)index
{
    EHIFilter *selectedFilter = self.filter.possibleFilters[index];
    self.filterValueTitle = selectedFilter.title;
    self.filter.currentFilter = selectedFilter;
}

# pragma mark - UIPickerViewDelegate

- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return self.filter.possibleFilters.count;
}

- (void)pickerView:(UIPickerView *)pickerView didSelectRow:(NSInteger)row inComponent:(NSInteger)component
{
    [self selectFilterValueAtIndex:row];
}

# pragma mark - UIPickerViewDataSource

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    EHIFilter *selectedFilter = self.filter.possibleFilters[row];
    return selectedFilter.title;
}

# pragma mark - Setters

- (void)setFilter:(EHIFilters *)filter
{
    _filter = filter;
    
    self.title = filter.title;
    self.filterValueTitle = filter.currentFilter.title;
}

@end
