//
//  EHIDialogModel.h
//  Enterprise
//
//  Created by Rafael Ramos on 1/25/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

typedef void (^EHIDialogModelHandler)(UIAlertAction *);

@interface EHIDialogModel : NSObject
@property (copy  , nonatomic) NSString *title;
@property (copy  , nonatomic) NSString *message;
@property (copy  , nonatomic) NSArray *actions;
@property (copy  , nonatomic) NSArray *buttonTitles;
@property (copy  , nonatomic) EHIDialogModelHandler actionHandler;
@property (assign, nonatomic) NSInteger cancelIndex;
@property (assign, nonatomic) BOOL secureInput;
@end
