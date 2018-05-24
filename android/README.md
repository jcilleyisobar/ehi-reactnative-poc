Enterprise Mobile
=================
Android version of the Enterprise Mobile App.

Building
===

Make sure you run `/utils/developer_setup.sh`

Architecture
----

```
{Services} <-> [Network Layer] <-> [Model] <-> [ViewModel] <-> [View]
```
The Enterprise Mobile application is utilizing Model-View-ViewModel as it's application architecture.
Our viewmodels generate network objects that are parsed and dispatched via our network layer (`EnterpriseNetworkService.java`).
This same layer generates our network model objects using `Gson` and returns the model to the viewmodel via our `IApiCallback` class.
On the completion of the response, the viewmodel sets the value in it's own `ReactorVar` properties that the view will react to, updating the user interface.

----

###Network
The network layer (`EnterpriseNetworkService.java`) utilizes `AbstractRequestProvider` objects to make network requests to services.
The `AbstractRequestProvider` specifies a request's type, header, body and response class.
Using this, the network service is able to fully create and dispatch requests and generate the correct deserialized model class.

----

###ViewModel
Our viewmodels inherit from `BaseViewModel` and have the following lifecycle:

```java
onAttachToView();   // called from a view's onResume()

onDetachFromView()  // called from a view's onPause()
```

These callbacks allow the viewmodel to instantiate and clean up resources cleanly.

In addition to this, the viewmodel exposes the following convenience methods

```java
performRequest(AbstractRequestProvider, IApiCallback)

showProgress(boolean)

getResources()
```
`performRequest` allows the viewmodel to queue requests, in the event the network service hasn't bound to the viewmodel yet.

`showProgress` allows the viewmodel to handle whether or not the view should be displaying a progress indicator.

`getResources` returns a resources object to dereference android resource IDs.

#####ReactorVar
ViewModels utilize `ReactorVar` objects from the `Reactor` package.
These are thin wrappers around an object value and a `ReactoDependency` object.
Using these objects allows the viewmodel to set properties that can be bound to by the view layer.

#####@AutoUnbind
Using the @AutoUnbind annotation on a `ReactorVar` will automatically unbind the dependency during the `onDetachFromView` callback.
This annotation is the preffered method of cleaning up `ReactorVar` object, as it is handled automatically reduces chances of memory leaks.

####@AutoUnbindAll
Similar to `@AutoUnbind` except that it applies to every enclosed `ReactorVar` in the class.
Use this when you want the unbind hammer to come down on the class.

###Tests
The project utilizes JUnit4 in conjunction with Mockito to test important pieces of our viewmodels. 
Additionally in order to have a viewmodel be able to work with resources there is a
`DefaultMockedContext` which will prevent a viewmodel from crashing when it attempts to get a value from resources.
This class is built using a wrapper around Mockito called `MockableObject`
There are examples and documentation inside of `src/test` all tests in this directory are JVM tests.
 
To run: right click the `java` folder in `src/test` and create a configuration for "All Tests" and run that configuration.

-----

###View
The Enterprise Mobile application utilizes a variety of view objects.
The majority of the application uses `Fragment` objects with custom view containers.
Views bind to viewmodels via `Reactor` and the convenience method `addReaction(String, ReactorComputationFunction)`

More information about reactor can be seen here: [github](https://github.com/dinosaurwithakatana/Reactor), [API](http://dwak.io/javadoc/io/dwak/reactor/index.html)

####@ViewModel
Using the `@ViewModel` annotation on the view class will allow the base class to inject the expected viewmodel into the annotated object, which becomes accessible via `getViewModel()`

####@Extra
Using the `@Extra` annotation on `public static final String` objects allows the annotation processor to generate builders and extractors for fragments or activities.
This should be used instead of the `newInstance` pattern to reduce code boilerplate on fragments, and be used to generate the expected `Intent` on activities.
The annotation processor will generate <FragmentName/ActivityName>Helper.<Builder, Extractor> objects that can be used to instantiate the expected fragment.

Creating a new fragment:

```java
public class FooFragment extends Fragment {
    @Extra(value = List.class, type = String.class, required = false)
    public static final EXTRA_LIST_OF_STRINGS = "LIST_OF_STRINGS";
    @Extra(String.class) public static final TEST_EXTRA = "TEST_EXTRA";
    @Extra(value = Date.class, required = false) public static final TEST_DATE_EXTRA = "TEST_DATE_EXTRA";

    @Override
    public void onCreate(...){
        if(getArguments() != null){
            final FooFragmentHelper.Extractor extractor = new FooFragmentHelper.Extractor(this);
            String testValue = extractor.testExtra(); //returns "testvalue" from activity implementation below

            // Since the TEST_DATE_EXTRA isn't required, the extractor has tagged
            // the corresponding method with `@Nullable`, so you should check if
            // extra is null before referencing it
            if(extractor.testDateExtra() != null){
              Date testDate = extractor.testDateExtra();
            }
            
            if(extractor.listOfStrings() != null){
                List<String> listOfString = extractor.listOfStrings(); //Client side casting isn't necessary, since we've passed in the expected types in the annotation
                listOfString.length(); 
            }
        }
    }
}

```

```java
public class FooActivity extends Activity {
    @Override
    public void onCreate(...){
        FooFragment fragment = new FooFragmentHelper.Builder()
                                                    .testExtra("testValue")
                                                    .build();
    }
}
```


`@NoExtras` can be used to generate builders as well

```java
@NoExtras
public class BarFragment extends Fragment {
}
```

```java
public class BarActivity extends Activity {
    @Override
    public void onCreate(...){
        BarFragment fragment = new BarFragmentHelper.Builder().build();
    }
}
```
###ReactorBinding
`ReactorBinding` contains a handful of helper methods for binding properties directly to views using `Reactor`.
Example usage can be seen in `RedemptionFragment.java` and `DatePickerFragment.java`.

###DataBinding
The application is using the new Android Databinding Framework ([guide](https://developer.android.com/tools/data-binding/guide.html)) to reduce the usage of `findViewById` throughout the code.

####BindingAdapters
* "font": We are using Data Binding for a binding adapter for custom fonts in the xml via the `app:font` attribute. All possible fonts can be found in `values/fonts.xml`
* "paddingHorizontal": Sets both the left and right padding for a view
* "paddingVertical": Sets both the top and bottom padding for a view

----

###Flavors
The app has 3 flavors:

* uat: proguarded, test logins enabled and stetho enabled

* dev: test logins enabled, stetho enabled

* prod: proguarded, test logins disabled, stetho disabled

---

###Stetho
The project utilizes [stetho](https://facebook.github.io/stetho/) for advanced debugging capabilities.
Stetho is available in the uat and dev build flavors.
