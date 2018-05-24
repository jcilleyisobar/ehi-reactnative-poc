import { Types } from "../actions/location";

export const location = (state = { isFetching: false, result: null, message: null }, action) => {
    switch(action.type) {
        case Types.LOCATION_FETCH_STARTED: {
            return {
                ...state,
                isFetching: true,
                result: null,
                message: null
            }
        }
        case Types.LOCATION_FETCH_COMPLETE: {
            return {
                ...state,
                isFetching: false,
                result: action.result,
                message: null
            }
        }
        case Types.LOCATION_FETCH_FAILED: {
            return {
                ...state,
                isFetching: false,
                result: null,
                message: action.error
            }
        }
        default:
            return state
    }
}