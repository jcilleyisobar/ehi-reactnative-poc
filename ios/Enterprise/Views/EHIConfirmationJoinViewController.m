//
//  EHIConfirmationJoinViewController.m
//  Enterprise
//
//  Created by Rafael Ramos on 10/04/18.
//  Copyright © 2018 Enterprise. All rights reserved.
//

#import "EHIConfirmationJoinViewController.h"
#import "EHIConfirmationJoinViewModel.h"
#import "EHIButton.h"

@interface EHIConfirmationJoinViewController ()
@property (strong, nonatomic) EHIConfirmationJoinViewModel *viewModel;

@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@property (weak, nonatomic) IBOutlet UIImageView *clockIconImageView;
@property (weak, nonatomic) IBOutlet UILabel *messageLabel;
@property (weak, nonatomic) IBOutlet EHIButton *joinButton;
@property (weak, nonatomic) IBOutlet EHIButton *addToCalendarButton;
@property (weak, nonatomic) IBOutlet UILabel *closeMessageLabel;
@property (weak, nonatomic) IBOutlet EHIButton *closeButton;

@end

@implementation EHIConfirmationJoinViewController

- (instancetype)initWithCoder:(NSCoder *)aDecoder
{
    if(self = [super initWithCoder:aDecoder]) {
        self.viewModel = [EHIConfirmationJoinViewModel new];
    }

    return self;
}

- (void)updateWithAttributes:(NAVAttributes *)attributes
{
    [super updateWithAttributes:attributes];
    
    if(attributes.handler) {
        self.viewModel.handler = attributes.handler;
    }
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.joinButton.type = EHIButtonTypeSecondary;
    self.closeButton.titleLabel.numberOfLines = 0;
    self.closeButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    
    UIImage *clockImage = [[UIImage imageNamed:@"icon_clock-1"] imageWithRenderingMode:UIImageRenderingModeAlwaysTemplate];
    self.clockIconImageView.image     = clockImage;
    self.clockIconImageView.tintColor = [UIColor ehi_greenColor];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self.view setNeedsUpdateConstraints];
    [self.view layoutIfNeeded];
}

# pragma mark - Reactions

- (void)registerReactions:(EHIConfirmationJoinViewModel *)model
{
    [super registerReactions:model];
    
    model.bind.map(@{
        source(model.messageText)        : dest(self, .messageLabel.text),
        source(model.joinTitle)          : dest(self, .joinButton.ehi_title),
        source(model.addToCalendarTitle) : dest(self, .addToCalendarButton.ehi_title),
        source(model.closeDescription)   : dest(self, .closeMessageLabel.text),
        source(model.closeTitle)         : dest(self, .closeButton.ehi_title),
    });
}

# pragma mark - Actions

- (IBAction)didTapJoin:(UIButton *)sender
{
    [self.viewModel join];
}

- (IBAction)didTapAddToCalendar:(UIButton *)sender
{
    [self.viewModel addToCalendar];
}

- (IBAction)didTapClose:(UIButton *)sender
{
    [self.viewModel close];
}

# pragma mark - EHIViewController


- (UIColor *)backgroundColor
{
    return [UIColor whiteColor];
}

- (CGSize)preferredContentSize
{
    CGSize result = self.view.bounds.size;
    
    // replace the scroll view height with its content height
    result.height -= self.scrollView.bounds.size.height;
    // add scroll view's content height
    result.height += self.scrollView.contentSize.height;
    
    return result;
}

- (EHIModalTransitionStyle)customModalTransitionStyle
{
    return EHIModalTransitionStyleOverlay;
}

# pragma mark - NAVViewController

+ (NSString *)screenName
{
    return EHIScreenConfirmationJoin;
}

@end
