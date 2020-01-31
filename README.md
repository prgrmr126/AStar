# AStar
A visualization of the A* pathfinding algorithm

## Usage

### Controls

* Hold the left mouse button and drag to make walls.
* Hold the right mouse button to remove walls and start and end points.
* Hold 's' and click point to create start point.
* Hold 'e' and click point to create end point.
* Click 'r' to reset pathfinder but keep walls and start and end points.
* Click 'c' to clear the entire screen including walls and start and end points.
* Click 'q' to quit the program.

### Running

**Java must be installed!**

If using shell, change directory to folder with AStar.jar and config.properties file

```sh
cd path/to/AStar/run
java -jar AStar.jar
```

OR

If using Jar launcher, just open the AStar.jar by double clicking

### Settings

To change settings, you can safely modify the config file (either  the [root](/config.properties) or the [run](/run/config.properties) file) but **do not change the property names** and config file must be kept in the same folder as the AStar.jar file

### Other

##### Notes:

If running with Eclipse, modify root config file.

If running JAR, modify config in run folder.

When resizing window, some nodes may go out of screen so clear the screen to prevent errors.
