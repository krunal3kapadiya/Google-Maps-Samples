# Google Maps Samples [![Build Status](https://travis-ci.org/krunal3kapadiya/G-Maps.svg?branch=master)](https://travis-ci.org/krunal3kapadiya/GoogleMapsDemo)
Google Maps Samples is created to use the google maps feature very quickly.  
All basic implementation used for map are at one place and handy to use.

What you will find in this repositories?

- Location updates
> IN PROGRESS
- Search Places
> IN PROGRESS
- Getting different types of maps
```
map?.mapType = GoogleMap.MAP_TYPE_SATELLITE
map?.mapType = GoogleMap.MAP_TYPE_NORMAL
map?.mapType = GoogleMap.MAP_TYPE_TERRAIN
map?.mapType = GoogleMap.MAP_TYPE_HYBRID
```
- From LatLang get the address in the map
```
val address = Geocoder(this, Locale.getDefault()).getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
```

## Screenshots

|![screenshot](screenshots/screenshot_1.png)|![screenshot](screenshots/screenshot_2.png)|![screenshot](screenshots/screenshot_3.png)|


## How to contribute?
- Check out contribution :point_right: [Guidelines](./CONTRIBUTING.md)  

## Questions

Tweet me at: 
[![Twitter URL](https://img.shields.io/badge/Twitter-@krunal3kapadiya-blue.svg?style=for-the-badge)](https://twitter.com/krunal3kapadiya)

## Licence
    Copyright 2018 Krunal Kapadiya

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
