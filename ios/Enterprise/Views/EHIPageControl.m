//
//  EHIPageControl.m
//  Enterprise
//
//  Created by Ty Cobb on 3/6/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIPageControl.h"
#import "EHIPageControlDot.h"

@interface EHIPageControl ()
@property (strong, nonatomic) MASConstraint *trailingConstraint;
@property (strong, nonatomic) UIView *dotsContainer;
@property (nonatomic, readonly) NSArray *dots;
@end

@implementation EHIPageControl

- (id)initWithCoder:(NSCoder *)decoder
{
    if(self = [super initWithCoder:decoder]) {
        _hidesForSinglePage = YES;
    }
    
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];

    [self setDotsContainer:[[UIView alloc] initWithFrame:self.bounds]];
    [self insertDotsContainer:self.dotsContainer];
}

# pragma mark - Control Updates

- (void)setNumberOfPages:(NSInteger)numberOfPages
{
    NSInteger delta = numberOfPages - _numberOfPages;
    if(delta == 0) {
        return;
    }
    
    _numberOfPages = numberOfPages;
    
    // hide if necessary
    self.hidden = self.hidesForSinglePage && self.numberOfPages < 2;
    
    // remove the last delta dots if neccessary, otherwise apppend dots
    if(delta < 0) {
        [self.dots.last(labs(delta)) makeObjectsPerformSelector:@selector(removeFromSuperview)];
    } else {
        [self appendDotsWithCount:delta];
    }
    
    // add the trailing constraint to the last dot
    self.trailingConstraint = [self applyTrailingConstraintToDot:self.dots.lastObject];
   
    // and re-highlight the current page
    [self highlightPageAtIndex:self.currentPage animated:YES];
}

- (void)setCurrentPage:(NSInteger)currentPage
{
    // filter page number to something that's valid
    currentPage = EHIClamp(currentPage, 0, self.numberOfPages);

    if(_currentPage != currentPage) {
        _currentPage = currentPage;
        [self highlightPageAtIndex:currentPage animated:YES];
    }
}

//
// Helpers
//

- (void)highlightPageAtIndex:(NSInteger)highlightedPage animated:(BOOL)animated
{
    UIView.animate(animated).duration(0.1).transform(^{
        NSInteger index = 0;
        for(EHIPageControlDot *dot in self.dots) {
            dot.isHighlighted = index++ == highlightedPage;
        }
    }).start(nil);
}

# pragma mark - Style Updates

- (void)setAutomaticallyResizesToFitWidth:(BOOL)automaticallyResizesToFitWidth
{
    if(_automaticallyResizesToFitWidth == automaticallyResizesToFitWidth) {
        return;
    }
    
    _automaticallyResizesToFitWidth = automaticallyResizesToFitWidth;
    
    [self.dotsContainer mas_remakeConstraints:^(MASConstraintMaker *make) {
        if(automaticallyResizesToFitWidth) {
            make.edges.equalTo(self).with.insets(UIEdgeInsetsZero);
        }
    }];
}

# pragma mark - Layout Helpers

- (void)insertDotsContainer:(UIView *)container
{
    [self addSubview:container];
    
    [container mas_makeConstraints:^(MASConstraintMaker *make) {
        make.center.equalTo(self);
        make.height.equalTo(self.mas_height);
    }];
}

- (void)appendDotsWithCount:(NSInteger)count
{
    [self.trailingConstraint uninstall];
    
    for(int index=0 ; index<count ; index++) {
        // create the new dot
        EHIPageControlDot *dot = [EHIPageControlDot new];
        EHIPageControlDot *previousDot = self.dots.lastObject;
      
        // add it into the view
        [self.dotsContainer addSubview:dot];
        [dot mas_makeConstraints:^(MASConstraintMaker *make) {
            // center vertically
            make.centerY.equalTo(@0.0f);
            
            // if theres a previous dot, add horizontal spacing, otherwise add a trailing constraint
            if(previousDot) {
                make.left.equalTo(previousDot.mas_right).with.offset(5.0f);
            } else {
                make.leading.equalTo(@0.0f);
            }
        }];
    }
}

- (MASConstraint *)applyTrailingConstraintToDot:(EHIPageControlDot *)dot;
{
    __block MASConstraint *constraint;
    
    [dot mas_makeConstraints:^(MASConstraintMaker *make) {
        constraint = make.trailing.equalTo(@0.0f);
    }];
    
    return constraint;
}

# pragma mark - Accessors

- (NSArray *)dots
{
    return self.dotsContainer.subviews;
}

@end
