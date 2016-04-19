# Cerner Three Trails campus construction time lapse generator

Cerner is building [a fancy new campus](http://www.kansascity.com/news/business/development/article3845781.html). The construction company has a webcam that takes a picture of the progress of the construction of the campus every 20 minutes every day from 7:00AM to 5:00PM. [Here's an example](http://p-tn.net/pCAM/CERNERNE/archivepics.asp?m=4&d=13&y=2016&h=12&min=0). 

This library splices together a bunch of images from the archived webcam pictures of the construction to create a time lapse.

Here's a (slightly choppy) result from January 1, 2016 to April 15, 2016: [3.4MB gfycat](https://gfycat.com/GroundedZanyGuanaco#?speed=0.25).

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
