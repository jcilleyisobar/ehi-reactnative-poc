//
//  EHISurveySession.h
//  Enterprise
//
//  Created by Rafael Ramos on 12/9/16.
//  Copyright © 2016 Enterprise. All rights reserved.
//

@interface EHISurveySession : NSObject

- (id)objectForKeyedSubscript:(id)key;
- (void)setObject:(id)obj forKeyedSubscript:(NSString *)key;

- (NSDictionary *)decodeSession;

@end
