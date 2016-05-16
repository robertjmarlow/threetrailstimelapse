# Cerner Three Trails campus construction time lapse generator

Cerner is building [a fancy new campus](http://www.kansascity.com/news/business/development/article3845781.html). The construction company has a webcam that takes a picture of the progress of the construction of the campus every 20 minutes every day from 7:00AM to 5:00PM. [Here's an example](http://p-tn.net/pCAM/CERNERNE/archivepics.asp?m=4&d=13&y=2016&h=12&min=0). 

This library splices together a bunch of images from the archived webcam pictures of the construction to create a time lapse.

Here's a result from July 30, 2015 to May 12, 2016: [14.2MB gfycat](https://gfycat.com/WiltedEnergeticCopepod#?speed=0.5).

# Building

This library uses [gradle](http://gradle.org/) for its build lifecycle. If Gradle isn't installed, wrappers for both Windows and Unix-based systems are included with the repo that can invoke Gradle without having to install it.

For Windows-based machines:

```Shell
gradlew.bat build
```

For Unix variants:

```Shell
gradlew build
```

If Gradle is installed, run the build task.

# Redis Cache

To avoid [DDOS'ing](https://en.wikipedia.org/wiki/Denial-of-service_attack) the website, an optional but _highly recommended_ [Redis](http://redis.io/) chache is used to cache pages and images from the site.

If running on OSX/*nix, installation of Redis is [pretty straightforward](http://redis.io/download#installation).

If running on Windows, installation is much less straightforward. [redis-windows](https://github.com/ServiceStack/redis-windows) on GitHub has Windows-flavored Redis binaries. The problem with this is that you'd be reliant on the maintainers of the repo to create new versions of Redis when they're released. A [Vagrant VM](https://www.vagrantup.com/) of [Ubuntu Server](https://atlas.hashicorp.com/ubuntu/boxes/trusty64) with the Redis [port fowraded](https://www.vagrantup.com/docs/networking/forwarded_ports.html) to the host Windows machine is a viable option.

# Usage

## Getting Images

To get all images for a particular day:

```Java
import com.marlowsoft.threetrailstimelapse;
/* ... */
CampusImageRetriever retriever = new CampusImageRetriever();
LocalDate day = new LocalDate(2016, 4, 7);

List<BufferedImage> images = retriever.getDay(day);
```

To get images for a particular date range at a specific time each day:

```Java
import com.marlowsoft.threetrailstimelapse;
/* ... */
CampusImageRetriever retriever = new CampusImageRetriever();
LocalDate beginDate = new LocalDate(2016, 1, 1);
LocalDate endDate = new LocalDate(2016, 4, 15);
LocalTime timeOfDay = new LocalTime(12, 0);

List<BufferedImage> images = retriever.getDateRange(beginDate, endDate, timeOfDay);
```

**Note** that all returned lists from the `CampusImageRetriever` class are immutable! Specifically, a Guava-flavored [ImmutableList](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/collect/ImmutableList.html) will be returned. Any attempt to add or remove from the collection will result in an exception! 

## Encoding Images into a video

A wrapper around [jcodec](http://jcodec.org/) v0.1.9 is included for H264 encoding.

```Java
import com.marlowsoft.threetrailstimelapse.encode.VideoEncoder;
/* ... */
encode(images, "timelapse.webm");
```

The resultant video file is easily uploaded to [gfycat](https://gfycat.com/).

# License

Copyright 2016 Rob Marlow

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
