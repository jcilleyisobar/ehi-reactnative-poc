export const Types = {
    LOCATION_FETCH: "LOCATION_FETCH",
    LOCATION_FETCH_STARTED: "LOCATION_FETCH_STARTED",
    LOCATION_FETCH_COMPLETE: "LOCATION_FETCH_COMPLETE",
    LOCATION_FETCH_FAILED: "LOCATION_FETCH_FAILED"
}

const fetchUserLocationFailed = (error) => {
    return {
        type: Types.LOCATION_FETCH_FAILED,
        error
    }
}

const fetchUserLocationComplete = (result) => {
    return {
        type: Types.LOCATION_FETCH_COMPLETE,
        result
    }
}

const fetchUserLocationStarted = () => {
    return {
        type: Types.LOCATION_FETCH_STARTED
    }
}

export const fetchUserLocation = () => (dispatch, getState) => {
    dispatch(fetchUserLocationStarted())
    global.fetch("https://ipinfo.io", {
        method: "GET",
        headers: {
            "accept": "application/json"
        }
    }).then( (response) => {
        if (response.status !== 200) {
            dispatch(fetchUserLocationFailed("An error occurred calling the location API"))
        }
        else {
            return response.json()
        }
    }).then( (json) => dispatch(fetchUserLocationComplete(json)))
    .catch( (error) => dispatch(fetchUserLocationFailed("An error occurred calling the location API")) )
}

