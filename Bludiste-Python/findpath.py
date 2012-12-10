#!/usr/bin/python

"""
The main program for finding a path through a maze

It must accept two input parameters:
  maze_filename ... full path to the txt image
  path_filename ... name where the found path will be stored

Example of call: findpath.py maps/easy/easy1.txt easy1_path.txt

- the text version of the map can be created by convert utility from
  imagemagick (convert map.bmp map.txt), see http://www.imagemagick.org/
- the path for the output file must exists and be writable otherwise it fails

specification of an eligible (valid) path:
  1/ must be continous in 4-neighborhood (distance 1 in L1 norm)
  2/ must begin (first node listed) in start node and finish in the final node

We suggest you implement a your own path evaluator that validates the
path eligibility. Assuming an eligible path, the main quality is its
length. The shorter path the better. 

The path must printed one node per line. If there is no path is found,
string None is to be printed on the first line.

The program may use some of the interactive graphics provided but in
any casy it must be able to run in a non-interactive mode. The code
must be uploaded in the non-interactive mode. So be sure to switch the
graphics off.  You may print to the stdout but please keep the amount
of printed information valuable.

The code below is just an example of working code, you may change
whatever you want. The above specified I/O is the only compatibility
required.

"""

import sys
import rphmaze
import mazegr
import solvers

DO_GRAPHICS = True

if DO_GRAPHICS:
    from Tkinter import *

def parse_inputs(arglist):
    """parse and check input CLI arguments"""
    if not(len(arglist)==3):
        raise IOError("wrong number of input arguments, expected: findpath maze_filename path_filename")
    maze_filename = arglist[1]
    path_filename = arglist[2]
    return(maze_filename,path_filename)

def init_graphics(solver):
    root = Tk()
    root.title(u"RPH Maze, q->quit")
    frame = Frame(root)
    frame.pack(fill=BOTH, expand=YES)
    root.bind("<Key>",lambda event, arg1=solver: handle_keys(event, arg1))
    return(root,frame)

def handle_keys(event,solver):
    print "The key: ",event.char, "has been pressed"
    if event.char == "q": # then quit the program
        print event.char,"pressed, exiting the program"
        # raise SystemExit
        sys.exit()
    if event.char == "d": # dummy path, regardless of walls or anything
        solver.clear_path()
        solver.compose_path()
        print solver.path
        draw_path(maze_canvas,solver.path)
        save_path(path_filename,my_solver.path)

def draw_path(maze_canvas,path):
    """draw the path to the maze graphics"""
    for pos in my_solver.path:
        maze_canvas.draw_cell(pos[0],pos[1],"brown")
        maze_canvas.draw_text(pos[0],pos[1],"P")
        maze_canvas.draw_start()
        maze_canvas.draw_final()

def save_path(path_filename,path):
    try:
        f = open(path_filename,'w')
    except:
        raise IOError("could not open %s for writing"%path_filename)
    if path is not(None):
        for node in path:
            f.write("%d,%d\n"%node)
    else:
        f.write("None\n")
    f.close()

########################################################
# main program
maze_filename, path_filename = parse_inputs(sys.argv)
maze = rphmaze.RPHMaze(maze_filename)
# solver init
my_solver = solvers.DummySolver(maze)
if DO_GRAPHICS:
    rootwin,frame = init_graphics(my_solver)
    maze_canvas = mazegr.MazeGr(frame,maze)
    rootwin.mainloop()
else:
    # find the path
    my_solver.compose_path()
    print my_solver.path
    save_path(path_filename,my_solver.path)
    
