# Android Drawing Canvas

This is an example Android app that shows you how to use my custom Drawing View control. You can find this control in co.martinbaciga.drawingtest.ui.component. Here's how to use it:

<img src="https://github.com/martinbaciga/android-drawing-canvas/blob/master/DrawingView.png" width="300"/>

###1. Add the DrawingView to your layout file:

```xml
<co.martinbaciga.drawingtest.ui.component.DrawingView
        android:id="@+id/drawing_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>
```

###2. Create a new DrawingView:

``` java
DrawingView mDrawingView = (DrawingCanvas) findViewById(R.id.drawing_view);
```

###3. Change background color:

```java
mDrawingView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
```

###4. Change paint color:

```java
mDrawingView.setPaintColor(ContextCompat.getColor(this, android.R.color.black));
```

###5. Change paint stroke width:

```java
mDrawingView.setPaintStrokeWidth(10);
```

###6. Clear canvas:

```java
mDrawingView.clear();
```

###7. Undo action:

```java
mDrawingView.undo();
```

###8. Redo action:

```java
mDrawingView.redo();
```

###9. Get bitmap of the canvas:

```java
mDrawingView.getBitmap();
```

Really easy to use right? Happy coding :)
