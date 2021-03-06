
Garson [![Build Status](https://travis-ci.org/kirussell/Garson.svg?branch=master)](https://travis-ci.org/kirussell/Garson)
---
![](/sample/src/main/res/mipmap-xhdpi/ic_launcher.png)

... at your service!

The android library to tip / highlight parts of your layout.
Just like that:

![](assets/screenshots/garson_sample_screenshots.png)

How to use:
---
- Highlight view
```java
Garson.in(MainActivity.this)
       .with("I'm at your service, sir!")
       .tip(viewToHighlight);
```
-
- More in sample

Release notes
---
* v0.1.0
       * ~~Main functionality~~
       * ~~Points to extend/customize tips ui (text positioning, content instead of text, highlight options)~~
       * ~~Tests ¯\_ツ_/¯~~
       * ~~Deploy~~

TODOs
---
* v0.2.0
       * Api to chain several Garsons' objs in appropriate conditional/eventual order