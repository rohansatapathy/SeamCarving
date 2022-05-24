# SeamCarving

A program that intelligently resizes images in an interactive GUI by using a seam carving algorithm. This 
project was completed to the specifications of [Princeton's CS Seam Carving assignment](https://www.cs.princeton.edu/courses/archive/fall14/cos226/assignments/seamCarving.html).

Images loaded in the program can be resized by resizing the GUI window, then can be saved using the File > Save dialog.

## Installation

To run this program, you need to have a Java JRE installed, version 11 or higher. After that, you can head to the
[releases page](https://github.com/rohansatapathy/SeamCarving/releases/latest) to download the file `SeamCarving.jar`.
Move the jar file into a directory containing images you would like to resize. 

## Usage

Run the following command to open the GUI:
```sh
$ java -jar SeamCarving.jar <path/to/image>
```
After running this command, a GUI should open up containing the image passed to the program. As you resize the window,
the image should resize in such a way that only irrelevant parts of the image are moved with each resize. Note that the
program will be somewhat laggy on larger image files, but will increase in speed as the image gets smaller. 

To save the resized image, use the `File > Save` dialog and choose a filename ending in `.jpg` or `.png`. 

## Contributing

If you run into any trouble using this program, please file an issue, so I can resolve it. If you would like to 
contribute, feel free to fork the repo and file a PR. This is one of my first projects using Java and any help would be 
much appreciated.

## License

Copyright (C) 2022 Rohan Satapathy. Code released under MIT License. See [LICENSE](./LICENSE) for more details. 
