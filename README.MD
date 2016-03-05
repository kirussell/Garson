Garson
---
... at your service!

The android library to tip / highlight parts of your layout.
Just like that:

// TODO pics

```java
Garson.in(MainActivity.this)
       .with("I'm at your service, sir!")
       .withDimColor(res.getColor(R.color.red_green_blue));
       .tip(viewToHighlight);
```

TODOs
---
- Points to extend/customize tips ui (text positioning, content instead of text, animations, highlight options, something else...)
- Api to chain several Garsons' objs in appropriate conditional/eventual order
- Tests