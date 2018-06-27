//
//  EHIListMergeFacilitator.m
//  Enterprise
//
//  Created by Ty Cobb on 1/22/15.
//  Copyright (c) 2015 Enterprise. All rights reserved.
//

#import "EHIListMergeFacilitator.h"
#import "EHIComparable.h"

typedef struct { int send, receive; } EHIMergeUpdate;
typedef struct { EHIMergeUpdate *updates; size_t size; } EHIMergeUpdateBuffer;

#if EHIMergeFacilitatorLogging
#define ehi_facilitator_log(...) printf(__VA_ARGS__)
#else
#define ehi_facilitator_log(...)
#endif

typedef NS_ENUM(int, EHIMergeUpdateType) {
    EHIMergeUpdateTypeNone   = -1,
    EHIMergeUpdateTypeStick  = -2,
    EHIMergeUpdateTypeDelete = -3,
    EHIMergeUpdateTypeInsert = -4,
};

@implementation EHIListMergeFaciliator

# pragma mark - Update Resolution

+ (void)resolveUpdates:(void *)updates inSection:(NSInteger)section againstCollectionView:(UICollectionView *)collectionView
{
    ehi_facilitator_log("--- EHIListMergeFacilitator: RESOLVING SECTION %d ---\n", (int)section);
    
    EHIMergeUpdateBuffer *buffer = (EHIMergeUpdateBuffer *)updates;
    
    NSMutableArray *indexPathsToInsert = [NSMutableArray new];
    NSMutableArray *indexPathsToDelete = [NSMutableArray new];
    
    for(int index=0 ; index<buffer->size ; index++) {
        EHIMergeUpdate update = buffer->updates[index];
        
        // if this index send a deletion, add a deletion
        if(update.send == EHIMergeUpdateTypeDelete) {
            [indexPathsToDelete addObject:[NSIndexPath indexPathForItem:index inSection:section]];
        }
        // if this index send a move, add an update from this index to the send
        else if(update.send > EHIMergeUpdateTypeNone) {
            [collectionView moveItemAtIndexPath:[NSIndexPath indexPathForItem:index inSection:section]
                                    toIndexPath:[NSIndexPath indexPathForItem:update.send inSection:section]];
        }
        
        // build up the list of inserts and delete
        switch(update.receive) {
            case EHIMergeUpdateTypeDelete:
                [indexPathsToDelete addObject:[NSIndexPath indexPathForItem:index inSection:section]]; break;
            case EHIMergeUpdateTypeInsert:
                [indexPathsToInsert addObject:[NSIndexPath indexPathForItem:index inSection:section]]; break;
            default: break;
        }
    }
    
    // update the inserted/deleted items
    [collectionView insertItemsAtIndexPaths:indexPathsToInsert];
    [collectionView deleteItemsAtIndexPaths:indexPathsToDelete];
}

# pragma mark - Update Generation

+ (void)processUpdates:(void *)updates fromModels:(NSArray *)sources toModels:(NSArray *)destinations
{
    // we want to ensure we have real objects to call against since we're going to be doing indexOf checks
    sources      = sources ?: @[];
    destinations = destinations ?: @[];
    
    // initialize the update values to EHIMergeUpdateTypeNone
    ehi_initializeUpdatesBuffer(updates);
    // process any indices which need to move first
    ehi_processIndexMoves(sources, destinations, updates);
    // ensure that we have record inserts and deletes where approrpriate after moves are resolved
    ehi_finalizeIndexUpdates(sources, destinations, updates);
    // log the completed updates if necessary
    ehi_logUpdates(sources, destinations, updates);
}

//
// Helpers
//

void ehi_initializeUpdatesBuffer(EHIMergeUpdateBuffer *buffer)
{
    for(int index=0 ; index<buffer->size ; index++) {
        buffer->updates[index].receive = EHIMergeUpdateTypeNone;
        buffer->updates[index].send    = EHIMergeUpdateTypeNone;
    }
}

void ehi_processIndexMoves(NSArray *sources, NSArray *destinations, EHIMergeUpdateBuffer *buffer)
{
    EHIMergeUpdate *updates = buffer->updates;
    
    int sourceCount      = (int)sources.count;
    int destinationCount = (int)destinations.count;
    
    // iterate through all the source items
    for(int index=0 ; index<sourceCount ; index++) {
        // grab each paired item
        id source      = sources[index];
        id destination = index < destinationCount ? destinations[index] : nil;
        
        // if the items are equal, then nothing is moving
        if([source isEqual:destination]) {
            updates[index].send    = EHIMergeUpdateTypeStick;
            updates[index].receive = EHIMergeUpdateTypeStick;
        }
        // otherwise, let's check where the source item is moving to, if anywhere
        else {
            int destinationIndex = (int)[destinations indexOfObject:sources[index]];
            // ensure we found a destination index
            BOOL shouldMove = destinationIndex != (int)NSNotFound;
            // and also ensure nobody has already moved to this index (in the case of duplicate items in the feed)
            shouldMove &= updates[destinationIndex].receive == EHIMergeUpdateTypeNone;
            
            if(shouldMove) {
                // the update sending this move should record the destination
                updates[index].send = destinationIndex;
                // the update receiving this move should simply accept the item
                updates[destinationIndex].receive = index;
            }
        }
    }
}

void ehi_finalizeIndexUpdates(NSArray *sources, NSArray *destinations, EHIMergeUpdateBuffer *buffer)
{
    EHIMergeUpdate *updates = buffer->updates;
    size_t updateCount = buffer->size;
    
    // interate through each update in the list, and if it receives or sends nothing, update it.
    for(int index=0 ; index<updateCount ; index++) {
        BOOL isWithinDestinations = index < destinations.count;
        BOOL isWithinSources      = index < sources.count;
        
        // if it sends nothing nothing then it's not moving anywhere. that means that this item is
        // being deleted from the sources.
        if(isWithinDestinations && isWithinSources && updates[index].send == EHIMergeUpdateTypeNone) {
            updates[index].send = EHIMergeUpdateTypeDelete;
        }
        
        if(updates[index].receive == EHIMergeUpdateTypeNone) {
            // if we're still within the destination range, then we're inserting an item
            if(isWithinDestinations) {
                updates[index].receive = EHIMergeUpdateTypeInsert;
            }
            // otherwise, this item is being truncated; if it hasn't already moved, then delete it
            else if(updates[index].send == EHIMergeUpdateTypeNone) {
                updates[index].send = EHIMergeUpdateTypeDelete;
            }
        }
    }
}

# pragma mark - Logging

void ehi_logUpdates(NSArray *sources, NSArray *destinations, EHIMergeUpdateBuffer *buffer)
{
#if EHIMergeFacilitatorLogging
    ehi_facilitator_log("--- EHIListMergeFacilitator: START MERGE BUFFER ---\n");
    
    for(int index = 0 ; index < buffer->size ; index++) {
        EHIMergeUpdate update = buffer->updates[index];
        id source      = index < sources.count ? sources[index] : nil;
        id destination = index < destinations.count ? destinations[index] : nil;
        
        ehi_facilitator_log("%2d -- %s -> %s | send: %-7s receive: %s\n", index,
            ehi_identifierFromData(source),
            ehi_identifierFromData(destination),
            ehi_stringFromUpdateType(update.send),
            ehi_stringFromUpdateType(update.receive)
        );
    }
    
    ehi_facilitator_log("--- EHIListMergeFacilitator: END MERGE BUFFER ---\n");
#endif
}

const char * ehi_identifierFromData(id<EHIComparable> data)
{
    // if we don't have data, use none
    if(!data) {
        return "none";
    }
    
    if(![data conformsToProtocol:@protocol(EHIComparable)]) {
        ehi_facilitator_log("error: %s does not conform to EHIComparable\n", [[data description] cStringUsingEncoding:NSUTF8StringEncoding]);
        return "none";
    }

    // TODO: obviously hacky, but it's for logging. would need to create a protocol like EHIMergable to make this legit.
    return [[data uid] cStringUsingEncoding:NSUTF8StringEncoding];
}

const char * ehi_stringFromUpdateType(EHIMergeUpdateType type)
{
    switch(type) {
        case EHIMergeUpdateTypeNone:
            return "NONE";
        case EHIMergeUpdateTypeStick:
            return "STICK";
        case EHIMergeUpdateTypeDelete:
            return "DELETE";
        case EHIMergeUpdateTypeInsert:
            return "INSERT";
        default:
            return [[NSString stringWithFormat:@"MOVE %d", (int)type] cStringUsingEncoding:NSUTF8StringEncoding];
    }
}

# pragma mark - Buffer

void * ehi_createUpdatesBuffer(NSUInteger count)
{
    EHIMergeUpdateBuffer *buffer = malloc(sizeof(EHIMergeUpdateBuffer));
    buffer->updates = calloc(count, sizeof(EHIMergeUpdate));
    buffer->size    = count;
    return buffer;
}

void ehi_deleteUpdatesBuffer(void *_buffer)
{
    EHIMergeUpdateBuffer *buffer = _buffer;
    free(buffer->updates);
    free(buffer);
}

@end