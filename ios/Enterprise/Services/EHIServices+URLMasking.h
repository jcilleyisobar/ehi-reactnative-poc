//
//  EHIServices+URLMasking.h
//  Enterprise
//
//  Created by Rafael Ramos on 24/07/17.
//  Copyright © 2017 Enterprise. All rights reserved.
//

#import "EHIServices.h"

@interface EHIServices (URLMasking)
- (NSString *)maskURL:(NSURL *)url;
@end
