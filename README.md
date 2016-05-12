# java-image-compare
A small java image comparer hacked in ~2 hours.

The goal of this project was to build a functional image comparer in Java in less than 3 hours, with the following contraints:

1. The output of the comparison should be a copy of one of the images image with differences outlined with red rectangles.

2. Pixels (with the same coordinates in two images) can be visually similar, but have different values of RGB. We should only consider 2 pixels to be "different" if the difference between them is more than 10%.

3. No third party libraries, no borrowed code.

Usage: `compare image1.png image2.jpg output.png`

![alt tag](techcrunch1.png)
![alt tag](techcrunch2.png)
![alt tag](comparison.png)

![alt tag](map.comparison.png)
