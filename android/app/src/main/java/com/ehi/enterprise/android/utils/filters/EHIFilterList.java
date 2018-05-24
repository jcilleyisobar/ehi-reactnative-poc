package com.ehi.enterprise.android.utils.filters;

import android.content.res.Resources;
import android.support.annotation.IntDef;
import android.util.SparseArray;

import com.ehi.enterprise.android.R;
import com.ehi.enterprise.android.models.location.solr.EHISolrLocation;
import com.ehi.enterprise.android.models.reservation.EHICarClassDetails;
import com.ehi.enterprise.android.models.reservation.EHICarFilter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class EHIFilterList {

	private static ArrayList<String> TRANSMISSIONS = null;
	private static ArrayList<String> PASSENGERS = null;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({LOC_FILTER_OPEN_24, LOC_FILTER_OPEN_SUNDAY, FILTER_NAN, LOC_FILTER_RAIL_STATION,
			LOC_FILTER_PORT_STATION, LOC_FILTER_PICK_YOU_UP_SERVICE, LOC_FILTER_TYPE_EXOTIC, LOC_FILTER_TYPE_AIRPORT,
			LOC_FILTER_TYPE_TRUCK_RENTALS, LOC_FILTER_TYPE_GENERAL_AVIATION_RENTALS, LOC_FILTER_TYPE_DELIVERY_AND_COLLECTION})
	public @interface EHILocationFilterTypes {
	}

	public static final int LOC_FILTER_OPEN_24 = 1;
	public static final int LOC_FILTER_OPEN_SUNDAY = 2;
	public static final int LOC_FILTER_RAIL_STATION = 3;
	public static final int LOC_FILTER_PORT_STATION = 4;
	public static final int LOC_FILTER_PICK_YOU_UP_SERVICE = 5;
	public static final int LOC_FILTER_TYPE_EXOTIC = 6;
	public static final int LOC_FILTER_TYPE_TRUCK_RENTALS = 7;
	public static final int LOC_FILTER_TYPE_GENERAL_AVIATION_RENTALS = 8;
	public static final int LOC_FILTER_TYPE_DELIVERY_AND_COLLECTION = 9;
	public static final int LOC_FILTER_TYPE_AIRPORT = 101;
	public static final int FILTER_NAN = 999;


	@Retention(RetentionPolicy.SOURCE)
	@IntDef({CAR_FILTER_CAR_TYPE_SUV, CAR_FILTER_CAR_TYPE_LUXURY, CAR_FILTER_CAR_TYPE_TRUCK, CAR_FILTER_CAR_TYPE_CARGO_VAN,
			CAR_FILTER_PASSENGER_CAPACITY_1, CAR_FILTER_PASSENGER_CAPACITY_2, CAR_FILTER_PASSENGER_CAPACITY_3, CAR_FILTER_PASSENGER_CAPACITY_4,
			CAR_FILTER_PASSENGER_CAPACITY_5, CAR_FILTER_PASSENGER_CAPACITY_6_or_more, CAR_FILTER_TRANSMISSION_MANUAL, CAR_FILTER_TRANSMISSION_AUTOMATIC,
			CAR_FILTER_CAR_TYPE_MINI_VAN})
	public @interface EHICarFilterTypes {
	}

	public static final int CAR_FILTER_TRANSMISSION_MANUAL = 10;
	public static final int CAR_FILTER_TRANSMISSION_AUTOMATIC = 11;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_1 = 30;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_2 = 31;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_3 = 32;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_4 = 33;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_5 = 34;
	public static final int CAR_FILTER_PASSENGER_CAPACITY_6_or_more = 35;
	public static final int CAR_FILTER_CAR_TYPE_CARGO_VAN = 12;
	public static final int CAR_FILTER_CAR_TYPE_SUV = 13;
	public static final int CAR_FILTER_CAR_TYPE_TRUCK = 14;
	public static final int CAR_FILTER_CAR_TYPE_LUXURY = 15;
	public static final int CAR_FILTER_CAR_TYPE_MINI_VAN = 16;


	public static EHIFilter getFilter(int key, Resources resource) {


		switch (key) {
			case LOC_FILTER_OPEN_24:
				return open24Filter(resource, key);
			case LOC_FILTER_TYPE_AIRPORT:
				return airportLocationFilter(resource, key);
			case LOC_FILTER_OPEN_SUNDAY:
				return openSundaysFilter(resource, key);
			case LOC_FILTER_PORT_STATION:
				return portLocationFilter(resource, key);
			case LOC_FILTER_RAIL_STATION:
				return railLocationFilter(resource, key);
			case CAR_FILTER_CAR_TYPE_SUV:
				return carTypeFilter(resource, key);
			case CAR_FILTER_CAR_TYPE_CARGO_VAN:
				return carTypeFilter(resource, key);
			case CAR_FILTER_CAR_TYPE_MINI_VAN:
				return carTypeFilter(resource, key);
			case CAR_FILTER_CAR_TYPE_LUXURY:
				return carTypeFilter(resource, key);
			case CAR_FILTER_CAR_TYPE_TRUCK:
				return carTypeFilter(resource, key);
			case CAR_FILTER_TRANSMISSION_AUTOMATIC:
				return transmissionAutomaticFilter(resource, key);
			case CAR_FILTER_TRANSMISSION_MANUAL:
				return transmissionManualFilter(resource, key);
			case CAR_FILTER_PASSENGER_CAPACITY_1:
				return passengerCountFilter(resource, 1, key);
			case CAR_FILTER_PASSENGER_CAPACITY_2:
				return passengerCountFilter(resource, 2, key);
			case CAR_FILTER_PASSENGER_CAPACITY_3:
				return passengerCountFilter(resource, 3, key);
			case CAR_FILTER_PASSENGER_CAPACITY_4:
				return passengerCountFilter(resource, 4, key);
			case CAR_FILTER_PASSENGER_CAPACITY_5:
				return passengerCountFilter(resource, 5, key);
			case CAR_FILTER_PASSENGER_CAPACITY_6_or_more:
				return passengerCountFilter(resource, 6, key);
			default:
				return nanFilter();
		}
	}

	private static EHIFilter airportLocationFilter(final Resources resource, final int id) {
		return new EHIFilter<EHISolrLocation>() {
			@Override
			public <T> T applyFilter(EHISolrLocation filter) {
				return null;
			}

			@Override
			public String getTitle() {
				return resource.getString(R.string.location_filter_airport_title);
			}

            @Override
            public int getID() {
                return id;
            }
        };
	}

	private static EHIFilter railLocationFilter(final Resources resource, final int id) {

		return new EHIFilter<EHISolrLocation>() {
			@Override
			public <T> T applyFilter(EHISolrLocation filter) {
				return null;
			}

			@Override
			public String getTitle() {
				return resource.getString(R.string.location_filter_rail_station_title);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	private static EHIFilter portLocationFilter(final Resources resource, final int id) {
		return new EHIFilter<EHISolrLocation>() {
			@Override
			public <T> T applyFilter(EHISolrLocation filter) {
				return null;
			}

			@Override
			public String getTitle() {
				return resource.getString(R.string.location_filter_port_of_call_title);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	public static EHIFilter<EHICarClassDetails> transmissionManualFilter(final Resources resources, final int id) {
		return new EHIFilter<EHICarClassDetails>() {
			@Override
			public Boolean applyFilter(EHICarClassDetails filter) {
				return filter.getTransmissionType().equalsIgnoreCase(EHICarFilter.TRANSMISSION_MANUAL);
			}

			@Override
			public String getTitle() {
				return getTransmissionList(resources).get(2);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	public static EHIFilter<EHICarClassDetails> transmissionAutomaticFilter(final Resources resources, final int id) {
		return new EHIFilter<EHICarClassDetails>() {
			@Override
			public Boolean applyFilter(EHICarClassDetails filter) {
				return filter.getTransmissionType().equalsIgnoreCase(EHICarFilter.TRANSMISSION_AUTOMATIC);
			}

			@Override
			public String getTitle() {
				return getTransmissionList(resources).get(1);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	public static EHIFilter<EHICarClassDetails> passengerCountFilter(final Resources resources, final int passengers, final int id) {
		return new EHIFilter<EHICarClassDetails>() {
			@Override
			public Boolean applyFilter(EHICarClassDetails filter) {
				String code = filter.getPassengerCode();
				if(code == null){
					return null;
				}
				return Integer.parseInt(code) >= passengers;
			}

			@Override
			public String getTitle() {
				return getPassengerList(resources).get(passengers - 1);
			}
            @Override
            public int getID() {
                return id;
            }

		};
	}

	public static ArrayList<String> getPassengerList(Resources resources) {
		if(PASSENGERS != null){
			return PASSENGERS;
		}

		ArrayList<String> passengerList = new ArrayList<>();
		passengerList.add(resources.getString(R.string.class_select_filter_passenger_capacity_all));
		passengerList.add(resources.getString(R.string.class_select_filter_passenger_capacity_two));
		passengerList.add(resources.getString(R.string.class_select_filter_passenger_capacity_three));
		passengerList.add(resources.getString(R.string.class_select_filter_passenger_capacity_four));
		passengerList.add(resources.getString(R.string.class_select_filter__passenger_capacity_five));
		passengerList.add(resources.getString(R.string.class_select_filter_passenger_capacity_6_plus));
		PASSENGERS = passengerList;

		return PASSENGERS;
	}

	public static ArrayList<String> getTransmissionList(Resources resources) {
		if(TRANSMISSIONS != null){
			return TRANSMISSIONS;
		}

		ArrayList<String> transmissionList = new ArrayList<>();

		transmissionList.add(resources.getString(R.string.class_select_filter_transmission_all));
		transmissionList.add(resources.getString(R.string.class_select_filter_transmission_automatic));
		transmissionList.add(resources.getString(R.string.class_select_filter_transmission_manual));

		TRANSMISSIONS = transmissionList;
		return TRANSMISSIONS;
	}

	public static EHIFilter<EHICarClassDetails> carTypeFilter(final Resources resources, final int carTypeFilter) {
		return new EHIFilter<EHICarClassDetails>() {
			@Override
			public Boolean applyFilter(EHICarClassDetails filter) {

				switch (carTypeFilter) {
					case CAR_FILTER_CAR_TYPE_SUV:
						return filter.getCode().toUpperCase().charAt(1) == 'F';
					case CAR_FILTER_CAR_TYPE_CARGO_VAN:
						return filter.getCode().toUpperCase().charAt(1) == 'K';
					case CAR_FILTER_CAR_TYPE_MINI_VAN:
						return filter.getCode().toUpperCase().charAt(1) == 'V';
					case CAR_FILTER_CAR_TYPE_LUXURY:
						return filter.getCode().toUpperCase().charAt(0) == 'L' || filter.getCode().toUpperCase().charAt(0) == 'P'
								|| filter.getCode().toUpperCase().charAt(0) == 'U' || filter.getCode().toUpperCase().charAt(0) == 'W'
                                || filter.getCode().toUpperCase().charAt(0) == 'J' || filter.getCode().toUpperCase().charAt(0) == 'R';
					case CAR_FILTER_CAR_TYPE_TRUCK:
						return filter.getCode().toUpperCase().charAt(1) == 'K' || filter.getCode().toUpperCase().charAt(1) == 'P';
					default:
						return false;
				}
			}

			@Override
			public String getTitle() {
				switch (carTypeFilter) {
					case CAR_FILTER_CAR_TYPE_SUV:
						return resources.getString(R.string.class_select_filter_suv);
					case CAR_FILTER_CAR_TYPE_CARGO_VAN:
						return resources.getString(R.string.class_select_filter_cargo_van);
					case CAR_FILTER_CAR_TYPE_MINI_VAN:
						return resources.getString(R.string.class_select_filter_minivan);
					case CAR_FILTER_CAR_TYPE_LUXURY:
						return resources.getString(R.string.class_select_filter_luxury);
					case CAR_FILTER_CAR_TYPE_TRUCK:
						return resources.getString(R.string.class_select_filter_trucks);
					default:
						return "Not Found";
				}
			}

            @Override
            public int getID() {
                return carTypeFilter;
            }
		};
	}

	public static EHIFilter nanFilter() {
		return new EHIFilter<EHISolrLocation>() {
			@Override
			public Boolean applyFilter(EHISolrLocation solrLocation) {
				return false;
			}

			@Override
			public String getTitle() {
				return "";
			}

            @Override
            public int getID() {
                return 0;
            }
		};
	}

	public static EHIFilter openSundaysFilter(final Resources resource, final int id) {
		return new EHIFilter<EHISolrLocation>() {
			@Override
			public Boolean applyFilter(EHISolrLocation solrLocation) {
				return solrLocation.isOpenSundays();
			}

			@Override
			public String getTitle() {
				return resource.getString(R.string.location_filter_open_sunday_title);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	public static EHIFilter open24Filter(final Resources resource, final int id) {
		return new EHIFilter<EHISolrLocation>() {
			@Override
			public Boolean applyFilter(EHISolrLocation solrLocation) {
				return solrLocation.isOpen247();
			}

			@Override
			public String getTitle() {
				return resource.getString(R.string.location_filter_open_always_title);
			}

            @Override
            public int getID() {
                return id;
            }
		};
	}

	public static SparseArray<EHIFilter> translateToSparse(ArrayList<Integer> filterTypes, Resources resource) {
		SparseArray<EHIFilter> filters = new SparseArray<>();
		for (int i = 0; i < filterTypes.size(); i++) {
			filters.put(filterTypes.get(i), getFilter(filterTypes.get(i), resource));
		}
		return filters;
	}

	public static ArrayList<Integer> fetchActiveFilterTypes(SparseArray<EHIFilter> filters) {
		ArrayList<Integer> filterTypes = new ArrayList<>();

		int key;
		for (int i = 0; i < filters.size(); i++) {
			key = filters.keyAt(i);
			filterTypes.add(key);
		}
		return filterTypes;
	}


    public static boolean isFilter(SparseArray<EHIFilter> filters, int filterType, Resources resources){
        EHIFilter filter = getFilter(filterType, resources);
        for(int a=0; a < filters.size(); a++){
            if(filters.get(a).getID() == filter.getID()){
                return true;
            }
        }
        return false;
    }

	public static <T> ArrayList<T> applyFiltersAndOperator(SparseArray<EHIFilter> filters, List<T> data) {
		ArrayList<T> returnLocations = new ArrayList<>();
		Boolean add = true;
		int key = 0;
		for (int a = 0; a < data.size(); a++) {
			add = true;
			for (int b = 0; b < filters.size(); b++) {
				key = filters.keyAt(b);
				if (!(Boolean) filters.get(key).applyFilter(data.get(a))) {
					add = false;
					break;
				}
			}

			if (add) {
				returnLocations.add(data.get(a));
			}
		}
		return returnLocations;
	}

    public static <T> ArrayList<T> applyFiltersOrOperator(SparseArray<EHIFilter> filters, List<T> data) {
        if(filters.size() == 0){
            return new ArrayList<>(data);
        }
        ArrayList<T> returnLocations = new ArrayList<>();
        Boolean add = false;
        int key = 0;
        for (int a = 0; a < data.size(); a++) {
            add = false;
            for (int b = 0; b < filters.size(); b++) {
                key = filters.keyAt(b);
                if ((Boolean) filters.get(key).applyFilter(data.get(a))) {
                    add = true;
                    break;
                }
            }

            if (add) {
                returnLocations.add(data.get(a));
            }
        }
        return returnLocations;
    }

    public static boolean isPassengerOrTransmission(EHIFilter filter, Resources resources){
        return filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_1, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_2, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_3, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_4, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_5, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_PASSENGER_CAPACITY_6_or_more, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_TRANSMISSION_AUTOMATIC, resources).getID() ||
                filter.getID() == EHIFilterList.getFilter(EHIFilterList.CAR_FILTER_TRANSMISSION_MANUAL, resources).getID();
    }

	public static String getFilterText(SparseArray<EHIFilter> filters) {
		String string = "";
		for (int a = 0; a < filters.size(); a++) {
			string += filters.valueAt(a).getTitle() + ((a < filters.size() - 1) ? ", " : "");
		}
		return string;
	}


}