# Group Project - *RoadTrip - Let's Go!*

### Group Members - *Yingbin Wang*, *Liubov Sireneva*, *Prerna Manaktala*, *Tessa Voon*

## Description
Put driving(walking, biking) route and points of interest together.

#### What does the app do?<br>
This app can tell you dining/gas station etc. choices (within certain distance or driving time) along the driving route so that user can dine/pump gas etc. and still reach destination on time.<br>
The choices will be presented on the map as well as listed with highlight information and user can click in to get more information. We will use Yelp, Foursquare etc. APIs.

<br><br>
## Wireframes

<br><br>

## User Stories:

### Onboarding
* [ ] User can login with email 
* [ ] User can view an onboarding guide (instructions to start using app)

### Setting Trip Filters
* [ ] User can update filters to improve route result  
  * [ ] User can specify starting location and destination
  * [ ] Address is sent to a Geocoding API to get latitude and longitude coordinates of start & end locations (autocomplete addresses if possible)
  * [ ] User can specify the kind of stops he / she is interested in (such as gas station, restaurant, hotel)
  
### Suggested Stops 
* [ ] User can view route with candidate stops 
  * [ ] Coordinates are sent to a Navigation API to generate & display route 
  * [ ] Retrieve & display candidate stops from Yelp / Foursquare API's with filters applied
  * [ ] User can click on a stop to view more details such as location, rating etc (from Yelp / Foursquare API)
  * [ ] User can filter further (e.g. by rating, proximity)
  
### Managing Trip Filters
* [ ] User can save current filter for use next time 
* [ ] User can view, edit or delete a list of filters he / she saved 

### Optional 
* [ ] Estimate the amount of time needed to complete the trip
* [ ] Give a few suggested routes and estimated time / cost etc 
